package dev.krishnanand.agreementstream.messaging.gcp;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

import dev.krishnanand.agreementstream.messaging.EventConsumer;
import dev.krishnanand.agreementstream.messaging.EventPublisher;
import dev.krishnanand.agreementstream.model.AgreementEvent;

public class GcpPubSubAdapter implements EventPublisher, EventConsumer {

    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public GcpPubSubAdapter(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    @Override
    public void publish(String destination, AgreementEvent event) {
        try {
            byte[] payload = objectMapper.writeValueAsBytes(event);
            PubsubMessage message = PubsubMessage.newBuilder()
                    .setData(ByteString.copyFrom(payload))
                    .putAttributes("eventId", event.eventId())
                    .putAttributes("eventType", event.eventType())
                    .build();
            pubSubTemplate.publish(destination, message);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize agreement event", ex);
        }
    }

    @Override
    public void start(String source, Consumer<AgreementEvent> handler) {
        pubSubTemplate.subscribe(source, message -> handle(message, handler));
    }

    @Override
    public void stop() {
        // PubSubTemplate manages subscriber lifecycle with the application context.
    }

    private void handle(BasicAcknowledgeablePubsubMessage message, Consumer<AgreementEvent> handler) {
        try {
            AgreementEvent event = objectMapper.readValue(
                    message.getPubsubMessage().getData().toByteArray(),
                    AgreementEvent.class
            );
            handler.accept(event);
            message.ack();
        } catch (Exception ex) {
            message.nack();
            throw new IllegalStateException("Failed to process Pub/Sub message", ex);
        }
    }

    public AgreementEvent decodeForTest(byte[] payload) {
        try {
            return objectMapper.readValue(payload, AgreementEvent.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to decode test payload", ex);
        }
    }

    public Map<String, String> headersFor(AgreementEvent event) {
        Map<String, String> headers = new HashMap<>();
        headers.put("eventId", event.eventId());
        headers.put("eventType", event.eventType());
        headers.put("occurredAt", event.occurredAt() == null ? Instant.now().toString() : event.occurredAt().toString());
        return headers;
    }
}
