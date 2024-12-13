package org.koreait.global.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class FileConfig implements WebMvcConfigurer {

    private final FileProperties properties;
    // 밸류를 가지고 설정항목들 경로를 가져올 수 있는데
    // 파일쪽 설정을 범주화하기 위해서

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // addResourceHandlers는 WebMvcConfigurer설정에 접근가능하게 정적경로로 연결시킨거
        // 예를 들면 이미지가 화면에 보이게 연결해야하는데 그 접근가능하게 하는 것
        registry.addResourceHandler(properties.getUrl() + "**")
                .addResourceLocations("file:///" + properties.getPath());
        // 서로 열결시킨거 브라우저에서 연결해서 볼 수 있게 // 이건 이미 정해진 설정 틀임
        // 폴더경로에 들어가면 이미지 있다 이런거
    }
}
