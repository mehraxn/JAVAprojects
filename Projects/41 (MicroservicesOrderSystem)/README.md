# Microservices Order System

*A four-service order workflow in dependency-free Java 21 — service ownership, synchronous HTTP calls, failure handling with compensation, containerized and Kubernetes-ready.*

## Problem this project solves

A monolith couples unrelated concerns and forces one deploy/scale unit. This
project demonstrates the **microservices** alternative on a small, honest scale:
an order flow split into four independently packaged services, each owning its
own data, talking only over HTTP contracts — with explicit failure states and
**compensation** when a step fails. It shows the boundaries and trade-offs
without hiding behind a framework.

## Technologies & concepts

- **Java 21** (built-in `HttpServer`; no Spring/framework) across four services
- **Service ownership** — each service owns its in-memory data; HTTP is the only boundary
- **Synchronous orchestration** with timeouts, **idempotency keys**, and **best-effort compensation**
- **Docker** (multi-stage, non-root per service) + **Docker Compose** topology
- **Kubernetes** — Deployments, ClusterIP Services, ConfigMap, namespace, Kustomize

## Architecture overview

```
client
  └─▶ order-service (8080) ── reserve ─▶ inventory-service (8081)
             │              ── authorize ─▶ payment-service (8082)
             │                     └─ (reject) ── release ─▶ inventory-service
             └── (confirm) ── notify ─▶ notification-service (8083)
  ◀── final order status: CREATED | INVENTORY_REJECTED | PAYMENT_REJECTED | CONFIRMED
```

| Service | Port | Owns |
| --- | ---: | --- |
| order-service | 8080 | orders, status, orchestration |
| inventory-service | 8081 | stock + idempotent reservations |
| payment-service | 8082 | mock authorizations (deterministic) |
| notification-service | 8083 | mock notification history |

## Project structure

```text
services/
  order-service/          model, service, controller, HTTP gateway, Dockerfile
  inventory-service/      stock/reservation model + service, controller, Dockerfile
  payment-service/        mock payment model + service, controller, Dockerfile
  notification-service/   mock notification model + service, controller, Dockerfile
docker-compose.yml        four-service local topology on one bridge network
k8s/                      Deployments, Services, ConfigMap, namespace, kustomization
docs/architecture.md  docs/service-communication.md
README.md  TESTING.md
```

## Important files explained

- **order-service/…/OrderService + HttpDownstreamGateway** — the orchestrator: validates, reserves, authorizes, compensates, confirms; `DownstreamGateway` abstracts the HTTP calls.
- **inventory-service** — stock keyed by SKU; reservations keyed by **order ID** so identical retries are idempotent; supports `release` for compensation.
- **payment-service** — deterministic mock rule (approves positive totals ≤ 10000.00); never touches real payment data.
- **k8s/** — one Deployment + ClusterIP Service per service, non-sensitive URL ConfigMap, probes, resource limits, non-root, read-only rootfs, dropped capabilities.
- **docker-compose.yml** — builds each image, wires services by Compose DNS names on `order-network`.

## How it would work in a real environment

`docker compose up` builds and runs all four services; the order service reaches
the others by DNS name. A `POST /orders` walks the flow: reserve → authorize →
(confirm + notify) or (reject + release). On Kubernetes, `kubectl apply -k k8s`
creates the namespace and four workloads; ClusterIP Services provide stable
in-cluster DNS. Placeholder image names must be replaced with a real registry
first.

## What was prepared but NOT executed

Prepared: four Java services (model/service/controller each), per-service
Dockerfiles, Compose topology, and Kubernetes manifests. **Not executed:** no
`javac`, `docker`, `docker compose`, `kubectl`, or network call ran. Nothing was
built, started, deployed, or load-tested.

## Security notes

- **No real secrets or credentials** anywhere; no payment/email/SMS provider is integrated (all mock).
- Containers run as **UID/GID 10001** (non-root); K8s adds read-only rootfs + dropped capabilities.
- ConfigMap holds only **non-sensitive** service URLs.
- Images are local placeholders and must be replaced before any real deployment.

## Limitations

- All state is **in-memory** — it disappears on restart; no database.
- No API gateway, authentication, TLS, or distributed tracing.
- Calls are synchronous with short fixed timeouts — no retries/backoff or circuit breakers.
- Compensation is **best-effort**, not a distributed transaction; the workflow is not exactly-once.
- The mock payment rule is not payment processing; `depends_on` controls start order, not readiness.

## Future improvements

- Unit + contract tests; correlation-ID and idempotency request headers.
- Per-service persistent storage owned independently.
- Structured logs, metrics, traces; retry budgets and circuit breaking.
- API authentication and secret management; an API gateway.
- Event-driven orders with an outbox pattern (no Kafka here).

## What I learned

- Designing around **service ownership** and HTTP contracts instead of a shared model.
- Why **idempotency keys** and **compensation** matter once a call can fail mid-flow.
- The real cost of distribution: partial failure, timeouts, and no free transactions.
- Packaging several services consistently with multi-stage, non-root images and Compose/Kubernetes.
