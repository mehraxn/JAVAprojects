# One To Many and Many To One

## Learning goals

- Map parent-child relationships.
- Understand `@ManyToOne`, `@OneToMany`, `@JoinColumn`, and `mappedBy`.
- Keep both sides of a bidirectional relationship consistent.

## Example: Order and OrderItem

Many order items belong to one order.

```java
@Entity
public class OrderItem {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
```

```java
@Entity
public class Order {
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}
```

## Owning side

The many-to-one side owns the foreign key because `OrderItem` has the `order_id` column.

## Why helper methods matter

If you add an item only to the list but do not set the item's order, Java state and database state can disagree.

## Common mistakes

- Forgetting `mappedBy`.
- Updating only one side.
- Using eager loading for every collection.
- Cascading remove to objects that should live independently.

## Mini exercises

1. Model `Author` and `Book`.
2. Identify the owning side.
3. Write an `addBook` helper method.

## Quick summary

One-to-many and many-to-one mappings are common, but ownership and helper methods must be handled carefully.
