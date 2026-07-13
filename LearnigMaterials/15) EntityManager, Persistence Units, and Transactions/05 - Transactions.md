# Transactions

## Learning goals

- Understand why database changes need transactions.
- Learn `begin`, `commit`, and `rollback`.
- Avoid committing after failures.

## What is a transaction?

A transaction groups database changes into one unit of work. It should either commit completely or roll back.

## Basic transaction pattern

```java
EntityTransaction tx = em.getTransaction();
try {
    tx.begin();
    em.persist(new Order("A-100"));
    tx.commit();
} catch (RuntimeException ex) {
    if (tx.isActive()) {
        tx.rollback();
    }
    throw ex;
}
```

## Why transactions matter

Without transactions, a workflow can leave partial changes. For example, an order might be saved without its order items.

## Read operations

Simple reads may not always require an explicit transaction in basic examples, but writes do. In real applications, transaction handling is often managed by a framework.

## Common mistakes

- Forgetting `begin`.
- Forgetting `commit`.
- Forgetting `rollback` after an exception.
- Swallowing the original exception.
- Trying to reuse a failed transaction.

## Mini exercise

Write a transaction block that creates an `Order` and two `OrderItem` entities. Add rollback logic.

## Quick summary

Transactions protect consistency. If something fails, roll back and do not pretend the operation succeeded.
