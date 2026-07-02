# AgreementStream

Distributed event router for agreement lifecycle events. Routes inbound events to a configured messaging backend with idempotent handlers, retry/backoff, and dead-letter publishing.

## Stack

- Java 17, Spring Boot 3
- GCP Pub/Sub (emulator-friendly) or in-memory mock
- Azure Event Hubs adapter interface (mock implementation for local dev)
- H2 idempotency store, Prometheus metrics, JSON logging

## Architecture

```text
POST /api/events -> EventRouterService -> EventPublisher -> outbound topic
                              |-> processed_events (idempotency)
                              +-> DLQ after retry exhaustion
```

Provider selection is config-driven:

```yaml
agreementstream:
  messaging:
    provider: mock   # mock | gcp | azure
```

## Local development

### Mock mode (default)

```bash
mvn spring-boot:run
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{"eventId":"evt-1","agreementId":"agr-1","eventType":"AGREEMENT_SIGNED"}'
```

### Pub/Sub emulator

```bash
docker compose up --build
```

Metrics: `http://localhost:8080/actuator/prometheus`

## Design notes

Adapter layout is inspired by portable messaging abstractions in open-source cloud messaging libraries. Azure Event Hubs uses a mock adapter locally; production deployments can swap in an `EventProcessorClient` implementation without changing router code.

## References

- [Spring Cloud GCP Pub/Sub](https://github.com/GoogleCloudPlatform/spring-cloud-gcp)
- Cloud messaging adapter patterns (DLQ/retry separation)
