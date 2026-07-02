package dev.krishnanand.agreementstream.messaging.mock;

import dev.krishnanand.agreementstream.messaging.EventPublisher;
import dev.krishnanand.agreementstream.model.AgreementEvent;

public class MockEventPublisher implements EventPublisher {

    private final InMemoryEventBus eventBus;

    public MockEventPublisher(InMemoryEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void publish(String destination, AgreementEvent event) {
        eventBus.publish(destination, event);
    }
}
