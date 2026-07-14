# Query Injection and Safe Parameters

## Learning goals

- Avoid concatenating user input into queries.
- Use `setParameter`.
- Understand JPQL injection risk.

## Bad query construction

```java
String jpql = "SELECT p FROM Product p WHERE p.name = '" + input + "'";
```

If input contains special query text, the query can behave differently than intended.

## Safe parameter

```java
TypedQuery<Product> query = em.createQuery(
        "SELECT p FROM Product p WHERE p.name = :name",
        Product.class);
query.setParameter("name", input);
```

The parameter is treated as a value, not as query syntax.

## SQL injection vs JPQL injection

SQL injection targets SQL strings. JPQL injection targets JPQL strings. The prevention habit is the same: do not concatenate untrusted input into query text.

## LIKE parameters

```java
query.setParameter("pattern", "%" + searchText.toLowerCase() + "%");
```

The wildcard can be part of the parameter value.

## Common mistakes

- Concatenating user input for `WHERE`.
- Concatenating sort fields without validation.
- Forgetting to set a named parameter.
- Assuming JPQL is automatically safe without parameters.

## Mini exercises

1. Refactor a concatenated product-name query.
2. Write a safe `LIKE` query.
3. Explain why sort field names need validation.

## Quick summary

Use named parameters for values. Treat query text as code, not as a place for raw user input.
