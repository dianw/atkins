package org.enkrip.atkins.shared.websocket;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.enkrip.atkins.proto.ChatMessage;
import org.enkrip.atkins.proto.Conversation;
import org.enkrip.atkins.shared.mapper.ProtobufMapper;
import org.enkrip.atkins.shared.websocket.dto.ChatMessageDto;
import org.enkrip.atkins.shared.websocket.dto.ConversationDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Chat", description = "Chat endpoints for conversations and messaging")
public class ChatRestController {
    private final WebSocketSessionService sessionService;
    private final ConversationService conversationService;
    private final ProtobufMapper protobufMapper = ProtobufMapper.INSTANCE;

    public ChatRestController(WebSocketSessionService sessionService, ConversationService conversationService) {
        this.sessionService = sessionService;
        this.conversationService = conversationService;
    }

    @Operation(
            summary = "Get active chat participants",
            description = "Retrieves a list of all currently active users who are available for chat"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active participants retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))))
    })
    @GetMapping("/api/chat/participants")
    public Set<String> participants(HttpServletRequest request) {
        return sessionService.getAllActiveUsernames(request);
    }

    @Operation(
            summary = "Get user conversations",
            description = "Retrieves all conversations that the current user is participating in"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversations retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ConversationDto.class))))
    })
    @GetMapping("/api/chat/conversations")
    public Set<ConversationDto> conversations(HttpServletRequest request) {
        String currentUser = sessionService.getCurrentUsername(request);
        Set<Conversation> protobufConversations = conversationService.getUserConversations(currentUser);
        
        return protobufConversations.stream()
                .map(protobufMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Operation(
            summary = "Get messages by conversation ID",
            description = "Retrieves all messages from a specific conversation that the current user has access to"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ChatMessageDto.class)))),
            @ApiResponse(responseCode = "403", description = "User does not have access to this conversation"),
            @ApiResponse(responseCode = "404", description = "Conversation not found")
    })
    @GetMapping("/api/chat/conversations/{conversationId}/messages")
    public List<ChatMessageDto> getMessagesByConversationId(
            @Parameter(description = "Unique identifier of the conversation", required = true)
            @PathVariable("conversationId") String conversationId, 
            HttpServletRequest request) {
        String currentUser = sessionService.getCurrentUsername(request);
        Set<ChatMessage> messages = conversationService.getConversationMessages(conversationId, currentUser);
        
        return messages.stream()
                .map(protobufMapper::toDto)
                .collect(Collectors.toList());
    }
}
