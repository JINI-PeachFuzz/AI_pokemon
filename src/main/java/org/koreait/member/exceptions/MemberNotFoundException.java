package org.koreait.member.exceptions;


import org.koreait.global.exceptions.scripts.AlertBackException;
import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends AlertBackException {
    public MemberNotFoundException() {
        super("NotFound.member", HttpStatus.NOT_FOUND);
        setErrorCode(true); // 메세지 보낼때 실제로 존재하는 이메일이어야하니까 예외처리 함
    }
}
