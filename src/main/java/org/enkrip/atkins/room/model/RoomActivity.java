package org.enkrip.atkins.room.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("room_activity")
public class RoomActivity {

    @PrimaryKey
    private RoomActivityKey key;

    @Column("activity_type")
    private String activityType;

    // Constructors
    public RoomActivity() {}

    public RoomActivity(RoomActivityKey key, String activityType) {
        this.key = key;
        this.activityType = activityType;
    }

    // Getters and Setters
    public RoomActivityKey getKey() {
        return key;
    }

    public void setKey(RoomActivityKey key) {
        this.key = key;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    // Convenience methods for accessing key components
    public UUID getRoomId() {
        return key != null ? key.getRoomId() : null;
    }

    public Instant getActivityTime() {
        return key != null ? key.getActivityTime() : null;
    }

    public UUID getUserId() {
        return key != null ? key.getUserId() : null;
    }

    @Override
    public String toString() {
        return "RoomActivity{" +
                "key=" + key +
                ", activityType='" + activityType + '\'' +
                '}';
    }
}
