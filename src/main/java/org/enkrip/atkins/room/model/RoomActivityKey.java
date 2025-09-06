package org.enkrip.atkins.room.model;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@PrimaryKeyClass
public class RoomActivityKey implements Serializable {

    @PrimaryKeyColumn(name = "room_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID roomId;

    @PrimaryKeyColumn(name = "activity_time", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant activityTime;

    @PrimaryKeyColumn(name = "user_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private UUID userId;

    // Constructors
    public RoomActivityKey() {}

    public RoomActivityKey(UUID roomId, Instant activityTime, UUID userId) {
        this.roomId = roomId;
        this.activityTime = activityTime;
        this.userId = userId;
    }

    // Getters and Setters
    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public Instant getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(Instant activityTime) {
        this.activityTime = activityTime;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomActivityKey that = (RoomActivityKey) o;
        return Objects.equals(roomId, that.roomId) &&
                Objects.equals(activityTime, that.activityTime) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, activityTime, userId);
    }

    @Override
    public String toString() {
        return "RoomActivityKey{" +
                "roomId=" + roomId +
                ", activityTime=" + activityTime +
                ", userId=" + userId +
                '}';
    }
}
