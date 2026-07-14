# Application Layer and Authorization Exercises

## Exercise 1: Bank transfer all-or-nothing

Difficulty: Hard

## Goal

Implement a transfer workflow that updates two accounts safely.

## Background

Transfers must not leave one account changed if the other update fails.

## Starter Code

```java
void transfer(String fromId, String toId, int cents) {
    // implement
}
```

## Requirements

- Source and target accounts must exist.
- Amount must be positive.
- Source must have enough balance.
- Failed transfer leaves both balances unchanged.

## Expected Behavior

Valid transfer moves money. Invalid transfer changes nothing.

## Test Cases

- Transfer 300 from 1000 to 200.
- Reject negative amount.
- Reject insufficient balance.
- Missing account leaves state unchanged.

## Hints

Validate before mutating.

## Common Mistakes

- Withdrawing before all validation passes.
- Saving one account before the second update succeeds.

## Bonus Challenge

Add transaction-style rollback for persistence.

## Solution Outline

Load both accounts, validate, then mutate both inside one workflow boundary.

## Exercise 2: Order cancellation idempotency

Difficulty: Medium

## Goal

Make repeated cancellation safe.

## Background

Cancel requests may be retried. A second cancel should not create duplicate side effects.

## Starter Code

```java
OrderSnapshot cancelOrder(String orderId) {
    // implement
}
```

## Requirements

- Missing order fails clearly.
- First cancel changes status.
- Second cancel returns current cancelled state.

## Expected Behavior

Calling cancel twice has the same final result as calling it once.

## Test Cases

- Cancel active order.
- Cancel already cancelled order.
- Missing order throws `NotFoundException`.

## Hints

Check current state before mutating.

## Common Mistakes

- Treating every repeated request as a new event.
- Throwing after state already changed.

## Bonus Challenge

Store cancellation timestamp only once.

## Solution Outline

If already cancelled, return snapshot. Otherwise cancel and save.

## Exercise 3: Admin-only approval workflow

Difficulty: Medium

## Goal

Protect approval with authorization.

## Background

Some operations should be allowed only for specific roles.

## Starter Code

```java
RequestSnapshot approve(String requestId, UserContext user) {
    // implement
}
```

## Requirements

- `ADMIN` can approve.
- `MAINTAINER` can approve if allowed by your matrix.
- `VIEWER` cannot approve.
- Already approved request fails or is idempotent; document choice.

## Expected Behavior

Unauthorized approval is rejected before mutation.

## Test Cases

- Admin approval succeeds.
- Viewer approval fails.
- Missing request fails.
- Already approved request follows documented behavior.

## Hints

Use `AuthorizationService`.

## Common Mistakes

- Checking authorization only in UI.
- Saving before permission check.

## Bonus Challenge

Add audit fields to approval.

## Solution Outline

Authorize, load request, validate state, mutate, save, return snapshot.

## Exercise 4: Audit createdAt and modifiedAt using Clock

Difficulty: Medium

## Goal

Add deterministic audit fields.

## Background

Direct system time makes tests unpredictable.

## Starter Code

```java
Clock clock = Clock.systemUTC();
```

## Requirements

- Use `createdBy`, `createdAt`, `modifiedBy`, `modifiedAt`.
- Inject `Clock`.
- Use fixed clock in tests.

## Expected Behavior

Timestamps are predictable in tests.

## Test Cases

- Creation sets created and modified fields.
- Update changes modified fields only.
- Fixed clock produces expected instant.

## Hints

Use `Instant.now(clock)`.

## Common Mistakes

- Calling `Instant.now()` directly in domain code.
- Allowing callers to set audit fields freely.

## Bonus Challenge

Track approvedBy and approvedAt separately.

## Solution Outline

Service supplies user ID and current instant to domain methods.

## Exercise 5: State consistency for borrow and return

Difficulty: Hard

## Goal

Keep borrowing state consistent.

## Background

A member cannot return an item that was not borrowed, and a returned item cannot be returned again.

## Starter Code

```java
LoanSnapshot borrow(String memberId, String bookId) { }
LoanSnapshot returnBook(String memberId, String bookId) { }
```

## Requirements

- Member exists.
- Book exists.
- Book is not already borrowed.
- Return requires active loan.

## Expected Behavior

Borrow and return maintain valid loan state.

## Test Cases

- Borrow available book.
- Reject duplicate borrow.
- Return borrowed book.
- Reject second return.

## Hints

Use a `Loan` domain object with state methods.

## Common Mistakes

- Storing only a boolean on book without loan history.
- Returning without checking active loan.

## Bonus Challenge

Add due date.

## Solution Outline

Service validates existence, domain object protects state transitions.

## Exercise 6: Centralized AuthorizationService

Difficulty: Medium

## Goal

Avoid scattered permission checks.

## Background

Permission logic becomes hard to maintain when copied into many services.

## Starter Code

```java
authorization.requireAnyRole(user, Role.ADMIN, Role.MAINTAINER);
```

## Requirements

- Create `Role`.
- Create `UserContext`.
- Implement `requireRole` and `requireAnyRole`.
- Test allowed and denied cases.

## Expected Behavior

Protected services call one authorization component.

## Test Cases

- Admin allowed.
- Viewer denied.
- Null user rejected.

## Hints

Keep exception messages clear.

## Common Mistakes

- Using raw strings for roles everywhere.
- Returning false and letting caller ignore it.

## Bonus Challenge

Add permission enum for operation-level checks.

## Solution Outline

Centralize role checks and throw `UnauthorizedException` on failure.

## Exercise 7: Custom exceptions

Difficulty: Easy

## Goal

Create meaningful business exceptions.

## Background

Specific exception types make failure paths clearer.

## Starter Code

```java
class BusinessException extends RuntimeException { }
```

## Requirements

- Add `InvalidInputException`.
- Add `NotFoundException`.
- Add `DuplicateIdException`.
- Add `UnauthorizedException`.
- Add `BusinessRuleException`.

## Expected Behavior

Each failure path uses a meaningful exception.

## Test Cases

- Missing product throws `NotFoundException`.
- Duplicate ID throws `DuplicateIdException`.
- Forbidden operation throws `UnauthorizedException`.

## Hints

Keep constructors simple.

## Common Mistakes

- Creating too many exception types.
- Catching and ignoring business exceptions.

## Bonus Challenge

Add an error code field.

## Solution Outline

Use a small hierarchy and document when each exception is used.

## Exercise 8: Fake repository workflow tests

Difficulty: Medium

## Goal

Test service logic without a database.

## Background

Fake repositories are small in-memory implementations for tests.

## Starter Code

```java
class FakeProductRepository implements ProductRepository {
    private final Map<String, Product> products = new LinkedHashMap<>();
}
```

## Requirements

- Test success path.
- Test invalid input.
- Test unauthorized path.
- Test not found.
- Test duplicate.
- Test state unchanged after failure.

## Expected Behavior

Service tests should be fast and focused.

## Test Cases

- Create product succeeds.
- Duplicate SKU fails.
- Failed create does not add product.

## Hints

Make fake repository obey the same contract as the real repository.

## Common Mistakes

- Testing only happy path.
- Making fake behavior too different from real behavior.

## Bonus Challenge

Add fake repository call counters for advanced assertions.

## Solution Outline

Use fake repositories to test service behavior, not persistence details.
