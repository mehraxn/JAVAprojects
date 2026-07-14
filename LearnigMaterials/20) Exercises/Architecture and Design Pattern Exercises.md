# Architecture and Design Pattern Exercises

## Exercise 1: Refactor a Main-heavy app into layers

Difficulty: Medium

## Goal

Move business logic out of `Main` into domain, service, repository, and snapshot classes.

## Background

Beginner programs often put validation, calculations, storage, and printing in one method. Larger Java applications need clear responsibilities.

## Starter Code

```java
public class Main {
    public static void main(String[] args) {
        // create orders, validate totals, store in a list, print summary
    }
}
```

## Requirements

- Create `Order`, `OrderService`, `OrderRepository`, `InMemoryOrderRepository`, and `OrderSnapshot`.
- Keep printing in `Main`.
- Put workflow logic in `OrderService`.

## Expected Behavior

`Main` should call the service and print returned snapshots.

## Test Cases

- Valid order is created.
- Invalid total is rejected.
- Repository stores the order.

## Hints

Start by moving validation into `Order`.

## Common Mistakes

- Keeping validation in `Main`.
- Returning mutable `Order` objects directly.

## Bonus Challenge

Add deterministic ranking by total.

## Solution Outline

Domain protects order rules. Service coordinates creation. Repository stores. Snapshot returns read-only output.

## Exercise 2: Add a facade over multiple services

Difficulty: Medium

## Goal

Create one simple API over multiple focused services.

## Background

A facade is useful when callers should not coordinate many internal services manually.

## Starter Code

```java
OrderService orders = new OrderService();
PaymentService payments = new PaymentService();
InvoiceService invoices = new InvoiceService();
```

## Requirements

- Create `CheckoutFacade`.
- Add `checkout(customerId, orderId)`.
- Internally call order, payment, and invoice services.

## Expected Behavior

The caller uses only the facade for checkout.

## Test Cases

- Checkout succeeds.
- Payment failure prevents invoice creation.
- Missing order is reported clearly.

## Hints

The facade coordinates. It should not become a giant class.

## Common Mistakes

- Moving all service logic into the facade.
- Returning mutable internals.

## Bonus Challenge

Add a `CheckoutReceipt` snapshot.

## Solution Outline

Keep internal services focused and let the facade provide a simple entry point.

## Exercise 3: Add a factory for object creation

Difficulty: Easy

## Goal

Centralize meaningful object creation.

## Background

Factories help when constructors need defaults, validation, or named variants.

## Starter Code

```java
Account account = new Account("A1", "STANDARD", 0);
```

## Requirements

- Create `AccountFactory`.
- Add `standardAccount`, `studentAccount`, and `premiumAccount`.
- Keep account invariants inside `Account`.

## Expected Behavior

Factory methods make creation intent clear.

## Test Cases

- Student account has student defaults.
- Premium account has premium defaults.
- Invalid owner name is rejected.

## Hints

Use static factory methods if a full factory class feels too large.

## Common Mistakes

- Putting account business operations in the factory.
- Hiding invalid default data.

## Bonus Challenge

Add a factory method from a CSV row.

## Solution Outline

Factory names explain variants; domain class still protects rules.

## Exercise 4: Add repository interface and in-memory repository

Difficulty: Medium

## Goal

Separate storage from service logic.

## Background

Repositories make service tests easier because tests can use in-memory storage.

## Starter Code

```java
private final List<Product> products = new ArrayList<>();
```

## Requirements

- Create `ProductRepository`.
- Add `save`, `findById`, `findAll`, and `deleteById`.
- Implement `InMemoryProductRepository`.

## Expected Behavior

Service code should depend on the interface.

## Test Cases

- Save and find product.
- Missing product returns empty optional.
- Delete removes product.

## Hints

Use `Map<String, Product>` internally.

## Common Mistakes

- Letting repository print messages.
- Putting business approval rules in the repository.

## Bonus Challenge

Return snapshots from service methods.

## Solution Outline

Service depends on repository abstraction; implementation stores objects in memory.

## Exercise 5: Add DTO/snapshot to avoid mutable exposure

Difficulty: Medium

## Goal

Protect internal mutable objects.

## Background

Returning domain objects directly lets callers bypass validation.

## Starter Code

```java
public List<String> itemNames() {
    return itemNames;
}
```

## Requirements

- Create `OrderSnapshot`.
- Use `List.copyOf`.
- Return snapshots from service methods.

## Expected Behavior

Caller cannot modify internal lists.

## Test Cases

- Returned list is unmodifiable.
- Internal order is unchanged after snapshot access.
- Snapshot contains expected fields.

## Hints

Be careful with nested mutable collections.

## Common Mistakes

- Returning an unmodifiable wrapper around a still-changing internal list.
- Assuming `final` means deeply immutable.

## Bonus Challenge

Add a report snapshot with totals.

## Solution Outline

Copy data at the boundary and expose only read methods.

## Exercise 6: Apply dependency injection without Spring

Difficulty: Medium

## Goal

Use constructor injection in plain Java.

## Background

Dependency injection makes services easier to test.

## Starter Code

```java
public class OrderService {
    private final OrderRepository repository = new InMemoryOrderRepository();
}
```

## Requirements

- Pass `OrderRepository` into the constructor.
- Wire dependencies in `Main`.
- Use fake repository in tests.

## Expected Behavior

The same service works with different repository implementations.

## Test Cases

- Service works with in-memory repository.
- Test can provide a fake.
- Constructor rejects null dependency.

## Hints

Dependencies should be visible in constructor parameters.

## Common Mistakes

- Creating dependencies inside the service.
- Using static global repositories.

## Bonus Challenge

Add a second dependency such as `Clock`.

## Solution Outline

Define interfaces, inject implementations, and wire in the entry point.

## Exercise 7: Identify SOLID violations

Difficulty: Hard

## Goal

Read code and identify design problems.

## Background

SOLID principles help diagnose why code is hard to change.

## Starter Code

```java
public class ReportManager {
    void readCsv() {}
    void calculateTotals() {}
    void saveToDatabase() {}
    void printConsole() {}
}
```

## Requirements

- Identify at least three violations.
- Propose a class split.
- Explain dependency direction.

## Expected Behavior

The rewritten design should have focused responsibilities.

## Test Cases

- CSV parsing can be tested separately.
- Report calculation can be tested without files.
- Printing can be tested without persistence.

## Hints

Look for reasons the class might change.

## Common Mistakes

- Creating interfaces with no purpose.
- Moving all methods to a different giant class.

## Bonus Challenge

Add a diagram of the improved design.

## Solution Outline

Split into importer, report service, repository, and printer.

## Exercise 8: Draw a Mermaid architecture diagram

Difficulty: Easy

## Goal

Represent architecture visually.

## Background

Diagrams help explain dependency direction and boundaries.

## Starter Code

```text
CLI -> Service -> Repository -> Storage
Service -> Domain
Service -> Snapshot
```

## Requirements

- Draw a layered diagram.
- Draw a service-to-repository sequence.
- Draw a snapshot boundary.

## Expected Behavior

The diagram should match the code structure.

## Test Cases

- Arrows point from caller to dependency.
- Domain does not depend on CLI.
- Snapshot boundary is clear.

## Hints

Use `flowchart TD` for layers and `sequenceDiagram` for workflow calls.

## Common Mistakes

- Drawing arrows both ways without reason.
- Including every private helper method.

## Bonus Challenge

Add a facade diagram over three services.

## Solution Outline

Keep diagrams small and focused on responsibility.
