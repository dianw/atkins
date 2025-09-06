package org.enkrip.atkins.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Chat Management", description = "APIs for managing chat messages and room communications")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatCoordinatorService chatCoordinatorService;

    @Operation(
            summary = "Send a message to a room",
            description = "Sends a new message to a specified chat room from a user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessagesByRoomTime.class))),
            @ApiResponse(responseCode = "400", description = "Invalid message parameters"),
            @ApiResponse(responseCode = "404", description = "Room or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<MessagesByRoomTime> sendMessage(
            @Parameter(description = "Unique identifier of the chat room", required = true)
            @PathVariable UUID roomId,
            @Parameter(description = "Unique identifier of the user sending the message", required = true)
            @RequestParam UUID userId,
            @Parameter(description = "Content of the message", required = true, example = "Hello everyone!")
            @RequestParam String messageText,
            @Parameter(description = "Type of message (1=TEXT, 2=IMAGE, 3=FILE, 4=SYSTEM)", example = "1")
            @RequestParam(defaultValue = "1") Integer messageTypeCode) {
        
        MessageType messageType = MessageType.fromCode(messageTypeCode);
        MessagesByRoomTime message = chatCoordinatorService.sendMessage(roomId, userId, messageText, messageType);
        return ResponseEntity.ok(message);
    }

    @Operation(
            summary = "Get recent messages from a room",
            description = "Retrieves the most recent messages from a specified chat room"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved room messages",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessagesByRoomTime.class))),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<MessagesByRoomTime>> getRoomMessages(
            @Parameter(description = "Unique identifier of the chat room", required = true)
            @PathVariable UUID roomId,
            @Parameter(description = "Maximum number of messages to retrieve", example = "50")
            @RequestParam(defaultValue = "50") int limit) {
        
        List<MessagesByRoomTime> messages = chatService.getRecentRoomMessages(roomId, limit);
        return ResponseEntity.ok(messages);
    }

    @Operation(
            summary = "Get messages from a specific time bucket",
            description = "Retrieves messages from a room within a specific time bucket partition"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved messages from time bucket",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessagesByRoomTime.class))),
            @ApiResponse(responseCode = "400", description = "Invalid time bucket format"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/rooms/{roomId}/messages/bucket/{timeBucket}")
    public ResponseEntity<List<MessagesByRoomTime>> getMessagesFromBucket(
            @Parameter(description = "Unique identifier of the chat room", required = true)
            @PathVariable UUID roomId,
            @Parameter(description = "Time bucket identifier (e.g., '2023-12-01')", required = true, example = "2023-12-01")
            @PathVariable String timeBucket) {
        
        List<MessagesByRoomTime> messages = chatService.getMessagesFromTimeBucket(roomId, timeBucket);
        return ResponseEntity.ok(messages);
    }

    @Operation(
            summary = "Get messages in a time range",
            description = "Retrieves messages from a room within a specific time range and bucket"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved messages in time range",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessagesByRoomTime.class))),
            @ApiResponse(responseCode = "400", description = "Invalid time range parameters"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/rooms/{roomId}/messages/range")
    public ResponseEntity<List<MessagesByRoomTime>> getMessagesInRange(
            @Parameter(description = "Unique identifier of the chat room", required = true)
            @PathVariable UUID roomId,
            @Parameter(description = "Time bucket identifier", required = true, example = "2023-12-01")
            @RequestParam String timeBucket,
            @Parameter(description = "Start time for the message range", required = true, example = "2023-12-01T10:00:00Z")
            @RequestParam Instant startTime,
            @Parameter(description = "End time for the message range", required = true, example = "2023-12-01T18:00:00Z")
            @RequestParam Instant endTime) {
        
        List<MessagesByRoomTime> messages = chatService.getMessagesInTimeRange(roomId, timeBucket, startTime, endTime);
        return ResponseEntity.ok(messages);
    }

    @Operation(
            summary = "Get messages by a specific user in a room",
            description = "Retrieves all messages sent by a specific user in a room within a time bucket"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user messages",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessagesByRoomTime.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "404", description = "Room or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/rooms/{roomId}/messages/user/{userId}")
    public ResponseEntity<List<MessagesByRoomTime>> getMessagesByUser(
            @Parameter(description = "Unique identifier of the chat room", required = true)
            @PathVariable UUID roomId,
            @Parameter(description = "Unique identifier of the user", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Time bucket identifier", required = true, example = "2023-12-01")
            @RequestParam String timeBucket) {
        
        List<MessagesByRoomTime> messages = chatService.getMessagesByUser(roomId, timeBucket, userId);
        return ResponseEntity.ok(messages);
    }
}
