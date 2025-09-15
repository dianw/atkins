package org.enkrip.atkins.shared.websocket;

import com.google.protobuf.Timestamp;
import org.enkrip.atkins.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class ChatWebSocketHandler extends BinaryWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    // Thread-safe set to store all active WebSocket sessions
    private final Map<String, WebSocketSession> usernameSessionId = new ConcurrentHashMap<>();
    private final Map<String, String> sessionIdUsername = new ConcurrentHashMap<>();

    // In-memory storage for user conversations and messages
    private final Map<String, List<Conversation>> usernameConversations = new ConcurrentHashMap<>();
    private final Map<String, List<ChatMessage>> conversationIdChats = new ConcurrentHashMap<>();
    private final Map<String, Conversation> conversationIdConversation = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        String username = session.getHandshakeHeaders()
                .getOrDefault("X-Username", List.of(UUID.randomUUID().toString()))
                .getFirst();

        if (usernameSessionId.containsKey(username)) {
            session.close(CloseStatus.NO_STATUS_CODE);
        }

        usernameSessionId.put(username, session);
        sessionIdUsername.put(session.getId(), username);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = sessionIdUsername.remove(session.getId());
        usernameSessionId.remove(username);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        RPCRequestEnvelope request = RPCRequestEnvelope.parseFrom(message.getPayload());
        if (!MessageType.REQUEST.equals(request.getMessageType())) {
            session.close();
        }

        String currentUser = sessionIdUsername.get(session.getId());

        switch (request.getOperationType()) {
            case START_CONVERSATION -> {
                StartConversationRequest startConversationRequest = request.getStartConversationRequest();
                String participant = startConversationRequest.getParticipantUserIdsList().getFirst();

                if (currentUser.equals(participant)) {
                    RPCResponseEnvelope response = RPCResponseEnvelope.newBuilder()
                            .setMessageType(MessageType.RESPONSE)
                            .setOperationType(MessageOperationType.START_CONVERSATION)
                            .setSuccess(false)
                            .setErrorMessage("Cannot start conversation with yourself")
                            .build();
                    session.sendMessage(new BinaryMessage(response.toByteArray()));
                    return;
                }

                if (!usernameSessionId.containsKey(participant)) {
                    RPCResponseEnvelope response = RPCResponseEnvelope.newBuilder()
                            .setMessageType(MessageType.RESPONSE)
                            .setOperationType(MessageOperationType.START_CONVERSATION)
                            .setSuccess(false)
                            .setErrorMessage("Participant not online")
                            .build();
                    session.sendMessage(new BinaryMessage(response.toByteArray()));
                    return;
                }

                Instant now = Instant.now();
                String conversationId = Stream.of(currentUser, participant).sorted()
                        .collect(Collectors.joining());
                conversationId = UUID
                        .nameUUIDFromBytes(conversationId.getBytes(StandardCharsets.UTF_8))
                        .toString();

                Conversation conversation = Conversation.newBuilder()
                        .setConversationId(conversationId)
                        .setLastUpdatedTimestamp(Timestamp.newBuilder()
                                .setSeconds(now.getEpochSecond())
                                .setNanos(now.getNano())
                        )
                        .addParticipants(ChatUser.newBuilder().setUserId(currentUser).setDisplayName(currentUser))
                        .addParticipants(ChatUser.newBuilder().setUserId(participant).setDisplayName(participant))
                        .build();

                RPCResponseEnvelope response = RPCResponseEnvelope.newBuilder()
                        .setMessageType(MessageType.RESPONSE)
                        .setOperationType(MessageOperationType.START_CONVERSATION)
                        .setSuccess(true)
                        .setStartConversationResponse(
                                StartConversationResponse.newBuilder().setConversation(conversation)
                        )
                        .build();

                conversationIdConversation.put(conversationId, conversation);
                Stream.of(currentUser, participant).forEach(user -> {
                    usernameConversations.computeIfAbsent(user, k -> new java.util.ArrayList<>())
                            .add(conversation);
                });
                conversationIdChats.putIfAbsent(conversationId, new java.util.ArrayList<>());

                session.sendMessage(new BinaryMessage(response.toByteArray()));
            }
            case GET_LIST_OF_CONVERSATIONS -> {
                List<Conversation> conversations = usernameConversations.getOrDefault(currentUser, new ArrayList<>());
                RPCResponseEnvelope response =RPCResponseEnvelope.newBuilder()
                        .setMessageType(MessageType.RESPONSE)
                        .setOperationType(MessageOperationType.GET_LIST_OF_CONVERSATIONS)
                        .setSuccess(true)
                        .setGetListOfConversationsResponse(
                                GetListOfConversationsResponse.newBuilder().addAllConversations(conversations)
                        )
                        .build();
                session.sendMessage(new BinaryMessage(response.toByteArray()));
            }
            case SEND_CONVERSATION_MESSAGE -> {
                SendConversationMessageRequest sendRequest = request.getSendConversationMessageRequest();

                if (!conversationIdConversation.containsKey(sendRequest.getConversationId())) {
                    RPCResponseEnvelope response = RPCResponseEnvelope.newBuilder()
                            .setMessageType(MessageType.RESPONSE)
                            .setOperationType(MessageOperationType.SEND_CONVERSATION_MESSAGE)
                            .setSuccess(false)
                            .setErrorMessage("Conversation does not exist")
                            .build();
                    session.sendMessage(new BinaryMessage(response.toByteArray()));
                    return;
                }

                Conversation conversation = conversationIdConversation.get(sendRequest.getConversationId());
                boolean userInConversation = conversation.getParticipantsList()
                        .stream()
                        .map(ChatUser::getUserId)
                        .anyMatch(currentUser::equals);
                if (!userInConversation) {
                    RPCResponseEnvelope response = RPCResponseEnvelope.newBuilder()
                            .setMessageType(MessageType.RESPONSE)
                            .setOperationType(MessageOperationType.SEND_CONVERSATION_MESSAGE)
                            .setSuccess(false)
                            .setErrorMessage("You are not part of this conversation")
                            .build();
                    session.sendMessage(new BinaryMessage(response.toByteArray()));
                    return;
                }

                Instant now = Instant.now();

                // send to target user if online
                ChatMessage.Builder chatMessageBuilder = ChatMessage.newBuilder()
                        .setMessageId(UUID.randomUUID().toString())
                        .setMessageType(sendRequest.getMessageType())
                        .setSender(ChatUser.newBuilder().setUserId(currentUser).setDisplayName(currentUser))
                        .setContent(sendRequest.getContent())
                        .setConversationId(conversation.getConversationId())
                        .setTimestamp(Timestamp.newBuilder()
                                .setSeconds(now.getEpochSecond())
                                .setNanos(now.getNano())
                        );

                // send response to sender
                RPCResponseEnvelope response = RPCResponseEnvelope.newBuilder()
                        .setMessageType(MessageType.RESPONSE)
                        .setOperationType(MessageOperationType.SEND_CONVERSATION_MESSAGE)
                        .setSuccess(true)
                        .setSendConversationMessageResponse(SendConversationMessageResponse.newBuilder()
                                .setMessage(chatMessageBuilder.setMyMessage(true))
                        )
                        .build();
                session.sendMessage(new BinaryMessage(response.toByteArray()));

                // send notification to recipient if online
                RPCResponseEnvelope notification = RPCResponseEnvelope.newBuilder()
                        .setMessageType(MessageType.NOTIFICATION)
                        .setOperationType(MessageOperationType.SEND_CONVERSATION_MESSAGE)
                        .setReceiveConversationMessageNotification(ReceiveConversationMessageNotification.newBuilder()
                                .setMessage(chatMessageBuilder.setMyMessage(false))
                        )
                        .build();
                WebSocketSession participantSession = usernameSessionId
                        .get(conversation.getParticipantsList().stream()
                                .map(ChatUser::getUserId)
                                .filter(userId -> !userId.equals(currentUser))
                                .findFirst()
                                .orElseThrow(RuntimeException::new));
                participantSession.sendMessage(new BinaryMessage(notification.toByteArray()));

                updateConversation(conversation.getConversationId(), chatMessageBuilder.build());
            }
        }
    }

    private void updateConversation(String conversationId, ChatMessage chatMessage) {
        Conversation oldConversation = conversationIdConversation.get(conversationId);
        Conversation updatedConversation = Conversation.newBuilder(oldConversation)
                .setLastUpdatedTimestamp(chatMessage.getTimestamp())
                .setLastMessage(chatMessage)
                .build();
        conversationIdConversation.put(conversationId, updatedConversation);
        conversationIdChats.computeIfAbsent(conversationId, k -> new ArrayList<>()).add(chatMessage);

        updatedConversation.getParticipantsList().forEach(participant -> {
            String userId = participant.getUserId();
            List<Conversation> conversations = usernameConversations.get(userId);
            if (conversations != null) {
                conversations.removeIf(c -> c.getConversationId().equals(conversationId));
                conversations.addFirst(updatedConversation); // Move to top
            }
        });
    }

    public Map<String, String> getSessionIdUsername() {
        return sessionIdUsername;
    }
}
