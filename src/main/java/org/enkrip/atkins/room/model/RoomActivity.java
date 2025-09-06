package org.enkrip.atkins.room.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("room_activity")
@Schema(description = "Room activity record tracking user actions and presence in chat rooms")
public class RoomActivity {

    @PrimaryKey
    @Schema(description = "Composite primary key containing room ID, activity time, and user ID")
    private RoomActivityKey key;

    @Column("activity_type")
    @Schema(description = "Type of activity performed by the user", example = "typing", allowableValues = {"typing", "online", "joined", "left"})
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
    @Schema(description = "Room ID from the composite key", example = "123e4567-e89b-12d3-a456-426614174000")
    public UUID getRoomId() {
        return key != null ? key.getRoomId() : null;
    }

    @Schema(description = "Timestamp when the activity occurred", example = "2023-12-01T12:00:00Z")
    public Instant getActivityTime() {
        return key != null ? key.getActivityTime() : null;
    }

    @Schema(description = "User ID from the composite key", example = "123e4567-e89b-12d3-a456-426614174000")
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
