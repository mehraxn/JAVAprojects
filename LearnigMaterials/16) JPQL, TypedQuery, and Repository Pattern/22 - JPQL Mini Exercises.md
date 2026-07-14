# JPQL Mini Exercises

## Learning goals

- Practice JPQL queries.
- Use parameters, joins, aggregations, projections, and pagination.
- Design repository methods with clear return types.

## Exercise 1: Find by name

Create `StudentRepository.findByName(String name)` using a named parameter.

## Exercise 2: Date range

Create `OrderRepository.findCreatedBetween(start, end)` using:

```text
createdAt >= start AND createdAt < end
```

## Exercise 3: Count by category

Count products by category and map results to a DTO.

## Exercise 4: Average by group

Calculate average order total by customer.

## Exercise 5: Normal join

Find orders by customer email.

## Exercise 6: Fetch join

Load orders with order items for an order-detail use case.

## Exercise 7: DTO projection

Create `InvoiceSummary(id, customerName, total)` and return it from a query.

## Exercise 8: Pagination

Return products page by page with deterministic sorting.

## Exercise 9: No result safely

Write `findBySku` returning `Optional<Product>`.

## Exercise 10: Generic CRUD repository

Create a generic repository and a specific `BookRepository` with custom methods.

## Quick summary

JPQL practice should happen inside repository methods with safe parameters and predictable results.
