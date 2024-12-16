package org.koreait.email.controllers;

import lombok.Data;

import java.util.List;

@Data
public class RequestEmail {
    private List<String> to; // 받는 쪽 이메일 / 여러개도 받기 위해서 리스트형으로 만듦
    private List<String> cc; // 참조
    private List<String> bcc; // 숨은 참조
    private String subject; // 메일 제목
    private String content; // 메일 내용
}
// 전송데이터를 담을 수 있는 커맨드 객체 만들었음