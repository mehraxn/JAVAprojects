# Bulk Update and Delete Queries

## Learning goals

- Understand JPQL bulk update and delete.
- Know that bulk operations bypass the persistence context.
- Use clear or refresh carefully.

## Bulk update

```java
int updated = em.createQuery(
        "UPDATE Product p SET p.active = false WHERE p.discontinued = true")
        .executeUpdate();
```

This updates rows directly.

## Bulk delete

```java
int deleted = em.createQuery(
        "DELETE FROM Invoice i WHERE i.cancelled = true")
        .executeUpdate();
```

## Transaction required

Bulk updates and deletes modify data, so they require a transaction.

## Persistence context caution

Bulk operations bypass managed entities already loaded in the persistence context. Existing managed objects may now be stale.

```java
em.clear();
```

Clearing after a bulk operation is often useful.

## When to use carefully

Use bulk operations for large changes where loading each entity would be inefficient. Do not use them when domain methods must enforce per-entity rules.

## Common mistakes

- Forgetting transaction.
- Expecting entity lifecycle callbacks for every affected row.
- Not clearing stale managed entities.
- Bypassing business rules accidentally.

## Mini exercises

1. Write a bulk update to deactivate old products.
2. Explain why managed objects can become stale.
3. Decide whether a bulk delete is safe for paid invoices.

## Quick summary

Bulk JPQL operations are efficient but bypass normal managed-entity behavior.
