# Repository Pattern with JPA

## Learning goals

- Put JPQL and `EntityManager` code inside repositories.
- Keep services focused on workflows.
- Return clear results from repository methods.

## Repository example

```java
public final class ProductRepository {
    private final EntityManager em;

    public ProductRepository(EntityManager em) {
        this.em = em;
    }

    public void create(Product product) {
        em.persist(product);
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(em.find(Product.class, id));
    }

    public List<Product> findAll() {
        return em.createQuery("SELECT p FROM Product p ORDER BY p.name", Product.class)
                .getResultList();
    }

    public void delete(Product product) {
        em.remove(product);
    }
}
```

## Custom query method

```java
public List<Product> findByCategory(String category) {
    return em.createQuery(
            "SELECT p FROM Product p WHERE p.category = :category ORDER BY p.name",
            Product.class)
            .setParameter("category", category)
            .getResultList();
}
```

## Common mistakes

- Putting JPQL directly in controllers or CLI classes.
- Returning mutable entities to code that should only read data.
- Hiding transaction boundaries inside random helper methods.

## Mini exercise

Create a `BookRepository` with `create`, `findById`, `findAll`, `delete`, and `findByAuthor`.

## Quick summary

JPA repositories keep query and persistence details out of business workflows.
