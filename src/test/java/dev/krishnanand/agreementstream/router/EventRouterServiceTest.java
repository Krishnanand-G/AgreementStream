package dev.krishnanand.agreementstream.router;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.krishnanand.agreementstream.messaging.mock.InMemoryEventBus;
import dev.krishnanand.agreementstream.model.AgreementEvent;

@SpringBootTest
class EventRouterServiceTest {

    @Autowired
    private EventRouterService eventRouterService;

    @Autowired
    private InMemoryEventBus inMemoryEventBus;

    @Test
    void routesEventAndSkipsDuplicates() {
        AgreementEvent event = new AgreementEvent(
                "evt-1",
                "agr-42",
                "AGREEMENT_SIGNED",
                Instant.parse("2026-01-15T10:00:00Z"),
                Map.of("tenant", "demo")
        );

        eventRouterService.route(event);
        eventRouterService.route(event);

        assertThat(inMemoryEventBus.eventsOn("agreements-outbound")).hasSize(1);
    }
}
