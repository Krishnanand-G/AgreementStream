package dev.krishnanand.agreementstream.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dev.krishnanand.agreementstream.config.AgreementStreamProperties;
import dev.krishnanand.agreementstream.idempotency.ProcessedEventRepository;
import dev.krishnanand.agreementstream.messaging.EventPublisher;
import dev.krishnanand.agreementstream.model.AgreementEvent;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class EventRouterService {

    private static final Logger log = LoggerFactory.getLogger(EventRouterService.class);

    private final AgreementStreamProperties properties;
    private final EventPublisher publisher;
    private final ProcessedEventRepository processedEventRepository;
    private final MeterRegistry meterRegistry;

    public EventRouterService(
            AgreementStreamProperties properties,
            EventPublisher publisher,
            ProcessedEventRepository processedEventRepository,
            MeterRegistry meterRegistry
    ) {
        this.properties = properties;
        this.publisher = publisher;
        this.processedEventRepository = processedEventRepository;
        this.meterRegistry = meterRegistry;
    }

    public void route(AgreementEvent event) {
        meterRegistry.counter("agreementstream.events.received", "type", event.eventType()).increment();

        if (!processedEventRepository.tryMarkProcessed(event.eventId())) {
            meterRegistry.counter("agreementstream.events.duplicate").increment();
            log.info("Skipping duplicate event {}", event.eventId());
            return;
        }

        int attempts = 0;
        while (true) {
            try {
                publisher.publish(properties.messaging().outboundTopic(), event);
                meterRegistry.counter("agreementstream.events.routed", "type", event.eventType()).increment();
                return;
            } catch (RuntimeException ex) {
                attempts++;
                if (attempts >= properties.router().maxAttempts()) {
                    publisher.publish(properties.messaging().dlqTopic(), event);
                    meterRegistry.counter("agreementstream.events.dlq").increment();
                    log.warn("Routed event {} to DLQ after {} attempts", event.eventId(), attempts, ex);
                    return;
                }
                sleep(properties.router().backoffMillis());
            }
        }
    }

    private void sleep(long backoffMillis) {
        try {
            Thread.sleep(backoffMillis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Router backoff interrupted", ex);
        }
    }
}
