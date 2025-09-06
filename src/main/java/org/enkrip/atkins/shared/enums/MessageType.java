package org.enkrip.atkins.shared.enums;

/**
 * Enum representing different types of messages in the chat system
 */
public enum MessageType {
    TEXT(1, "Text message"),
    IMAGE(2, "Image message"),
    FILE(3, "File attachment"),
    SYSTEM(4, "System message"),
    EMOJI(5, "Emoji reaction"),
    VOICE(6, "Voice message"),
    VIDEO(7, "Video message");

    private final int code;
    private final String description;

    MessageType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get MessageType from code
     */
    public static MessageType fromCode(int code) {
        for (MessageType type : MessageType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown message type code: " + code);
    }

    /**
     * Check if the given code is valid
     */
    public static boolean isValidCode(int code) {
        try {
            fromCode(code);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
