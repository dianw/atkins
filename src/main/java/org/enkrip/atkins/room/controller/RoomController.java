package org.enkrip.atkins.room.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Room Management", description = "APIs for managing room activities and user presence")
public class RoomController {

    @Autowired
    private RoomActivityService roomActivityService;

    @Autowired
    private ChatCoordinatorService chatCoordinatorService;

    @Operation(
            summary = "Record user activity",
            description = "Records various user activities in a room such as typing, online status, etc."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity recorded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid activity type or parameters"),
            @ApiResponse(responseCode = "404", description = "Room or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{roomId}/activity")
    public ResponseEntity<String> recordActivity(
            @Parameter(description = "Unique identifier of the room", required = true)
            @PathVariable UUID roomId,
            @Parameter(description = "Unique identifier of the user", required = true)
            @RequestParam UUID userId,
            @Parameter(description = "Type of activity (typing, online, joined, left)", required = true, example = "typing")
            @RequestParam String activityType) {
        
        // Validate activity type
        if (!ActivityType.isValidValue(activityType)) {
            return ResponseEntity.badRequest().body("Invalid activity type: " + activityType);
        }

        roomActivityService.recordActivity(roomId, userId, activityType);
        return ResponseEntity.ok("Activity recorded");
    }

    @Operation(
            summary = "Record user typing",
            description = "Records that a user is currently typing in a room"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Typing activity recorded successfully"),
            @ApiResponse(responseCode = "404", description = "Room or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{roomId}/typing")
    public ResponseEntity<String> recordTyping(
            @Parameter(description = "Unique identifier of the room", required = true)
            @PathVariable UUID roomId,
            @Parameter(description = "Unique identifier of the user", required = true)
            @RequestParam UUID userId) {
        
        chatCoordinatorService.recordTyping(roomId, userId);
        return ResponseEntity.ok("Typing activity recorded");
    }

    @Operation(
            summary = "Record user online status",
            description = "Records that a user is currently online in a room"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User online status recorded successfully"),
            @ApiResponse(responseCode = "404", description = "Room or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{roomId}/online")
    public ResponseEntity<String> recordUserOnline(
            @Parameter(description = "Unique identifier of the room", required = true)
            @PathVariable UUID roomId,
            @Parameter(description = "Unique identifier of the user", required = true)
            @RequestParam UUID userId) {
        
        chatCoordinatorService.recordUserOnline(roomId, userId);
        return ResponseEntity.ok("User online status recorded");
    }

    @Operation(
            summary = "Record user joining room",
            description = "Records that a user has joined a room"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User joined activity recorded successfully"),
            @ApiResponse(responseCode = "404", description = "Room or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{roomId}/join")
    public ResponseEntity<String> recordUserJoined(
            @Parameter(description = "Unique identifier of the room", required = true)
            @PathVariable UUID roomId,
            @Parameter(description = "Unique identifier of the user", required = true)
            @RequestParam UUID userId) {
        
        chatCoordinatorService.recordUserJoined(roomId, userId);
        return ResponseEntity.ok("User joined activity recorded");
    }

    @Operation(
            summary = "Get recent activity in a room",
            description = "Retrieves the most recent activities that occurred in a room"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved room activities",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoomActivity.class))),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{roomId}/activity")
    public ResponseEntity<List<RoomActivity>> getRoomActivity(
            @Parameter(description = "Unique identifier of the room", required = true)
            @PathVariable UUID roomId,
            @Parameter(description = "Maximum number of activities to retrieve", example = "20")
            @RequestParam(defaultValue = "20") int limit) {
        
        List<RoomActivity> activities = roomActivityService.getRecentRoomActivity(roomId, limit);
        return ResponseEntity.ok(activities);
    }

    @Operation(
            summary = "Get activity in a room within a time range",
            description = "Retrieves room activities that occurred within a specified time range"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved room activities for time range",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoomActivity.class))),
            @ApiResponse(responseCode = "400", description = "Invalid time range parameters"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{roomId}/activity/range")
    public ResponseEntity<List<RoomActivity>> getRoomActivityInRange(
            @Parameter(description = "Unique identifier of the room", required = true)
            @PathVariable UUID roomId,
            @Parameter(description = "Start time for the activity range", required = true, example = "2023-12-01T10:00:00Z")
            @RequestParam Instant startTime,
            @Parameter(description = "End time for the activity range", required = true, example = "2023-12-01T18:00:00Z")
            @RequestParam Instant endTime) {
        
        List<RoomActivity> activities = roomActivityService.getRoomActivityInRange(roomId, startTime, endTime);
        return ResponseEntity.ok(activities);
    }

    @Operation(
            summary = "Get specific type of activity in a room",
            description = "Retrieves activities of a specific type that occurred in a room"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved activities by type",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoomActivity.class))),
            @ApiResponse(responseCode = "400", description = "Invalid activity type"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{roomId}/activity/{activityType}")
    public ResponseEntity<List<RoomActivity>> getRoomActivityByType(
            @Parameter(description = "Unique identifier of the room", required = true)
            @PathVariable UUID roomId,
            @Parameter(description = "Type of activity to filter by", required = true, example = "typing")
            @PathVariable String activityType) {
        
        if (!ActivityType.isValidValue(activityType)) {
            return ResponseEntity.badRequest().body(null);
        }

        List<RoomActivity> activities = roomActivityService.getRoomActivityByType(roomId, activityType);
        return ResponseEntity.ok(activities);
    }

    @Operation(
            summary = "Get activity for a specific user in a room",
            description = "Retrieves all activities performed by a specific user in a room"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user activities in room",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoomActivity.class))),
            @ApiResponse(responseCode = "404", description = "Room or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{roomId}/activity/user/{userId}")
    public ResponseEntity<List<RoomActivity>> getRoomActivityByUser(
            @Parameter(description = "Unique identifier of the room", required = true)
            @PathVariable UUID roomId,
            @Parameter(description = "Unique identifier of the user", required = true)
            @PathVariable UUID userId) {
        
        List<RoomActivity> activities = roomActivityService.getRoomActivityByUser(roomId, userId);
        return ResponseEntity.ok(activities);
    }
}
