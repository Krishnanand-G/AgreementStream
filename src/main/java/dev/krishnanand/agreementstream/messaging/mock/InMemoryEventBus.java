package dev.krishnanand.agreementstream.messaging.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import dev.krishnanand.agreementstream.model.AgreementEvent;

public class InMemoryEventBus {

    private final Map<String, List<Consumer<AgreementEvent>>> subscribers = new ConcurrentHashMap<>();
    private final Map<String, List<AgreementEvent>> topics = new ConcurrentHashMap<>();

    public void publish(String destination, AgreementEvent event) {
        topics.computeIfAbsent(destination, ignored -> new ArrayList<>()).add(event);
        subscribers.getOrDefault(destination, List.of()).forEach(handler -> handler.accept(event));
    }

    public void subscribe(String source, Consumer<AgreementEvent> handler) {
        subscribers.computeIfAbsent(source, ignored -> new ArrayList<>()).add(handler);
        topics.getOrDefault(source, List.of()).forEach(handler);
    }

    public void clear() {
        subscribers.clear();
        topics.clear();
    }

    public List<AgreementEvent> eventsOn(String destination) {
        return List.copyOf(topics.getOrDefault(destination, List.of()));
    }
}
