# SELECT Queries

## Learning goals

- Write simple JPQL `SELECT` queries.
- Sort results with `ORDER BY`.
- Select entities or specific values.

## Select all entities

```java
TypedQuery<Student> query = em.createQuery(
        "SELECT s FROM Student s",
        Student.class
);
List<Student> students = query.getResultList();
```

## Select with sorting

```java
TypedQuery<Product> query = em.createQuery(
        "SELECT p FROM Product p ORDER BY p.name ASC",
        Product.class
);
```

## Select a specific field

```java
TypedQuery<String> query = em.createQuery(
        "SELECT b.title FROM Book b ORDER BY b.title",
        String.class
);
```

## Common mistakes

- Missing a space when building query strings.
- Selecting a field but using the entity class as the result type.
- Returning too many rows when pagination is needed.

## Mini exercise

Write JPQL queries for:

- all courses;
- all book titles;
- all products ordered by price descending.

## Quick summary

Simple `SELECT` queries are the foundation for most repository query methods.
