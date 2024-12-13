package org.koreait.member.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.koreait.member.controllers.RequestLogin;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginFailureHandler implements AuthenticationFailureHandler { // AuthenticationFailureHandler 설정핸들러
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException { // 검증에 대한 부분이 여기에 들어가있음

        // 이걸 추가한 이유? 커맨드객체 검증을 최대한 활용하기 위해서!
        HttpSession session = request.getSession();
        RequestLogin form = Objects.requireNonNullElse((RequestLogin)session.getAttribute("requestLogin"), new RequestLogin());
        form.setErrorCodes(null); // 세션범위로 했기 때문에 나머지는 남아있어도 에러코드는 새로 검증을 해야하니까 null값을 넣었음

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        form.setEmail(email);
        form.setPassword(password);

        String redirectUrl = request.getContextPath() + "/member/login";
        // 오류가 발생하면 로그인페이지로 넘어가야하니까
        // 24~35 줄은 준비단계임

        // 아이디 또는 비밀번호를 입력하지 않은 경우, 아이디로 조회 X, 비번이 일치하지 않는 경우
        if (exception instanceof BadCredentialsException) { // BadCredentialsException 시큐리티제공 예외처리/사용자인증으로 발생하는 예외
            List<String> errorCodes = Objects.requireNonNullElse(form.getErrorCodes(), new ArrayList<>());

            if (!StringUtils.hasText(email)) {
                errorCodes.add("NotBlank_email");
            }

            if (!StringUtils.hasText(password)) {
                errorCodes.add("NotBlank_password");
            }


            if (errorCodes.isEmpty()) {
                errorCodes.add("Failure.validate.login");
            } // 이메일과 비번이 있는데 오류발생했다 -> 일치하지않는다는 뜻 / 그에 대한 에러처리

            form.setErrorCodes(errorCodes);
        } else if (exception instanceof CredentialsExpiredException) { //  비밀번호가 만료된 경우 / 시큐리티 제공, 사용자의 자격 증명(비밀번호 등)이 만료된 경우 발생하는 예외
            redirectUrl = request.getContextPath() + "/member/password/change";
        } else if (exception instanceof DisabledException) { // 탈퇴한 회원 / 사용자의 계정이 비활성화(Disabled) 상태인 경우
            form.setErrorCodes(List.of("Failure.disabled.login"));

        }

        System.out.println(exception);

        session.setAttribute("requestLogin", form); // 틀리다고 해서 다시 안쳐도 되게 해주는거 / 남아있는거

        // 로그인 실패시에는 로그인 페이지로 이동
        response.sendRedirect(redirectUrl);
    }
}