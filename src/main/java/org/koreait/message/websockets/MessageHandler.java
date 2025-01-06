package org.koreait.message.websockets;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageHandler extends TextWebSocketHandler {

    private List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        for (WebSocketSession s : sessions) { // 새로고침없이 바로 확인가능해짐
            s.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session); // session 접속하는게 많으면 문제가 있을 수 도 있음
        // 지울때 제대로 안지워질수도 있어서 세션이 열린게 없으면 일괄적으로 삭제가능하게 기능 추가

        sessions.stream().filter(s -> !s.isOpen()).forEach(sessions::remove);
    }
}