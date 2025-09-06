package org.enkrip.atkins.shared.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "System", description = "System information and health check endpoints")
public class SystemController {

    @Value("${spring.application.name:atkins}")
    private String applicationName;

    @Operation(
            summary = "Get API information",
            description = "Returns basic information about the API including version and status"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API information retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", applicationName);
        info.put("description", "My name is Atkins, Chat Atkins - A comprehensive chat application API");
        info.put("version", "v1.0.0");
        info.put("timestamp", Instant.now());
        info.put("status", "running");
        
        return ResponseEntity.ok(info);
    }

    @Operation(
            summary = "Health check endpoint",
            description = "Simple health check to verify the API is running"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is healthy",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", Instant.now());
        health.put("service", applicationName);
        
        return ResponseEntity.ok(health);
    }
}
