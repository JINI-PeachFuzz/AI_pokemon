package org.koreait.member.social.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.member.social.entities.AuthToken;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;


@RestController
@RequestMapping("/member/social")
@RequiredArgsConstructor // 의존성 주입
public class SocialController {

    private final RestTemplate restTemplate;

    @GetMapping("/callback")
    public void callback(@RequestParam(name="code", required = false) String code) {
        HttpHeaders headers = new HttpHeaders(); // 헤더스는 스프링껄로 넣어줘야함
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

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
        System.out.println(response2); // 필수인 id밖에 안나옴
    }


//    f0ac580a42d382cfa7a1b66b82576a0a

//    https://kauth.kakao.com/oauth/authorize?client_id=f0ac580a42d382cfa7a1b66b82576a0a&redirect_uri=http://localhost:3000/member/social/callback&response_type=code
}
