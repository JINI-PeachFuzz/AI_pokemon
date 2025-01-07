package org.koreait.admin.board.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.admin.board.validators.BoardValidator;
import org.koreait.admin.global.menu.SubMenus;
import org.koreait.board.entities.Board;
import org.koreait.board.services.configs.BoardConfigInfoService;
import org.koreait.board.services.configs.BoardConfigUpdateService;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.koreait.global.paging.ListData;
import org.koreait.member.constants.Authority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@ApplyErrorPage
@RequiredArgsConstructor
@Controller("adminBoardController")
@RequestMapping("/admin/board")
public class BoardController implements SubMenus {

    private final Utils utils; // 템플릿은 아니지만 제목이라던지 필요할때가 있을 거 같아서 추가함
    private final BoardValidator boardValidator;
    private final BoardConfigUpdateService configUpdateService;
    private final BoardConfigInfoService configInfoService;

    @Override
    @ModelAttribute("menuCode")
    public String menuCode() {
        return "board";
    }

    /**
     * 게시판 목록
     *
     * @param model
     * @return
     */
    @GetMapping({"", "/list"})
    public String list(@ModelAttribute BoardConfigSearch search, Model model) {
        commonProcess("list", model);

        ListData<Board> data = configInfoService.getList(search);

        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());

        return "admin/board/list";
    }

    /**
     * 게시판 설정 등록
     * // 게시판설정 하나가 게시판하나!
     * @param model
     * @return
     */
    @GetMapping("/add")
    public String add(@ModelAttribute RequestBoard form, Model model) {
        commonProcess("add", model);

        form.setSkin("default");
        form.setListAuthority(Authority.ALL);
        form.setViewAuthority(Authority.ALL);
        form.setWriteAuthority(Authority.ALL);
        form.setCommentAuthority(Authority.ALL);

        return "admin/board/add";
    }

    /**
     * 게시판 설정 수정
     * @param bid
     * @param model
     * @return
     */
    @GetMapping("/edit/{bid}")
    public String edit(@PathVariable("bid") String bid, Model model) {
        commonProcess("edit", model);

        RequestBoard form = configInfoService.getForm(bid); // bid = 보드게시판 id
        model.addAttribute("requestBoard", form);

        return "admin/board/edit";
    }

    /**
     * 게시판 등록, 수정 처리
     *
     * @return
     */
    @PostMapping("/save")
    public String save(@Valid RequestBoard form, Errors errors, Model model) {
        String mode = form.getMode(); // 이게 없을때는 기본값
        mode = StringUtils.hasText(mode) ? mode : "add";
        commonProcess(mode, model);

        boardValidator.validate(form, errors);

        if (errors.hasErrors()) {
            return "admin/board/" + mode; // 여기 모드에 따라서 등록이 될지 , 수정이 될지 결정 // 쇼핑몰과 비슷
        }

        configUpdateService.process(form);

        return "redirect:/admin/board/list";
    }

    /**
     * 게시글 관리 // 사용자가 작성한.
     *
     * @param model
     * @return
     */
    @GetMapping("/posts")
    public String posts(Model model) {
        commonProcess("posts", model);

        return "admin/board/posts";
    }

    /**
     * 공통 처리 부분
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "list";

        List<String> addCommonScript = new ArrayList<>();


        String pageTitle = "";
        if (mode.equals("list")) {
            pageTitle = "게시판 목록";
        } else if (mode.equals("add") || mode.equals("edit")) {
            pageTitle = mode.equals("edit") ? "게시판 수정" : "게시판 등록";
            addCommonScript.add("fileManager");

        } else if (mode.equals("posts")) {
            pageTitle = "게시글 관리";
        }

        pageTitle += " - 게시판 관리";

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("subMenuCode", mode);
    }
}