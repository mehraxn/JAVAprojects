# Persistence Context and First Level Cache

## Learning goals

- Understand what a persistence context is.
- Learn managed entities and first-level cache behavior.
- Understand automatic dirty checking.

## What is a persistence context?

A persistence context is the set of entity objects currently managed by an `EntityManager`. When an entity is managed, JPA tracks it.

```java
EntityManager em = emf.createEntityManager();
Product product = em.find(Product.class, 1L); // managed if found
```

Inside the same `EntityManager`, JPA keeps one managed object instance for a database row.

## First-level cache

The first-level cache belongs to the `EntityManager`.

```java
Product a = em.find(Product.class, 1L);
Product b = em.find(Product.class, 1L);
System.out.println(a == b); // true inside the same persistence context
```

JPA does not need to create a second object for the same row in the same context.

## Automatic dirty checking

If a managed entity changes inside a transaction, JPA can detect the change and write it at flush/commit time.

```java
em.getTransaction().begin();
Product product = em.find(Product.class, 1L);
product.rename("Updated Name");
em.getTransaction().commit();
```

No explicit `update` method is required for a managed entity.

## Why it matters

The persistence context explains many JPA behaviors: identity, updates, flushes, lazy loading, and why detached objects behave differently.

## Common mistakes

- Expecting dirty checking on detached entities.
- Keeping an `EntityManager` open forever.
- Forgetting that the first-level cache is not shared across all `EntityManager` instances.
- Confusing Java object identity with database identity.

## Mini exercises

1. Load the same product twice in one `EntityManager` and explain why the references match.
2. Update a managed entity inside a transaction without calling `merge`.
3. Explain what changes after closing the `EntityManager`.

## Quick summary

The persistence context is the managed-entity workspace of an `EntityManager`. It provides first-level caching and dirty checking.
