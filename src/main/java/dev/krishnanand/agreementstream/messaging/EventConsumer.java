package dev.krishnanand.agreementstream.messaging;

import java.util.function.Consumer;

import dev.krishnanand.agreementstream.model.AgreementEvent;

public interface EventConsumer {
    void start(String source, Consumer<AgreementEvent> handler);

    void stop();
}
