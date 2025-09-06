package org.enkrip.atkins.user.controller;

import org.enkrip.atkins.user.model.UserTimeline;
import org.enkrip.atkins.user.service.UserTimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserTimelineService userTimelineService;

    /**
     * Get user's timeline across all rooms
     */
    @GetMapping("/{userId}/timeline")
    public ResponseEntity<List<UserTimeline>> getUserTimeline(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "20") int limit) {
        
        List<UserTimeline> timeline = userTimelineService.getUserTimeline(userId, limit);
        return ResponseEntity.ok(timeline);
    }

    /**
     * Get user's timeline within a time range
     */
    @GetMapping("/{userId}/timeline/range")
    public ResponseEntity<List<UserTimeline>> getUserTimelineInRange(
            @PathVariable UUID userId,
            @RequestParam Instant startTime,
            @RequestParam Instant endTime) {
        
        List<UserTimeline> timeline = userTimelineService.getUserTimelineInRange(userId, startTime, endTime);
        return ResponseEntity.ok(timeline);
    }

    /**
     * Get user's messages in a specific room
     */
    @GetMapping("/{userId}/timeline/room/{roomId}")
    public ResponseEntity<List<UserTimeline>> getUserMessagesInRoom(
            @PathVariable UUID userId,
            @PathVariable UUID roomId) {
        
        List<UserTimeline> timeline = userTimelineService.getUserMessagesInRoom(userId, roomId);
        return ResponseEntity.ok(timeline);
    }

    /**
     * Get user's timeline after a specific time
     */
    @GetMapping("/{userId}/timeline/after")
    public ResponseEntity<List<UserTimeline>> getUserTimelineAfterTime(
            @PathVariable UUID userId,
            @RequestParam Instant afterTime) {
        
        List<UserTimeline> timeline = userTimelineService.getUserTimelineAfterTime(userId, afterTime);
        return ResponseEntity.ok(timeline);
    }
}
