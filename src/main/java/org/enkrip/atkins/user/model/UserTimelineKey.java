package org.enkrip.atkins.user.model;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@PrimaryKeyClass
public class UserTimelineKey implements Serializable {

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID userId;

    @PrimaryKeyColumn(name = "message_time", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant messageTime;

    @PrimaryKeyColumn(name = "message_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private UUID messageId;

    // Constructors
    public UserTimelineKey() {}

    public UserTimelineKey(UUID userId, Instant messageTime, UUID messageId) {
        this.userId = userId;
        this.messageTime = messageTime;
        this.messageId = messageId;
    }

    // Getters and Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Instant getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Instant messageTime) {
        this.messageTime = messageTime;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTimelineKey that = (UserTimelineKey) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(messageTime, that.messageTime) &&
                Objects.equals(messageId, that.messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, messageTime, messageId);
    }

    @Override
    public String toString() {
        return "UserTimelineKey{" +
                "userId=" + userId +
                ", messageTime=" + messageTime +
                ", messageId=" + messageId +
                '}';
    }
}
