-- Messages partitioned by room + time bucket (for performance)
CREATE TABLE IF NOT EXISTS messages_by_room_time (
    room_id UUID,
    time_bucket TEXT, -- "2024-12-YYYY-MM-DD-HH"
    message_time TIMESTAMP,
    message_id UUID,
    user_id UUID,
    message_text TEXT,
    message_type INT,
    PRIMARY KEY ((room_id, time_bucket), message_time, message_id)
) WITH CLUSTERING ORDER BY (message_time DESC);

-- User timeline for quick "my recent messages across all rooms"
CREATE TABLE IF NOT EXISTS user_timeline (
    user_id UUID,
    message_time TIMESTAMP,
    room_id UUID,
    message_id UUID,
    message_preview TEXT,
    PRIMARY KEY (user_id, message_time, message_id)
) WITH CLUSTERING ORDER BY (message_time DESC);

-- Room member activity (for presence, typing indicators)
CREATE TABLE IF NOT EXISTS room_activity (
    room_id UUID,
    activity_time TIMESTAMP,
    user_id UUID,
    activity_type TEXT, -- 'typing', 'online', 'message_read'
    PRIMARY KEY (room_id, activity_time, user_id)
) WITH CLUSTERING ORDER BY (activity_time DESC)
AND default_time_to_live = 3600; -- Auto-expire after 1 hour
