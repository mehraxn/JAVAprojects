# Why JPQL Exists

## Learning goals

- Understand why JPQL exists.
- Learn that JPQL queries entities, not tables.
- Know when JPQL is useful.

## What JPQL is

JPQL means Java Persistence Query Language. It is a query language for JPA entities.

SQL queries tables and columns. JPQL queries entity classes and fields.

```java
SELECT s FROM Student s
```

This means: select `Student` entity objects.

## Why not always use find?

`EntityManager.find` works when you know the primary key.

JPQL is useful when you need:

- all students with a name;
- products in a category;
- orders inside a date range;
- counts and averages;
- sorted results.

## Common mistake

Using table names in JPQL:

```java
SELECT * FROM students
```

That is SQL, not JPQL.

## Mini exercise

Write a JPQL query that selects all `Book` entities.

## Quick summary

JPQL exists so JPA code can query entity objects without writing table-specific SQL for every operation.
