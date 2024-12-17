package org.koreait.email.exceptions;

import org.koreait.global.exceptions.BadRequestException;

public class AuthCodeExpiredException extends BadRequestException {
    public AuthCodeExpiredException() {
        super("Expired.authCode"); // 만료되었다는 메세지
        setErrorCode(true);
    }

}
