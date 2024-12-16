package org.koreait.email;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.koreait.email.controllers.RequestEmail;
import org.koreait.email.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ActiveProfiles({"default", "test", "email"})
public class EmailSendTest {
    /***
     * to : 받는 이메일
     * cc : 참조
     * bcc : 숨은 참조
     */
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private EmailService service;

    @Test
    void test1() throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
        helper.setTo("jj0411.park@gmail.com");
        helper.setSubject("테스트 이메일 제목...");
        helper.setText("테스트 이메일 내용...");
        javaMailSender.send(message);
    }

    @Test
    void test2() { // 템플릿으로 사용하는 방법 / auth.html이 subject를 통해서 가져와짐
        Context context = new Context();
        // Context는 타임리프껄 받아와야함
        context.setVariable("subject", "테스트 제목...");

        String text = templateEngine.process("email/auth", context); // 확장자는 안넣어도 html로 바로 받음 / context는 el식을 말함
        System.out.println(text);
    }

    @Test
    void test3() {
        RequestEmail form = new RequestEmail();
        form.setTo(List.of("jj0411.park@gmail.com", "jj0411.park@gmail.com"));
        form.setCc(List.of("jj0411.park@gmail.com"));
        form.setBcc(List.of("jj0411.park@gmail.com"));
        form.setSubject("테스트 이메일 제목... 섭젯");
        form.setContent("<h1>테스트 이메일 내용...콘텟</h1>");

        Map<String, Object> tplData = new HashMap<>();
        tplData.put("key1", "값1");
        tplData.put("key2", "값2");

        boolean result = service.sendEmail(form,"auth", tplData);
        System.out.println(result);

    }
}
