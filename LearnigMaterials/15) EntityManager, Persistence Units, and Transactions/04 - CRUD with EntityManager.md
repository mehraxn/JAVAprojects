# CRUD with EntityManager

## Learning goals

- Perform create, read, update, and delete operations.
- Know which operations require transactions.
- Keep examples small and readable.

## Create

```java
em.getTransaction().begin();
em.persist(new Product("Notebook"));
em.getTransaction().commit();
```

## Read

```java
Product product = em.find(Product.class, 1L);
```

## Update

Managed entities are tracked automatically inside a transaction.

```java
em.getTransaction().begin();
Product product = em.find(Product.class, 1L);
product.rename("Premium Notebook");
em.getTransaction().commit();
```

## Delete

```java
em.getTransaction().begin();
Product product = em.find(Product.class, 1L);
if (product != null) {
    em.remove(product);
}
em.getTransaction().commit();
```

## Merge detached object

```java
em.getTransaction().begin();
Product managed = em.merge(detachedProduct);
em.getTransaction().commit();
```

## Common mistakes

- Expecting `persist`, update, or `remove` to work without a transaction.
- Calling `remove` on a detached object.
- Ignoring `null` from `find`.

## Mini exercise

Write CRUD methods for a `Course` entity using `EntityManager`.

## Quick summary

CRUD with JPA is simple when you understand managed objects and transaction boundaries.
