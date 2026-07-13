# Rollback and Error Handling

## Learning goals

- Handle persistence errors safely.
- Roll back active transactions.
- Avoid hiding failures.

## Safe pattern

```java
EntityTransaction tx = em.getTransaction();
try {
    tx.begin();
    serviceOperation(em);
    tx.commit();
} catch (RuntimeException ex) {
    if (tx.isActive()) {
        tx.rollback();
    }
    throw ex;
}
```

## Why rollback matters

If an exception happens during a transaction, the persistence context may no longer be safe to commit. Roll back the transaction and report the failure.

## Error handling rules

- Roll back if the transaction is active.
- Do not commit after an exception.
- Do not swallow persistence exceptions.
- Close the `EntityManager` in a `finally` block or try-with-resources if supported.

## Common mistakes

- Catching `Exception` and printing only a message.
- Continuing with a broken transaction.
- Returning success after rollback.
- Forgetting to close resources.

## Mini exercise

Write a method that creates a `Book`, catches runtime failures, rolls back, and rethrows the exception.

## Quick summary

Rollback handling is part of reliable persistence code. Failed database work should not be treated as successful.
