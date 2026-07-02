package dev.krishnanand.agreementstream.messaging.mock;

import java.util.function.Consumer;

import dev.krishnanand.agreementstream.messaging.EventConsumer;
import dev.krishnanand.agreementstream.model.AgreementEvent;

public class MockEventConsumer implements EventConsumer {

    private final InMemoryEventBus eventBus;

    public MockEventConsumer(InMemoryEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void start(String source, Consumer<AgreementEvent> handler) {
        eventBus.subscribe(source, handler);
    }

    @Override
    public void stop() {
        eventBus.clear();
    }
}
