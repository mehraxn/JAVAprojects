# Testing Microservices Order System

## Static checks

- [ ] Confirm each service package and responsibility is independent.
- [ ] Confirm API examples do not share private database state.
- [ ] Review timeout, retry, idempotency, and compensation assumptions.
- [ ] Confirm all hosts, images, and credentials remain placeholders.

## Deferred checks

- [ ] Compile each service separately.
- [ ] Test normal, unavailable-service, duplicate-request, and partial-failure flows.
- [ ] Validate container and Kubernetes files only after implementation.

No Java, container, network, or deployment command was executed.
