# Architecture Testing Strategy

## Learning goals

- Test each architectural layer at the right level.
- Use fake repositories for service tests.
- Avoid testing everything only through `Main`.

## Why testing strategy matters

If every test launches the full program, failures are slow and hard to diagnose. Good architecture allows focused tests.

## Domain tests

Domain tests check rules inside one business object.

```java
@Test
void accountCannotWithdrawMoreThanBalance() {
    Account account = new Account("A-1", 100);
    assertThrows(BusinessRuleException.class, () -> account.withdraw(150));
}
```

## Service tests with fake repositories

Service tests check workflows without a database.

```java
InMemoryAccountRepository accounts = new InMemoryAccountRepository();
TransferService service = new TransferService(accounts);
```

This tests validation, lookups, state changes, and returned snapshots.

## Repository tests

Repository tests check storage behavior separately. If the repository uses JPA, use a test database and verify queries.

## CLI or controller tests

CLI/controller tests should verify input parsing, output, and error handling. They should not be the only tests for business rules.

## Suggested pyramid

```text
Many domain and service tests
Some repository tests
Few full-entry tests
```

## Common mistakes

- Testing everything through `Main`.
- Skipping failure paths.
- Using a real database for every service test.
- Testing private methods instead of behavior.

## Mini exercises

1. Write a domain test for `Order.cancel`.
2. Write a service test for account transfer using a fake repository.
3. Write a repository test for `findByCategory`.
4. Write one CLI test for invalid command handling.

## Quick summary

Test domain rules directly, service workflows with fakes, repositories separately, and entry points lightly.
