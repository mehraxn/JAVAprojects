# orphanRemoval Explained

## Learning goals

- Understand `orphanRemoval`.
- Compare it with cascade remove.
- Use it for parent-owned child entities.

## What is an orphan?

An orphan is a child entity that no longer belongs to its parent.

Example: an `OrderItem` removed from an `Order`.

## Example

```java
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderItem> items = new ArrayList<>();

public void removeItem(OrderItem item) {
    items.remove(item);
    item.setOrder(null);
}
```

With `orphanRemoval = true`, removing the item from the collection can delete it from the database.

## Cascade remove vs orphanRemoval

- Cascade remove applies when the parent is removed.
- orphanRemoval applies when a child is removed from the parent relationship.

## When to use it

Use it when the child should not exist without the parent, such as order items.

## Common mistakes

- Using orphan removal for shared entities.
- Removing from the collection but not updating the child side.
- Expecting orphan removal to work without a managed entity and transaction.

## Mini exercises

1. Explain why `OrderItem` is a good orphan-removal candidate.
2. Explain why `Course` is not.
3. Write a `removeItem` helper method.

## Quick summary

orphanRemoval deletes child entities that no longer belong to their parent.
