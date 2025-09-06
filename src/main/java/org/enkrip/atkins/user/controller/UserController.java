package org.enkrip.atkins.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Management", description = "APIs for managing user timelines and activities")
public class UserController {

    @Autowired
    private UserTimelineService userTimelineService;

    @Operation(
            summary = "Get user's timeline across all rooms",
            description = "Retrieves the user's message timeline across all chat rooms they participate in"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user timeline",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserTimeline.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{userId}/timeline")
    public ResponseEntity<List<UserTimeline>> getUserTimeline(
            @Parameter(description = "Unique identifier of the user", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Maximum number of timeline entries to retrieve", example = "20")
            @RequestParam(defaultValue = "20") int limit) {
        
        List<UserTimeline> timeline = userTimelineService.getUserTimeline(userId, limit);
        return ResponseEntity.ok(timeline);
    }

    @Operation(
            summary = "Get user's timeline within a time range",
            description = "Retrieves the user's message timeline within a specified time range"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user timeline for time range",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserTimeline.class))),
            @ApiResponse(responseCode = "400", description = "Invalid time range parameters"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{userId}/timeline/range")
    public ResponseEntity<List<UserTimeline>> getUserTimelineInRange(
            @Parameter(description = "Unique identifier of the user", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Start time for the timeline range", required = true, example = "2023-12-01T10:00:00Z")
            @RequestParam Instant startTime,
            @Parameter(description = "End time for the timeline range", required = true, example = "2023-12-01T18:00:00Z")
            @RequestParam Instant endTime) {
        
        List<UserTimeline> timeline = userTimelineService.getUserTimelineInRange(userId, startTime, endTime);
        return ResponseEntity.ok(timeline);
    }

    @Operation(
            summary = "Get user's messages in a specific room",
            description = "Retrieves all messages sent by the user in a specific chat room"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user messages in room",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserTimeline.class))),
            @ApiResponse(responseCode = "404", description = "User or room not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{userId}/timeline/room/{roomId}")
    public ResponseEntity<List<UserTimeline>> getUserMessagesInRoom(
            @Parameter(description = "Unique identifier of the user", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Unique identifier of the chat room", required = true)
            @PathVariable UUID roomId) {
        
        List<UserTimeline> timeline = userTimelineService.getUserMessagesInRoom(userId, roomId);
        return ResponseEntity.ok(timeline);
    }

    @Operation(
            summary = "Get user's timeline after a specific time",
            description = "Retrieves the user's message timeline starting from a specific timestamp"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user timeline after specified time",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserTimeline.class))),
            @ApiResponse(responseCode = "400", description = "Invalid time parameter"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{userId}/timeline/after")
    public ResponseEntity<List<UserTimeline>> getUserTimelineAfterTime(
            @Parameter(description = "Unique identifier of the user", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Timestamp to start retrieving timeline from", required = true, example = "2023-12-01T12:00:00Z")
            @RequestParam Instant afterTime) {
        
        List<UserTimeline> timeline = userTimelineService.getUserTimelineAfterTime(userId, afterTime);
        return ResponseEntity.ok(timeline);
    }
}
