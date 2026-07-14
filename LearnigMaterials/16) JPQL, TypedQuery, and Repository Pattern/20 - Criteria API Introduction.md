# Criteria API Introduction

## Learning goals

- Understand why Criteria API exists.
- See a small type-safe dynamic query.
- Know why it is more verbose than JPQL.

## What is Criteria API?

Criteria API builds queries with Java objects instead of query strings. It is useful for dynamic filters.

## Small example

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Product> cq = cb.createQuery(Product.class);
Root<Product> product = cq.from(Product.class);

cq.select(product)
  .where(cb.equal(product.get("category"), category))
  .orderBy(cb.asc(product.get("name")));

List<Product> results = em.createQuery(cq).getResultList();
```

## When it helps

Use Criteria API when a query has optional filters:

- category may or may not be present;
- minimum price may or may not be present;
- active flag may or may not be present.

## Trade-off

Criteria API avoids string concatenation for dynamic queries, but it is more verbose and can be harder for beginners to read.

## Common mistakes

- Using Criteria API for every simple query.
- Building dynamic JPQL by unsafe string concatenation instead.
- Forgetting that field names are still strings unless using a metamodel.

## Mini exercises

1. Build a Criteria query for active products.
2. Add an optional category condition.
3. Compare readability with JPQL.

## Quick summary

Criteria API is useful for dynamic, type-oriented queries, but simple JPQL is often easier to read.
