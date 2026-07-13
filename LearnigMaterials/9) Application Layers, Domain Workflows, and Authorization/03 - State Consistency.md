# State Consistency

## Learning goals

- Understand all-or-nothing behavior.
- Learn why half-updated state is dangerous.
- Design workflows that leave objects consistent after failure.

## What is state consistency?

State consistency means the application data still makes sense after an operation finishes or fails.

Example: transferring money should not withdraw from one account unless the other account also receives the money.

## All-or-nothing behavior

A workflow should either:

- complete every required change; or
- fail and leave the previous state unchanged.

In database applications, transactions help enforce this. In in-memory exercises, you still need careful ordering and validation.

## Validate before mutating

```java
public void transfer(String fromId, String toId, int cents) {
    Account from = accounts.findRequired(fromId);
    Account to = accounts.findRequired(toId);

    if (cents <= 0) {
        throw new IllegalArgumentException("Amount must be positive");
    }
    if (!from.canWithdraw(cents)) {
        throw new BusinessRuleException("Insufficient funds");
    }

    from.withdraw(cents);
    to.deposit(cents);
}
```

Validation happens before state changes when possible.

## When validation cannot happen first

Some checks happen during the domain method. In that case, keep the workflow small and make failure behavior obvious. With databases, use transaction rollback.

## Common consistency problems

- Saving the first object before the second object succeeds.
- Catching an exception and continuing.
- Changing collections before checking duplicates.
- Updating audit fields for a failed operation.

## Mini exercise

Write pseudocode for assigning an employee to a department. What must be true before the assignment happens?

## Quick summary

Good workflows protect consistency. They validate early, mutate carefully, and rely on transactions when persistence is involved.
