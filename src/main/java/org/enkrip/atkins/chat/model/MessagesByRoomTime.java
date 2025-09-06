package org.enkrip.atkins.chat.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("messages_by_room_time")
@Schema(description = "Chat message stored by room and time for efficient retrieval")
public class MessagesByRoomTime {

    @PrimaryKey
    @Schema(description = "Composite primary key containing room ID, time bucket, message time, and message ID")
    private MessagesByRoomTimeKey key;

    @Column("user_id")
    @Schema(description = "Unique identifier of the user who sent the message", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Column("message_text")
    @Schema(description = "Content of the message", example = "Hello everyone! How are you doing today?")
    private String messageText;

    @Column("message_type")
    @Schema(description = "Type of message (1=TEXT, 2=IMAGE, 3=FILE, 4=SYSTEM)", example = "1")
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
    @Schema(description = "Room ID from the composite key", example = "123e4567-e89b-12d3-a456-426614174000")
    public UUID getRoomId() {
        return key != null ? key.getRoomId() : null;
    }

    @Schema(description = "Time bucket for partitioning (usually date in YYYY-MM-DD format)", example = "2023-12-01")
    public String getTimeBucket() {
        return key != null ? key.getTimeBucket() : null;
    }

    @Schema(description = "Timestamp when the message was sent", example = "2023-12-01T12:00:00Z")
    public Instant getMessageTime() {
        return key != null ? key.getMessageTime() : null;
    }

    @Schema(description = "Unique identifier of the message", example = "123e4567-e89b-12d3-a456-426614174000")
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
