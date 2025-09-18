package org.enkrip.atkins.shared.websocket.dto;

import java.time.Instant;
import java.util.List;

/**
 * DTO for Conversation data that can be serialized to JSON
 */
public class ConversationDto {
    private String conversationId;
    private ChatMessageDto lastMessage;
    private List<ChatUserDto> participants;
    private Instant lastUpdatedTimestamp;
    private int version;
    private int unreadCount;

    // Getters and Setters
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public ChatMessageDto getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(ChatMessageDto lastMessage) {
        this.lastMessage = lastMessage;
    }

    public List<ChatUserDto> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ChatUserDto> participants) {
        this.participants = participants;
    }

    public Instant getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    public void setLastUpdatedTimestamp(Instant lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
