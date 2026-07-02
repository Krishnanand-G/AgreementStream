package dev.krishnanand.agreementstream.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.krishnanand.agreementstream.messaging.EventConsumer;
import dev.krishnanand.agreementstream.messaging.EventPublisher;
import dev.krishnanand.agreementstream.messaging.MessagingProvider;
import dev.krishnanand.agreementstream.messaging.azure.MockAzureEventHubsAdapter;
import dev.krishnanand.agreementstream.messaging.gcp.GcpPubSubAdapter;
import dev.krishnanand.agreementstream.messaging.mock.InMemoryEventBus;
import dev.krishnanand.agreementstream.messaging.mock.MockEventConsumer;
import dev.krishnanand.agreementstream.messaging.mock.MockEventPublisher;

@Configuration
@EnableConfigurationProperties(AgreementStreamProperties.class)
public class MessagingConfig {

    @Bean
    public InMemoryEventBus inMemoryEventBus() {
        return new InMemoryEventBus();
    }

    @Bean
    @ConditionalOnProperty(prefix = "agreementstream.messaging", name = "provider", havingValue = "gcp")
    public GcpPubSubAdapter gcpPubSubAdapter(com.google.cloud.spring.pubsub.core.PubSubTemplate pubSubTemplate) {
        return new GcpPubSubAdapter(pubSubTemplate);
    }

    @Bean
    @ConditionalOnProperty(prefix = "agreementstream.messaging", name = "provider", havingValue = "azure")
    public MockAzureEventHubsAdapter mockAzureEventHubsAdapter(InMemoryEventBus eventBus) {
        return new MockAzureEventHubsAdapter(eventBus);
    }

    @Bean
    public EventPublisher eventPublisher(
            AgreementStreamProperties properties,
            InMemoryEventBus eventBus,
            org.springframework.beans.factory.ObjectProvider<GcpPubSubAdapter> gcpPubSubAdapter,
            org.springframework.beans.factory.ObjectProvider<MockAzureEventHubsAdapter> mockAzureEventHubsAdapter
    ) {
        return switch (MessagingProvider.valueOf(properties.messaging().provider().toUpperCase())) {
            case GCP -> gcpPubSubAdapter.getObject();
            case AZURE -> mockAzureEventHubsAdapter.getObject();
            case MOCK -> new MockEventPublisher(eventBus);
        };
    }

    @Bean
    public EventConsumer eventConsumer(
            AgreementStreamProperties properties,
            InMemoryEventBus eventBus,
            org.springframework.beans.factory.ObjectProvider<GcpPubSubAdapter> gcpPubSubAdapter,
            org.springframework.beans.factory.ObjectProvider<MockAzureEventHubsAdapter> mockAzureEventHubsAdapter
    ) {
        return switch (MessagingProvider.valueOf(properties.messaging().provider().toUpperCase())) {
            case GCP -> gcpPubSubAdapter.getObject();
            case AZURE -> mockAzureEventHubsAdapter.getObject();
            case MOCK -> new MockEventConsumer(eventBus);
        };
    }
}
