package org.koreait.admin.basic.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.admin.global.menu.MenuDetail;
import org.koreait.admin.global.menu.Menus;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.entities.SiteConfig;
import org.koreait.global.libs.Utils;
import org.koreait.global.services.CodeValueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;

@Controller
@ApplyErrorPage
@RequiredArgsConstructor
@RequestMapping("/admin/basic")
public class BasicController {

    private final CodeValueService codeValueService;
    private final Utils utils;

    @ModelAttribute("menuCode")
    public String menuCode() {
        return "basic"; // 컨트롤 상단쪽에 메뉴코드가 들어가 있다. 스타일적으로 구분하기 위해서 넣어준거
        // 검사쪽에서 menu on 같이 on이 추가되게 하기 위해서
    }

    @ModelAttribute("submenus")
    public List<MenuDetail> submenus() {
        return Menus.getMenus(menuCode());
    }

    @GetMapping({"", "/siteConfig"})
    public String siteConfig(Model model) {
        commonProcess("siteConfig", model);

        SiteConfig form = Objects.requireNonNullElseGet(codeValueService.get("siteConfig", SiteConfig.class), SiteConfig::new);

        model.addAttribute("siteConfig", form);

        return "admin/basic/siteConfig"; // 템플릿 경로 / 위에 @RequestMapping("/admin/basic")와 관련없음
    }


    @PatchMapping("/siteConfig")
    public String siteConfigPs(SiteConfig form, Model model) {
        commonProcess("siteConfig", model);

        codeValueService.save("siteConfig", form);

        utils.showSessionMessage("저장되었습니다.");

        return "admin/basic/siteConfig";
    }

    /***
     * 기본설정 공통 처리 부분
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        // 모드값이 코드가 될거임
        model.addAttribute("subMenuCode", mode);

    }
}
