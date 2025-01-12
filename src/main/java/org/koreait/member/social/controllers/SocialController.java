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
@ApplyErrorPage // 뒤로 빽해야하니까 넣어줬음
@RequestMapping("/member/social")
@RequiredArgsConstructor // 의존성 주입
public class SocialController {

//    private final RestTemplate restTemplate;

    private final KakaoLoginService kakaoLoginService;
    private final HttpSession session;
    private final Utils utils;


    @GetMapping("/callback/kakao")
    public String callback(@RequestParam(name="code", required = false) String code, @RequestParam(name="state", required = false) String redirectUrl) {
        // void에서 String으로 변경했는데 토큰인증시 넘어갈테고 반환값이될테니 String으로 변경?


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
    }
}

     /*   HttpHeaders headers = new HttpHeaders(); // 헤더스는 스프링껄로 넣어줘야함
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 이건 테스트 코드로 나오는지 확인한거
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "f0ac580a42d382cfa7a1b66b82576a0a"); // id는 관리자쪽에 기록될 필요가 있음
        params.add("redirect_uri", "http://localhost:3000/member/social/callback");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers); // MultiValueMap 사용 / 값나오는지 확인용

        ResponseEntity<AuthToken> response = restTemplate.postForEntity(URI.create("https://kauth.kakao.com/oauth/token"), request, AuthToken.class); // 바디데이터에 넣음

        AuthToken token = response.getBody();
//        System.out.println(token);

        String accessToken = token.getAccessToken();
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(accessToken);

        HttpEntity<Void> request2 = new HttpEntity<>(headers2); // 헤더만 실을거라 반환값이 필요없어서 보이드사용

        ResponseEntity<String> response2 = restTemplate.exchange(URI.create("https://kapi.kakao.com/v2/user/me"), HttpMethod.GET, request2, String.class); // 겟방식으로 요청헤더에 토큰을 실어서 보내고 있는거
        System.out.println(response2); // 필수인 id밖에 안나옴*/


//    f0ac580a42d382cfa7a1b66b82576a0a

//    https://kauth.kakao.com/oauth/authorize?client_id=f0ac580a42d382cfa7a1b66b82576a0a&redirect_uri=http://localhost:3000/member/social/callback&response_type=code

// https://kauth.kakao.com/oauth/authorize?client_id=f0ac580a42d382cfa7a1b66b82576a0a&redirect_uri=http://localhost:3000/member/social/callback/kakao&response_type=code


