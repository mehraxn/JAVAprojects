# ID Generation Strategies

## Learning goals

- Understand generated IDs.
- Compare `IDENTITY`, `SEQUENCE`, and `AUTO`.
- Separate business codes from database IDs.

## Surrogate ID vs natural ID

A surrogate ID is a technical database identifier, often a number.

A natural ID is a real business value, such as product SKU or employee number.

Both can exist:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false, unique = true)
private String sku;
```

## IDENTITY

```java
@GeneratedValue(strategy = GenerationType.IDENTITY)
```

The database generates the value, often with an auto-increment column.

## SEQUENCE

```java
@GeneratedValue(strategy = GenerationType.SEQUENCE)
```

The database sequence provides IDs. This is common in databases that support sequences well.

## AUTO

```java
@GeneratedValue(strategy = GenerationType.AUTO)
```

The provider chooses a strategy based on the database.

## UUID concept

Some applications use UUIDs for identifiers. UUIDs are useful when IDs must be generated without asking the database first, but they have storage and indexing trade-offs.

## Common mistakes

- Letting users edit generated IDs.
- Using a mutable business field as the primary key.
- Assuming every database handles ID generation the same way.
- Confusing `id` with a public business code.

## Mini exercises

1. Add a generated ID and unique SKU to `Product`.
2. Explain why `Order` might have both `id` and `orderNumber`.
3. Compare `IDENTITY` and `SEQUENCE` in one paragraph.

## Quick summary

Generated IDs are technical identity. Business identifiers should be modeled separately when needed.
