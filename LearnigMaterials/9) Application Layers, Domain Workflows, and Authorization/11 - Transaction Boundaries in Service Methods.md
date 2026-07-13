# Transaction Boundaries in Service Methods

## Learning goals

- Understand all-or-nothing workflow boundaries.
- Validate first and mutate after validation.
- Connect in-memory consistency to database transactions.

## What is a transaction boundary?

A transaction boundary is the start and end of a unit of work. Inside that boundary, all required changes should succeed together or fail together.

Service methods often define this boundary because they know the full workflow.

## In-memory example: bank transfer

```java
public void transfer(String sourceId, String targetId, int cents) {
    Account source = accounts.findRequired(sourceId);
    Account target = accounts.findRequired(targetId);

    if (cents <= 0) {
        throw new IllegalArgumentException("Amount must be positive");
    }
    if (!source.canWithdraw(cents)) {
        throw new BusinessRuleException("Insufficient balance");
    }

    source.withdraw(cents);
    target.deposit(cents);
}
```

The service validates both accounts and the amount before mutation.

## Database connection

With JPA or JDBC, the same idea becomes:

```java
begin transaction
validate
mutate
save
commit
if failure: rollback
```

## Why service boundaries are practical

The repository usually does not know the full workflow. It can save one object, but the service knows whether two accounts, an order and payment, or a member and loan must change together.

## Common mistakes

- Saving the first object before validating the second.
- Starting transactions inside many tiny repository methods when the service needs one larger transaction.
- Continuing after a failed validation.
- Treating rollback as optional.

## Mini exercises

1. Write transaction-boundary pseudocode for order payment.
2. Explain why transfer should not be two unrelated repository saves.
3. Add a failed-transfer test that proves balances stay unchanged.

## Quick summary

Transaction boundaries belong around complete workflows. Validate first, mutate second, and roll back on failure.
