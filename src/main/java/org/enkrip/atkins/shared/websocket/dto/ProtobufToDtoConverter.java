package org.enkrip.atkins.shared.websocket.dto;

import org.enkrip.atkins.proto.ChatMessage;
import org.enkrip.atkins.proto.ChatUser;
import org.enkrip.atkins.proto.Conversation;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to convert protobuf objects to DTOs for JSON serialization
 */
public class ProtobufToDtoConverter {

    /**
     * Convert protobuf Conversation to ConversationDto
     */
    public static ConversationDto toDto(Conversation conversation) {
        if (conversation == null) {
            return null;
        }

        ChatMessageDto lastMessageDto = null;
        if (conversation.hasLastMessage()) {
            lastMessageDto = toDto(conversation.getLastMessage());
        }

        List<ChatUserDto> participantsDto = conversation.getParticipantsList()
                .stream()
                .map(ProtobufToDtoConverter::toDto)
                .collect(Collectors.toList());

        Instant lastUpdatedTimestamp = null;
        if (conversation.hasLastUpdatedTimestamp()) {
            lastUpdatedTimestamp = Instant.ofEpochSecond(
                    conversation.getLastUpdatedTimestamp().getSeconds(),
                    conversation.getLastUpdatedTimestamp().getNanos()
            );
        }

        return new ConversationDto(
                conversation.getConversationId(),
                lastMessageDto,
                participantsDto,
                lastUpdatedTimestamp,
                conversation.getVersion(),
                conversation.getUnreadCount()
        );
    }

    /**
     * Convert protobuf ChatMessage to ChatMessageDto
     */
    public static ChatMessageDto toDto(ChatMessage chatMessage) {
        if (chatMessage == null) {
            return null;
        }

        ChatUserDto senderDto = null;
        if (chatMessage.hasSender()) {
            senderDto = toDto(chatMessage.getSender());
        }

        Instant timestamp = null;
        if (chatMessage.hasTimestamp()) {
            timestamp = Instant.ofEpochSecond(
                    chatMessage.getTimestamp().getSeconds(),
                    chatMessage.getTimestamp().getNanos()
            );
        }

        String conversationId = chatMessage.hasConversationId() ? chatMessage.getConversationId() : null;

        return new ChatMessageDto(
                chatMessage.getMessageId(),
                conversationId,
                senderDto,
                timestamp,
                chatMessage.getMessageType().name(),
                chatMessage.getContent(),
                chatMessage.getMyMessage(),
                chatMessage.getRead(),
                chatMessage.getVersion()
        );
    }

    /**
     * Convert protobuf ChatUser to ChatUserDto
     */
    public static ChatUserDto toDto(ChatUser chatUser) {
        if (chatUser == null) {
            return null;
        }

        String displayName = chatUser.hasDisplayName() ? chatUser.getDisplayName() : null;
        String avatarUrl = chatUser.hasAvatarUrl() ? chatUser.getAvatarUrl() : null;

        return new ChatUserDto(
                chatUser.getUserId(),
                chatUser.getUsername(),
                displayName,
                avatarUrl
        );
    }
}
