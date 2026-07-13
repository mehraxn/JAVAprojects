# Common Architecture Mistakes

## Learning goals

- Recognize common design problems in beginner Java applications.
- Learn practical fixes without overengineering.

## Mistake 1: Everything in Main

Bad design:

```java
public static void main(String[] args) {
    // read input
    // validate input
    // update objects
    // calculate reports
    // print output
}
```

Better design:

```java
public static void main(String[] args) {
    OrderService service = new OrderService(new InMemoryOrderRepository());
    OrderSnapshot order = service.createOrder("A-100");
    System.out.println(order.orderId());
}
```

`Main` should start the program, not own the whole program.

## Mistake 2: Business logic inside UI

If a menu decides whether an account can withdraw money, the same rule must be duplicated in every UI. Put the rule in the domain model or service.

## Mistake 3: Exposing mutable internals

```java
public List<Item> getItems() {
    return items; // risky
}
```

Prefer:

```java
public List<Item> getItems() {
    return List.copyOf(items);
}
```

## Mistake 4: Mixing persistence everywhere

Avoid writing SQL, JPA, or file code in every business class. Keep storage details behind repositories.

## Mistake 5: Unclear class responsibility

Names like `Manager`, `Helper`, and `Utility` can hide unclear design. Prefer names that describe a responsibility, such as `OrderService`, `ProductRepository`, or `InvoiceReport`.

## Mistake 6: Too many layers too early

A tiny exercise may not need five layers. Add structure when it improves clarity, testing, or changeability.

## Mini exercise

Look at a previous Java exercise and mark each class as UI, service, domain, repository, or report. If a class has more than two roles, suggest a split.

## Quick summary

Good architecture is practical. It separates responsibilities enough to keep the code testable and understandable.
