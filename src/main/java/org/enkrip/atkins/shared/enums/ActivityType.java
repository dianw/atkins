package org.enkrip.atkins.shared.enums;

/**
 * Enum representing different types of activities in rooms
 */
public enum ActivityType {
    TYPING("typing", "User is typing"),
    ONLINE("online", "User is online"),
    MESSAGE_READ("message_read", "Message was read"),
    OFFLINE("offline", "User went offline"),
    JOINED("joined", "User joined the room"),
    LEFT("left", "User left the room"),
    MESSAGE("message", "User sent a message");

    private final String value;
    private final String description;

    ActivityType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get ActivityType from string value
     */
    public static ActivityType fromValue(String value) {
        for (ActivityType type : ActivityType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown activity type: " + value);
    }

    /**
     * Check if the given value is valid
     */
    public static boolean isValidValue(String value) {
        try {
            fromValue(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
