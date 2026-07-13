# Domain Invariants

## Learning goals

- Understand what an invariant is.
- Keep business rules true after every operation.
- Place invariant checks in domain or service code, not only UI code.

## What is an invariant?

An invariant is a rule that must always remain true.

Examples:

- Account balance cannot be negative.
- Order cannot be paid twice.
- A returned item cannot be returned again.
- A deleted object cannot be modified.
- A user without permission cannot perform protected actions.

## Bad approach: check only in UI

```java
if (amount <= account.balance()) {
    account.setBalance(account.balance() - amount);
}
```

Another caller might skip that UI check and corrupt the account.

## Better approach: domain object protects itself

```java
public final class Account {
    private int balanceCents;

    public void withdraw(int cents) {
        if (cents <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (cents > balanceCents) {
            throw new BusinessRuleException("Insufficient balance");
        }
        balanceCents -= cents;
    }
}
```

## Service-level invariants

Some invariants involve more than one object, so the service protects them.

```java
public void returnBook(String memberId, String bookId) {
    Loan loan = loans.findActiveLoan(memberId, bookId)
            .orElseThrow(() -> new BusinessRuleException("No active loan"));
    loan.markReturned();
    loans.save(loan);
}
```

## Common mistakes

- Putting invariant checks only in screens or menus.
- Adding setters that bypass business methods.
- Updating state before checking the rule.
- Catching and ignoring invariant failures.

## Mini exercises

1. List three invariants for an order workflow.
2. Write an `Account.withdraw` method that protects balance.
3. Explain whether "user must be admin" is a domain or service-level invariant.

## Quick summary

Invariants protect correctness. Put them where every caller must obey them.
