package web_example.web_example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.HashMap;
import org.json.JSONObject; // JSON 처리 라이브러리 추가

public class ChatSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions;
    private static final Logger logger = LogManager.getLogger(ChatSocketHandler.class);

    @Autowired
    public ChatSocketHandler() {
        sessions = new HashMap<>();
    }

    @Override // 웹 소켓 연결시
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);

        JSONObject message = new JSONObject();
        message.put("action", "newConnect");
        message.put("sender", session.getId());
        message.put("receiver", "all");

        for (WebSocketSession s : sessions.values()) {
            if (!(s.getId().equals(session.getId()))) {
                JSONObject responseJson = new JSONObject();
                responseJson.put("data", "Hi " + message.toString() + "!");
                s.sendMessage(new TextMessage(responseJson.toString()));
            }
        }
    }

    @Override // 데이터 통신시
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JSONObject jsonObject = new JSONObject(payload);

        for (WebSocketSession s : sessions.values()) {
            JSONObject responseJson = new JSONObject();
            responseJson.put("data", jsonObject.getString("data"));
            responseJson.put("id", jsonObject.getString("id"));
            s.sendMessage(new TextMessage(responseJson.toString()));
        }
    }

    @Override // 웹소켓 통신 에러시
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket error: ", exception);
        super.handleTransportError(session, exception);
    }

    @Override // 웹 소켓 연결 종료시
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();

        sessions.remove(sessionId);

        JSONObject message = new JSONObject();
        message.put("action", "closeConnect");
        message.put("sender", sessionId);

        for (WebSocketSession s : sessions.values()) {
            s.sendMessage(new TextMessage("BYE " + message.toString() + "!"));
        }
    }
}
