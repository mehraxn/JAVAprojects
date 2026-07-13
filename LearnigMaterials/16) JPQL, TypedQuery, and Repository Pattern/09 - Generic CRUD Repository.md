# Generic CRUD Repository

## Learning goals

- Understand generic CRUD repositories.
- Learn how `Class<T>` helps JPA find entity types.
- Know pros and cons.

## Basic generic repository

```java
public class CrudRepository<T, ID> {
    private final EntityManager em;
    private final Class<T> entityClass;

    public CrudRepository(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    public void save(T entity) {
        em.persist(entity);
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(em.find(entityClass, id));
    }

    public void delete(T entity) {
        em.remove(entity);
    }
}
```

## Why `Class<T>` is needed

JPA needs the runtime entity class:

```java
em.find(Product.class, 1L);
```

Generics alone do not preserve that class at runtime, so the repository receives `Class<T>`.

## Pros

- Reduces repeated CRUD code.
- Gives consistent method names.
- Useful for simple entities.

## Cons

- Custom queries still need specific repositories.
- Can hide important entity-specific behavior.
- May become too abstract for beginners.

## Mini exercise

Create a generic repository, then extend it with a `ProductRepository` that adds `findBySku`.

## Quick summary

Generic CRUD repositories reduce repetition, but specific repositories are still useful for business-specific queries.
