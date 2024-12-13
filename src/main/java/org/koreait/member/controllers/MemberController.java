package org.koreait.member.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.exceptions.BadRequestException;
import org.koreait.global.libs.Utils;
import org.koreait.member.MemberInfo;
import org.koreait.member.libs.MemberUtil;
import org.koreait.member.services.MemberInfoService;
import org.koreait.member.services.MemberUpdateService;
import org.koreait.member.validators.JoinValidator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@ApplyErrorPage // 예외발생시 던져주는 거
@RequestMapping("/member")
@RequiredArgsConstructor
@SessionAttributes({"requestAgree", "requestLogin"})
public class MemberController {

    private final Utils utils;
    private final MemberUtil memberUtil;
    private final JoinValidator joinValidator; // 회원 가입 검증
    private final MemberUpdateService updateService; // 회원 가입 처리
    private final MemberInfoService infoService; // 회원 정보 조회

    @ModelAttribute("requestAgree")
    public RequestAgree requestAgree() {
        return new RequestAgree();
    }

    @ModelAttribute("requestLogin")
    public RequestLogin requestLogin() {
        return new RequestLogin();
    }

    /* 회원 페이지 CSS */
    @ModelAttribute("addCss")
    public List<String> addCss() {
        return List.of("member/style");
    }

    @GetMapping("/login")
    public String login(@ModelAttribute RequestLogin form, Errors errors, Model model) {
        commonProcess("login", model); // 로그인 페이지 공통 처리

        if (form.getErrorCodes() != null) { // 검증 실패 // 쪼개논거
            form.getErrorCodes().stream().map(s -> s.split("_"))
                    .forEach(s -> {
                        if (s.length > 1) {
                            errors.rejectValue(s[1], s[0]);
                        } else {
                            errors.reject(s[0]);
                        }
                    });
        }

        return utils.tpl("member/login"); // tpl은 모바일과 pc를 구분하기 위해서
        // 유틸쪽에 만들어져있음
    }

    @ResponseBody
    @GetMapping("/test")
    public void test() {
        /*
        MemberInfo memberInfo = (MemberInfo)SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        System.out.println(memberInfo);

         */
        System.out.println(SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()); // 미로그인 상태 anonymousUser 문자열
    }
    /*
    public void test(@AuthenticationPrincipal MemberInfo memberInfo) {
        System.out.println(memberInfo);
    } */
    /*
    public void test(Principal principal) {
        String email = principal.getName();
        System.out.println(email);
    } */


    // 세션에 데이터를 넣어서 확장가능하게 해준거 // 페이지를 각각 나눠놨음
    // "member/agree" -> RequestAgree agree -> @SessionAttribute("requestAgree") 방식으로
    /**
     * 회원가입 약관 동의
     *
     * @return
     */
    @GetMapping("/agree")
    public String joinAgree(Model model) {
        commonProcess("agree", model);

        return utils.tpl("member/agree");
    }

    /**
     * 회원 가입 양식 페이지
     * - 필수 약관 동의 여부 검증
     *
     * @return
     */
    @PostMapping("/join")
    public String join(RequestAgree agree, Errors errors, @ModelAttribute RequestJoin form, Model model) {
        commonProcess("join", model); // 회원 가입 공통 처리

        joinValidator.validate(agree, errors);


        if (errors.hasErrors()) { // 약관 동의를 하지 않았다면 약관 동의 화면을 출력
            return utils.tpl("member/agree");
        }

        return utils.tpl("member/join");
    }

    /***
     * 회원가입 처리
     *
     * @return
     */
    @PostMapping("/join_ps")
    public String joinPs(@SessionAttribute("requestAgree") RequestAgree agree, @Valid RequestJoin form, Errors errors, SessionStatus status, Model model) {
        commonProcess("join", model); // 회원가입 공통 처리

        joinValidator.validate(agree, errors); // 약관 동의 여부 체크
        joinValidator.validate(form, errors); // 회원 가입 양식 검증

        if (errors.hasErrors()) {
            return utils.tpl("member/join"); // 검증안됐으면 다시 회원가입쪽으로 가서 양식을 보여준다던지 해야함 // join.html 10J 참고!
            // 글로벌 에러는 62J 참고

        }

        // 회원 가입 처리
        form.setRequiredTerms1(agree.isRequiredTerms1());
        form.setRequiredTerms2(agree.isRequiredTerms2());
        form.setRequiredTerms3(agree.isRequiredTerms3());
        form.setOptionalTerms(agree.getOptionalTerms());

        updateService.process(form);
        // 회원가입도 가입이지만 수정도 가능하게 하기 위해서 만듦
        // 서비스 - 멤버업뎃서비스 / 2가지 가능하게 만듦

        status.setComplete();

        // 회원가입 처리 완료 후 - 로그인 페이지로 이동
        return "redirect:/member/login";
    }

    @ResponseBody
    @GetMapping("/refresh")
    @PreAuthorize("isAuthenticated()") // 회원만 접근가능함
    public void refresh(Principal principal){
        MemberInfo memberInfo = (MemberInfo) infoService.loadUserByUsername(principal.getName());
        memberUtil.setMember(memberInfo.getMember());
    }

    /**
     * 공통 처리 부분
     *
     * @param mode // 모드는 로그인, 조인, 어그리를 얘기하는 거임
     * @param model
     */                        // agree      // agree에서 주는 Model
    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "login";

        String pageTitle = null; // 페이지 제목
        List<String> addCommonScript = new ArrayList<>(); // 공통 자바스크립트
        List<String> addScript = new ArrayList<>(); // front쪽에 추가하는 자바스크립트 // 공통으로 처리할 것들도 있지만 여기서만 처리할 것도 있는데 그런거 프론트쪽에만 추가할려고 만든 스크립트 // 위치가 다름

        if (mode.equals("login")) {  // 로그인 공통 처리
            pageTitle = utils.getMessage("로그인");

        } else if (mode.equals("join")) { // 회원가입 공통 처리
            pageTitle = utils.getMessage("회원가입");
            addCommonScript.add("address");
            addScript.add("member/join");

        } else if (mode.equals("agree")) {
            pageTitle = utils.getMessage("약관동의");
            // 약관 동의 페이지에 최초 접근시 약관 선택을 초기화
            model.addAttribute("requestAgree", requestAgree()); // "requestAgree" 키값, requestAgree() 밸류

        }


        // 페이지 제목
        model.addAttribute("pageTitle", pageTitle);

        // 공통 스크립트
        model.addAttribute("addCommonScript", addCommonScript);

        // front 스크립트
        model.addAttribute("addScript", addScript);
    }

}