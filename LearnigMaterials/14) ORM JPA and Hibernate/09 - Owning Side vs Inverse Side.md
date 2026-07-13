# Owning Side vs Inverse Side

## Learning goals

- Understand relationship ownership.
- Use `mappedBy` correctly.
- Avoid updating only the inverse side.

## What ownership means

The owning side controls the database relationship. In many mappings, the owning side is the side with the foreign key.

## Example

```java
public class OrderItem {
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
```

`OrderItem` owns the relationship.

```java
public class Order {
    @OneToMany(mappedBy = "order")
    private List<OrderItem> items = new ArrayList<>();
}
```

`Order` is the inverse side because `mappedBy = "order"` points to the field in `OrderItem`.

## Helper method

```java
public void addItem(OrderItem item) {
    items.add(item);
    item.setOrder(this);
}
```

Keep both Java sides consistent.

## Common mistakes

- Putting `mappedBy` on the wrong side.
- Updating only the inverse collection.
- Using database thinking without checking Java object consistency.

## Mini exercises

1. Identify the owning side in `Book` and `Author`.
2. Write helper methods for a bidirectional relationship.
3. Explain what `mappedBy` references.

## Quick summary

The owning side controls the database relationship. The inverse side must still be kept consistent in Java.
