package dev.krishnanand.agreementstream.messaging.azure;

import java.util.function.Consumer;

import dev.krishnanand.agreementstream.messaging.EventConsumer;
import dev.krishnanand.agreementstream.messaging.EventPublisher;
import dev.krishnanand.agreementstream.messaging.mock.InMemoryEventBus;
import dev.krishnanand.agreementstream.model.AgreementEvent;

/**
 * Local stand-in for Azure Event Hubs. Production deployments swap this adapter
 * for an EventProcessorClient-backed implementation without changing router code.
 */
public class MockAzureEventHubsAdapter implements EventPublisher, EventConsumer {

    private final InMemoryEventBus eventBus;

    public MockAzureEventHubsAdapter(InMemoryEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void publish(String destination, AgreementEvent event) {
        eventBus.publish(prefix(destination), event);
    }

    @Override
    public void start(String source, Consumer<AgreementEvent> handler) {
        eventBus.subscribe(prefix(source), handler);
    }

    @Override
    public void stop() {
        eventBus.clear();
    }

    private String prefix(String destination) {
        return "azure:" + destination;
    }
}
