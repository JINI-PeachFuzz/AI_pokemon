package org.koreait.global.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class I18NConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
                //.addPathPatterns("/**") - 사이트 전역에 적용하는 패턴일 경우 생략 가능
        // 어디서 사용할 건지 정한 거!
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("language"); // ?language=en
        // language는 쿠키에 적히는 이름

        return interceptor;
    }

    @Bean
    public CookieLocaleResolver localeResolver() { // Resolver : Controller로 들어온 파라미터를 가공하거나 수정 기능을 제공하는 객체
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setCookieMaxAge(60*60); // 한시간만 유지
        resolver.setCookieName("language"); // 한시간뒤엔 설정에 있는 언어순위대로 나올꺼임
        // 예를 들면, 도감이라던지 다른 페이지로 갔다 오면 설정이 풀리는데 그걸 방지할려고 설정한거

        return resolver;
    }
}
