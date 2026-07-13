# WHERE Conditions and Parameters

## Learning goals

- Use `WHERE` clauses.
- Use named parameters.
- Avoid string concatenation with user input.

## Named parameter example

```java
TypedQuery<Student> query = em.createQuery(
        "SELECT s FROM Student s WHERE s.name = :name",
        Student.class
);
query.setParameter("name", "Amina");
List<Student> students = query.getResultList();
```

## Multiple conditions

```java
TypedQuery<Product> query = em.createQuery(
        "SELECT p FROM Product p WHERE p.category = :category AND p.active = true",
        Product.class
);
query.setParameter("category", "Books");
```

## LIKE query

```java
TypedQuery<Book> query = em.createQuery(
        "SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(:pattern)",
        Book.class
);
query.setParameter("pattern", "%java%");
```

## Common mistakes

- Concatenating user input into query strings.
- Forgetting to set a parameter.
- Misspelling parameter names.
- Using `=` when `LIKE` is intended.

## Mini exercise

Write a query that finds products with a minimum price and an active flag.

## Quick summary

Use named parameters for safer, clearer queries.
