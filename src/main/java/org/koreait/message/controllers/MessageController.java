package org.koreait.message.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.koreait.message.controllers.validators.MessageValidator;
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
            return utils.tpl("message/form"); // 검증실패시에는 넘어가는게 아니라 양식을 보여주는 걸로~
        }

        return "redirect:/message/list";
    }

    /**
     * 보낸거나 받은 쪽지 목록
     *
     * @return
     */
    @GetMapping("/list")
    public String list(Model model) {
        commonProcess("list", model);

        return utils.tpl("message/list");
    }

    @GetMapping("/view/{seq}")
    public String view(@PathVariable("seq") Long seq, Model model) {
        commonProcess("view", model);

        return utils.tpl("message/view");
    }

    @DeleteMapping
    public String delete(@RequestParam(name="seq", required = false) List<String> seq) { // 쪽지 삭제기능
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
            addCommonScript.add("ckeditor5/ckeditor");
            addScript.add("message/send");

        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);

    }
}