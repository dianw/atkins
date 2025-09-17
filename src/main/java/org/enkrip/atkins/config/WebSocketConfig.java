package org.enkrip.atkins.config;

import org.enkrip.atkins.shared.websocket.ChatWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ChatWebSocketHandler chatWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        HttpSessionHandshakeInterceptor httpSessionHandshakeInterceptor = new HttpSessionHandshakeInterceptor();
        httpSessionHandshakeInterceptor.setCreateSession(true);

        registry.addHandler(chatWebSocketHandler, "/websocket/chat")
                .addInterceptors(httpSessionHandshakeInterceptor)
                .setAllowedOrigins("*"); // For demo purposes - in production, specify allowed origins
    }
}
