package org.koreait.global.libs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.MemberUtils;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.services.FileInfoService;
import org.koreait.member.libs.MemberUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Utils {

    private final HttpServletRequest request;
    private final MessageSource messageSource;
    private final FileInfoService fileInfoService;
    private final MemberUtil memberUtil;

    public boolean isMobile() {

        // 요청 헤더 - User-Agent / 브라우저 정보
        String ua = request.getHeader("User-Agent");
        String pattern = ".*(iPhone|iPod|iPad|BlackBerry|Android|Windows CE|LG|MOT|SAMSUNG|SonyEricsson).*"; // 모바일 구현 / 요청헤드쪽에 있는 걸로 모바일인지 아닌지 확인 / 정규 표현식!


        return StringUtils.hasText(ua) && ua.matches(pattern); // DB조회시 NULL로 나와서 StringUtils.hasText(ua) 넣었음
    }

    /**
     * mobile, front 템플릿 분리 함수
     *
     * @param path
     * @return
     */
    public String tpl(String path) { // 템플릿 / 모바일과 pc를 구분하기 위해만든거서
        String prefix = isMobile() ? "mobile" : "front"; // 모바일인지 프론트인지 구분할 수 있게 해줌

        return String.format("%s/%s", prefix, path);
    }

    /**
     * 메서지 코드로 조회된 문구
     *
     * @param code
     * @return
     */
    public String getMessage(String code) {
        Locale lo = request.getLocale(); // 사용자 요청 헤더(Accept-Language)
        // 브라우저에 있는 언어설정 / 이걸가지고 만든게 lacale 정보임!// 톰캣서버가 결정하고 정해줌

        return messageSource.getMessage(code, null, lo); // 싱글톤이라서 메세지소스는 하나임 / lo : locale정보!
    }

    public List<String> getMessages(String[] codes) {

        return Arrays.stream(codes).map(c -> {
            try { // 있는것만 나오게 하기위해 트라이 캐치를 사용함
                return getMessage(c);
            } catch (Exception e) {
                return ""; // 예외처리났을때 멈추는 것보단 비어있는 걸로 나오는게 좋으니 "" 로 처리한거 / 예외처리 코드다발안보이고 정해진걸로 보이게 하기 위해서!
            }
        }).filter(s -> !s.isBlank()).toList();

    }

    /**
     * REST 커맨드 객체 검증 실패시에 에러 코드를 가지고 메세지 추출
     *
     * @param errors
     * @return
     */
    public Map<String, List<String>> getErrorMessages(Errors errors) { // getErrorMessages 는 tpl이 아니기 때문에 JSON형태로 만들기위해
        ResourceBundleMessageSource ms = (ResourceBundleMessageSource) messageSource;
        ms.setUseCodeAsDefaultMessage(false); // false로 변경한 이유 : 프로퍼티쪽에 메세지가 없으면 에러코드가 나오는데 의도하지않은 코드가 나올 수 있어서 감추기 위해 등록하지 않은 메시지일경우 예외발생함
        try {
            // 필드별 에러코드 - getFieldErrors() // 커맨드객체 에러
            // Collectors.toMap
            Map<String, List<String>> messages = errors.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, f -> getMessages(f.getCodes()), (v1, v2) -> v2)); // 동일한 키값이 있으면 오류가 발생하므로 중복된게 있으면 v1은 기존, v2는 현재인데 현재껄로 바꿔줘라
            // Codes 여러개가 될 수 있기 때문에 배열로

            // 글로벌 에러코드 - getGlobalErrors()
            List<String> gMessages = errors.getGlobalErrors()
                    .stream()
                    .flatMap(o -> getMessages(o.getCodes()).stream())
                    .toList();
            // 글로벌 에러코드 필드 - global
            if (!gMessages.isEmpty()) {
                messages.put("global", gMessages);
            }

            return messages;
        } finally {
            ms.setUseCodeAsDefaultMessage(true); // 싱글톤이기 때문에 다시 원래형으로 돌린거
            // 다썼으면 원래형태로 돌린거 싱글톤이라서 다른 곳에서도 false일테니까
        }
    }

    /**
     * 이미지 출력
     *
     * @param width
     * @param height
     * @param mode - image : 이미지 태그로 출력, background : 배경 이미지 형태 출력
     * @return
     */
    public String showImage(Long seq, int width, int height, String mode, String className) {
        return showImage(seq, null, width, height, mode, className);
    }

    public String showImage(Long seq, int width, int height, String className) {
        return showImage(seq, null, width, height, "image", className);
    }

    public String showBackground(Long seq, int width, int height, String className) {
        return showImage(seq, null, width, height, "background", className);
    }

    public String showImage(String url, int width, int height, String mode, String className) {
        return showImage(null, url, width, height, mode, className);
    }

    public String showImage(String url, int width, int height, String className) {
        return showImage(null, url, width, height, "image", className);
    }

    public String showBackground(String url, int width, int height, String className) {
        return showImage(null, url, width, height, "background", className);
    }

    public String showImage(Long seq, String url, int width, int height, String mode, String className) {
// seq : 파일등록번호 , url은 원격번호 주소 / 너비, 높이, / 모드:이미지태그, 백그라운드(이미지) // 어느 이미지로 할지 구분 // 썸네일관련 // 크기 맞추는거 api쪽에 sum이라는게 있는게 거기에 들어가서 맞춰지는 거

        try {
            String imageurl = null;
            if (seq != null && seq > 0L) {
                FileInfo item = fileInfoService.get(seq); // 화면에 꽉채울때등등
                if (!item.isImage()) {
                    return ""; // null 보단 비어있는 문자열이 오류방지위해서는 좋음
                }

                imageurl = String.format("%s&width=%d&height=%d", item.getThumbUrl(), width, height); // getThumbUrl 2차가공!

            } else if (StringUtils.hasText(url)) {
                imageurl = String.format("%s/api/file/thumb?url=%s&width=%d&height=%d", request.getContextPath(), url, width, height);
            } // url을 가지고 썸네일을 만듦

            if(!StringUtils.hasText(imageurl)) return "";

            mode = Objects.requireNonNullElse(mode, "image");
            className = Objects.requireNonNullElse(className, "image");
            if (mode.equals("background")) { // 배경 이미지

                return String.format("<div style='width: %dpx; height: %dpx; background:url(\"%s\") no-repeat center center; background-size:cover;' class='%s'></div>", width, height, imageurl, className);
            } else { // 이미지 태그
                return String.format("<img src='%s' class='%s'>", imageurl, className); // 단일 태그에서는 /로 안닫아도 됌 // 리액트에서는 꼭 닫아야함
            }
        } catch (Exception e) {}

        return "";
    }

    /**
     * 메세지를 세션쪽에 저장해서 임시 팝업으로 띄운다.
     * @param message
     */
    public void showSessionMessage(String message) {
        HttpSession session = request.getSession();
        session.setAttribute("showMessage", message);

    }

    public void removeSessionMessage() {
        HttpSession session = request.getSession();
        session.removeAttribute("showMessage");
    }
    public String getParam(String name) {
        return request.getParameter(name);
    }

    public String[] getParams(String name) {
        return request.getParameterValues(name);

    }

    /***
     * 줄개행 문자(\n 또는 \r\n)를 br 태그로 변환
     * @param text
     * @return
     */
    public String nl2br(String text) {
        return text == null ? "" : text.replaceAll("\\r", "")
                .replaceAll("\\n", "<br>"); // br을 줄개행으로 변환 / 약관조회 오류로 인해 널조건을 좀더 추가했음
    }

    public String popup(String url, int width, int height) {
        return String.format("commonLib.popup('%s', %d, %d);", url, width, height);
    }

    // 회원, 비회원 구분 해시
    public int getMemberHash() {
        // 회원 - 회원번호, 비회원 - IP + User-Agent
        if (memberUtil.isLogin()) return Objects.hash(memberUtil.getMember().getSeq());
        else { // 비회원
            String ip = request.getRemoteAddr();
            String ua = request.getHeader("User-Agent");

            return Objects.hash(ip, ua);
        }
    }
}






