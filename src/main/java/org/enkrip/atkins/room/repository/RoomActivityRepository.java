package org.enkrip.atkins.room.repository;

import org.enkrip.atkins.room.model.RoomActivity;
import org.enkrip.atkins.room.model.RoomActivityKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoomActivityRepository extends CassandraRepository<RoomActivity, RoomActivityKey> {

    /**
     * Find recent activity in a room with limit
     */
    @Query("SELECT * FROM room_activity WHERE room_id = ?0 LIMIT ?1")
    List<RoomActivity> findByRoomIdWithLimit(UUID roomId, int limit);

    /**
     * Find activity in a room within a time range
     */
    @Query("SELECT * FROM room_activity WHERE room_id = ?0 AND activity_time >= ?1 AND activity_time <= ?2")
    List<RoomActivity> findByRoomIdAndTimeRange(UUID roomId, Instant startTime, Instant endTime);

    /**
     * Find activity in a room by activity type
     */
    @Query("SELECT * FROM room_activity WHERE room_id = ?0 AND activity_type = ?1 ALLOW FILTERING")
    List<RoomActivity> findByRoomIdAndActivityType(UUID roomId, String activityType);

    /**
     * Find activity for a specific user in a room
     */
    @Query("SELECT * FROM room_activity WHERE room_id = ?0 AND user_id = ?1 ALLOW FILTERING")
    List<RoomActivity> findByRoomIdAndUserId(UUID roomId, UUID userId);

    /**
     * Find recent activity in a room after a specific time
     */
    @Query("SELECT * FROM room_activity WHERE room_id = ?0 AND activity_time >= ?1")
    List<RoomActivity> findByRoomIdAfterTime(UUID roomId, Instant afterTime);
}
