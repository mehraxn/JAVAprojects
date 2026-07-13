# Database Constraints and Indexes

## Learning goals

- Understand constraints and indexes.
- Know why they matter for correctness and performance.
- Avoid reserved word naming problems.

## Constraints

Constraints protect data.

Examples:

- primary key;
- foreign key;
- unique constraint;
- not-null constraint;
- check constraint when supported.

## Unique constraint example

```java
@Table(
    name = "products",
    uniqueConstraints = @UniqueConstraint(name = "uk_product_sku", columnNames = "sku")
)
public class Product {
}
```

## Indexes

Indexes help the database find rows faster.

```java
@Table(
    name = "orders",
    indexes = @Index(name = "idx_orders_customer_id", columnList = "customer_id")
)
public class Order {
}
```

## Naming

Use clear table and column names. Avoid reserved words or quote them deliberately only when necessary.

## Common mistakes

- Expecting indexes to fix every slow query.
- Forgetting unique constraints for business keys.
- Using reserved words as table names.
- Creating indexes without knowing query patterns.

## Mini exercises

1. Add a unique constraint for employee number.
2. Add an index for invoice date.
3. Explain why foreign keys matter.

## Quick summary

Constraints protect correctness. Indexes support query performance.
