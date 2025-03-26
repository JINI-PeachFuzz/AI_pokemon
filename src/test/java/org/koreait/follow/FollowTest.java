package org.koreait.follow;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.koreait.global.paging.CommonSearch;
import org.koreait.global.paging.ListData;
import org.koreait.member.entities.Member;
import org.koreait.member.repositories.MemberRepository;
import org.koreait.member.services.test.annotations.MockMember;
import org.koreait.mypage.repositories.FollowRepository;
import org.koreait.mypage.services.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
// @ActiveProfiles({"default", "test"})
public class FollowTest {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private FollowService followService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpSession session;

    private Member member1;
    private Member member2;
    private Member member3;

    private CommonSearch paging;

    @BeforeEach
    void init() {

//        List<Member> members = new ArrayList<>();
//        for (int i = 1; i <= 3; i++) {
//            Member member = new Member();
//            member.setEmail("user" + i + "@test.org");
//            member.setPassword("_aA123456");
//            member.setNickName("user" + i);
//            member.setName("사용자" + i);
//            member.setAddress("인천시 계양구 계양문화로" + i + "번길");
//            member.setZipCode("12345" + i);
//            member.setBirthDt(LocalDate.now().minusMonths(3));
//            member.setGender(Gender.FEMALE);
//            members.add(member);
//        }

//        memberRepository.saveAllAndFlush(members);

        List<Member> members = new ArrayList<>();

        for (long i = 1; i <= 3; i++) {

            Member member = new Member();

            member = memberRepository.findById(i).orElse(null);

            members.add(member);
        }

        memberRepository.saveAllAndFlush(members);

        member1 = members.get(0);
        member2 = members.get(1);
        member3 = members.get(2);

        session.setAttribute("member", member1);

        followService.follow(member2);
        followService.follow(member3);

        paging = new CommonSearch();
    }

    /**
     * member1은 member2, member3을 following 한다
     * member1에서 getFollowers 에서 member2와 member3이 나와야 하고
     * getTotalFollowers 에서는 2가 나와야 한다
     *
     */
    @Test
    @MockMember
    void test1() {

        List<Member> members = followRepository.getFollowings(member1);

        System.out.println("멤버들" + members);

//        assertTrue(members.stream().map(Member::getNickName).anyMatch(u -> u.equals(member2.getNickName()) || u.equals(member3.getNickName())));
//        assertEquals(members.size(), followRepository.getTotalFollowings(member1));
    }

    /**
     * member2, member3는 각각 member1이라는 follower 를 가지고 있어야 하고
     * getTotalFollowers()는 1명이 되어야 함
     *
     */
    @Test
    @MockMember
    void test2() {

        ListData<Member> members1 = followRepository.getFollowers(member2, paging, request);
        ListData<Member> members2 = followRepository.getFollowers(member3, paging, request);

        assertEquals(member1.getNickName(), members1.getItems().get(0).getNickName());
        assertEquals(member1.getNickName(), members2.getItems().get(0).getNickName());
        assertEquals(1, followRepository.getTotalFollowers(member2));
        assertEquals(1, followRepository.getTotalFollowers(member3));
    }

    /**
     * 로그인 회원을 follow 한 회원 목록 - followers
     */
    @Test
    @MockMember
    void test3() {
        ListData<Member> members = followService.getFollowers(paging);

        System.out.println(members);

        assertEquals(0, members.getItems().size());
    }

    /**
     * 로그인 회원이 follow 한 회원 목록 - followings
     */
    @Test
    @MockMember
    void test4() {
        ListData<Member> members = followService.getFollowings(paging);
        assertEquals(2, members.getItems().size());
    }
}