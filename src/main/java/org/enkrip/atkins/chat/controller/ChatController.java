package org.enkrip.atkins.chat.controller;

import org.enkrip.atkins.chat.model.MessagesByRoomTime;
import org.enkrip.atkins.chat.service.ChatService;
import org.enkrip.atkins.shared.enums.MessageType;
import org.enkrip.atkins.shared.service.ChatCoordinatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatCoordinatorService chatCoordinatorService;

    /**
     * Send a message to a room
     */
    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<MessagesByRoomTime> sendMessage(
            @PathVariable UUID roomId,
            @RequestParam UUID userId,
            @RequestParam String messageText,
            @RequestParam(defaultValue = "1") Integer messageTypeCode) {
        
        MessageType messageType = MessageType.fromCode(messageTypeCode);
        MessagesByRoomTime message = chatCoordinatorService.sendMessage(roomId, userId, messageText, messageType);
        return ResponseEntity.ok(message);
    }

    /**
     * Get recent messages from a room
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<MessagesByRoomTime>> getRoomMessages(
            @PathVariable UUID roomId,
            @RequestParam(defaultValue = "50") int limit) {
        
        List<MessagesByRoomTime> messages = chatService.getRecentRoomMessages(roomId, limit);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get messages from a specific time bucket
     */
    @GetMapping("/rooms/{roomId}/messages/bucket/{timeBucket}")
    public ResponseEntity<List<MessagesByRoomTime>> getMessagesFromBucket(
            @PathVariable UUID roomId,
            @PathVariable String timeBucket) {
        
        List<MessagesByRoomTime> messages = chatService.getMessagesFromTimeBucket(roomId, timeBucket);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get messages in a time range
     */
    @GetMapping("/rooms/{roomId}/messages/range")
    public ResponseEntity<List<MessagesByRoomTime>> getMessagesInRange(
            @PathVariable UUID roomId,
            @RequestParam String timeBucket,
            @RequestParam Instant startTime,
            @RequestParam Instant endTime) {
        
        List<MessagesByRoomTime> messages = chatService.getMessagesInTimeRange(roomId, timeBucket, startTime, endTime);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get messages by a specific user in a room
     */
    @GetMapping("/rooms/{roomId}/messages/user/{userId}")
    public ResponseEntity<List<MessagesByRoomTime>> getMessagesByUser(
            @PathVariable UUID roomId,
            @PathVariable UUID userId,
            @RequestParam String timeBucket) {
        
        List<MessagesByRoomTime> messages = chatService.getMessagesByUser(roomId, timeBucket, userId);
        return ResponseEntity.ok(messages);
    }
}
