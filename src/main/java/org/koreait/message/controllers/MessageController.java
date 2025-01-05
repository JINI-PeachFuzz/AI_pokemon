package org.koreait.message.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.services.FileInfoService;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.koreait.global.paging.ListData;
import org.koreait.message.entities.Message;
import org.koreait.message.services.MessageInfoService;
import org.koreait.message.services.MessageSendService;
import org.koreait.message.services.MessageStatusService;
import org.koreait.message.validators.MessageValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public String process(@Valid RequestMessage form, Errors errors, Model model) { // 쪽지작성 처리
        commonProcess("send", model);

        messageValidator.validate(form, errors);

        if (errors.hasErrors()) {
            // 업로드한 파일 목록 form에 추가 / gid 와 location 정보를 가지고
            String gid = form.getGid();
            form.setEditorImages(fileInfoService.getList(gid, "editor", FileStatus.ALL));
            form.setAttachFiles(fileInfoService.getList(gid, "attach", FileStatus.ALL));


            return utils.tpl("message/form"); // 검증실패시에는 넘어가는게 아니라 양식을 보여주는 걸로~
        }

        sendService.process(form); // 전송이 되었는지 확인하고 조회쪽으로 넘어가게 됨 // 서비스 연동

        return "redirect:/message/list";
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
    public String view(@PathVariable("seq") Long seq, Model model) {
        commonProcess("view", model);

        Message item = infoService.get(seq);

        model.addAttribute("item", item);

        statusService.change(seq); // 열람 상태로 변경

        return utils.tpl("message/view");
    }

    @DeleteMapping
    public String delete(@RequestParam(name = "seq", required = false) List<String> seq) { // 쪽지 삭제기능
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