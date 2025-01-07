package org.koreait.admin.product.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.admin.global.menu.SubMenus;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.services.FileInfoService;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplyErrorPage
@RequiredArgsConstructor
@Controller("adminProductController")
@RequestMapping("/admin/product")
public class ProductController implements SubMenus {

    private final Utils utils; // 템플릿이 필요할 수도 있으니 남겨둠 / 관리자라서 필요는 없음
    private final FileInfoService fileInfoService; // 이걸가지고 바로 조회할꺼 / gid뿐만아니라 location도 같이 조회할 예정

    @Override
    @ModelAttribute("menuCode")
    public String menuCode() {
        return "product";
    }

    /***
     * 상품 목록
     *
     * @param model
     * @return
     */
    @GetMapping({"", "/list"}) // 상품목록
    public String list(Model model) {
        commonProcess("list", model);

        return "admin/product/list";
    }


    /***
     * 상품 등록
     *
     * @param model
     * @return
     *
     * // Menus의 상품관리에 맞게 commonProcess("요기위치", model);를 변경해줌
     */
    @GetMapping("/add")
    public String add(@ModelAttribute RequestProduct form, Model model) {
        commonProcess("add", model); // save를 해서 상품등록 수정을 같이

        form.setGid(UUID.randomUUID().toString()); // 중복되지않는 ID생성

        return "admin/product/add";
    }

    /***
     * 상품 정보 수정
     *
     * @param seq
     * @param model
     * @return
     */
    @GetMapping("/edit/{seq}")
    public String edit(@PathVariable("seq") Long seq, Model model) {
        commonProcess("edit", model);

        return "admin/product/edit";

    }

    /***
     *  상품 등록, 수정 처리
     *
     * @return
     */
    @PostMapping("/save")
    public String save(@Valid RequestProduct form, Errors errors, Model model) {
        String mode = form.getMode();
        mode = StringUtils.hasText(mode) ? mode : "add";

        commonProcess(mode, model); // 모드가 어떤건지에 따라 달라져야하니까

        if (errors.hasErrors()) { // 에러발생해도 form에 있는 데이터는 메시지와 동일하게 유지되어야함
            String gid = form.getGid();
            form.setMainImages(fileInfoService.getList(gid, "main", FileStatus.ALL));
            form.setListImages(fileInfoService.getList(gid, "list", FileStatus.ALL));
            form.setEditorImages(fileInfoService.getList(gid, "editor", FileStatus.ALL));

            return "admin/product/" + mode;
        }

        // 여기에는 상품 등록, 수정 처리 서비스를 넣으면 됨.

        return "redirect:/admin/product/list"; // 상품목록 수정이 다되면 목록으로 돌아가게
    }

    /***
     * 상품 분류 목록
     *
     * @return
     */
    @GetMapping("/category")
    public String categoryList(Model model) {
        commonProcess("category", model);

        return "admin/product/category/list";
    }

    /***
     * 분류 등록
     *
     * @return
     */
    @GetMapping({"/category/add", "/category/edit/{cate}"})
    public String categoryUpdate(@PathVariable(name="cate", required = false) String cate, Model model) {
        commonProcess("category", model);

        return "admin/product/category/add";
    }

    /***
     * 분류 등록, 수정 처리
     *
     * @param model
     * @return
     */
    @PostMapping("/category/save")
    public String categorySave(Model model) {
        commonProcess("category", model);

        return "redirect:/admin/product/category";
    }

    /**
     * 배송 정책 관리
     *
     * @param model
     * @return
     */
    @GetMapping("/delivery")
    public String delivery(Model model) {
        commonProcess("delivery", model);

        return "admin/product/delivery/list";
    }


    /**
     * 공통 처리 부분
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "list"; // 기본값, 상품목록이 나오게

        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        String pageTitle = "";

        if (mode.equals("list")) {
            pageTitle = "상품목록";
        } else if (mode.equals("add") || mode.equals("edit")) {
            pageTitle = mode.equals("edit") ? "상품수정" : "상품등록";
            addCommonScript.add("fileManager");
            addCommonScript.add("ckeditor5/ckeditor");
            addScript.add("product/product");

        } else if (mode.equals("category")) {
            pageTitle = "분류관리";

        } else if (mode.equals("delivery")) {
            pageTitle = "배송정책관리";
        }

        pageTitle += " - 상품관리";

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("subMenuCode", mode);
    }
}