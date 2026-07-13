# Date Range Queries

## Learning goals

- Query by date ranges.
- Use start and end boundaries clearly.
- Avoid off-by-one date mistakes.

## Inclusive start, exclusive end

A common pattern is:

```java
WHERE o.createdAt >= :start AND o.createdAt < :end
```

This works well for day, month, and year ranges.

## Example

```java
TypedQuery<Order> query = em.createQuery(
        "SELECT o FROM Order o WHERE o.createdAt >= :start AND o.createdAt < :end ORDER BY o.createdAt",
        Order.class
);
query.setParameter("start", startInstant);
query.setParameter("end", endInstant);
```

## Validate dates

```java
if (!start.isBefore(end)) {
    throw new IllegalArgumentException("Start must be before end");
}
```

## LocalDate example

For a full day:

```java
LocalDate day = LocalDate.of(2026, 1, 15);
LocalDateTime start = day.atStartOfDay();
LocalDateTime end = day.plusDays(1).atStartOfDay();
```

## Common mistakes

- Using an inclusive end and accidentally double-counting boundary rows.
- Forgetting to validate `start <= end`.
- Comparing dates as strings.

## Mini exercise

Write a repository method that finds invoices created during one calendar month.

## Quick summary

Date range queries need explicit boundary rules. Inclusive start and exclusive end is often the cleanest pattern.
