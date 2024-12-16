package org.koreait.global.configs;

import org.koreait.member.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 스프링 시큐리티 설정
 *
 */
@Configuration
@EnableMethodSecurity // 특정 메서드만 통제할때도 사용가능
public class SecurityConfig {

    @Autowired
    private MemberInfoService memberInfoService;

    @Bean // 이 빈이 중요함 / 로그인설정에 대한 설정들
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
// filterChain메서드가 설정인거임
        /* 인증 설정 S - 로그인, 로그아웃 */
        http.formLogin(c -> {
            c.loginPage("/member/login") // 로그인 양식을 처리할 주소
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .failureHandler(new LoginFailureHandler())
                    .successHandler(new LoginSuccessHandler());
        }); // 상세하게 하기 위해서 핸들러를 나눔 / 멤버-서비스

        http.logout(c -> {
            c.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                    .logoutSuccessUrl("/member/login");
        });
        /* 인증 설정 E */

        /* 인가 설정 S - 페이지 접근 통제 */
        /**
         * authenticated() : 인증받은 사용자만 접근
         * anonymous() : 인증 받지 않은 사용자만 접근
         * permitAll() : 모든 사용자가 접근 가능
         * hasAuthority("권한 명칭") : 하나의 권한을 체크
         * hasAnyAuthority("권한1", "권한2", ...) : 나열된 권한 중 하나라도 충족하면 접근 가능
         * ROLE
         * ROLE_명칭
         * hasRole("명칭")
         * hasAnyRole(...)
         */
        http.authorizeHttpRequests(c -> { // 람다형태로 한건 영역별로 설정할 수 있게 하기 위해서임
            c.requestMatchers("/mypage/**").authenticated() // 인증한 회원
                    .requestMatchers("/member/login", "/member/join", "/member/agree").anonymous() // 미인증 회원
                    .requestMatchers("/admin/**").hasAnyAuthority("MANAGER", "ADMIN") // 관리자 페이지는 MANAGER, ADMIN 권한
                    .anyRequest().permitAll(); // 나머지 페이지는 모두 접근 가능
        });

        http.exceptionHandling(c -> {
            c.authenticationEntryPoint(new MemberAuthenticationExceptionHandler())  // 미로그인시 인가 실패
                    .accessDeniedHandler(new MemberAccessDeniedHandler()); // 로그인 이후 인가 실패

        });

        /* 인가 설정 E */

        /* 자동 로그인 설정 S */
        http.rememberMe(c-> {
            c.rememberMeParameter("autoLogin") // html에 작성한걸 시큐리티는 모르기때문에 rememberMeParameter을 넣어줬음
                    .tokenValiditySeconds(60 * 60 * 24 * 30) // 자동 로그인을 유지할 시간, 기본값 14일 / 30일로 수정했음 60은 1분
                    .userDetailsService(memberInfoService) // 조회
                    .authenticationSuccessHandler(new LoginSuccessHandler()); // 성공시 콜백!
        });
        /* 자동 로그인 설정 E */

        return http.build(); // 설정 무력화
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // 시큐리티제공, 비밀번호를 안전하게 암호화(해싱)하고 이후 사용자가 입력한 비밀번호와 암호화된 비밀번호를 비교할 수 있는 기능을 제공함
}