# Layered Architecture

## Learning goals

- Understand common Java application layers.
- Know what each layer should and should not do.
- Learn a simple call direction for beginner backend programs.

## The basic layers

```text
UI / CLI / Controller
        ↓
Service layer
        ↓
Domain model
        ↓
Repository layer
        ↓
Persistence layer
```

The arrows show the usual direction of calls. Higher layers use lower layers. Lower layers should not depend on menus, controllers, or console output.

## UI, controller, or CLI layer

This layer handles how a user or external caller starts an operation.

Typical work:

- read command-line arguments;
- receive request data;
- print or return output;
- translate user input into service calls.

It should not contain important business rules.

## Service layer

The service layer coordinates business operations.

Typical work:

- validate use-case input;
- check whether objects exist;
- coordinate multiple domain objects;
- call repositories;
- decide transaction boundaries in database applications.

Example:

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

## Domain model

The domain model represents important business concepts: `Order`, `Product`, `Account`, `Course`, `Book`.

Domain objects should protect their own rules:

```java
public final class Account {
    private double balance;

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > balance) {
            throw new IllegalStateException("Insufficient balance");
        }
        balance -= amount;
    }
}
```

## Repository layer

A repository hides storage details behind a clear Java interface.

```java
public interface ProductRepository {
    Product findRequired(String productId);
    void save(Product product);
    void delete(String productId);
}
```

The service uses the interface. The implementation can be in-memory, file-based, or database-backed.

## Persistence layer

This layer contains storage-specific code, such as JDBC or JPA code. It should not decide business rules like whether an account transfer is allowed.

## Common mistakes

- Putting SQL or JPA code directly in every service method.
- Letting repositories print messages to the console.
- Letting domain objects read files.
- Calling UI classes from the service layer.

## Mini exercise

Draw the layers for a library application with `Member`, `Book`, `LoanService`, and `BookRepository`.

## Quick summary

Layered architecture keeps input/output, workflows, domain rules, and storage responsibilities separated.
