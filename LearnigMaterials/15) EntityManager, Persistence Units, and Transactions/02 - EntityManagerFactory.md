# EntityManagerFactory

## Learning goals

- Understand what `EntityManagerFactory` creates.
- Learn why it is expensive.
- Know when to close it.

## What is EntityManagerFactory?

`EntityManagerFactory` creates `EntityManager` instances.

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("devPU");
EntityManager em = emf.createEntityManager();
```

## Expensive object

An `EntityManagerFactory` reads configuration, initializes the provider, and prepares metadata. Create it once for the application, not once per method.

## Typical lifecycle

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("devPU");
try {
    // application work
} finally {
    emf.close();
}
```

## Common mistakes

- Creating a new factory for every repository method.
- Forgetting to close the factory on shutdown.
- Using the wrong persistence unit name.

## Mini exercise

Sketch a small `JpaUtil` class that creates one `EntityManagerFactory` and exposes a method to create `EntityManager` objects.

## Quick summary

Create the factory once, use it to create many `EntityManager` instances, and close it at shutdown.
