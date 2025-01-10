package org.koreait.member.controllers;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.koreait.member.constants.Gender;
import org.koreait.member.social.constants.SocialChannel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Data
public class RequestJoin extends RequestAgree{

    @Email
    @NotBlank
    private String email; // 이메일

    @NotBlank
    private String name; // 회원명

    @Size(min = 8)
    private String password; // 비밀번호

    private String confirmPassword; // 비밀번호 확인 // 소셜로그인을 하기 때문에 낫블랭크를 제거했음

    @NotBlank
    private String nickName; // 닉네임

    @NotNull
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate birthDt;  // 생년월일

    @NotNull
    private Gender gender; // 성별

    @NotBlank
    private String zipCode; // 우편번호

    @NotBlank
    private String address; // 주소
    private String addressSub; // 나머지 주소

    private SocialChannel socialChannel; // 소셜로그인시에는 이걸 히든값으로 넣어서 만들어줄거임
    private String socialToken;

    // 소셜 로그인으로 가입하는 건지 체크
    public boolean isSocial() {
        return socialChannel != null && StringUtils.hasText(socialToken);

    }
}