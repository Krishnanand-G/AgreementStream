# AgreementStream

Small event router for agreement lifecycle events. You POST an event, it goes out on a configured topic. Handlers are idempotent, retries back off, and anything that still fails lands in a dead-letter queue.

## Stack

- Java 17, Spring Boot 3
- GCP Pub/Sub (works with the emulator) or an in-memory mock
- Azure Event Hubs adapter (mock locally; swap in `EventProcessorClient` for prod)
- H2 for idempotency tracking, Prometheus metrics, JSON logs

## How it works

```text
POST /api/events -> EventRouterService -> EventPublisher -> outbound topic
                              |-> processed_events (idempotency)
                              +-> DLQ after retries run out
```

Pick the backend in config:

```yaml
agreementstream:
  messaging:
    provider: mock   # mock | gcp | azure
```

## Running it locally

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

Prometheus metrics: `http://localhost:8080/actuator/prometheus`

## Notes

The adapter layout borrows ideas from portable messaging libraries. Router code stays the same whether you run mock, GCP, or Azure. Only the adapter changes.

## References

- [Spring Cloud GCP Pub/Sub](https://github.com/GoogleCloudPlatform/spring-cloud-gcp)
- Cloud messaging adapter patterns (DLQ/retry separation)
