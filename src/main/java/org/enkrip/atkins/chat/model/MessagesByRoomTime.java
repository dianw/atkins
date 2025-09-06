package org.enkrip.atkins.chat.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("messages_by_room_time")
public class MessagesByRoomTime {

    @PrimaryKey
    private MessagesByRoomTimeKey key;

    @Column("user_id")
    private UUID userId;

    @Column("message_text")
    private String messageText;

    @Column("message_type")
    private Integer messageType;

    // Constructors
    public MessagesByRoomTime() {}

    public MessagesByRoomTime(MessagesByRoomTimeKey key, UUID userId, String messageText, Integer messageType) {
        this.key = key;
        this.userId = userId;
        this.messageText = messageText;
        this.messageType = messageType;
    }

    // Getters and Setters
    public MessagesByRoomTimeKey getKey() {
        return key;
    }

    public void setKey(MessagesByRoomTimeKey key) {
        this.key = key;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    // Convenience methods for accessing key components
    public UUID getRoomId() {
        return key != null ? key.getRoomId() : null;
    }

    public String getTimeBucket() {
        return key != null ? key.getTimeBucket() : null;
    }

    public Instant getMessageTime() {
        return key != null ? key.getMessageTime() : null;
    }

    public UUID getMessageId() {
        return key != null ? key.getMessageId() : null;
    }

    @Override
    public String toString() {
        return "MessagesByRoomTime{" +
                "key=" + key +
                ", userId=" + userId +
                ", messageText='" + messageText + '\'' +
                ", messageType=" + messageType +
                '}';
    }
}
