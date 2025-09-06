package org.enkrip.atkins.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("user_timeline")
@Schema(description = "User timeline entry representing a message in the user's activity timeline")
public class UserTimeline {

    @PrimaryKey
    @Schema(description = "Composite primary key containing user ID, message time, and message ID")
    private UserTimelineKey key;

    @Column("room_id")
    @Schema(description = "Unique identifier of the room where the message was sent", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID roomId;

    @Column("message_preview")
    @Schema(description = "Preview/snippet of the message content", example = "Hello everyone! How are you doing today?")
    private String messagePreview;

    // Constructors
    public UserTimeline() {}

    public UserTimeline(UserTimelineKey key, UUID roomId, String messagePreview) {
        this.key = key;
        this.roomId = roomId;
        this.messagePreview = messagePreview;
    }

    // Getters and Setters
    public UserTimelineKey getKey() {
        return key;
    }

    public void setKey(UserTimelineKey key) {
        this.key = key;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public String getMessagePreview() {
        return messagePreview;
    }

    public void setMessagePreview(String messagePreview) {
        this.messagePreview = messagePreview;
    }

    // Convenience methods for accessing key components
    @Schema(description = "User ID from the composite key", example = "123e4567-e89b-12d3-a456-426614174000")
    public UUID getUserId() {
        return key != null ? key.getUserId() : null;
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
        return "UserTimeline{" +
                "key=" + key +
                ", roomId=" + roomId +
                ", messagePreview='" + messagePreview + '\'' +
                '}';
    }
}
