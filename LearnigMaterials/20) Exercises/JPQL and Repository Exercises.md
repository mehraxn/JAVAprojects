# JPQL and Repository Exercises

## Exercise 1: Find students by name

Difficulty: Easy

## Goal

Write a safe JPQL query with parameters.

## Background

JPQL uses entity names and fields.

## Starter Code

```java
List<Student> findByName(String name) { }
```

## Requirements

- Use `TypedQuery<Student>`.
- Use `:name` parameter.
- Return list.

## Expected Behavior

Matching students are returned.

## Test Cases

- Exact name match.
- No match returns empty list.
- Parameter is set.

## Hints

Do not concatenate input.

## Common Mistakes

- Using table names.
- Forgetting `setParameter`.

## Bonus Challenge

Add case-insensitive search.

## Solution Outline

Create JPQL over `Student`, bind parameter, return result list.

## Exercise 2: Find orders by date range

Difficulty: Medium

## Goal

Use inclusive start and exclusive end.

## Background

Date boundaries should be explicit.

## Starter Code

```java
findCreatedBetween(LocalDateTime start, LocalDateTime end)
```

## Requirements

- Validate start before end.
- Query `createdAt >= :start AND createdAt < :end`.
- Sort by date.

## Expected Behavior

Orders inside range are returned.

## Test Cases

- Start boundary included.
- End boundary excluded.
- Invalid range rejected.

## Hints

Use named parameters.

## Common Mistakes

- Inclusive end causing double counting.
- Comparing dates as strings.

## Bonus Challenge

Add pagination.

## Solution Outline

Validate, bind start/end, order results.

## Exercise 3: Count products by category

Difficulty: Medium

## Goal

Use aggregation and `GROUP BY`.

## Background

Aggregation is often better in the database than in Java.

## Starter Code

```java
record CategoryCount(String category, long count) {}
```

## Requirements

- Query category and count.
- Map to DTO.
- Sort by category.

## Expected Behavior

Each category has a count.

## Test Cases

- Empty table returns empty list.
- Multiple products grouped correctly.

## Hints

Use constructor expression or map `Object[]`.

## Common Mistakes

- Returning raw arrays to UI.
- Forgetting `GROUP BY`.

## Bonus Challenge

Add count only active products.

## Solution Outline

Use `SELECT category, COUNT(...) GROUP BY category`.

## Exercise 4: Average order totals by customer

Difficulty: Medium

## Goal

Calculate grouped averages.

## Background

Reports often group data by customer.

## Starter Code

```java
record CustomerAverage(String customerName, BigDecimal averageTotal) {}
```

## Requirements

- Join orders to customer.
- Group by customer.
- Return DTO.

## Expected Behavior

Each customer has an average order total.

## Test Cases

- Customer with two orders average is correct.
- Customer without orders is excluded or handled as documented.

## Hints

JPA average may return `Double`; document conversion if using money.

## Common Mistakes

- Using `double` carelessly for financial reports.
- Missing group by fields.

## Bonus Challenge

Use BigDecimal-safe calculation in Java after filtered query.

## Solution Outline

Filter relevant orders, group by customer, map report rows.

## Exercise 5: Join orders and customers

Difficulty: Medium

## Goal

Use normal join for filtering.

## Background

Normal joins are useful when related data decides which rows match.

## Starter Code

```java
findOrdersByCustomerEmail(String email)
```

## Requirements

- Join `Order` to `Customer`.
- Filter by email parameter.
- Return orders sorted by date.

## Expected Behavior

Only matching customer's orders are returned.

## Test Cases

- Existing email returns orders.
- Unknown email returns empty list.

## Hints

Use `JOIN o.customer c`.

## Common Mistakes

- Fetching customer when only filtering is needed.
- Concatenating email into JPQL.

## Bonus Challenge

Return `OrderSummary` DTO.

## Solution Outline

Join relation, bind email, sort.

## Exercise 6: Fetch join order items

Difficulty: Hard

## Goal

Load required relationship data efficiently.

## Background

Fetch join can solve lazy-loading and N+1 problems for a specific use case.

## Starter Code

```java
findOrderDetails(Long orderId)
```

## Requirements

- Use `LEFT JOIN FETCH o.items`.
- Use `DISTINCT`.
- Return one order or optional.

## Expected Behavior

Order and items are available before leaving repository boundary.

## Test Cases

- Order with items loads items.
- Order without items works.
- Missing order returns empty.

## Hints

Use `getResultList` to avoid `NoResultException`.

## Common Mistakes

- Forgetting `DISTINCT`.
- Fetch joining too much data.

## Bonus Challenge

Return `OrderDetailsSnapshot`.

## Solution Outline

Fetch join for the needed relationship only.

## Exercise 7: DTO projection

Difficulty: Medium

## Goal

Return selected fields into a DTO.

## Background

DTO projections avoid exposing entities for reports.

## Starter Code

```java
record InvoiceSummary(Long id, String customerName, BigDecimal total) {}
```

## Requirements

- Use JPQL constructor expression.
- Select only needed fields.
- Sort by invoice ID.

## Expected Behavior

Report receives DTOs, not entities.

## Test Cases

- DTO has correct fields.
- No entity mutation methods are exposed.

## Hints

Constructor package name must match.

## Common Mistakes

- Constructor parameter order mismatch.
- Using column names.

## Bonus Challenge

Add invoice status to DTO.

## Solution Outline

Write `SELECT new package.InvoiceSummary(...)`.

## Exercise 8: Pagination

Difficulty: Medium

## Goal

Return stable pages.

## Background

Pagination needs sorting.

## Starter Code

```java
findPage(int pageNumber, int pageSize)
```

## Requirements

- Validate page number and size.
- Use `setFirstResult`.
- Use `setMaxResults`.
- Add `ORDER BY`.

## Expected Behavior

Page results are deterministic.

## Test Cases

- First page returns first items.
- Second page skips first page.
- Invalid page size rejected.

## Hints

Decide if page numbers start at 0 or 1.

## Common Mistakes

- No ordering.
- Confusing offset and page number.

## Bonus Challenge

Return total count with page data.

## Solution Outline

Calculate offset, sort, limit.

## Exercise 9: Handle no result safely

Difficulty: Easy

## Goal

Avoid unsafe `getSingleResult`.

## Background

Missing data is often normal.

## Starter Code

```java
Optional<Product> findBySku(String sku)
```

## Requirements

- Use result list with max 1 or handle exception clearly.
- Return `Optional`.
- Enforce SKU uniqueness in mapping.

## Expected Behavior

Missing SKU returns `Optional.empty()`.

## Test Cases

- Existing SKU found.
- Missing SKU empty.
- Duplicate SKU prevented by data model.

## Hints

Use `setMaxResults(1)`.

## Common Mistakes

- Returning null.
- Swallowing multiple-result data issue.

## Bonus Challenge

Add `findRequiredBySku`.

## Solution Outline

Use safe list-based optional pattern.

## Exercise 10: Generic CRUD repository

Difficulty: Hard

## Goal

Create reusable CRUD behavior.

## Background

Generics can reduce repeated repository code.

## Starter Code

```java
class CrudRepository<T, ID> {
}
```

## Requirements

- Store `Class<T> entityClass`.
- Add `save`, `findById`, `delete`.
- Extend or compose for `BookRepository`.

## Expected Behavior

Generic methods work for multiple entity types.

## Test Cases

- Save and find book.
- Delete book.
- Specific repository adds `findByTitle`.

## Hints

JPA needs runtime class object.

## Common Mistakes

- Over-abstracting custom queries.
- Forgetting transaction ownership.

## Bonus Challenge

Add pagination support.

## Solution Outline

Generic repository handles simple CRUD; specific repositories handle custom queries.
