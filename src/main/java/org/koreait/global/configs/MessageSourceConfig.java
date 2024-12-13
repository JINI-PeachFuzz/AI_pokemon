package org.koreait.global.configs;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {
    @Bean // 빈을 추가해야 다른곳에서도 사용가능함
    public MessageSource messageSource() {
        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
        ms.addBasenames("messages.commons", "messages.errors", "messages.validations", "messages.pokemon");
        ms.setDefaultEncoding("UTF-8");
        ms.setUseCodeAsDefaultMessage(true); // 기본 메세지/??형태로 나오는 걸 기본형태 코드로 나오게 해주는 거

        return ms;
    }
}
