# Lazy vs Eager Loading

## Learning goals

- Understand `FetchType.LAZY` and `FetchType.EAGER`.
- Know why eager loading can be dangerous.
- Recognize lazy loading errors.

## Lazy loading

Lazy loading means related data loads when accessed.

```java
@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
private List<OrderItem> items;
```

This avoids loading unnecessary data.

## Eager loading

Eager loading means related data loads immediately.

```java
@ManyToOne(fetch = FetchType.EAGER)
private Customer customer;
```

Eager can be convenient but may load too much data.

## LazyInitializationException

This happens when code tries to access lazy data after the persistence context is closed.

```java
Order order = repository.findById(id);
em.close();
order.getItems().size(); // can fail if items were not loaded
```

## Best practices

- Prefer lazy for collections.
- Fetch what a use case needs inside the repository or service boundary.
- Return DTOs or snapshots when crossing boundaries.

## Common mistakes

- Setting everything eager to avoid one error.
- Accessing lazy collections in views after the transaction is closed.
- Returning entities when a DTO would be clearer.

## Mini exercises

1. Explain why a large collection should be lazy.
2. Fix a lazy-loading failure using a repository query.
3. Decide whether `Order.customer` should be lazy or eager.

## Quick summary

Lazy loading protects performance, but code must fetch needed data before leaving the persistence boundary.
