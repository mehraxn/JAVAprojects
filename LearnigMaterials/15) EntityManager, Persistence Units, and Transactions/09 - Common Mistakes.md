# Common Mistakes

## Forgetting transactions

Writes such as `persist`, `merge`, and `remove` need transaction boundaries.

## Wrong persistence unit name

The string in code must match the XML exactly.

```java
Persistence.createEntityManagerFactory("devPU");
```

## Not closing EntityManager

Always close `EntityManager` after use.

## Using one EntityManager forever

Use an `EntityManager` for a unit of work, then close it.

## Lazy loading after close

Accessing lazy relationships after the persistence context closes can fail. Fetch needed data before closing or design repository methods carefully.

## Committing after exception

If an exception occurs, roll back instead of committing.

## Swallowing persistence exceptions

Do not hide failures with empty catch blocks. The caller needs to know the operation failed.

## Mini exercise

Read a repository method and identify where the transaction starts, commits, rolls back, and where the `EntityManager` closes.

## Quick summary

Most JPA bugs come from unclear lifecycle, transaction, and resource boundaries.
