# flush, clear, detach, and close

## Learning goals

- Understand `flush`, `clear`, `detach`, and `close`.
- Know that flush is not commit.
- Use each method safely.

## flush

`flush` sends pending SQL changes to the database, but it does not commit the transaction.

```java
em.getTransaction().begin();
em.persist(new Product("Notebook"));
em.flush(); // SQL may be sent now
em.getTransaction().commit(); // transaction is finalized here
```

If the transaction rolls back after `flush`, the database changes should still roll back.

## clear

`clear` detaches all managed entities from the persistence context.

```java
em.clear();
```

After `clear`, dirty checking no longer tracks those objects.

## detach

`detach` removes one entity from the persistence context.

```java
Product product = em.find(Product.class, 1L);
em.detach(product);
```

Changes to `product` after detach are not automatically saved.

## close

`close` closes the `EntityManager`.

```java
em.close();
```

After closing, the `EntityManager` cannot be used.

## When to use them

- `flush`: force SQL before commit when you need early constraint feedback.
- `clear`: process many rows without keeping all managed objects.
- `detach`: stop tracking one object.
- `close`: release resources after a unit of work.

## Common mistakes

- Thinking `flush` commits data.
- Calling methods on a closed `EntityManager`.
- Detaching an entity and expecting dirty checking.
- Clearing the context and then wondering why updates were not saved.

## Mini exercises

1. Explain the difference between flush and commit.
2. Detach a product and predict whether later changes are saved.
3. Describe why batch processing might use `flush` and `clear`.

## Quick summary

`flush`, `clear`, `detach`, and `close` control persistence context behavior. They are powerful but easy to misuse.
