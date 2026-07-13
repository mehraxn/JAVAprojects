# Dependency Injection Without Spring

## Learning goals

- Understand dependency injection in plain Java.
- Use constructor injection.
- See how dependency injection improves tests.

## What dependency injection means

Dependency injection means a class receives the objects it depends on instead of creating them internally.

Bad example:

```java
public final class OrderService {
    private final OrderRepository repository = new DatabaseOrderRepository();
}
```

This service is locked to one repository implementation.

Better:

```java
public final class OrderService {
    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }
}
```

Now the caller decides which repository to provide.

## Interface-based design

```java
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String orderId);
}
```

Production code might use a database implementation. Tests can use an in-memory fake.

## In-memory fake repository

```java
public final class InMemoryOrderRepository implements OrderRepository {
    private final Map<String, Order> orders = new LinkedHashMap<>();

    public void save(Order order) {
        orders.put(order.id(), order);
    }

    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }
}
```

## Manual wiring in Main

```java
public final class Main {
    public static void main(String[] args) {
        OrderRepository orders = new InMemoryOrderRepository();
        OrderService service = new OrderService(orders);
        service.createOrder("C-100");
    }
}
```

No framework is required. This is still dependency injection.

## Why it matters

Constructor injection makes dependencies visible. It also makes service tests fast because tests can provide fake repositories.

## Common mistakes

- Creating dependencies inside the class being tested.
- Using static globals for repositories.
- Adding dependency injection to every tiny value object.
- Hiding required dependencies behind setters.

## Mini exercises

1. Refactor a service that creates its own repository.
2. Create an in-memory fake repository for `Product`.
3. Write a service test that uses the fake repository.

## Quick summary

Dependency injection is simply passing dependencies in. Constructor injection is the clearest beginner-friendly approach.
