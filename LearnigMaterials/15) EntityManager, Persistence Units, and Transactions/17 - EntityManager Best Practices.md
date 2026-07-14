# EntityManager Best Practices

## Learning goals

- Use `EntityManager` safely.
- Keep persistence boundaries clear.
- Avoid common lifecycle and exception mistakes.

## One EntityManager per unit of work

Create an `EntityManager` for a focused unit of work, then close it.

```java
EntityManager em = emf.createEntityManager();
try {
    // one workflow
} finally {
    em.close();
}
```

## Do not use one static global EntityManager for everything

A long-lived global `EntityManager` causes stale state, lifecycle confusion, and resource problems.

## Close resources

Always close the `EntityManager`. Close the `EntityManagerFactory` on application shutdown.

## Handle exceptions honestly

Do not swallow persistence failures.

```java
catch (RuntimeException ex) {
    rollbackIfActive(tx);
    throw ex;
}
```

## Keep business logic out of repositories

Repositories should load and save. Services should coordinate workflows and transaction boundaries.

## Common mistakes

- Keeping `EntityManager` in a static field for all operations.
- Letting UI code manage transactions directly.
- Forgetting rollback.
- Logging an exception and returning success.
- Returning managed entities where immutable DTOs are safer.

## Mini exercises

1. Refactor a static global `EntityManager` into a unit-of-work pattern.
2. Add rollback handling to a repository operation.
3. Decide whether a service or repository should own a transaction.

## Quick summary

Use `EntityManager` with clear unit-of-work boundaries, honest error handling, and reliable cleanup.
