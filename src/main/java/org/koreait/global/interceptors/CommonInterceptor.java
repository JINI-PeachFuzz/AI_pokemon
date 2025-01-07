package org.koreait.global.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.koreait.global.entities.SiteConfig;
import org.koreait.global.services.CodeValueService;
import org.koreait.member.libs.MemberUtil;
import org.koreait.message.services.MessageInfoService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CommonInterceptor implements HandlerInterceptor {

    private final CodeValueService codeValueService;
    private final MemberUtil memberUtil;
    private final MessageInfoService messageInfoService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        setSiteConfig(modelAndView); // setSiteConfig의 기능이 좀 더 추가될수도 있어서 여기에 한번더 적어준거
        setProfile(modelAndView); // 공통으로 유지할거를 넣어준거
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

    /* 회원 프로필 설정 */
    private void setProfile(ModelAndView mv) {
        if (mv == null || !memberUtil.isLogin()) { // 회원정보 / 로그인되어있는지 확인 / 로그인되어있을때만 진행가능하게.
            return;
        }

        mv.addObject("profile", memberUtil.getMember()); // profile이 있으면 회원정보가 될거임 모델앤뷰가있고 회원정보가 있어야만 업데이트가 되게
        mv.addObject("totalUnRead", messageInfoService.totalUnRead()); // 미열람 쪽지 갯수
    }
}
