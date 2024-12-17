package org.koreait.email.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"default", "test", "email"})
public class EmailAuthServiceTest {

    @Autowired
    private EmailAuthService service;

    @Test
    void test1() {
        boolean result = service.sendCode("jj0411.park@gmail.com");
        System.out.println(result); // 인증코드가 5자리로 메일로 왔음!

    }
}
