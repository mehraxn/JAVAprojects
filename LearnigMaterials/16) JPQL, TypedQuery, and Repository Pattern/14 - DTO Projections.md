# DTO Projections

## Learning goals

- Select only needed fields.
- Use constructor expressions.
- Return report DTOs instead of full entities.

## Why DTO projections matter

Sometimes a screen or report needs only a few fields. Returning full entities can load too much data and expose mutable persistence objects.

## DTO class

```java
public record StudentSummary(Long id, String name, Double averageScore) {
}
```

## Constructor expression

```java
TypedQuery<StudentSummary> query = em.createQuery(
        "SELECT new com.example.StudentSummary(s.id, s.name, AVG(g.value)) " +
        "FROM Student s JOIN s.grades g GROUP BY s.id, s.name",
        StudentSummary.class);
```

The package and constructor must match the DTO.

## Report DTO example

```java
public record ProductCategoryCount(String category, long count) {
}
```

```java
SELECT new com.example.ProductCategoryCount(p.category, COUNT(p))
FROM Product p
GROUP BY p.category
```

## Common mistakes

- Using table column names instead of entity fields.
- Constructor parameter order does not match.
- Returning entities when a read-only report is enough.
- Putting business mutation methods on DTOs.

## Mini exercises

1. Create `OrderSummary(orderId, customerName, total)`.
2. Write a JPQL constructor expression for product counts by category.
3. Explain why DTO projection can be safer than returning entities.

## Quick summary

DTO projections return exactly the data a use case needs, often improving clarity and performance.
