# Common Mistakes

## Using table names in JPQL

JPQL uses entity names:

```java
SELECT p FROM Product p
```

Not:

```sql
SELECT * FROM products
```

## Forgetting parameters

If the query contains `:category`, code must call:

```java
query.setParameter("category", category);
```

## Concatenating user input

Do not build queries with raw user input:

```java
// Avoid
"SELECT p FROM Product p WHERE p.name = '" + input + "'"
```

Use parameters.

## getSingleResult surprises

`getSingleResult` fails for zero results and for multiple results. Use it only when exactly one result is guaranteed.

## Returning mutable entities carelessly

Entities are mutable and may be managed by JPA. For read-only public results, consider snapshots or report objects.

## Filtering in Java unnecessarily

Avoid loading all rows and filtering in Java when the database can filter:

```java
WHERE p.category = :category
```

## Mini exercise

Find three problems in a repository method that reads every product, filters by category in Java, and returns managed entities to UI code.

## Quick summary

Most JPQL problems come from mixing SQL thinking with entity-based querying.
