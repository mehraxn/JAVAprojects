# JOIN and FETCH JOIN

## Learning goals

- Use normal joins for filtering.
- Use fetch joins to load relationships.
- Understand duplicates and `DISTINCT`.

## Normal join

A normal join is useful when filtering by related data.

```java
TypedQuery<Order> query = em.createQuery(
        "SELECT o FROM Order o JOIN o.customer c WHERE c.email = :email",
        Order.class);
query.setParameter("email", email);
```

This filters orders by customer email.

## FETCH JOIN

A fetch join loads related data with the main entity.

```java
TypedQuery<Order> query = em.createQuery(
        "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id",
        Order.class);
query.setParameter("id", id);
```

This can prevent lazy-loading problems and reduce repeated queries.

## Why DISTINCT appears

Joining a collection can duplicate parent rows in the result. `DISTINCT` tells JPQL to return each parent entity once.

## When fetch join helps

Use fetch join when the use case needs related data immediately, such as an order detail screen or report.

## When not to fetch too much

Do not fetch every relationship by default. Large graphs can produce huge result sets.

## Common mistakes

- Using fetch join for every query.
- Forgetting `DISTINCT` with collection fetch joins.
- Fetching multiple large collections at once.
- Joining when a simple field filter is enough.

## Mini exercises

1. Write a join query for orders by customer email.
2. Write a fetch join query for order items.
3. Explain why duplicate orders can appear.

## Quick summary

Use joins to filter and fetch joins to load required relationships intentionally.
