# TypedQuery

## Learning goals

- Understand `TypedQuery`.
- Use `setParameter`, `getResultList`, and `getSingleResult`.
- Avoid unsafe casts.

## What is TypedQuery?

`TypedQuery<T>` tells Java what type the query returns.

```java
TypedQuery<Product> query = em.createQuery(
        "SELECT p FROM Product p",
        Product.class
);
```

The result is a `List<Product>`.

## Common methods

```java
query.setParameter("name", "Notebook");
List<Product> products = query.getResultList();
```

For one result:

```java
Product product = query.getSingleResult();
```

## Be careful with getSingleResult

`getSingleResult` throws an exception if there is no result or more than one result.

A safer repository method can use `getResultList`:

```java
List<Product> results = query.setMaxResults(1).getResultList();
return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
```

## Common mistakes

- Using raw `Query` when `TypedQuery` is available.
- Calling `getSingleResult` without thinking about zero results.
- Setting a parameter with the wrong name.

## Mini exercise

Write a `findBySku` method that returns `Optional<Product>`.

## Quick summary

`TypedQuery` gives safer query results and clearer repository code.
