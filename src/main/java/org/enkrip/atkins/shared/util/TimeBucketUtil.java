package org.enkrip.atkins.shared.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating time buckets for partitioning messages
 */
public class TimeBucketUtil {

    private static final DateTimeFormatter TIME_BUCKET_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd-HH").withZone(ZoneOffset.UTC);

    /**
     * Generate a time bucket string from an Instant
     * Format: "YYYY-MM-DD-HH" (e.g., "2024-12-06-14")
     */
    public static String generateTimeBucket(Instant instant) {
        return TIME_BUCKET_FORMATTER.format(instant);
    }

    /**
     * Generate a time bucket string for the current time
     */
    public static String generateCurrentTimeBucket() {
        return generateTimeBucket(Instant.now());
    }

    /**
     * Parse a time bucket string back to the start of that hour
     */
    public static Instant parseTimeBucketToInstant(String timeBucket) {
        // Add seconds and minutes to make it a complete timestamp
        String fullTimestamp = timeBucket + ":00:00Z";
        return Instant.parse(fullTimestamp.replace(' ', 'T'));
    }

    /**
     * Get the time bucket for a specific hour offset from now
     * @param hourOffset positive for future, negative for past
     */
    public static String getTimeBucketWithOffset(int hourOffset) {
        Instant targetTime = Instant.now().plusSeconds(hourOffset * 3600L);
        return generateTimeBucket(targetTime);
    }
}
