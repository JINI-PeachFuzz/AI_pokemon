package org.koreait.member.social.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.exceptions.scripts.AlertBackException;
import org.koreait.global.exceptions.scripts.AlertRedirectException;
import org.koreait.global.libs.Utils;
import org.koreait.member.social.constants.SocialChannel;
import org.koreait.member.social.services.KakaoLoginService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@ApplyErrorPage
@RequestMapping("/member/social")
@RequiredArgsConstructor
public class SocialController {

    private final KakaoLoginService kakaoLoginService;
    private final HttpSession session;
    private final Utils utils;

    @GetMapping("/callback/kakao")
    public String callback(@RequestParam(name="code", required = false) String code, @RequestParam(name="state", required = false) String redirectUrl) {

        // 연결 해제 요청 처리 S
        if (StringUtils.hasText(redirectUrl) && redirectUrl.equals("disconnect")) {
            kakaoLoginService.disconnect();

            return "redirect:/mypage/profile";
        }
        // 연결 해제 요청 처리 E

        String token = kakaoLoginService.getToken(code);
        if (!StringUtils.hasText(token)) {
            throw new AlertBackException(utils.getMessage("UnAuthorized"), HttpStatus.UNAUTHORIZED);
        }

        // 카카오 로그인 연결 요청 처리 S
        if (StringUtils.hasText(redirectUrl) && redirectUrl.equals("connect")) {
            if (kakaoLoginService.exists(token)) {
                throw new AlertRedirectException(utils.getMessage("Duplicated.kakaoLogin"), "/mypage/profile", HttpStatus.BAD_REQUEST);
            }

            kakaoLoginService.connect(token);

            return "redirect:/mypage/profile";
        }
        // 카카오 로그인 연결 요청 처리 E

        boolean result = kakaoLoginService.login(token);
        if (result) { // 로그인 성공
            redirectUrl = StringUtils.hasText(redirectUrl) ? redirectUrl : "/";
            return "redirect:" + redirectUrl;
        }

        // 소셜 회원 미가입 -> 회원가입 페이지 이동
        session.setAttribute("socialChannel", SocialChannel.KAKAO);
        session.setAttribute("socialToken", token);

        return "redirect:/member/agree";


               /*
        // Test 코드

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 데이터 실기
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("grant_type", "authorization_code");
        params.add("client_id", "1a5bea2e59af27746da39f680c35ce58");
        params.add("redirect_uri", "http://localhost:3000/member/social/callback");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<AuthToken> response = restTemplate.postForEntity(URI.create("https://kauth.kakao.com/oauth/token"), request, AuthToken.class);

        AuthToken token = response.getBody();

        // System.out.println(token);

        String accessToken = token.getAccessToken();

        HttpHeaders headers2 = new HttpHeaders();

        headers2.setBearerAuth(accessToken);

        HttpEntity<Void> request2 = new HttpEntity<>(headers2);

        ResponseEntity<String> response2 = restTemplate.exchange(URI.create("https://kapi.kakao.com/v2/user/me"), HttpMethod.GET, request2, String.class);

        System.out.println(response2);
         */
    }
}