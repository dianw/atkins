package org.enkrip.atkins.shared.websocket;

import org.enkrip.atkins.proto.MessageType;
import org.enkrip.atkins.proto.RPCRequestEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;

/**
 * Demo WebSocket handler for real-time chat functionality.
 * <p>
 * This class demonstrates how to:
 * - Handle WebSocket connections
 * - Broadcast messages to all connected clients
 * - Manage connected sessions
 * - Handle connection events
 * <p>
 * Usage:
 * Connect to ws://localhost:8080/websocket/chat
 * Send any text message and it will be broadcast to all connected clients
 */
@Service
public class ChatWebSocketHandler extends BinaryWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    private final WebSocketSessionService sessionService;
    private final ConversationService conversationService;

    public ChatWebSocketHandler(WebSocketSessionService sessionService, ConversationService conversationService) {
        this.sessionService = sessionService;
        this.conversationService = conversationService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        sessionService.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionService.afterConnectionClosed(session, status);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        RPCRequestEnvelope request = RPCRequestEnvelope.parseFrom(message.getPayload());
        if (!MessageType.REQUEST.equals(request.getMessageType())) {
            session.close();
        }

        switch (request.getOperationType()) {
            case GET_LIST_OF_CONVERSATIONS -> {
                conversationService.getListOfConversations(session, request);
            }
            case START_CONVERSATION -> {
                conversationService.startConversation(session, request);
            }
            case SEND_CONVERSATION_MESSAGE -> {
                conversationService.sendConversationMessage(session, request);
            }
            default -> {
                logger.info("Unknown operation type: {}", request.getOperationType());
            }
        }
    }
}
