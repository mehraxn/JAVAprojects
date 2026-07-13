# Service Layer vs Domain Model

## Learning goals

- Know what belongs in a service class.
- Know what belongs in a domain object.
- Avoid putting all business logic in one place.

## Domain model responsibility

Domain objects protect rules about themselves.

```java
public final class Order {
    private boolean cancelled;

    public void cancel() {
        if (cancelled) {
            throw new IllegalStateException("Order is already cancelled");
        }
        cancelled = true;
    }
}
```

The `Order` class knows whether it can be cancelled.

## Service layer responsibility

Services coordinate use cases.

```java
public final class OrderService {
    private final OrderRepository orders;

    public OrderService(OrderRepository orders) {
        this.orders = orders;
    }

    public OrderSnapshot cancelOrder(String orderId) {
        Order order = orders.findRequired(orderId);
        order.cancel();
        orders.save(order);
        return OrderSnapshot.from(order);
    }
}
```

The service finds the order, runs the workflow, saves changes, and returns a snapshot.

## Good split of responsibility

| Responsibility | Good place |
|---|---|
| Validate a product price is not negative | `Product` |
| Assign a product to a category after checking both exist | `ProductService` |
| Prevent an account from overdrawing | `Account` |
| Transfer money between two accounts | `AccountService` |
| Format output for a CLI command | `Main` or CLI class |

## Common mistakes

- Domain objects with only getters and setters and no rules.
- Services that manually change every private field.
- UI code that performs business validation.
- Repositories that decide whether a workflow is allowed.

## Mini exercise

For an account transfer, decide which rules belong in `Account` and which rules belong in `AccountService`.

## Quick summary

Domain objects protect their own invariants. Services coordinate workflows that involve input validation, lookups, multiple objects, and persistence.
