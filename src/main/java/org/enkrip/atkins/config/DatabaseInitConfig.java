package org.enkrip.atkins.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import com.datastax.oss.driver.api.core.CqlSession;

@Configuration
public class DatabaseInitConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitConfig.class);

    private final CqlSession cqlSession;

    public DatabaseInitConfig(CqlSession cqlSession) {
        this.cqlSession = cqlSession;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeDatabase() {
        logger.info("Starting database initialization...");
        
        try {
            // Execute each table creation statement individually
            executeStatement("CREATE TABLE IF NOT EXISTS messages_by_room_time (" +
                    "room_id UUID, " +
                    "time_bucket TEXT, " +
                    "message_time TIMESTAMP, " +
                    "message_id UUID, " +
                    "user_id UUID, " +
                    "message_text TEXT, " +
                    "message_type INT, " +
                    "PRIMARY KEY ((room_id, time_bucket), message_time, message_id)" +
                    ") WITH CLUSTERING ORDER BY (message_time DESC)");
            
            executeStatement("CREATE TABLE IF NOT EXISTS user_timeline (" +
                    "user_id UUID, " +
                    "message_time TIMESTAMP, " +
                    "room_id UUID, " +
                    "message_id UUID, " +
                    "message_preview TEXT, " +
                    "PRIMARY KEY (user_id, message_time, message_id)" +
                    ") WITH CLUSTERING ORDER BY (message_time DESC)");
            
            executeStatement("CREATE TABLE IF NOT EXISTS room_activity (" +
                    "room_id UUID, " +
                    "activity_time TIMESTAMP, " +
                    "user_id UUID, " +
                    "activity_type TEXT, " +
                    "PRIMARY KEY (room_id, activity_time, user_id)" +
                    ") WITH CLUSTERING ORDER BY (activity_time DESC) " +
                    "AND default_time_to_live = 3600");
            
            logger.info("Database initialization completed successfully.");
            
        } catch (Exception e) {
            logger.warn("Failed to initialize database: {}. Tables might already exist.", e.getMessage());
            // Don't fail the application startup - the tables might already exist
        }
    }
    
    private void executeStatement(String statement) {
        try {
            logger.debug("Executing CQL: {}", statement);
            cqlSession.execute(statement);
            logger.debug("Successfully executed statement");
        } catch (Exception e) {
            logger.warn("Failed to execute statement (table might already exist): {}", e.getMessage());
        }
    }

}