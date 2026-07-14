# N Plus One Problem with JPQL

## Learning goals

- Recognize N+1 from a JPQL point of view.
- Fix common lazy-loading loops.
- Avoid fetching too much data.

## Bad pattern

```java
List<Order> orders = em.createQuery(
        "SELECT o FROM Order o",
        Order.class)
        .getResultList();

for (Order order : orders) {
    System.out.println(order.getItems().size());
}
```

The first query loads orders. Each lazy `items` access may trigger another query.

## Fetch join solution

```java
List<Order> orders = em.createQuery(
        "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items",
        Order.class)
        .getResultList();
```

This can load the needed relationship in one query.

## Alternative: DTO query

If the use case only needs counts, use aggregation instead of loading full items.

```java
SELECT o.id, COUNT(i)
FROM Order o JOIN o.items i
GROUP BY o.id
```

## Common mistakes

- Setting every relationship eager.
- Fetch joining too many large collections.
- Not checking generated SQL.
- Loading entities when a DTO projection is enough.

## Mini exercises

1. Identify N+1 in a loop over customers and orders.
2. Write a fetch join solution.
3. Write a DTO aggregation alternative.

## Quick summary

N+1 often appears when code loops over lazy relationships. Fix it by fetching or projecting what the use case needs.
