package org.koreait.member.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.admin.member.controllers.MemberSearch;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.services.FileInfoService;
import org.koreait.global.paging.ListData;
import org.koreait.global.paging.Pagination;
import org.koreait.member.MemberInfo;
import org.koreait.member.constants.Authority;
import org.koreait.member.entities.Authorities;
import org.koreait.member.entities.Member;
import org.koreait.member.entities.QMember;
import org.koreait.member.repositories.MemberRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class MemberInfoService implements UserDetailsService {
    // 스프링 시큐리티임!

    private final MemberRepository memberRepository;
    private final FileInfoService fileInfoService;
    private final JPAQueryFactory queryFactory;
    private final HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // loadUserByUsername 회원조회관련
        Member member = memberRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));


        List<Authorities> items = member.getAuthorities();
        if (items == null) {
            Authorities auth = new Authorities();
            auth.setMember(member);
            auth.setAuthority(Authority.USER);
            items = List.of(auth);
        }


        List<SimpleGrantedAuthority> authorities = items.stream().map(a -> new SimpleGrantedAuthority(a.getAuthority().name())).toList();

        // 추가 정보 처리
        addInfo(member); // 조회방식은 추가 처리를 add 이런식으로 해줌

        return MemberInfo.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .member(member)
                .authorities(authorities)
                .build();
    }
    // MemberInfo (유저디테일)구현체임 // 인증하는데 사용됨.

    /***
     * 회원 목록
     *
     * @param search
     * @return
     */
    public ListData<Member> getList(MemberSearch search) {
       // 페이지번호없을 땐 1번 고정
        int page = Math.max(search.getPage(), 1); // 1보다 작은경우엔 search.getPage() / 기본설정을 한 거
        int limit = search.getLimit();
        limit = limit < 1 ? 20 : limit;
        int offset = (page - 1) * limit; // 시작위치에 대한 오프셋 / 권한에 대한 부분도 바로 가져올려고 그런거

        QMember member = QMember.member;

        /* 검색 처리 S */
        BooleanBuilder andBuilder = new BooleanBuilder();
        // 키워드 검색 S
        String sopt = search.getSopt(); // 검색 옵션
        String skey = search.getSkey(); // 검색 키워드
        sopt = StringUtils.hasText(sopt) ? sopt : "ALL";
        /***
         * sopt - ALL : 통합 검색 - 이메일 + 회원명 + 닉네임 +
         *       NAME : 회원명 + 닉네임
         *      EMAIL : 이메일
         */
        if (StringUtils.hasText(skey)) {
            skey = skey.trim();
            StringExpression condition = null;
            if (sopt.equals("EMAIL")) {
                condition = member.email;
            } else if (sopt.equals("NAME")) {
                condition = member.name.concat(member.nickName);
            } else { // 통합 검색
                condition = member.email.concat(member.name).concat(member.nickName);
            }

            andBuilder.and(condition.contains(skey));
        }
        // 키워드 검색 E

        // 이메일 검색
        List<String> emails = search.getEmail();
        if (emails != null && !emails.isEmpty()) {
            andBuilder.and(member.email.in(emails));
        }

        // 권한 검색 S
        List<Authority> authorities = search.getAuthority();
        if (authorities != null && !authorities.isEmpty()) {

            //List<Authorities> _authorities = authorities.stream()
            //                .map(a -> )
            //

            //andBuilder.and(member.authorities.)
        }
        // 권한 검색 E

        // 날짜 검색 S
        String dateType = search.getDateType();
        dateType = StringUtils.hasText(dateType) ? dateType : "createdAt"; // 가입일 기준
        LocalDate sDate = search.getSDate();
        LocalDate eDate = search.getEDate();

        DateTimePath<LocalDateTime> condition;
        if (dateType.equals("deletedAt")) condition = member.deletedAt; // 탈퇴일 기준
        else if (dateType.equals("credentialChangedAt")) condition = member.credentialChangedAt; // 비밀번호 변경일 기준
        else condition = member.createdAt; // 가입일 기준

        if (sDate != null) {
            andBuilder.and(condition.after(sDate.atStartOfDay()));

        }
        if (eDate != null) {
            andBuilder.and(condition.before(eDate.atTime(LocalTime.of(23,59,59))));
        }

        // 날짜 검색 E

        /* 검색 처리 E */

        List<Member> items = queryFactory.selectFrom(member) //select 가 아니고 slelctFrom 이어야 함
                .leftJoin(member.authorities)
                .fetchJoin() // 얘는 지연로딩이기 때문에 fetch를 넣어야지만 처음부터 가져오게 됨
                .where(andBuilder)
                .orderBy(member.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();

        long total = memberRepository.count(andBuilder); // 총 회원 수 / 전체 회원 명수 구할 수 있음
//        total = 100000L; // 임시로 페이지 많이 나오게 한거 / 스타일 완료후 엔 지워줄 거임 -> 지웠음
        Pagination pagination = new Pagination(page, (int)total, 10, limit, request);

        return new ListData<>(items, pagination);
    }

    /***
     * 추가 정보 처리
     * @param member
     */
    public void addInfo(Member member) {
        List<FileInfo> files = fileInfoService.getList(member.getEmail(), "profile");
        if (files != null && !files.isEmpty()) {
            member.setProfileImage(files.get(0));
        }

    }



}