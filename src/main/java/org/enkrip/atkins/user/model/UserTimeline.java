package org.enkrip.atkins.user.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("user_timeline")
public class UserTimeline {

    @PrimaryKey
    private UserTimelineKey key;

    @Column("room_id")
    private UUID roomId;

    @Column("message_preview")
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
    public UUID getUserId() {
        return key != null ? key.getUserId() : null;
    }

    public Instant getMessageTime() {
        return key != null ? key.getMessageTime() : null;
    }

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
