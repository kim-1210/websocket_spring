package web_example.web_example;

import java.util.logging.SocketHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class webconfig implements WebSocketConfigurer{

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalingSocketHandler(), "/signal").setAllowedOrigins("*");
    }

    @Bean
    public ChatSocketHandler signalingSocketHandler() { // WebSocketHandler을 웹 소켓 핸들러로 정의
        return new ChatSocketHandler();
    }
    
}
