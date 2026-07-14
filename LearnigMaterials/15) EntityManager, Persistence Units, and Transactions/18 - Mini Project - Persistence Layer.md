# Mini Project - Persistence Layer

## Learning goals

- Practice a small JPA persistence layer.
- Use `EntityManager`, repositories, transactions, and H2.
- Design tests for rollback behavior.

## Goal

Build a small product persistence layer.

## Required entity

Create `Product` with:

- generated `id`;
- unique `sku`;
- required `name`;
- `BigDecimal price`;
- status enum.

## Required repository

Create `ProductRepository` with:

- `create(Product product)`;
- `findById(Long id)`;
- `findBySku(String sku)`;
- `findAll()`;
- `updateName(Long id, String newName)`;
- `delete(Long id)`.

## Required persistence setup

Use:

- development persistence unit;
- test persistence unit;
- H2 test database;
- `create-drop` for tests.

## Required transaction practice

Write a service method that creates two products in one transaction. Force the second product to violate a validation rule and verify rollback.

## Required tests

- create product;
- find product by ID;
- find product by SKU;
- update managed entity;
- delete product;
- failed transaction rolls back.

## Hints

- Keep the service responsible for transaction boundaries.
- Keep repository methods focused on persistence.
- Do not use real secrets.
- Use immutable snapshots for service return values if exposing results outside persistence code.

## Common mistakes

- Using the development persistence unit in tests.
- Ignoring the returned value from `merge`.
- Forgetting to close `EntityManager`.
- Committing after an exception.

## Solution outline

1. Create entity and enum.
2. Create repository.
3. Create service with transaction helper.
4. Create H2 test persistence unit.
5. Write rollback test last.

## Quick summary

This mini project connects entity mapping, repository design, transaction boundaries, and test persistence setup.
