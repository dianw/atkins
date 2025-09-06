package org.enkrip.atkins.room.service;

import org.enkrip.atkins.room.model.RoomActivity;
import org.enkrip.atkins.room.model.RoomActivityKey;
import org.enkrip.atkins.room.repository.RoomActivityRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class RoomActivityService {

    private final RoomActivityRepository roomActivityRepository;

    public RoomActivityService(RoomActivityRepository roomActivityRepository) {
        this.roomActivityRepository = roomActivityRepository;
    }

    /**
     * Record user activity in a room
     */
    public RoomActivity recordActivity(UUID roomId, UUID userId, String activityType) {
        Instant now = Instant.now();
        RoomActivityKey activityKey = new RoomActivityKey(roomId, now, userId);
        RoomActivity activity = new RoomActivity(activityKey, activityType);
        
        return roomActivityRepository.save(activity);
    }

    /**
     * Get recent activity in a room
     */
    public List<RoomActivity> getRecentRoomActivity(UUID roomId, int limit) {
        return roomActivityRepository.findByRoomIdWithLimit(roomId, limit);
    }

    /**
     * Get activity in a room within a time range
     */
    public List<RoomActivity> getRoomActivityInRange(UUID roomId, Instant startTime, Instant endTime) {
        return roomActivityRepository.findByRoomIdAndTimeRange(roomId, startTime, endTime);
    }

    /**
     * Get activity of a specific type in a room
     */
    public List<RoomActivity> getRoomActivityByType(UUID roomId, String activityType) {
        return roomActivityRepository.findByRoomIdAndActivityType(roomId, activityType);
    }

    /**
     * Get activity for a specific user in a room
     */
    public List<RoomActivity> getRoomActivityByUser(UUID roomId, UUID userId) {
        return roomActivityRepository.findByRoomIdAndUserId(roomId, userId);
    }

    /**
     * Get recent activity in a room after a specific time
     */
    public List<RoomActivity> getRoomActivityAfterTime(UUID roomId, Instant afterTime) {
        return roomActivityRepository.findByRoomIdAfterTime(roomId, afterTime);
    }
}
