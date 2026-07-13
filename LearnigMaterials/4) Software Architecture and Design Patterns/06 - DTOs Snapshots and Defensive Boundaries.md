# DTOs, Snapshots, and Defensive Boundaries

## Learning goals

- Understand why exposing mutable domain objects is dangerous.
- Learn immutable DTOs and snapshots.
- Use defensive copies and unmodifiable collections.

## The problem

If a service returns its internal object directly, outside code can change state without validation.

```java
Order order = orderService.getOrder("O-100");
order.getItems().clear(); // dangerous if this bypasses business rules
```

Public methods should protect important state.

## DTO vs snapshot

A DTO is a data transfer object. It carries data across a boundary.

A snapshot is an immutable view of current state. It is useful when callers need to read data but must not mutate the original object.

## Immutable snapshot example

```java
public final class OrderSnapshot {
    private final String orderId;
    private final List<String> itemNames;

    public OrderSnapshot(String orderId, List<String> itemNames) {
        this.orderId = orderId;
        this.itemNames = List.copyOf(itemNames);
    }

    public String orderId() {
        return orderId;
    }

    public List<String> itemNames() {
        return itemNames;
    }
}
```

`List.copyOf` creates an unmodifiable copy.

## Defensive copy example

```java
public final class Course {
    private final List<String> enrolledStudentIds = new ArrayList<>();

    public List<String> enrolledStudentIds() {
        return List.copyOf(enrolledStudentIds);
    }
}
```

The caller can read the values but cannot change the internal list.

## When to use snapshots

Use snapshots when:

- returning data from service methods;
- showing reports;
- exposing collections;
- sending data to UI or CLI code;
- protecting domain objects from accidental mutation.

## Common mistakes

- Returning `ArrayList` fields directly.
- Creating an unmodifiable list that still wraps a mutable internal list.
- Assuming `final` makes an object fully immutable.
- Exposing mutable objects inside an immutable wrapper.

## Mini exercise

Create an immutable `AccountSnapshot` with `accountId`, `ownerName`, and `balance`. Make sure callers cannot modify any collection fields.

## Quick summary

Snapshots and DTOs protect boundaries. They let callers read data without giving them control over internal state.
