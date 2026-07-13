# Mini Exercises

## 1. Find by name

Create `StudentRepository.findByName(String name)` using a named JPQL parameter.

## 2. Date range

Create `OrderRepository.findCreatedBetween(start, end)` using inclusive start and exclusive end boundaries.

## 3. Count by category

Create a query that counts products by category and maps the result to a report object.

## 4. Book repository

Create a `BookRepository` with:

- `create`;
- `findById`;
- `findAll`;
- `findByTitleContaining`;
- `delete`.

## 5. Generic repository

Create a generic CRUD repository and then add a specific `ProductRepository` for custom product queries.

## 6. Repository tests

Use H2 and a test persistence unit to test:

- create;
- find by ID;
- custom query;
- delete.

## Quick summary

JPQL exercises are best practiced inside repository methods with clear parameters and predictable return types.
