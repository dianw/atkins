package org.enkrip.atkins.room.controller;

import org.enkrip.atkins.room.model.RoomActivity;
import org.enkrip.atkins.room.service.RoomActivityService;
import org.enkrip.atkins.shared.enums.ActivityType;
import org.enkrip.atkins.shared.service.ChatCoordinatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomActivityService roomActivityService;

    @Autowired
    private ChatCoordinatorService chatCoordinatorService;

    /**
     * Record user activity (typing, online, etc.)
     */
    @PostMapping("/{roomId}/activity")
    public ResponseEntity<String> recordActivity(
            @PathVariable UUID roomId,
            @RequestParam UUID userId,
            @RequestParam String activityType) {
        
        // Validate activity type
        if (!ActivityType.isValidValue(activityType)) {
            return ResponseEntity.badRequest().body("Invalid activity type: " + activityType);
        }

        roomActivityService.recordActivity(roomId, userId, activityType);
        return ResponseEntity.ok("Activity recorded");
    }

    /**
     * Record user typing
     */
    @PostMapping("/{roomId}/typing")
    public ResponseEntity<String> recordTyping(
            @PathVariable UUID roomId,
            @RequestParam UUID userId) {
        
        chatCoordinatorService.recordTyping(roomId, userId);
        return ResponseEntity.ok("Typing activity recorded");
    }

    /**
     * Record user online status
     */
    @PostMapping("/{roomId}/online")
    public ResponseEntity<String> recordUserOnline(
            @PathVariable UUID roomId,
            @RequestParam UUID userId) {
        
        chatCoordinatorService.recordUserOnline(roomId, userId);
        return ResponseEntity.ok("User online status recorded");
    }

    /**
     * Record user joining room
     */
    @PostMapping("/{roomId}/join")
    public ResponseEntity<String> recordUserJoined(
            @PathVariable UUID roomId,
            @RequestParam UUID userId) {
        
        chatCoordinatorService.recordUserJoined(roomId, userId);
        return ResponseEntity.ok("User joined activity recorded");
    }

    /**
     * Get recent activity in a room
     */
    @GetMapping("/{roomId}/activity")
    public ResponseEntity<List<RoomActivity>> getRoomActivity(
            @PathVariable UUID roomId,
            @RequestParam(defaultValue = "20") int limit) {
        
        List<RoomActivity> activities = roomActivityService.getRecentRoomActivity(roomId, limit);
        return ResponseEntity.ok(activities);
    }

    /**
     * Get activity in a room within a time range
     */
    @GetMapping("/{roomId}/activity/range")
    public ResponseEntity<List<RoomActivity>> getRoomActivityInRange(
            @PathVariable UUID roomId,
            @RequestParam Instant startTime,
            @RequestParam Instant endTime) {
        
        List<RoomActivity> activities = roomActivityService.getRoomActivityInRange(roomId, startTime, endTime);
        return ResponseEntity.ok(activities);
    }

    /**
     * Get specific type of activity in a room
     */
    @GetMapping("/{roomId}/activity/{activityType}")
    public ResponseEntity<List<RoomActivity>> getRoomActivityByType(
            @PathVariable UUID roomId,
            @PathVariable String activityType) {
        
        if (!ActivityType.isValidValue(activityType)) {
            return ResponseEntity.badRequest().body(null);
        }

        List<RoomActivity> activities = roomActivityService.getRoomActivityByType(roomId, activityType);
        return ResponseEntity.ok(activities);
    }

    /**
     * Get activity for a specific user in a room
     */
    @GetMapping("/{roomId}/activity/user/{userId}")
    public ResponseEntity<List<RoomActivity>> getRoomActivityByUser(
            @PathVariable UUID roomId,
            @PathVariable UUID userId) {
        
        List<RoomActivity> activities = roomActivityService.getRoomActivityByUser(roomId, userId);
        return ResponseEntity.ok(activities);
    }
}
