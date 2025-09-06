package org.enkrip.atkins.chat.service;

import org.enkrip.atkins.chat.model.MessagesByRoomTime;
import org.enkrip.atkins.chat.model.MessagesByRoomTimeKey;
import org.enkrip.atkins.chat.repository.MessagesByRoomTimeRepository;
import org.enkrip.atkins.shared.util.TimeBucketUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final MessagesByRoomTimeRepository messagesByRoomTimeRepository;

    public ChatService(MessagesByRoomTimeRepository messagesByRoomTimeRepository) {
        this.messagesByRoomTimeRepository = messagesByRoomTimeRepository;
    }

    /**
     * Save a new message
     */
    public MessagesByRoomTime saveMessage(UUID roomId, UUID userId, String messageText, Integer messageType) {
        Instant now = Instant.now();
        UUID messageId = UUID.randomUUID();
        String timeBucket = TimeBucketUtil.generateTimeBucket(now);

        MessagesByRoomTimeKey messageKey = new MessagesByRoomTimeKey(roomId, timeBucket, now, messageId);
        MessagesByRoomTime message = new MessagesByRoomTime(messageKey, userId, messageText, messageType);
        
        return messagesByRoomTimeRepository.save(message);
    }

    /**
     * Get recent messages from a room
     */
    public List<MessagesByRoomTime> getRecentRoomMessages(UUID roomId, int limit) {
        String currentTimeBucket = TimeBucketUtil.generateCurrentTimeBucket();
        return messagesByRoomTimeRepository.findByRoomIdAndTimeBucketWithLimit(roomId, currentTimeBucket, limit);
    }

    /**
     * Get messages from a specific time bucket
     */
    public List<MessagesByRoomTime> getMessagesFromTimeBucket(UUID roomId, String timeBucket) {
        return messagesByRoomTimeRepository.findByRoomIdAndTimeBucket(roomId, timeBucket);
    }

    /**
     * Get messages from a room within a time range
     */
    public List<MessagesByRoomTime> getMessagesInTimeRange(UUID roomId, String timeBucket, Instant startTime, Instant endTime) {
        return messagesByRoomTimeRepository.findByRoomIdAndTimeBucketAndTimeRange(roomId, timeBucket, startTime, endTime);
    }

    /**
     * Get messages from a specific user in a room
     */
    public List<MessagesByRoomTime> getMessagesByUser(UUID roomId, String timeBucket, UUID userId) {
        return messagesByRoomTimeRepository.findByRoomIdAndTimeBucketAndUserId(roomId, timeBucket, userId);
    }
}
