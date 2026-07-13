# Persistence Revision

## Key concepts

- JDBC works directly with SQL.
- JPA maps Java entities to database tables.
- Hibernate is a JPA implementation.
- `EntityManagerFactory` is expensive and should be created once.
- `EntityManager` is used for a unit of work.
- Writes require transactions.
- JPQL queries entities and fields, not tables and columns.

## EntityManager methods

- `persist`
- `find`
- `merge`
- `remove`
- `createQuery`

## Transaction pattern

```java
EntityTransaction tx = em.getTransaction();
try {
    tx.begin();
    // work
    tx.commit();
} catch (RuntimeException ex) {
    if (tx.isActive()) {
        tx.rollback();
    }
    throw ex;
}
```

## JPQL reminders

```java
SELECT p FROM Product p WHERE p.category = :category
```

Use named parameters. Avoid building queries with raw input.

## Quick summary

Persistence code needs clear configuration, lifecycle management, transactions, and safe query methods.
