package org.koreait.global.configs;

import org.koreait.message.websockets.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Objects;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private MessageHandler messageHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // ws://도메인주소(localhost:3000/message이런식으로 접근가능 or 스프링부트쪽)
//        String profile = System.getenv("spring.profiles.active"); // 직접설정한걸로

        String profile = Objects.requireNonNullElse(System.getenv("spring.profiles.active"), "default");


//        registry.addHandler(messageHandler, "message")
////                .setAllowedOrigins("http://jinilog.com");
//                .setAllowedOrigins(profile.contains("prod") ? "" : "http://localhost:3000"); // "" -> jinilog.com을 넣으면 됨/도메인주소

        registry.addHandler(messageHandler, "msg")
                .setAllowedOrigins(profile.contains("prod") ? "" : "http://localhost:3000");

    }
}