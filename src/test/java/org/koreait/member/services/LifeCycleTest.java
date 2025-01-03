package org.koreait.member.services;

import org.junit.jupiter.api.*;

// 테스트 라이프 사이클 확인
public class LifeCycleTest {

    @BeforeAll
    static void beforeAll() {
        System.out.println("BEFORE ALL");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("AFTER ALL");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("BEFORE EACH");
    }

    @AfterEach
    void afterEach() {
        System.out.println("AFTER EACH");
    }

    @Test
    void test1() {
        System.out.println("TEST1");

    }

    @Test
    @Disabled // 이거는 테스트를 배제함
    void test2() {
        System.out.println("TEST2");

    }

    @Test
    @Timeout(3L) // 3초
    void test3() throws Exception{
        System.out.println("TEST3");
        Thread.sleep(5000); // 5초 대기
    }
}
