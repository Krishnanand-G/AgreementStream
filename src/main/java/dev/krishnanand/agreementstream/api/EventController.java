package dev.krishnanand.agreementstream.api;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.krishnanand.agreementstream.model.AgreementEvent;
import dev.krishnanand.agreementstream.router.EventRouterService;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRouterService eventRouterService;

    public EventController(EventRouterService eventRouterService) {
        this.eventRouterService = eventRouterService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publish(@RequestBody PublishEventRequest request) {
        AgreementEvent event = new AgreementEvent(
                request.eventId(),
                request.agreementId(),
                request.eventType(),
                request.occurredAt() == null ? Instant.now() : request.occurredAt(),
                request.attributes() == null ? Map.of() : request.attributes()
        );
        eventRouterService.route(event);
    }

    public record PublishEventRequest(
            String eventId,
            String agreementId,
            String eventType,
            Instant occurredAt,
            Map<String, String> attributes
    ) {
    }
}
