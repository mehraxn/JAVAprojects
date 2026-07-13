# Cascade Types Explained

## Learning goals

- Understand JPA cascade types.
- Know when cascade is useful.
- Avoid dangerous cascading deletes.

## Cascade types

| Type | Meaning |
|---|---|
| `PERSIST` | Persist related entity when parent is persisted |
| `MERGE` | Merge related entity when parent is merged |
| `REMOVE` | Remove related entity when parent is removed |
| `REFRESH` | Refresh related entity |
| `DETACH` | Detach related entity |
| `ALL` | All cascade operations |

## Useful example

Order and order items often belong together.

```java
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderItem> items = new ArrayList<>();
```

Saving an order can save its items.

## Dangerous example

Do not blindly cascade remove across shared entities such as students and courses. Removing one student should not delete a shared course.

## Common mistakes

- Using `CascadeType.ALL` everywhere.
- Cascading remove to shared lookup data.
- Thinking cascade controls fetch behavior.
- Forgetting that cascade is about entity operations.

## Mini exercises

1. Choose cascade settings for `Order` and `OrderItem`.
2. Explain why `Student` should not remove `Course` through cascade.
3. Identify the risk in `CascadeType.ALL` on a shared reference.

## Quick summary

Cascade can reduce boilerplate, but it must reflect real ownership.
