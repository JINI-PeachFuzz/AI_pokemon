package org.koreait.global.advices;

import lombok.RequiredArgsConstructor;
import org.koreait.global.exceptions.CommonException;
import org.koreait.global.libs.Utils;
import org.koreait.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
// 제이슨형태는 데이터를 보여주기만 하는거
@RestControllerAdvice(annotations = RestController.class) // 컨트롤러가 들어가 있음 그래서 다형성에 의해 동일하게 먹힘
// interface RestController extends controller
@RequiredArgsConstructor
public class CommonRestControllerAdvice { // 공통적인 에러출력 / JSON쪽!

    private final Utils utils;

    @ExceptionHandler(Exception.class) // CommonException에 에러메세지관련 내용 추가함
    public ResponseEntity<JSONData> errorHandler(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 기본 에러 코드 500

        Object message = e.getMessage();

        if (e instanceof CommonException commonException) {
            status = commonException.getStatus();

            Map<String, List<String>> errorMessages = commonException.getErrorMessages();
            if (errorMessages != null) {
                message = errorMessages;
            } else {
                message = commonException.isErrorCode() ? utils.getMessage((String)message) : message;
            }// 에러코드일때는 메시지를 가져오고 아니면 메세지를 출력함
        }

        JSONData data = new JSONData(); // 응답은 예측가능한 형태로 보여지는게 좋은데 규칙대로 보여지면 예측하기 편하니까 이렇게 만든거
        data.setSuccess(false); // 에러니까 실패한거
        data.setStatus(status); // 응답 상태
        data.setMessage(message); // 오브젝트로 되어있어서 다 나올거임

        e.printStackTrace();

        return ResponseEntity.status(status).body(data); // 템플릿이 아니라서 응답코드도 직접 넣어주면 됨 // 헤더도 가능한데 응답코드와 제이슨 형태를 바디데이터에 넘겨줌
    }
}
