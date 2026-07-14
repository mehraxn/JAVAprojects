# NoResultException and NonUniqueResultException

## Learning goals

- Understand `getSingleResult`.
- Handle zero or multiple results safely.
- Return `Optional` where useful.

## getSingleResult behavior

`getSingleResult` expects exactly one result.

It can throw:

- `NoResultException` if nothing matches;
- `NonUniqueResultException` if more than one result matches.

## Risky example

```java
Product product = em.createQuery(
        "SELECT p FROM Product p WHERE p.sku = :sku",
        Product.class)
        .setParameter("sku", sku)
        .getSingleResult();
```

This is acceptable only if the data model guarantees exactly one result and you handle missing data.

## Safer Optional method

```java
public Optional<Product> findBySku(String sku) {
    List<Product> results = em.createQuery(
            "SELECT p FROM Product p WHERE p.sku = :sku",
            Product.class)
            .setParameter("sku", sku)
            .setMaxResults(1)
            .getResultList();

    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
}
```

## When unique result is expected

Use a unique database constraint for fields like SKU or employee number if code assumes uniqueness.

## Common mistakes

- Calling `getSingleResult` for optional data.
- Catching exceptions and returning `null` without explanation.
- Not enforcing uniqueness in the database.
- Ignoring multiple-result data quality issues.

## Mini exercises

1. Write `findByEmail` returning `Optional<Customer>`.
2. Explain when `getSingleResult` is safe.
3. Add a unique constraint for SKU.

## Quick summary

`getSingleResult` is strict. Use `Optional` patterns for queries that may not find a row.
