# Microservices Order System

*A four-service order workflow in dependency-free Java 21 — service ownership,
synchronous HTTP orchestration, compensation on payment failure, downstream
idempotency by order ID, Docker Compose, Kubernetes manifests, and a real
integration test that proves the main business flows.*

## Problem this project solves

A monolith couples unrelated concerns and forces one deploy/scale unit. This
project demonstrates the **microservices** alternative on a small, honest
scale: an order flow split into four independently packaged services, each
owning its own data, talking only over HTTP contracts — with explicit failure
states and **compensation** when a step fails. No frameworks; every moving
part is visible.

## Services

| Service | Port | Owns |
| --- | ---: | --- |
| order-service | 8080 | orders, status, orchestration |
| inventory-service | 8081 | stock + reservations (idempotent by order ID) |
| payment-service | 8082 | mock authorizations (deterministic, idempotent by order ID) |
| notification-service | 8083 | mock notification history (one per order ID) |

## Main flow

```
client
  └─▶ order-service (8080) ── reserve ─▶ inventory-service (8081)
             │              ── authorize ─▶ payment-service (8082)
             │                     └─ (reject) ── release ─▶ inventory-service
             └── (confirm) ── notify ─▶ notification-service (8083)
  ◀── final order status: CREATED | INVENTORY_REJECTED | PAYMENT_REJECTED | CONFIRMED
```

1. Client `POST /orders?sku=…&quantity=…&unitPrice=…`
2. order-service **reserves inventory**; if that fails → `INVENTORY_REJECTED`
3. order-service **authorizes payment** (mock: approves totals ≤ 10000.00)
4. If payment is rejected → **compensation**: the inventory reservation is
   released, order ends `PAYMENT_REJECTED`
5. If approved → notification is recorded, order ends `CONFIRMED`

## Idempotency — what is and is not guaranteed

- **Downstream calls are idempotent by order ID.** Reserving or paying again
  with the same order ID has no additional effect (verified by the
  integration test: an identical retried reservation decrements stock exactly
  once). A retry with *different* details for the same order ID is rejected.
- **The public `POST /orders` endpoint is not client-idempotent.** Each POST
  creates a new order with a new ID, so a client retrying a timed-out request
  creates a second order. A client-provided `Idempotency-Key` header is a
  planned future improvement.
- Compensation is **best-effort**, not a distributed transaction; the
  workflow is not exactly-once. See [docs/architecture.md](docs/architecture.md).

## What is implemented (and validated — see TEST_RESULTS.md)

- Four Java 21 HTTP services (built-in `HttpServer`, zero dependencies), with
  exact route matching, method validation (405), input validation (400), and
  JSON 404s for unknown paths
- Service-to-service HTTP calls with connect/request timeouts
- Inventory reservation + release (compensation) keyed by order ID
- Deterministic payment rejection scenario (totals over 10000.00)
- Notification recording (one per confirmed order)
- Per-service multi-stage, non-root Dockerfiles
- Docker Compose topology with **healthchecks** and health-gated `depends_on`
- Kubernetes manifests (namespace, ConfigMap, 4 Deployments + Services,
  probes, resource limits, non-root/read-only rootfs) + kustomization
- `scripts/local-integration-test.sh` — compiles, starts, and tests the whole
  system on localhost with no Docker required

## What is not production-grade

- **No database** — all state is in-memory and disappears on restart
- **No message broker** — notification is a synchronous call, not an event
- **No distributed transactions** — compensation is best-effort
- **No client-supplied idempotency key** on `POST /orders` (see above)
- **No real auth**, TLS, API gateway, rate limiting
- **No production observability** — no metrics/tracing stack; plain stdout logs
- No retries/backoff or circuit breakers on downstream calls

## How to validate

Fast path (JDK 21 + curl, no Docker):

```bash
./scripts/local-integration-test.sh
```

If ports 8080–8083 are already in use, the script supports overrides:

```bash
ORDER_PORT=18080 INVENTORY_PORT=18081 PAYMENT_PORT=18082 NOTIFICATION_PORT=18083   ./scripts/local-integration-test.sh
```

Full commands — including Docker Compose (build, health-gated startup, the
order and rejection flows) and Kubernetes (`kubectl kustomize k8s/`, dry-run,
optional kind workflow) — are in [TESTING.md](TESTING.md). Actual recorded
results (what was really run, with outputs) are in
[TEST_RESULTS.md](TEST_RESULTS.md): the integration script, the Docker
Compose stack, and the Kubernetes manifest validation all passed on
2026-07-10; a live Kubernetes deployment was not run.

## Docker images

Compose builds each service as a versioned local image (never `latest`):

```
microservices-order/order-service:0.1.0
microservices-order/inventory-service:0.1.0
microservices-order/payment-service:0.1.0
microservices-order/notification-service:0.1.0
```

Manual build (context is the **project root**, because Dockerfiles copy
`services/<name>/src`):

```bash
docker build -f services/order-service/Dockerfile -t microservices-order/order-service:0.1.0 .
```

## Security notes

- **No secrets or credentials** anywhere; payment/notification are mocks.
- Containers run as UID/GID 10001 (non-root); Kubernetes adds read-only
  rootfs, dropped capabilities, seccomp, and a size-limited `/tmp` emptyDir.
- The Kubernetes ConfigMap holds only non-sensitive service URLs.
- Images are local-only; nothing is pushed to a registry.

## Resume Value

Built and locally validated a four-service Java order workflow with explicit HTTP boundaries, idempotent order handling, payment rejection, inventory compensation, Docker Compose orchestration, and Kubernetes manifests.

## Future improvements

- Client-provided `Idempotency-Key` header for `POST /orders`
- Per-service persistent storage; event-driven flow with an outbox pattern
- Retry budgets, circuit breaking, correlation IDs, structured logs/metrics
- API authentication and an API gateway

## What I learned

- Designing around **service ownership** and HTTP contracts instead of a
  shared data model.
- Why **idempotency keys** and **compensation** matter once a call can fail
  mid-flow — and where idempotency actually holds (downstream) vs where it
  doesn't yet (the public endpoint).
- `HttpServer` contexts are **prefix-matched**, so exact-path checks are a
  correctness requirement, not a nicety.
- Health-gated `depends_on` makes Compose startup deterministic instead of
  racy.
