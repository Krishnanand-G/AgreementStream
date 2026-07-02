package dev.krishnanand.agreementstream.model;

import java.time.Instant;
import java.util.Map;

public record AgreementEvent(
        String eventId,
        String agreementId,
        String eventType,
        Instant occurredAt,
        Map<String, String> attributes
) {
}
