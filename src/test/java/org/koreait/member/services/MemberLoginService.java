package org.koreait.member.services;

import jakarta.servlet.http.HttpServletRequest;

public class MemberLoginService {

    private HttpServletRequest request; // 이거는 톰캣서버내에서 만들어지는 객체인데 특정상황이 아니면 만들수 없는 객체임

    public MemberLoginService(HttpServletRequest request) {
        this.request = request;
    }

    public void process() {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        System.out.printf("%s, %s%n", email, password);

        request.setAttribute("email", email); // 호출되었을 때 뭐가 넘어왔는지 인자배출가능 // email
        request.setAttribute("password", password);
        // 사용되었을 때 어떤값을 사용했는지 알 수 있음
    }

}
