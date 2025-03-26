package org.koreait.member.services.test.annotations;

import org.koreait.member.MemberInfo;
import org.koreait.member.constants.Gender;
import org.koreait.member.entities.Authorities;
import org.koreait.member.entities.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class MockSecurityContextFactory implements WithSecurityContextFactory<MockMember> {

    @Override
    public SecurityContext createSecurityContext(MockMember annotation) {

        Member member = new Member();

        member.setSeq(annotation.seq());
        member.setEmail(annotation.email());
        member.setPassword(annotation.password());
        member.setName(annotation.name());
        member.setNickName(annotation.nickName());

        member.setBirthDt(LocalDate.now().minusYears(20L));
        member.setRequiredTerms1(true);
        member.setRequiredTerms2(true);
        member.setRequiredTerms3(true);
        member.setGender(Gender.FEMALE);

        // MockMember Annotation 에서 설정
        member.setCredentialChangedAt(LocalDateTime.now());

        // Annotation 의 권한을 가져와서 map 으로 변경후 List 로 변환 작업
        List<SimpleGrantedAuthority> authorities = Arrays.stream(annotation.authority())
                .map(a -> new SimpleGrantedAuthority(a.name())).toList();

        // Member 쪽에 설정할 값 (관계 Mapping 으로 Authorities 와 이어져 있어서 Test 시 필요할 수도 있음)
        List<Authorities> _authorities = Arrays.stream(annotation.authority())
                .map(a -> {

                    Authorities auth = new Authorities();
                    auth.setAuthority(a);
                    auth.setMember(member);

                    return auth;

                }).toList();

        member.setAuthorities(_authorities);

        // UserDetails 객체 생성
        MemberInfo memberInfo = MemberInfo
                .builder()
                .email(annotation.email())
                .password(annotation.password())
                .member(member)
                .authorities(authorities)
                .build();

        // Authentication interface 구현 객체
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(memberInfo, annotation.password(), authorities);

        // 새로운 Context 객체(인증한 사용자에 대한 정보 객체)
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // 로그인 처리
        // MemberUtils Class 의 getMember 메서드 내부 참고
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        context.setAuthentication(token);

        return context;
    }
}