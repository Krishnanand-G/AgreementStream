package dev.krishnanand.agreementstream.idempotency;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProcessedEventRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProcessedEventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS processed_events (
                    event_id VARCHAR(128) PRIMARY KEY,
                    processed_at TIMESTAMP NOT NULL
                )
                """);
    }

    public boolean tryMarkProcessed(String eventId) {
        try {
            int inserted = jdbcTemplate.update(
                    "INSERT INTO processed_events(event_id, processed_at) VALUES (?, CURRENT_TIMESTAMP)",
                    eventId
            );
            return inserted == 1;
        } catch (DuplicateKeyException ex) {
            return false;
        }
    }
}
