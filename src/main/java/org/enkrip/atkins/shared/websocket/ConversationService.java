/**
 * DANA Indonesia
 * Copyright (c) 2008‐2025 All Rights Reserved.
 */
package org.enkrip.atkins.shared.websocket;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import org.enkrip.atkins.proto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Dian Aditya
 * @version $Id: ConversationService.java, v 0.1 2025‐09‐16 22.32 Dian Aditya Exp $$
 */
@Service
public class ConversationService {
    private final WebSocketSessionService webSocketSessionService;

    // Maps conversationId to Conversation
    private final Map<String, Conversation> conversationIdConversation = new ConcurrentHashMap<>();

    // Maps userId to their conversations
    private final Map<String, Set<Conversation>> userConversations = new ConcurrentHashMap<>();

    // Maps conversationId to its messages
    private final Map<String, Set<ChatMessage>> conversationMessages = new ConcurrentHashMap<>();

    private final Comparator<Conversation> conversationDateComparator = (o1, o2) ->
            Timestamps.compare(o1.getLastUpdatedTimestamp(), o2.getLastUpdatedTimestamp());
    private final Comparator<ChatMessage> messageDateComparator = (o1, o2) ->
            Timestamps.compare(o1.getTimestamp(), o2.getTimestamp());

    public ConversationService(WebSocketSessionService webSocketSessionService) {
        this.webSocketSessionService = webSocketSessionService;
    }

    public void startConversation(WebSocketSession session, RPCRequestEnvelope request) {
        final String currentUser = webSocketSessionService.getCurrentUsername(session);

        StartConversationRequest startConversationRequest = request.getStartConversationRequest();
        String participant = startConversationRequest.getParticipantUserIdsList().getFirst();

        if (currentUser.equals(participant)) {
            RPCResponseEnvelope response = RPCResponseEnvelope.newBuilder()
                    .setMessageType(MessageType.RESPONSE)
                    .setOperationType(MessageOperationType.START_CONVERSATION)
                    .setSuccess(false)
                    .setErrorMessage("Cannot start conversation with yourself")
                    .build();

            webSocketSessionService.sendMessage(currentUser, new BinaryMessage(response.toByteArray()));
            return;
        }

        Set<String> activeUsers = webSocketSessionService.getAllActiveUsernames();
        if (!activeUsers.contains(participant)) {
            RPCResponseEnvelope response = RPCResponseEnvelope.newBuilder()
                    .setMessageType(MessageType.RESPONSE)
                    .setOperationType(MessageOperationType.START_CONVERSATION)
                    .setSuccess(false)
                    .setErrorMessage("Participant not online")
                    .build();
            webSocketSessionService.sendMessage(currentUser, new BinaryMessage(response.toByteArray()));
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
            userConversations.computeIfAbsent(user, k -> new TreeSet<>(conversationDateComparator))
                    .add(conversation);
        });
        conversationMessages.putIfAbsent(conversationId, new TreeSet<>(messageDateComparator));

        webSocketSessionService.sendMessage(currentUser, new BinaryMessage(response.toByteArray()));
    }

    public void sendConversationMessage(WebSocketSession session, RPCRequestEnvelope request) {
        final String currentUser = webSocketSessionService.getCurrentUsername(session);

        SendConversationMessageRequest sendRequest = request.getSendConversationMessageRequest();

        if (!conversationIdConversation.containsKey(sendRequest.getConversationId())) {
            RPCResponseEnvelope response = RPCResponseEnvelope.newBuilder()
                    .setMessageType(MessageType.RESPONSE)
                    .setOperationType(MessageOperationType.SEND_CONVERSATION_MESSAGE)
                    .setSuccess(false)
                    .setErrorMessage("Conversation does not exist")
                    .build();
            webSocketSessionService.sendMessage(currentUser, new BinaryMessage(response.toByteArray()));
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
            webSocketSessionService.sendMessage(currentUser, new BinaryMessage(response.toByteArray()));
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

        // send notification to recipient if online
        String recipient = conversation.getParticipantsList().stream()
                .map(ChatUser::getUserId)
                .filter(userId -> !userId.equals(currentUser))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        RPCResponseEnvelope notification = RPCResponseEnvelope.newBuilder()
                .setMessageType(MessageType.NOTIFICATION)
                .setOperationType(MessageOperationType.RECEIVE_CONVERSATION_MESSAGE)
                .setReceiveConversationMessageNotification(ReceiveConversationMessageNotification.newBuilder()
                        .setMessage(chatMessageBuilder.setMyMessage(false))
                )
                .build();
        int result = webSocketSessionService.sendMessage(recipient, new BinaryMessage(notification.toByteArray()));

        // send response to sender
        RPCResponseEnvelope response = RPCResponseEnvelope.newBuilder()
                .setMessageType(MessageType.RESPONSE)
                .setOperationType(MessageOperationType.SEND_CONVERSATION_MESSAGE)
                .setSuccess(result > 0)
                .setSendConversationMessageResponse(SendConversationMessageResponse.newBuilder()
                        .setMessage(chatMessageBuilder.setMyMessage(true))
                )
                .build();
        webSocketSessionService.sendMessage(currentUser, new BinaryMessage(response.toByteArray()));

        updateConversation(conversation.getConversationId(), chatMessageBuilder.build());
    }

    private void updateConversation(String conversationId, ChatMessage chatMessage) {
        Conversation oldConversation = conversationIdConversation.get(conversationId);
        Conversation updatedConversation = Conversation.newBuilder(oldConversation)
                .setLastUpdatedTimestamp(chatMessage.getTimestamp())
                .setLastMessage(chatMessage)
                .build();
        conversationIdConversation.put(conversationId, updatedConversation);
        conversationMessages.computeIfAbsent(conversationId, k -> new TreeSet<>(messageDateComparator)).add(chatMessage);

        updatedConversation.getParticipantsList().forEach(participant -> {
            String userId = participant.getUserId();
            Set<Conversation> conversations = userConversations.get(userId);
            if (conversations != null) {
                conversations.removeIf(c -> c.getConversationId().equals(conversationId));
                conversations.add(updatedConversation); // Move to top
            }
        });
    }

    public void getListOfConversations(WebSocketSession session, RPCRequestEnvelope request) {
        final String currentUser = webSocketSessionService.getCurrentUsername(session);
        
        GetListOfConversationsRequest getRequest = request.getGetListOfConversationsRequest();
        Set<Conversation> userConversations = getUserConversations(currentUser);

        GetListOfConversationsResponse getResponse = GetListOfConversationsResponse.newBuilder()
                .addAllConversations(userConversations)
                .setHasMore(false) // For simplicity, we'll assume no pagination for now
                .build();
        
        RPCResponseEnvelope response = RPCResponseEnvelope.newBuilder()
                .setRequestId(request.getRequestId())
                .setMessageType(MessageType.RESPONSE)
                .setOperationType(MessageOperationType.GET_LIST_OF_CONVERSATIONS)
                .setSuccess(true)
                .setGetListOfConversationsResponse(getResponse)
                .build();
        
        webSocketSessionService.sendMessage(currentUser, new BinaryMessage(response.toByteArray()));
    }

    public Set<Conversation> getUserConversations(String username) {
        if (username == null) {
            return Set.of();
        }
        return userConversations.getOrDefault(username, Set.of());
    }

}
