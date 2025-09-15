package org.enkrip.atkins.shared.websocket;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class ChatRestController {
    private final ChatWebSocketHandler chatWebSocketHandler;

    public ChatRestController(ChatWebSocketHandler chatWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @GetMapping("/api/chat/participants")
    public Collection<String> participants() {
        return chatWebSocketHandler.getSessionIdUsername().values();
    }
}
