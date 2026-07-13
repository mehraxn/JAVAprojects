# Entity Lifecycle States

## Learning goals

- Understand transient, managed, detached, and removed entities.
- Know why lifecycle state affects JPA behavior.

## Transient

A transient entity is a normal new Java object that JPA does not know about.

```java
Product product = new Product("Notebook"); // transient
```

## Managed

A managed entity is attached to an active persistence context.

```java
em.getTransaction().begin();
em.persist(product); // managed
em.getTransaction().commit();
```

Changes to managed entities can be detected and written at commit.

## Detached

A detached entity was managed before but is no longer attached.

```java
em.close(); // managed entities from this context become detached
```

Use `merge` to copy detached state into a managed instance.

## Removed

A removed entity is scheduled for deletion.

```java
em.remove(product);
```

## Common mistakes

- Updating a detached object and expecting automatic database changes.
- Calling `remove` on a detached object.
- Confusing Java object identity with database identity.

## Mini exercise

Describe the lifecycle state after each step: `new Product`, `persist`, `commit`, `close`, `merge`, `remove`.

## Quick summary

JPA behavior depends on whether an entity is transient, managed, detached, or removed.
