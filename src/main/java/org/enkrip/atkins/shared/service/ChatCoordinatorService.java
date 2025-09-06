package org.enkrip.atkins.shared.service;

import org.enkrip.atkins.chat.model.MessagesByRoomTime;
import org.enkrip.atkins.chat.service.ChatService;
import org.enkrip.atkins.room.service.RoomActivityService;
import org.enkrip.atkins.shared.enums.ActivityType;
import org.enkrip.atkins.shared.enums.MessageType;
import org.enkrip.atkins.user.service.UserTimelineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Coordinating service that orchestrates operations across multiple features
 */
@Service
public class ChatCoordinatorService {

    private final ChatService chatService;
    private final UserTimelineService userTimelineService;
    private final RoomActivityService roomActivityService;

    public ChatCoordinatorService(ChatService chatService, 
                                UserTimelineService userTimelineService,
                                RoomActivityService roomActivityService) {
        this.chatService = chatService;
        this.userTimelineService = userTimelineService;
        this.roomActivityService = roomActivityService;
    }

    /**
     * Send a message and update all related tables
     */
    @Transactional
    public MessagesByRoomTime sendMessage(UUID roomId, UUID userId, String messageText, MessageType messageType) {
        // Save the main message
        MessagesByRoomTime message = chatService.saveMessage(roomId, userId, messageText, messageType.getCode());

        // Add to user timeline
        String messagePreview = messageText.length() > 100 ? 
            messageText.substring(0, 100) + "..." : messageText;
        
        userTimelineService.addToTimeline(
            userId, 
            roomId, 
            message.getMessageId(), 
            message.getMessageTime(), 
            messagePreview
        );

        // Record message activity
        roomActivityService.recordActivity(roomId, userId, ActivityType.MESSAGE.getValue());

        return message;
    }

    /**
     * Record user typing activity
     */
    public void recordTyping(UUID roomId, UUID userId) {
        roomActivityService.recordActivity(roomId, userId, ActivityType.TYPING.getValue());
    }

    /**
     * Record user online activity
     */
    public void recordUserOnline(UUID roomId, UUID userId) {
        roomActivityService.recordActivity(roomId, userId, ActivityType.ONLINE.getValue());
    }

    /**
     * Record user offline activity
     */
    public void recordUserOffline(UUID roomId, UUID userId) {
        roomActivityService.recordActivity(roomId, userId, ActivityType.OFFLINE.getValue());
    }

    /**
     * Record user joining a room
     */
    public void recordUserJoined(UUID roomId, UUID userId) {
        roomActivityService.recordActivity(roomId, userId, ActivityType.JOINED.getValue());
    }

    /**
     * Record user leaving a room
     */
    public void recordUserLeft(UUID roomId, UUID userId) {
        roomActivityService.recordActivity(roomId, userId, ActivityType.LEFT.getValue());
    }

    /**
     * Record message read activity
     */
    public void recordMessageRead(UUID roomId, UUID userId) {
        roomActivityService.recordActivity(roomId, userId, ActivityType.MESSAGE_READ.getValue());
    }
}
