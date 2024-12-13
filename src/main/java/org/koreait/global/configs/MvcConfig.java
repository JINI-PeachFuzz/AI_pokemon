package org.koreait.global.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableJpaAuditing
@EnableScheduling // 쌓아놓고 이괄적으로 한번에 학습을 시킴
@EnableRedisHttpSession // 레디스에 대한 기본설정들이 여기 들어가 있다고 생각하면 될듯
public class MvcConfig implements WebMvcConfigurer {
    /**
     * 정적 경로 설정, CSS, JS, 이미지
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**") // **는 앤트패턴
                .addResourceLocations("classpath:/static/");
        // classpath는 클래스파일을 인식할 수 있는 경로 / 정적경로 / css나 js파일이 있는 곳
    }

    /**
     * PATCH, PUT, DELETE 등등
     * PATCH 메서드로 요청을 보내는 경우
     * <form method = 'POST' ...>
     *      <input type='hidden' name='_method' value='PATCH'>
     * </form>
     * 설정에 대한거 이렇게 써야함
     * @return
     */

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter(){
        return new HiddenHttpMethodFilter();
        // get이나 post방식으로 사용하는데 delete나 path, put등을 사용할려고 할때
        // 히든을 사용하면 교체해서 사용할 수 있게 해줌
        // 프로필html 에서 보면 타입을 히든으로 해서 넣어줬었음
    }
}
