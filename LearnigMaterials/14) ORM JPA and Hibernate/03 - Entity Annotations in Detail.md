# Entity Annotations in Detail

## Learning goals

- Understand the most common JPA entity annotations.
- Know which annotations affect tables, columns, IDs, and ignored fields.
- Use `java.time` types for modern date/time fields.

## Basic entity

```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String sku;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Transient
    private boolean selectedInUi;
}
```

## Annotation meanings

- `@Entity`: class is persistent.
- `@Table`: custom table name or constraints.
- `@Id`: primary key.
- `@GeneratedValue`: database or provider generates the ID.
- `@Column`: column rules.
- `@Transient`: Java field is not persisted.
- `@Enumerated`: enum storage strategy.

## About date annotations

`@Temporal` is mainly for old `java.util.Date` and `Calendar`. Prefer `LocalDate`, `LocalDateTime`, and `Instant` in modern Java.

## Common mistakes

- Forgetting a no-argument constructor.
- Persisting UI-only fields accidentally.
- Storing enums as ordinal numbers without understanding migration risk.
- Confusing Java validation with database constraints.

## Mini exercises

1. Create an `Employee` entity with `id`, `employeeNumber`, and `name`.
2. Add an enum status using `EnumType.STRING`.
3. Add one field that should not be persisted.

## Quick summary

JPA annotations describe how Java classes and fields map to relational database structures.
