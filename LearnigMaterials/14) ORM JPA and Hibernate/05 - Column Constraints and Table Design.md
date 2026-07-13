# Column Constraints and Table Design

## Learning goals

- Use column constraints intentionally.
- Understand database constraints vs Java validation.
- Design readable table names and columns.

## Common column options

```java
@Column(nullable = false, unique = true, length = 80)
private String email;

@Column(precision = 12, scale = 2)
private BigDecimal totalAmount;
```

## What each option means

- `nullable = false`: database should reject null.
- `unique = true`: values should not duplicate.
- `length`: maximum string length.
- `precision`: total digits for decimal numbers.
- `scale`: digits after decimal point.

## Java validation and database constraints

Use both:

- Java validation gives better error messages before saving.
- Database constraints protect data even if code has a bug.

## Table design basics

Use clear names:

```java
@Table(name = "orders")
```

Avoid reserved words such as `user` or `order` as raw table names without checking the database.

## Common mistakes

- Relying only on Java validation.
- Relying only on database errors.
- Making every string length unlimited.
- Using `double` for money values.

## Mini exercises

1. Add constraints to an `Invoice` entity.
2. Decide which fields should be unique.
3. Explain why `BigDecimal` is better than `double` for invoice totals.

## Quick summary

Column constraints help protect data quality at the database level while Java validation improves workflow clarity.
