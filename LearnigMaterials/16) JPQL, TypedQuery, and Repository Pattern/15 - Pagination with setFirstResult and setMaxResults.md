# Pagination with setFirstResult and setMaxResults

## Learning goals

- Use JPQL pagination.
- Understand offset and page number.
- Avoid unstable page results.

## Basic pagination

```java
TypedQuery<Product> query = em.createQuery(
        "SELECT p FROM Product p ORDER BY p.name, p.id",
        Product.class);
query.setFirstResult(20);
query.setMaxResults(10);
List<Product> page = query.getResultList();
```

This skips 20 rows and returns up to 10.

## Page number to offset

```java
int offset = pageNumber * pageSize;
query.setFirstResult(offset);
query.setMaxResults(pageSize);
```

If page numbers start at 1, use:

```java
int offset = (pageNumber - 1) * pageSize;
```

## Sorting is required

Pagination without `ORDER BY` is unstable. Rows may appear in different pages between runs.

## Common mistakes

- Forgetting `ORDER BY`.
- Confusing page number with offset.
- Allowing negative page size.
- Using pagination with collection fetch joins without understanding provider behavior.

## Mini exercises

1. Return page 3 of products with 20 items per page.
2. Validate page size and page number.
3. Explain why sorting by name and ID is more deterministic than sorting only by name.

## Quick summary

Pagination uses offset and limit. Always sort results for predictable pages.
