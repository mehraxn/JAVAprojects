# JPQL and Repository Exercises

## 1. Find students by name

Create:

```java
List<Student> findByName(String name)
```

Use JPQL with a named parameter.

## 2. Find orders in a date range

Create:

```java
List<Order> findCreatedBetween(LocalDateTime start, LocalDateTime end)
```

Use inclusive start and exclusive end boundaries.

## 3. Count products by category

Write a JPQL aggregation query and map the result to a report object.

## 4. Build a generic repository

Create:

- `save`
- `findById`
- `delete`

Use `Class<T>` for the entity type.

## 5. Test repositories

Use H2 and a test persistence unit. Clean data between tests.
