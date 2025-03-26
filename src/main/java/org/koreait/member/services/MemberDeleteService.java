//package org.koreait.member.services;
//
//import lombok.RequiredArgsConstructor;
//import org.koreait.member.MemberInfo;
//import org.koreait.member.entities.Member;
//import org.koreait.member.repositories.MemberRepository;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Service;
//
///**
// * Member 삭제 기능(Service)
// *
// */
//@Lazy
//@Service
//@RequiredArgsConstructor
//public class MemberDeleteService {
//
//    private final MemberRepository memberRepository;
//
//    private final MemberInfoService infoService;
//
//    public Member delete(Long seq) {
//
//        MemberInfo data = (MemberInfo) infoService.loadUserBySeq(seq);
//
//        Member member = data.getMember();
//
//        /*
//        UserDetails = interface
//        UserInfo = UserDetails 구현한 구현체
//         */
//
//        memberRepository.delete(member);
//
//        memberRepository.flush();
//
//        // memberRepository.deleteById(seq);
//
//        return member;
//    }
//}