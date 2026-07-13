# Hexagonal Architecture Basics

## Learning goals

- Understand the core idea of hexagonal architecture.
- Learn ports and adapters.
- Compare it with layered architecture.

## What is hexagonal architecture?

Hexagonal architecture puts the domain and application logic at the center. External details connect through adapters.

The goal is to protect core business logic from details such as CLI input, files, databases, or external APIs.

## Core, ports, and adapters

```text
Core application
  - domain objects
  - service workflows
  - business rules

Ports
  - interfaces the core uses or exposes

Adapters
  - implementations that connect to the outside
```

## Inbound adapter

An inbound adapter starts a use case.

Examples:

- CLI command
- REST controller
- scheduled job
- test driver

## Outbound adapter

An outbound adapter provides something the core needs.

Examples:

- repository implementation
- file writer
- payment client
- email sender

## Port example

```java
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String orderId);
}
```

The service depends on this port. An adapter implements it.

## Layered vs hexagonal

Layered architecture often shows top-to-bottom layers. Hexagonal architecture emphasizes the protected center and replaceable adapters.

Both can be useful. For beginners, layered architecture is easier to start with. Hexagonal architecture becomes helpful when you want core logic independent from infrastructure.

## Common mistakes

- Making hexagonal architecture sound more complex than it needs to be.
- Creating ports for every small class.
- Letting adapters contain business rules.
- Letting the core depend on concrete infrastructure classes.

## Mini exercises

1. Identify inbound and outbound adapters in a library application.
2. Create a repository port for `Invoice`.
3. Explain how an in-memory adapter helps tests.

## Quick summary

Hexagonal architecture protects the core by making outside details plug in through ports and adapters.
