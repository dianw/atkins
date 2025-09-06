package org.enkrip.atkins.user.service;

import org.enkrip.atkins.user.model.UserTimeline;
import org.enkrip.atkins.user.model.UserTimelineKey;
import org.enkrip.atkins.user.repository.UserTimelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class UserTimelineService {

    @Autowired
    private UserTimelineRepository userTimelineRepository;

    /**
     * Add a message to user's timeline
     */
    public UserTimeline addToTimeline(UUID userId, UUID roomId, UUID messageId, Instant messageTime, String messagePreview) {
        UserTimelineKey timelineKey = new UserTimelineKey(userId, messageTime, messageId);
        UserTimeline timeline = new UserTimeline(timelineKey, roomId, messagePreview);
        
        return userTimelineRepository.save(timeline);
    }

    /**
     * Get user's timeline with limit
     */
    public List<UserTimeline> getUserTimeline(UUID userId, int limit) {
        return userTimelineRepository.findByUserIdWithLimit(userId, limit);
    }

    /**
     * Get user's timeline within a time range
     */
    public List<UserTimeline> getUserTimelineInRange(UUID userId, Instant startTime, Instant endTime) {
        return userTimelineRepository.findByUserIdAndTimeRange(userId, startTime, endTime);
    }

    /**
     * Get user's messages in a specific room
     */
    public List<UserTimeline> getUserMessagesInRoom(UUID userId, UUID roomId) {
        return userTimelineRepository.findByUserIdAndRoomId(userId, roomId);
    }

    /**
     * Get user's timeline after a specific time
     */
    public List<UserTimeline> getUserTimelineAfterTime(UUID userId, Instant afterTime) {
        return userTimelineRepository.findByUserIdAfterTime(userId, afterTime);
    }
}
