package org.enkrip.atkins.chat.model;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@PrimaryKeyClass
public class MessagesByRoomTimeKey implements Serializable {

    @PrimaryKeyColumn(name = "room_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID roomId;

    @PrimaryKeyColumn(name = "time_bucket", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String timeBucket;

    @PrimaryKeyColumn(name = "message_time", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant messageTime;

    @PrimaryKeyColumn(name = "message_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    private UUID messageId;

    // Constructors
    public MessagesByRoomTimeKey() {}

    public MessagesByRoomTimeKey(UUID roomId, String timeBucket, Instant messageTime, UUID messageId) {
        this.roomId = roomId;
        this.timeBucket = timeBucket;
        this.messageTime = messageTime;
        this.messageId = messageId;
    }

    // Getters and Setters
    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public String getTimeBucket() {
        return timeBucket;
    }

    public void setTimeBucket(String timeBucket) {
        this.timeBucket = timeBucket;
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
        MessagesByRoomTimeKey that = (MessagesByRoomTimeKey) o;
        return Objects.equals(roomId, that.roomId) &&
                Objects.equals(timeBucket, that.timeBucket) &&
                Objects.equals(messageTime, that.messageTime) &&
                Objects.equals(messageId, that.messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, timeBucket, messageTime, messageId);
    }

    @Override
    public String toString() {
        return "MessagesByRoomTimeKey{" +
                "roomId=" + roomId +
                ", timeBucket='" + timeBucket + '\'' +
                ", messageTime=" + messageTime +
                ", messageId=" + messageId +
                '}';
    }
}
