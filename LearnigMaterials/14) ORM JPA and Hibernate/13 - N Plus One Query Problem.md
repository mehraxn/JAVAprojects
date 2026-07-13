# N Plus One Query Problem

## Learning goals

- Understand the N+1 query problem.
- See why it hurts performance.
- Learn fetch join as one possible solution.

## What N+1 means

N+1 means one query loads parent rows, then one additional query runs for each parent.

Example:

```java
List<Order> orders = em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
for (Order order : orders) {
    System.out.println(order.getItems().size());
}
```

If there are 100 orders, this can become 101 queries.

## Fetch join solution

```java
SELECT DISTINCT o FROM Order o
LEFT JOIN FETCH o.items
```

This asks JPA to load orders and items together.

## When not to fetch too much

Fetch joining several large collections can create huge result sets. Fetch only what the use case needs.

## Common mistakes

- Not checking generated SQL.
- Fetching everything eagerly.
- Ignoring duplicate parent rows after joins.
- Solving every case with one massive query.

## Mini exercises

1. Identify N+1 in a loop over orders and items.
2. Write a fetch join query.
3. Explain when fetch join might load too much data.

## Quick summary

N+1 is a performance problem caused by repeated lazy loading. Fetch intentionally for each use case.
