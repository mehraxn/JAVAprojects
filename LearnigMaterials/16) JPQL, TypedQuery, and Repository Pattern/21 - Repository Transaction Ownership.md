# Repository Transaction Ownership

## Learning goals

- Decide whether services or repositories should own transactions.
- Understand simple-project trade-offs.
- Keep transaction boundaries practical.

## The key question

Should a repository method start and commit a transaction, or should the service do it?

## Service-owned transaction

For workflows touching multiple objects, the service usually owns the transaction.

```java
public void transfer(String sourceId, String targetId, int cents) {
    tx.begin();
    try {
        Account source = accounts.findRequired(sourceId);
        Account target = accounts.findRequired(targetId);
        source.withdraw(cents);
        target.deposit(cents);
        tx.commit();
    } catch (RuntimeException ex) {
        tx.rollback();
        throw ex;
    }
}
```

The service knows the whole workflow.

## Repository-owned transaction

In very small learning applications, a repository method may wrap one simple save operation. This is simpler but less flexible for multi-step workflows.

## Practical rule

- Single simple operation: repository transaction can be acceptable in small examples.
- Complete business workflow: service should own the transaction.
- Larger applications: transaction boundaries usually align with service/use-case methods.

## Common mistakes

- Starting one transaction per repository call inside a workflow that needs all-or-nothing behavior.
- Hiding transaction behavior so tests cannot reason about rollback.
- Mixing both styles without documentation.

## Mini exercises

1. Decide transaction ownership for `createProduct`.
2. Decide transaction ownership for account transfer.
3. Explain why invoice creation with lines needs one boundary.

## Quick summary

Repositories persist data. Services usually know the complete transaction boundary.
