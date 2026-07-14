# Transaction Rollback Practice

## Learning goals

- Practice transaction rollback.
- Understand state after rollback.
- Avoid committing failed work.

## Basic rollback pattern

```java
EntityTransaction tx = em.getTransaction();
try {
    tx.begin();
    em.persist(new Product("Notebook"));
    tx.commit();
} catch (RuntimeException ex) {
    if (tx.isActive()) {
        tx.rollback();
    }
    throw ex;
}
```

## Why rollback matters

If a workflow fails halfway, the database should not keep partial changes. Rollback returns the transaction to a safe state.

## Example: order and payment

```java
try {
    tx.begin();
    em.persist(order);
    payment.charge();
    tx.commit();
} catch (RuntimeException ex) {
    if (tx.isActive()) {
        tx.rollback();
    }
    throw ex;
}
```

If charging fails, the order should not be committed as if everything succeeded.

## Testing rollback

A rollback test should verify that data was not saved after failure.

```java
assertThrows(RuntimeException.class, () -> service.createInvalidOrder());
assertTrue(repository.findAll().isEmpty());
```

## Common mistakes

- Catching an exception and still committing.
- Forgetting to check `tx.isActive()`.
- Returning success after rollback.
- Swallowing the root cause.

## Mini exercises

1. Write rollback logic for creating an invoice and invoice lines.
2. Create a test where the second line fails validation.
3. Verify no partial invoice remains.

## Quick summary

Rollback protects all-or-nothing behavior in persistence workflows.
