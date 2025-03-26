package org.koreait.email.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.email.exceptions.AuthCodeIssueException;
import org.koreait.email.services.EmailAuthService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Profile("email")
@RequestMapping("/api/email")
@RestController
@RequiredArgsConstructor
public class ApiEmailController {

    private final EmailAuthService authService;

    /**
     * 인증 코드 발급
     *
     * @param to
     */
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @GetMapping("/auth/{to}")
    public void authCode(@PathVariable("to") String to) {

        // 인증 코드 발급 실패할 경우
        if (!authService.sendCode(to)) {

            throw new AuthCodeIssueException();
        }
    }


    /**
     * 발급 받은 인증 코드 검증
     *
     * @param authCode
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping("/verify")
    public void verify(@RequestParam(name = "authCode", required = false) Integer authCode) {

        authService.verify(authCode);
    }
}