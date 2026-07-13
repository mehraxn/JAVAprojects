# Mini Exercises

## 1. Create an EntityManager

Write code that:

- creates an `EntityManagerFactory`;
- creates an `EntityManager`;
- finds a `Student`;
- closes both resources.

## 2. Add persistence.xml

Create a `persistence.xml` with:

- persistence unit name;
- provider;
- JDBC URL;
- username and password placeholders;
- schema generation setting.

Do not add real secrets.

## 3. CRUD methods

Write methods for:

- create product;
- find product;
- update product name;
- delete product.

## 4. Transaction rollback

Create a method that saves an order and rolls back if any item fails validation.

## 5. H2 test setup

Create a test persistence unit using H2 in-memory mode. Explain why it should be separate from development data.

## Quick summary

These exercises practice the core JPA workflow: configure, open, transact, persist, query, roll back, and close.
