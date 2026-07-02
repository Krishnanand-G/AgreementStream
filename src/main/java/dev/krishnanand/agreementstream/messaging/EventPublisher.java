package dev.krishnanand.agreementstream.messaging;

import dev.krishnanand.agreementstream.model.AgreementEvent;

public interface EventPublisher {
    void publish(String destination, AgreementEvent event);
}
