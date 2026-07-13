# What EntityManager Does

## Learning goals

- Understand the role of `EntityManager`.
- Learn the most common methods.
- Know why it belongs near the persistence layer.

## What is EntityManager?

`EntityManager` is the main JPA object used to work with entities.

Common methods:

- `persist`
- `find`
- `merge`
- `remove`
- `createQuery`

## Basic usage

```java
EntityManager em = entityManagerFactory.createEntityManager();
try {
    Student student = em.find(Student.class, 1L);
} finally {
    em.close();
}
```

`EntityManager` instances are lightweight compared with `EntityManagerFactory`, but they should still be closed.

## Common operations

```java
em.persist(new Student("Amina"));
Student found = em.find(Student.class, 1L);
Student updated = em.merge(detachedStudent);
em.remove(found);
```

## Common mistakes

- Forgetting to close `EntityManager`.
- Using one `EntityManager` forever.
- Calling persistence methods outside a needed transaction.
- Mixing `EntityManager` code into UI classes.

## Mini exercise

Write pseudocode for creating an `EntityManager`, finding a `Product`, and closing the `EntityManager`.

## Quick summary

`EntityManager` is the JPA work object for creating, finding, updating, deleting, and querying entities.
