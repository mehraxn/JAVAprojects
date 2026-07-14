# EntityManager and Transaction Exercises

## Exercise 1: Create Product entity

Difficulty: Easy

## Goal

Map a basic JPA entity.

## Background

Entities need identity, required fields, and a no-argument constructor.

## Starter Code

```java
public class Product {
}
```

## Requirements

- Add `@Entity`.
- Add generated `id`.
- Add unique `sku`.
- Add required `name`.
- Add `BigDecimal price`.

## Expected Behavior

JPA recognizes `Product` as an entity.

## Test Cases

- Entity has ID.
- SKU is required and unique.
- Price is not stored as `double`.

## Hints

Use `@Column(nullable = false)`.

## Common Mistakes

- Missing no-argument constructor.
- Making generated ID user-editable.

## Bonus Challenge

Add enum status with `@Enumerated(EnumType.STRING)`.

## Solution Outline

Create fields, annotations, protected constructor, and domain constructor.

## Exercise 2: Persist product

Difficulty: Easy

## Goal

Use `EntityManager.persist`.

## Background

Writes need transactions.

## Starter Code

```java
void create(Product product) {
    em.persist(product);
}
```

## Requirements

- Begin transaction.
- Persist product.
- Commit.
- Roll back on failure.

## Expected Behavior

Product is stored.

## Test Cases

- Valid product persists.
- Invalid product rolls back.

## Hints

Use `EntityTransaction`.

## Common Mistakes

- Persisting without transaction.
- Committing after exception.

## Bonus Challenge

Return a `ProductSnapshot`.

## Solution Outline

Wrap persist in try/commit/catch/rollback.

## Exercise 3: Find product by ID

Difficulty: Easy

## Goal

Use `EntityManager.find`.

## Background

`find` returns null if no entity exists.

## Starter Code

```java
Product product = em.find(Product.class, id);
```

## Requirements

- Return `Optional<Product>`.
- Add `findRequired` variant.
- Do not throw `NullPointerException`.

## Expected Behavior

Missing product is handled clearly.

## Test Cases

- Existing ID returns product.
- Missing ID returns empty optional.
- Required method throws `NotFoundException`.

## Hints

Wrap nullable result with `Optional.ofNullable`.

## Common Mistakes

- Returning null silently.
- Throwing generic runtime errors with unclear messages.

## Bonus Challenge

Return DTO projection for read-only result.

## Solution Outline

Use `em.find`, optional wrapping, and clear exception.

## Exercise 4: Update managed entity

Difficulty: Medium

## Goal

Update a managed entity through dirty checking.

## Background

Managed entities are tracked by the persistence context.

## Starter Code

```java
Product product = em.find(Product.class, id);
product.rename(newName);
```

## Requirements

- Load inside transaction.
- Change managed entity.
- Commit.
- Do not call `merge` unnecessarily.

## Expected Behavior

Name change is saved.

## Test Cases

- Existing product name changes.
- Missing product fails.
- Blank name is rejected.

## Hints

Domain method should validate name.

## Common Mistakes

- Updating detached object and expecting dirty checking.
- Putting validation only in UI.

## Bonus Challenge

Update `modifiedAt` using `Clock`.

## Solution Outline

Find managed entity, call domain method, commit.

## Exercise 5: Merge detached entity

Difficulty: Medium

## Goal

Practice `merge`.

## Background

Detached objects are not tracked. `merge` returns a managed copy.

## Starter Code

```java
Product managed = em.merge(detachedProduct);
```

## Requirements

- Demonstrate detached update.
- Use returned managed instance.
- Commit transaction.

## Expected Behavior

Merged changes are saved.

## Test Cases

- Detached product changes saved after merge.
- Ignoring merge return value is explained.

## Hints

Close first `EntityManager` to create a detached object.

## Common Mistakes

- Calling `persist` on detached entity.
- Ignoring `merge` return value.

## Bonus Challenge

Compare managed update and merge in notes.

## Solution Outline

Detach, modify, merge, use returned entity, commit.

## Exercise 6: Delete entity

Difficulty: Medium

## Goal

Remove an entity safely.

## Background

`remove` expects a managed entity.

## Starter Code

```java
em.remove(product);
```

## Requirements

- Find managed product.
- Remove if found.
- Commit.
- Handle missing product.

## Expected Behavior

Deleted product no longer exists.

## Test Cases

- Existing product is deleted.
- Missing product returns false or throws documented exception.

## Hints

Use `find` inside transaction.

## Common Mistakes

- Removing detached entity directly.
- Ignoring relationship constraints.

## Bonus Challenge

Prevent deleting product used by an order.

## Solution Outline

Load managed entity, remove, commit.

## Exercise 7: Rollback failed transaction

Difficulty: Hard

## Goal

Prove failed transaction leaves no partial data.

## Background

Rollback protects consistency.

## Starter Code

```java
createTwoProducts(first, invalidSecond);
```

## Requirements

- Persist first product.
- Fail second product.
- Roll back transaction.
- Verify first product is not stored.

## Expected Behavior

No partial data after failure.

## Test Cases

- Both valid products persist.
- Invalid second product rolls back both.

## Hints

Use one transaction around both persists.

## Common Mistakes

- One transaction per product.
- Catching and swallowing exception.

## Bonus Challenge

Add row-level error object for failed import later.

## Solution Outline

Begin once, persist both, commit once, rollback on any failure.

## Exercise 8: Use H2 test persistence unit

Difficulty: Hard

## Goal

Create repeatable repository tests.

## Background

Tests should not touch development data.

## Starter Code

```xml
<persistence-unit name="appTestPU">
</persistence-unit>
```

## Requirements

- Use H2 in-memory database.
- Use `create-drop`.
- Load test persistence unit in tests.
- Clean state between tests.

## Expected Behavior

Tests run independently and safely.

## Test Cases

- Create/find product.
- Update product.
- Delete product.
- Rollback test.

## Hints

Put test `persistence.xml` under `src/test/resources/META-INF`.

## Common Mistakes

- Using development persistence unit in tests.
- Forgetting names must match code.

## Bonus Challenge

Add repository integration tests named `ProductRepositoryIT`.

## Solution Outline

Configure test PU, create factory, open EntityManager per test, close resources.
