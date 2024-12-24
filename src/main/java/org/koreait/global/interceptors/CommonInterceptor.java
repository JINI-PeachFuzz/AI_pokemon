package org.koreait.global.interceptors;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.koreait.global.entities.SiteConfig;
import org.koreait.global.services.CodeValueService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CommonInterceptor implements HandlerInterceptor {

    private final CodeValueService codeValueService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        setSiteConfig(modelAndView); // setSiteConfig의 기능이 좀 더 추가될수도 있어서 여기에 한번더 적어준거
    }
    /* 사이트 설정 */
    private void setSiteConfig(ModelAndView mv) {
        if (mv == null) { // 모델앤뷰는 콘트롤러쪽에 유입될때만
            return;
        }
//        System.out.println("uri :" + uri);
        SiteConfig config = Objects.requireNonNullElseGet(codeValueService.get("siteConfig", SiteConfig.class), SiteConfig::new);

        mv.addObject("siteConfig", config);
    }
}
