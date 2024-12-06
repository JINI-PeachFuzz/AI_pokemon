package org.koreait.file.exceptions;

import org.koreait.global.exceptions.scripts.AlertException;
import org.springframework.http.HttpStatus;

public class FileNotFoundException extends AlertException {
    public FileNotFoundException() {
        super("NotFound.file", HttpStatus.NOT_FOUND);
        setErrorCode(true);
    }
}
