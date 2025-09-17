package org.enkrip.atkins.shared.websocket;

import jakarta.servlet.http.HttpServletRequest;
import org.enkrip.atkins.proto.Conversation;
import org.enkrip.atkins.shared.websocket.dto.ConversationDto;
import org.enkrip.atkins.shared.websocket.dto.ProtobufToDtoConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class ChatRestController {
    private final WebSocketSessionService sessionService;
    private final ConversationService conversationService;

    public ChatRestController(WebSocketSessionService sessionService, ConversationService conversationService) {
        this.sessionService = sessionService;
        this.conversationService = conversationService;
    }


    @GetMapping("/api/chat/participants")
    public Set<String> participants(HttpServletRequest request) {
        return sessionService.getAllActiveUsernames(request);
    }

    @GetMapping("/api/chat/conversations")
    public Set<ConversationDto> conversations(HttpServletRequest request) {
        String currentUser = sessionService.getCurrentUsername(request);
        Set<Conversation> protobufConversations = conversationService.getUserConversations(currentUser);
        
        return protobufConversations.stream()
                .map(ProtobufToDtoConverter::toDto)
                .collect(Collectors.toSet());
    }
}
