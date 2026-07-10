# Test Results — Microservices Order System

Validation performed on **2026-07-10** on Windows 11 with Docker Desktop
(engine 29.4.2) and kubectl. No JDK was installed on the host, so the
Java-only integration test ran inside the official `eclipse-temurin:21-jdk`
container (same commands, real results). Everything below was actually run.

## Java compile result — PASS

All four services compiled cleanly with `javac` (JDK 21):
`inventory-service`, `payment-service`, `notification-service`,
`order-service`, each into its own `out/<service>` folder.

## Local integration test result — PASS (all checks)

```
./scripts/local-integration-test.sh
```

Output: `ALL CHECKS PASSED`, exit code 0. Individual results below.

## Valid order flow result — PASS

`POST /orders?sku=JAVA-BOOK&quantity=2&unitPrice=25.00` returned 201:

```json
{"id":"ORD-1","sku":"JAVA-BOOK","quantity":2,"unitPrice":25.00,"total":50.00,
 "status":"CONFIRMED","detail":"Order confirmed and mock notification recorded"}
```

- JAVA-BOOK stock decreased 10 → 8 (verified via `GET /inventory?sku=JAVA-BOOK`)
- Notification recorded (verified via `GET /notifications`)

## Payment rejection result — PASS

`POST /orders?sku=DEVOPS-KIT&quantity=1&unitPrice=10001.00` (total over the
mock 10000.00 approval limit) returned:

```json
{"id":"ORD-2","sku":"DEVOPS-KIT","quantity":1,"unitPrice":10001.00,"total":10001.00,
 "status":"PAYMENT_REJECTED","detail":"Mock payment was rejected; inventory was released"}
```

## Inventory compensation result — PASS

After the rejected order, DEVOPS-KIT stock was back at its starting value
(`"availableQuantity":5`) — the reservation made before the payment attempt
was released.

Additionally, downstream idempotency was verified directly: reserving twice
with the same order ID succeeded both times but decremented stock exactly
once (8 → 7, not 6); releasing restored it to 8.

## Unknown route result — PASS

All return 404 with a JSON error body:

- order-service: `/unknown`, `/orders/unknown`, `/orders-wrong`
- inventory-service: `/inventory/unknown`
- payment-service: `/payments`
- notification-service: `/notifications/unknown`

Method validation also passed: `DELETE /orders`, `GET /payments/authorize`,
`GET /inventory/reserve`, and `POST /health` all return 405; negative or
non-numeric quantity returns 400.

## Docker Compose build result — PASS

`docker compose config --quiet` exited 0; `docker compose build` built all
four images tagged `microservices-order/<service>:0.1.0`.

## Docker Compose runtime result — PASS

`docker compose up -d --wait` brought up all four services; `docker compose
ps` showed every container `(healthy)`. The wget healthchecks work in the
`eclipse-temurin:21-jre-alpine` runtime image, and order-service started only
after all three downstream services were healthy (health-gated `depends_on`).

Against the running stack (from the host):

- all four `/health` endpoints returned 200
- valid order: 201 `CONFIRMED`, stock 10 → 8, one notification recorded
- rejected order: `PAYMENT_REJECTED`, `inventory was released`, DEVOPS-KIT
  stock unchanged at 5
- `/orders-wrong` → 404; `DELETE /orders` → 405

`docker compose down` cleaned up.

## Kubernetes YAML/kustomize result — PASS

- `kubectl kustomize k8s/` rendered all 10 resources (Namespace, ConfigMap,
  4 Deployments, 4 Services) with no errors.
- `kubectl apply --dry-run=client -k k8s/` validated all 10 resources
  (`created (dry run)` for each), creating nothing.

## Not run

- **Live Kubernetes deployment** (kind workflow in TESTING.md section E): the
  manifests were only rendered and dry-run validated; no cluster deployment
  of this system was performed. No claim is made that the services were
  tested on Kubernetes.

## Tools unavailable

- JDK on the host — compilation/tests ran in the `eclipse-temurin:21-jdk`
  container instead (curl installed into the ephemeral container for the
  test script).

## Known limitations

- All state is in-memory; every restart resets stock, orders, payments, and
  notifications.
- No database, no message broker, no distributed transactions — compensation
  is best-effort.
- `POST /orders` is not client-idempotent (no `Idempotency-Key` support yet);
  retried client POSTs create separate orders.
- No auth, TLS, or production observability stack.
- Results are a point-in-time snapshot of one validation run.
