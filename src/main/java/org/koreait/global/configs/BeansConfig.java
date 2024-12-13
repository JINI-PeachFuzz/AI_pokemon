package org.koreait.global.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

// 수동등록 빈설정
@Configuration
public class BeansConfig {

    @Lazy
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Lazy
    @Bean
    public ModelMapper modelMapper() { // Mapper 범용기능기
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return mapper;
    }

    // 자바객체를 JSON으로 서로 바꿔주는 기능!
    // ObjectMapper
    @Lazy
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule()); // java8 data & time api - java.time 패키지

        return om;
    }
}
