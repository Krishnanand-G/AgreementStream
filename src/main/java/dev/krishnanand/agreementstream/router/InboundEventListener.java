package dev.krishnanand.agreementstream.router;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import dev.krishnanand.agreementstream.config.AgreementStreamProperties;
import dev.krishnanand.agreementstream.messaging.EventConsumer;

@Component
public class InboundEventListener {

    private final AgreementStreamProperties properties;
    private final EventConsumer eventConsumer;
    private final EventRouterService eventRouterService;

    public InboundEventListener(
            AgreementStreamProperties properties,
            EventConsumer eventConsumer,
            EventRouterService eventRouterService
    ) {
        this.properties = properties;
        this.eventConsumer = eventConsumer;
        this.eventRouterService = eventRouterService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        eventConsumer.start(properties.messaging().inboundTopic(), eventRouterService::route);
    }
}
