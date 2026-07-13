# Workflow Testing

## Learning goals

- Test service-layer workflows.
- Cover success and failure paths.
- Prove failed operations leave state unchanged.

## Important test paths

For each workflow, test:

- success path;
- invalid input path;
- unauthorized path;
- not found path;
- duplicate path;
- failed operation leaves state unchanged.

## Example test idea

```java
@Test
void transferMovesMoneyBetweenAccounts() {
    AccountRepository accounts = new InMemoryAccountRepository();
    accounts.save(new Account("A", 1000));
    accounts.save(new Account("B", 200));

    TransferService service = new TransferService(accounts);
    service.transfer("A", "B", 300);

    assertEquals(700, accounts.findRequired("A").balance());
    assertEquals(500, accounts.findRequired("B").balance());
}
```

## Testing unchanged state

```java
@Test
void failedTransferDoesNotChangeBalances() {
    AccountRepository accounts = new InMemoryAccountRepository();
    accounts.save(new Account("A", 100));
    accounts.save(new Account("B", 200));

    TransferService service = new TransferService(accounts);

    assertThrows(BusinessRuleException.class, () -> service.transfer("A", "B", 500));
    assertEquals(100, accounts.findRequired("A").balance());
    assertEquals(200, accounts.findRequired("B").balance());
}
```

## What to assert

- Returned snapshot values.
- Repository state after the operation.
- Correct exception type.
- No partial changes after failure.
- Audit fields when relevant.

## Common mistakes

- Only testing the happy path.
- Testing implementation details instead of observable behavior.
- Ignoring unauthorized paths.
- Reusing the same repository state across tests.

## Mini exercise

Write test cases for approving a request. Include success, already approved, unauthorized, and missing request paths.

## Quick summary

Workflow tests prove that services coordinate domain objects correctly and safely handle failure.
