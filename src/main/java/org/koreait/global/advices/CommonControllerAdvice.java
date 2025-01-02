package org.koreait.global.advices;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.exceptions.CommonException;
import org.koreait.global.exceptions.scripts.AlertBackException;
import org.koreait.global.exceptions.scripts.AlertException;
import org.koreait.global.exceptions.scripts.AlertRedirectException;
import org.koreait.global.libs.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
// 자바형태로서 보여지는 쪽이고 아래 Rest는 제이슨형태로 데이터가 보여지는거
@ControllerAdvice(annotations = ApplyErrorPage.class)
@RequiredArgsConstructor
public class CommonControllerAdvice {
    private final Utils utils;

    // 아래에 에러페이지에 대한 공통처리를 넣었음
    @ExceptionHandler(Exception.class)
    public ModelAndView errorHandler(Exception e, HttpServletRequest request) {
        Map<String, Object> data = new HashMap<>();

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 기본 응답 코드 500
        String tpl = "error/error"; // 기본 출력 템플릿
        String message = e.getMessage();

        data.put("method", request.getMethod());
        data.put("path", request.getContextPath() + request.getRequestURI());
        data.put("querystring", request.getQueryString());
        data.put("exception", e);

        if (e instanceof CommonException commonException) {
            status = commonException.getStatus(); // 응답코드에 관련된부분(정확하게 나타내기 위해서) / 예외체계를 만들었는데 상속받은 객체면 그걸 사용함
            message = commonException.isErrorCode() ? utils.getMessage(message) : message;

            StringBuffer sb = new StringBuffer(2048);
            if (e instanceof AlertException) {
                tpl = "common/_execute_script"; // 스크립트를 실행하기 위한 HTML 템플릿
                sb.append(String.format("alert('%s');", message));
            }

            if (e instanceof AlertBackException backException) {
                String target = backException.getTarget();
                sb.append(String.format("%s.history.back();", target));
            }

            if (e instanceof AlertRedirectException redirectException) {
                String target = redirectException.getTarget();
                String url = redirectException.getUrl();
                sb.append(String.format("%s.location.replace('%s');", target, url));
            }
// 위 3개 예외처리는 자주 사용하는 것들이라 공통으로 사용하기 위해 만듦
            if (!sb.isEmpty()) {
                data.put("script", sb.toString());
            }
        }

        data.put("status", status.value());
        data.put("_status", status);
        data.put("message", message);
        ModelAndView mv = new ModelAndView();
        mv.setStatus(status); // 응답코드
        mv.addAllObjects(data);
        mv.setViewName(tpl);
        return mv;
    }
}
