# merge vs managed update

## Learning goals

- Update managed entities correctly.
- Understand detached entities.
- Avoid ignoring the return value of `merge`.

## Managed update

If an entity is managed, update it directly inside a transaction.

```java
em.getTransaction().begin();
Product product = em.find(Product.class, 1L);
product.rename("New Name");
em.getTransaction().commit();
```

JPA tracks the change through dirty checking.

## Detached entity

An entity becomes detached when the `EntityManager` closes or the entity is detached/cleared.

```java
Product product = em.find(Product.class, 1L);
em.close();
product.rename("Detached Name"); // not tracked
```

## merge

`merge` copies detached state into a managed instance and returns that managed instance.

```java
em.getTransaction().begin();
Product managed = em.merge(detachedProduct);
managed.rename("Safe Update");
em.getTransaction().commit();
```

## Common mistake: ignoring merge result

```java
em.merge(detachedProduct);
detachedProduct.rename("Changed Again"); // this object is still detached
```

Use the returned managed object if you need to continue modifying it.

## Why it matters

Many beginner JPA bugs come from not knowing whether an entity is managed or detached.

## Common mistakes

- Calling `persist` on a detached entity.
- Ignoring the returned object from `merge`.
- Using `merge` when the entity is already managed.
- Assuming a detached object is automatically saved.

## Mini exercises

1. Update a managed `Course` entity.
2. Merge a detached `Product` and use the returned value.
3. Explain why `persist(detachedProduct)` can fail.

## Quick summary

Managed entities update through dirty checking. Detached entities need `merge`, and the returned managed copy matters.
