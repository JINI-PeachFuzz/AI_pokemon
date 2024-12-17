package org.koreait.email.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.koreait.email.controllers.RequestEmail;
import org.koreait.email.exceptions.AuthCodeExpiredException;
import org.koreait.email.exceptions.AuthCodeMismatchException;
import org.koreait.global.exceptions.BadRequestException;
import org.koreait.global.libs.Utils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// 이메일인증 서비스
@Service
@Profile("email")
@RequiredArgsConstructor // 의존성 주입
public class EmailAuthService {

    private final Utils utils; // 글로벌-libs-utils 쪽에 이미 만들어놓은게 있어서 그걸 사용할 거임
    private final EmailService emailService;
    private final HttpSession session;

    /***
     *
     * @param to : 수신쪽 이메일 주소
     * @return
     */
    public boolean sendCode(String to) {
        Random random = new Random();
        String subject = utils.getMessage("Email.AuthCode.subject");

        /***
         * 인증 코드는 5자리 정수
         * 만료시간을 3분으로 기록
         * 사용자의 입력을 검증하기 위해서 세션에 인증 코드와 만료시간을 기록 // 개인서비스데이터!
         */
        Integer authCode = random.nextInt(99999); // 총 5자리내에서 코드가 발급됨. // int -> Integer로 바꿨음 / 오토박싱이 발생하면서 null오류가 발생하지 않을까 싶어서

        LocalDateTime expired = LocalDateTime.now().plusMinutes(3L);
        //3분정도 유효시간 설정

        session.setAttribute("authCode", authCode);
        session.setAttribute("expiredTime", expired);
        session.setAttribute("authCodeVerified", false); //코드를 발급받으면 인증이 필요하므로

        Map<String, Object> tplData = new HashMap<>();
        tplData.put("authCode", authCode);

        RequestEmail form = new RequestEmail();
        form.setTo(List.of(to));
        form.setSubject(subject);

        return emailService.sendEmail(form, "auth", tplData);
    }

    /***
     * 인증코드 검증
     * @param code : 사용자가 입력한 인증 코드
     */
    public void verify(Integer code) { // null값이 있을 수도 있어서 int나 string이 아닌 Integer로 했음
        if (code == null) {
            throw new BadRequestException(utils.getMessage("NotBlank.authCode"));
        }

        LocalDateTime expired = (LocalDateTime) session.getAttribute("expiredTime"); // 현재시간보다 앞서있다 == 만료시간!
        Integer authCode = (Integer) session.getAttribute("authCode");
        // Integer → intValue() → int

        if (expired != null && expired.isBefore(LocalDateTime.now())) { // 코드가 만료된 경우 / null이 아닐때도 넣어줬음 / 인티저로 했는데 널에 대한것도 해줘야해서
            throw new AuthCodeExpiredException();
        }

        if (authCode == null) {
            throw new BadRequestException();
        }

        if (!code.equals(authCode)) { // 인증 코드가 일치하지 않는 경우
            throw new AuthCodeMismatchException();
        }

        // 인증 성공 상태 세션에 기록
        session.setAttribute("authCodeVerified", true); // 검증할 때 필요함
    }
}
