# Testing Repositories

## Learning goals

- Test JPA repository methods.
- Use H2 and a test persistence unit.
- Clean data between tests.

## Test persistence unit

Use a separate persistence unit for tests so tests do not touch development data.

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("testPU");
```

## Test structure

```java
@BeforeEach
void setUp() {
    em = emf.createEntityManager();
    repository = new ProductRepository(em);
}

@AfterEach
void tearDown() {
    if (em != null) {
        em.close();
    }
}
```

## Transaction in tests

```java
em.getTransaction().begin();
repository.create(new Product("Notebook"));
em.getTransaction().commit();
```

## Clean database

Options:

- delete rows after each test;
- recreate schema;
- use a fresh in-memory database;
- wrap test changes in a rollback.

## Common mistakes

- Sharing dirty state between tests.
- Using development persistence settings in tests.
- Forgetting transactions for writes.
- Testing only `findAll`.

## Mini exercise

Write tests for `ProductRepository.findByCategory` using H2 and a test persistence unit.

## Quick summary

Repository tests prove that JPQL, mappings, and transaction assumptions work together.
