package org.koreait.message.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.services.FileInfoService;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.koreait.global.paging.ListData;
import org.koreait.message.entities.Message;
import org.koreait.message.services.MessageDeleteService;
import org.koreait.message.services.MessageInfoService;
import org.koreait.message.services.MessageSendService;
import org.koreait.message.services.MessageStatusService;
import org.koreait.message.validators.MessageValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@ApplyErrorPage
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final Utils utils;
    private final MessageValidator messageValidator;
    private final FileInfoService fileInfoService;
    private final MessageSendService sendService; // 전송서비스
    private final MessageInfoService infoService;
    private final MessageStatusService statusService;
    private final MessageDeleteService deleteService;
    private final ObjectMapper om;

    @ModelAttribute("addCss")
    public List<String> addCss() {
        return List.of("message/style");

    }

    /**
     * 쪽지 작성 양식
     *
     * @return
     */
    @GetMapping
    public String form(@ModelAttribute RequestMessage form, Model model) {
        commonProcess("send", model);

        form.setGid(UUID.randomUUID().toString()); // 처음에는 발급해야함/그룹아이디를 발급한거
        // 구분하기 위한거 UUID를 랜덤으로 만듦

        return utils.tpl("message/form");
    }

    /**
     * 쪽지 작성
     *
     * @return
     */
    @PostMapping
    public String process(@Valid RequestMessage form, Errors errors, Model model, HttpServletRequest request) { // 쪽지작성 처리
        commonProcess("send", model);

        messageValidator.validate(form, errors);

        if (errors.hasErrors()) {
            // 업로드한 파일 목록 form에 추가 / gid 와 location 정보를 가지고
            String gid = form.getGid();
            form.setEditorImages(fileInfoService.getList(gid, "editor", FileStatus.ALL));
            form.setAttachFiles(fileInfoService.getList(gid, "attach", FileStatus.ALL));


            return utils.tpl("message/form"); // 검증실패시에는 넘어가는게 아니라 양식을 보여주는 걸로~
        }

        Message message = sendService.process(form); // 전송이 되었는지 확인하고 조회쪽으로 넘어가게 됨 // 서비스 연동
        long totalUnRead = infoService.totalUnRead(form.getEmail());
        System.out.println("totalUnRead:" + totalUnRead); // totalUnRead 값이 0으로 나와서 확인하기 위해서 테스트!!
        // 데이터 가공 S
        Map<String, Object> data = new HashMap<>();
        data.put("item", message);
        data.put("totalUnRead", totalUnRead); // JSON문자열로 바꿔서 보내볼거임


        StringBuffer sb = new StringBuffer();

        try {
            String json = om.writeValueAsString(data);
            sb.append(String.format("if (typeof webSocket != undefined) { webSocket.onopen = () => webSocket.send('%s'); }", json));
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // 유입이 되는지 확인위해서 넣었음 / 순환참조가 발생해서 안보였던것
        }

        sb.append(String.format("location.replace('%s');",request.getContextPath() + "/message/list"));

        System.out.println(sb);

        // 여기까지가 데이터 가공한거 E
        model.addAttribute("script", sb.toString());

        return "common/_execute_script";
    }

    /**
     * 보낸거나 받은 쪽지 목록
     *
     * @return
     */
    // 리스트 목록에 대한 거
    @GetMapping("/list")
    public String list(@ModelAttribute MessageSearch search, Model model) {
        commonProcess("list", model);
        // MessageSearch search의 서치에서 모드값이 없을 경우를 대비해 기본값을 추가해줌
        String mode = search.getMode();
        search.setMode(StringUtils.hasText(mode) ? mode : "receive"); // 내가 받은거 먼저 나올거임 / 기본값은 받은 쪽지!

        ListData<Message> data = infoService.getList(search);
        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());

        return utils.tpl("message/list");
    }

    // 리스트 개별 조회
    @GetMapping("/view/{seq}")
    public String view(@PathVariable("seq") Long seq, Model model, HttpServletRequest request) {
        commonProcess("view", model); // HttpServletRequest request를 가지고 referer을 확인할 수 있게

        Message item = infoService.get(seq);
        model.addAttribute("item", item);

        statusService.change(seq); // 열람 상태로 변경

        String referer = Objects.requireNonNullElse(request.getHeader("referer"),"");
        // referer정보가 없어도 빈문자열로 나올 수 있게
        model.addAttribute("mode", referer.contains("mode=send") ? "send":"receive");

        return utils.tpl("message/view");
    }

    @GetMapping("/delete/{seq}")
    public String delete(@PathVariable("seq") Long seq, @RequestParam(name = "mode", defaultValue = "receive") String mode) { // 쪽지 삭제기능 / 모드값을 추가했고 없을 땐 기본값이 receive임

        deleteService.process(seq, mode);

        return "redirect:/message/list";
    }


    /***
     * 컨트롤러 공통 처리
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "list";
        String pageTitle = "";
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();

        if (mode.equals("send")) { // 쪽지 보내기
            pageTitle = utils.getMessage("쪽지_보내기");
            addCommonScript.add("fileManager");
            addCommonScript.add("ckeditor/ckeditor");
            addScript.add("message/send");

        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);

    }
}