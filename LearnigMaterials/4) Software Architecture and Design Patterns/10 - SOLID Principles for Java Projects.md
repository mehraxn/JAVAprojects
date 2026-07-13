# SOLID Principles for Java Projects

## Learning goals

- Understand the five SOLID principles in plain Java.
- Recognize when a class is doing too much.
- Use interfaces and constructor injection without overengineering.

## Why this topic matters

SOLID is a set of design principles that helps Java code stay easier to test, extend, and maintain. The goal is not to create many interfaces for every tiny class. The goal is to keep responsibilities clear and dependency direction clean.

## 1. Single Responsibility Principle

A class should have one main reason to change.

Bad example:

```java
public class OrderService {
    public void createOrder(String customerId) {
        // validate customer
        // calculate prices
        // save to database
        // print receipt
        // send confirmation
    }
}
```

Better split:

```java
public final class OrderService {
    private final OrderValidator validator;
    private final OrderRepository repository;

    public OrderService(OrderValidator validator, OrderRepository repository) {
        this.validator = validator;
        this.repository = repository;
    }

    public OrderSnapshot createOrder(CreateOrderRequest request) {
        validator.validate(request);
        Order order = Order.create(request.customerId(), request.items());
        repository.save(order);
        return OrderSnapshot.from(order);
    }
}
```

The service coordinates. The validator validates. The repository saves.

## 2. Open/Closed Principle

Code should be open for extension but closed for careless modification. One common Java approach is using an interface.

```java
public interface DiscountPolicy {
    Money discountFor(Order order);
}
```

New policies can be added without editing the order calculation class.

## 3. Liskov Substitution Principle

If class `B` extends class `A`, code using `A` should still work with `B`. A subtype should not break promises made by the parent type.

Bad sign: a subclass throws `UnsupportedOperationException` for normal parent behavior.

## 4. Interface Segregation Principle

Prefer focused interfaces over one giant interface.

Bad:

```java
public interface UserOperations {
    void createUser();
    void approveInvoice();
    void exportPayroll();
}
```

Better:

```java
public interface UserRepository {
    void save(User user);
}

public interface InvoiceApprover {
    void approve(String invoiceId);
}
```

## 5. Dependency Inversion Principle

High-level business code should depend on abstractions, not low-level details.

```java
public final class PaymentService {
    private final PaymentRepository payments;

    public PaymentService(PaymentRepository payments) {
        this.payments = payments;
    }
}
```

The service does not know whether the repository is in-memory, JDBC-based, or JPA-based.

## When to use SOLID

Use SOLID when code starts to grow, when tests become hard to write, or when one change forces edits in many unrelated classes.

## Common mistakes

- Forcing interfaces everywhere before there is a real need.
- Creating five classes for a tiny one-method exercise.
- Putting every method into one giant service.
- Letting services depend directly on concrete persistence classes.
- Calling SOLID a rulebook instead of a practical design guide.

## Mini exercises

1. Find a class that validates input, saves data, and prints output. Split the responsibilities.
2. Create a `PaymentRepository` interface and an in-memory implementation.
3. Explain which SOLID principle is violated by a `ReportService` that also reads CSV files and sends emails.

## Quick summary

SOLID helps keep Java classes focused, replaceable, and testable. Use it pragmatically, not mechanically.
