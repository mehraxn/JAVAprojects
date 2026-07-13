# equals and hashCode for JPA Entities

## Learning goals

- Understand why entity equality is tricky.
- Compare generated ID and business key equality.
- Avoid mutable fields in equality.

## Why it is tricky

New entities often have no generated database ID until they are persisted. If `equals` and `hashCode` depend on that ID, behavior can change after saving.

## Business key approach

If an entity has an immutable natural key, it can be useful.

```java
@Column(nullable = false, unique = true, updatable = false)
private String sku;
```

`sku` can identify a product if it never changes.

## Generated ID caution

Using generated ID can work, but be careful with new unsaved entities where ID is null.

## Avoid mutable fields

Do not base equality on fields like name, price, or status if they can change.

## Common mistakes

- Including collections in `equals`.
- Using mutable fields.
- Changing hash code while object is inside a `HashSet`.
- Assuming one equality rule fits every entity.

## Mini exercises

1. Decide whether `Product.sku` is a good business key.
2. Explain why `Order.status` should not be in `equals`.
3. Identify a bad `hashCode` implementation for an entity.

## Quick summary

JPA entity equality needs careful design. Prefer stable identity fields and avoid mutable relationship fields.
