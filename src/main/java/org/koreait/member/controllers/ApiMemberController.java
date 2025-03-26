package org.koreait.member.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.global.libs.Utils;
import org.koreait.global.rests.JSONData;
import org.koreait.mypage.services.FollowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class ApiMemberController {

    private final FollowService service;

    private final Utils utils;

    @GetMapping("/follow/{seq}")
    public JSONData follow(@PathVariable("seq") Long seq) {

        service.follow(seq);

        utils.showSessionMessage("Follow 완료");

        return new JSONData();
    }

    @GetMapping("/unfollow/{seq}")
    public JSONData unfollow(@PathVariable("seq") Long seq) {

        service.unfollow(seq);

        utils.showSessionMessage("Unfollow 완료");

        return new JSONData();
    }
}