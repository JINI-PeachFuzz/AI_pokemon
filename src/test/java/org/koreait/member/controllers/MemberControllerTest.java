package org.koreait.member.controllers;

import org.junit.jupiter.api.Test;
import org.koreait.member.entities.Member;
import org.koreait.member.repositories.MemberRepository;
import org.koreait.member.services.MemberDeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberControllerTest {

    @Autowired
    private MemberRepository repository;

    @Autowired
    private MemberDeleteService deleteService;

    @Test
    void test1() {

        // 멤버 단일 삭제
        Member member = deleteService.delete(1L);

        System.out.println(member);
    }

}