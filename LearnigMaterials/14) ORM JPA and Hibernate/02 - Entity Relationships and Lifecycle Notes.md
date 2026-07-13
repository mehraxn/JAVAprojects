# Entity Relationships and Lifecycle Notes

## Learning goals

- Review common JPA relationship annotations.
- Understand owning side vs inverse side.
- Learn fetch, cascade, orphan removal, and lifecycle basics.

## Relationship examples

### Many-to-one and one-to-many

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
}
```

`OrderItem` owns the relationship because it contains the foreign key column. `Order` is the inverse side because it uses `mappedBy`.

## Owning side vs inverse side

- Owning side controls the database relationship.
- Inverse side reflects the relationship in Java.
- `mappedBy` points to the field name on the owning side.

## Cascade

Cascade means an operation on one entity can apply to related entities.

Example:

- saving an `Order` also saves its `OrderItem` objects;
- removing an `Order` also removes its items when configured correctly.

Use cascade carefully. Do not cascade deletes across important shared entities such as `User` and `Role` without understanding the effect.

## orphanRemoval

`orphanRemoval = true` means removing a child object from the parent collection can delete it from the database.

This is useful for parent-owned child objects such as order items.

## Fetch eager vs lazy

| Fetch type | Meaning |
|---|---|
| `LAZY` | Load related data when needed |
| `EAGER` | Load related data immediately |

Prefer `LAZY` for many relationships unless there is a clear reason.

## Entity lifecycle basics

- Transient: new object, not managed by JPA.
- Managed: attached to an active persistence context.
- Detached: was managed, but no longer attached.
- Removed: scheduled for deletion.

## Common mistakes

- Updating only one side of a bidirectional relationship.
- Using `EAGER` everywhere.
- Cascading remove to shared reference data.
- Forgetting `mappedBy`.
- Accessing lazy data after the persistence context is closed.

## Mini exercise

Model `Student` and `Course` with a many-to-many relationship. Then explain why many-to-many sometimes becomes two one-to-many relationships with an enrollment entity.

## Quick summary

JPA relationships are powerful but require careful ownership, cascade, fetch, and lifecycle decisions.
