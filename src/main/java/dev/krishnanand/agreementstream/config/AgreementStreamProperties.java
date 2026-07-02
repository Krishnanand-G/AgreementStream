package dev.krishnanand.agreementstream.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "agreementstream")
public record AgreementStreamProperties(
        Messaging messaging,
        Router router
) {
    public record Messaging(
            String provider,
            String inboundTopic,
            String outboundTopic,
            String dlqTopic
    ) {
    }

    public record Router(
            int maxAttempts,
            long backoffMillis
    ) {
    }
}
