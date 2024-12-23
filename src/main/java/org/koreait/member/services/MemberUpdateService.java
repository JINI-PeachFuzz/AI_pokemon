package org.koreait.member.services;

import lombok.RequiredArgsConstructor;
import org.koreait.member.constants.Authority;
import org.koreait.member.controllers.RequestJoin;
import org.koreait.member.entities.Authorities;
import org.koreait.member.entities.Member;
import org.koreait.member.entities.QAuthorities;
import org.koreait.member.libs.MemberUtil;
import org.koreait.member.repositories.AuthoritiesRepository;
import org.koreait.member.repositories.MemberRepository;
import org.koreait.mypage.controllers.RequestProfile;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Lazy // 지연로딩 - 최초로 빈을 사용할때 생성
@Service
@RequiredArgsConstructor
@Transactional
public class MemberUpdateService {

    private final MemberRepository memberRepository;
    private final AuthoritiesRepository authoritiesRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final MemberUtil memberUtil;
    private final MemberInfoService infoService;

    /**
     * 커맨드 객체의 타입에 따라서 RequestJoin이면 회원 가입 처리
     *                      RequestProfile이면 회원정보 수정 처리
     * @param form
     */
    public void process(RequestJoin form) {
        // 커맨드 객체 -> 엔티티 객체 데이터 옮기기
        // modelMapper 를 사용하면서 셋셋셋하는거 그걸 안써도 되게 해줌 근데 조건이 자료형이랑 그런것들이 일치해야함

        Member member = modelMapper.map(form, Member.class);

        // 선택 약관 -> 약관 항목1||약관 항목2||...
        List<String> optionalTerms = form.getOptionalTerms();
        if (optionalTerms != null) {
            member.setOptionalTerms(String.join("||", optionalTerms));
        }

        // 비밀번호 해시화 - BCrypt
        String hash = passwordEncoder.encode(form.getPassword());
        member.setPassword(hash);
        member.setCredentialChangedAt(LocalDateTime.now());

        // 회원 권한
        Authorities auth = new Authorities();
        auth.setMember(member);
        auth.setAuthority(Authority.USER);  // 회원 권한이 없는 경우 - 회원 가입시, 기본 권한 USER

        save(member, List.of(auth)); // 회원 저장 처리
    }

    /***
     * 회원정보 수정
     * @param form
     */
    public void process(RequestProfile form) {
        process(form, null);
    }
    // 메서드 오버로드 사용함 / 열린구조

    public void process(RequestProfile form, List<Authority> authorities) { // 관리자일때만 추가할 거
        Member member = memberUtil.getMember(); // 로그인한 사용자의 정보
        member.setName(form.getName());
        member.setNickName(form.getNickName());
        member.setBirthDt(form.getBirthDt());
        member.setGender(form.getGender());
        member.setZipCode(form.getZipCode());
        member.setAddress(form.getAddress());
        member.setAddressSub(form.getAddressSub());

        List<String> optionalTerms = form.getOptionalTerms();
        if (optionalTerms != null) {
            member.setOptionalTerms(String.join("||", optionalTerms));
        }

        // 회원정보 수정일때는 비밀번호가 입력 된 경우만 저장
        String password = form.getPassword();
        if (StringUtils.hasText(password)) { // 비번은 변경하지 않음
            String hash = passwordEncoder.encode(password);
            member.setPassword(hash);
            member.setCredentialChangedAt(LocalDateTime.now()); // 30일후 비번변경 디데이 다시 시작하게 할려고
        }

        // 회원 권한은 관리자만 수정 가능!
        List<Authorities> _authorities = null;
        if (authorities != null && memberUtil.isAdmin()) { // 관리자일때만 권한을 변경할 수있게 검증하는 곳
            _authorities = authorities.stream().map(a -> {
                Authorities auth = new Authorities();
                auth.setAuthority(a);
                auth.setMember(member);
                return auth;
            }).toList();
        }

        save(member, _authorities);

        // 로그인 회원 정보 업데이트
        Member _member = memberRepository.findByEmail(member.getEmail()).orElse(null);
        if (_member != null) {
            infoService.addInfo(_member); // 싱글톤 패턴이라 이것만 넣어도 됨
            memberUtil.setMember(_member); // 2차 가공해야함
        }
    }


    /**
     *
     * 회원정보 추가 또는 수정 처리
     *
     */
    private void save(Member member, List<Authorities> authorities) {

        memberRepository.saveAndFlush(member);

        // 회원 권한 업데이트 처리 S

        if (authorities != null) {
            /**
             * 기존 권한을 삭제하고 다시 등록
             */

            QAuthorities qAuthorities = QAuthorities.authorities;
            List<Authorities> items = (List<Authorities>) authoritiesRepository.findAll(qAuthorities.member.eq(member));
            if (items != null) { // 권한은 기존껄 삭제하고 추가하는게 더 편함
                authoritiesRepository.deleteAll(items);
                authoritiesRepository.flush();
            }


            authoritiesRepository.saveAllAndFlush(authorities);
        }

        // 회원 권한 업데이트 처리 E


    }

}