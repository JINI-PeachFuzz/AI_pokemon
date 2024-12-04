package org.koreait.global.rests;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class JSONData {
    private HttpStatus status = HttpStatus.OK; // 응답도 200이 많음 OK는 200을 의미함
    private boolean success = true; // 성공이 많으니 기본적으로 넘어가게 설정한거 기본은 false임!
    private Object message; // 실패시 에러 메세지 / 중첩된 필드와 자료형으로 나올수 있으니 옵젝으로 만든거
    private Object data; // 성공시 데이터

    public JSONData(Object data) {
        this.data = data; // 생성자!
    }
}
