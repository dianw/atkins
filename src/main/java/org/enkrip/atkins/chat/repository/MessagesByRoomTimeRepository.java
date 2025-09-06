package org.enkrip.atkins.chat.repository;

import org.enkrip.atkins.chat.model.MessagesByRoomTime;
import org.enkrip.atkins.chat.model.MessagesByRoomTimeKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessagesByRoomTimeRepository extends CassandraRepository<MessagesByRoomTime, MessagesByRoomTimeKey> {

    /**
     * Find messages by room ID and time bucket
     */
    @Query("SELECT * FROM messages_by_room_time WHERE room_id = ?0 AND time_bucket = ?1")
    List<MessagesByRoomTime> findByRoomIdAndTimeBucket(UUID roomId, String timeBucket);

    /**
     * Find messages by room ID and time bucket with a limit
     */
    @Query("SELECT * FROM messages_by_room_time WHERE room_id = ?0 AND time_bucket = ?1 LIMIT ?2")
    List<MessagesByRoomTime> findByRoomIdAndTimeBucketWithLimit(UUID roomId, String timeBucket, int limit);

    /**
     * Find messages by room ID, time bucket and time range
     */
    @Query("SELECT * FROM messages_by_room_time WHERE room_id = ?0 AND time_bucket = ?1 AND message_time >= ?2 AND message_time <= ?3")
    List<MessagesByRoomTime> findByRoomIdAndTimeBucketAndTimeRange(
        UUID roomId, 
        String timeBucket, 
        Instant startTime, 
        Instant endTime
    );

    /**
     * Find messages by room ID, time bucket and user ID
     */
    @Query("SELECT * FROM messages_by_room_time WHERE room_id = ?0 AND time_bucket = ?1 AND user_id = ?2 ALLOW FILTERING")
    List<MessagesByRoomTime> findByRoomIdAndTimeBucketAndUserId(UUID roomId, String timeBucket, UUID userId);
}
