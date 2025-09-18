package org.enkrip.atkins.shared.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for ChatUser data that can be serialized to JSON
 */
public class ChatUserDto {
    private String userId;
    private String username;
    private String displayName;
    private String avatarUrl;

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
