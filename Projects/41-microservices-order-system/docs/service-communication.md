# Service Communication

## Synchronous REST-style calls

The order service uses Java's built-in HttpClient to call the other services sequentially. Each dependency uses Java's built-in HttpServer. Inputs are URL-encoded query parameters and responses are small manually generated JSON documents.

| Caller | Callee | Endpoint | Success meaning |
| --- | --- | --- | --- |
| order | inventory | POST /inventory/reserve | Stock is reserved for the order ID |
| order | payment | POST /payments/authorize | Deterministic mock authorization approved |
| order | inventory | POST /inventory/release | Reservation was found and restored |
| order | notification | POST /notifications | Mock notification was recorded |

A non-2xx response, invalid URL, timeout, interruption, or connection failure is treated as an unsuccessful call. The gateway restores the thread interruption flag when interrupted.

## Environment-specific addressing

- Local defaults use localhost ports 8081–8083.
- Compose uses service names such as http://inventory-service:8081.
- Kubernetes uses the same names through ClusterIP Service DNS inside the namespace.

## Deliberate omissions

There are no retries, circuit breakers, correlation headers, API versions, service authentication, TLS, schema registry, or distributed tracing. Adding blind retries would be unsafe without stronger idempotency contracts and retry budgets.

## Event-driven future option

A later version could publish order lifecycle events and use an outbox pattern so notification work is decoupled from the request. It would also need delivery semantics, consumer idempotency, dead-letter handling, observability, and schema evolution. Kafka or another broker is not implemented in this project.
