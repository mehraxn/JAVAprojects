# Microservices Order System

Starter structure for three small Java services with explicit order, inventory, and payment boundaries.

## Current scope

- Separate standard-Java entry points
- API and interaction contract documentation
- Container and Kubernetes design placeholders
- No database, broker, gateway, or distributed transaction implementation yet

## Structure

```text
src/orderservice/
src/inventoryservice/
src/paymentservice/
docs/architecture.md
docs/api-contracts.md
docker/CONTAINERS.md
k8s/README.md
README.md
TESTING.md
```

## Status

Skeleton only. Services were not compiled, started, connected, containerized, or deployed.

## Next implementation decisions

- Confirm synchronous REST versus asynchronous messaging boundaries.
- Define identifiers, failure responses, retries, idempotency, and data ownership.
- Keep the first implementation in-memory and dependency-light.
