package org.enkrip.atkins.user.repository;

import org.enkrip.atkins.user.model.UserTimeline;
import org.enkrip.atkins.user.model.UserTimelineKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserTimelineRepository extends CassandraRepository<UserTimeline, UserTimelineKey> {

    /**
     * Find recent messages for a user with limit
     */
    @Query("SELECT * FROM user_timeline WHERE user_id = ?0 LIMIT ?1")
    List<UserTimeline> findByUserIdWithLimit(UUID userId, int limit);

    /**
     * Find messages for a user within a time range
     */
    @Query("SELECT * FROM user_timeline WHERE user_id = ?0 AND message_time >= ?1 AND message_time <= ?2")
    List<UserTimeline> findByUserIdAndTimeRange(UUID userId, Instant startTime, Instant endTime);

    /**
     * Find messages for a user in a specific room
     */
    @Query("SELECT * FROM user_timeline WHERE user_id = ?0 AND room_id = ?1 ALLOW FILTERING")
    List<UserTimeline> findByUserIdAndRoomId(UUID userId, UUID roomId);

    /**
     * Find messages for a user after a specific time
     */
    @Query("SELECT * FROM user_timeline WHERE user_id = ?0 AND message_time >= ?1")
    List<UserTimeline> findByUserIdAfterTime(UUID userId, Instant afterTime);
}
