package org.koreait.member.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RequestLogin implements Serializable {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
    private String redirectUrl; // 로그인 완료 후 이동할 주소 / 있을때만 처리할 거라서 이거는 NotBlank안붙임


    private List<String> errorCodes;

    private String kakaoLoginUrl; // 에러코드를 템플릿쪽에 넣어주는 방식으로 구현했음
}