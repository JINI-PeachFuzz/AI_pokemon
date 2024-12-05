package org.koreait.global.advices;

import org.koreait.global.exceptions.CommonException;
import org.koreait.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice(annotations = RestController.class)
public class CommonRestControllerAdvice {

    @ExceptionHandler(Exception.class) // CommonException에 에러메세지관련 내용 추가함
    public ResponseEntity<JSONData> errorHandler(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 기본 에러 코드 500

        Object message = e.getMessage();

        if (e instanceof CommonException commonException) {
            status = commonException.getStatus();

            Map<String, List<String>> errorMessages = commonException.getErrorMessages();
            if (errorMessages != null) {
                message = errorMessages;
            }
        }

        JSONData data = new JSONData();
        data.setSuccess(false); // 에러니까 실패한거
        data.setStatus(status); // 응답 상태
        data.setMessage(message); // 오브젝트로 되어있어서 다 나올거임

        e.printStackTrace();

        return ResponseEntity.status(status).body(data); // 템플릿이 아니라서 응답코드도 직접 넣어주면 됨
    }
}
