package org.koreait.member.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestLogin {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
    private String redirectUrl; // 로그인 완료 후 이동할 주소 / 있을때만 처리할 거라서 이거는 NotBlank안붙임

}
