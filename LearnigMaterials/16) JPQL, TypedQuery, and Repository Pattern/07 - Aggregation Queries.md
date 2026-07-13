# Aggregation Queries

## Learning goals

- Use `COUNT`, `AVG`, `MIN`, and `MAX`.
- Understand `GROUP BY`.
- Return summary values from repositories.

## Count

```java
TypedQuery<Long> query = em.createQuery(
        "SELECT COUNT(p) FROM Product p",
        Long.class
);
Long count = query.getSingleResult();
```

## Average, min, max

```java
TypedQuery<Double> averageQuery = em.createQuery(
        "SELECT AVG(p.price) FROM Product p",
        Double.class
);
```

## Group by

```java
TypedQuery<Object[]> query = em.createQuery(
        "SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category",
        Object[].class
);
```

For cleaner code, map results into a report object.

## Common mistakes

- Forgetting that aggregates can return `null` for empty data.
- Returning raw `Object[]` all the way to UI code.
- Reading all rows into Java and aggregating when the database should do it.

## Mini exercise

Write JPQL for:

- count all books;
- average product price;
- minimum and maximum order total;
- count products by category.

## Quick summary

Aggregation queries let the database calculate summary values efficiently.
