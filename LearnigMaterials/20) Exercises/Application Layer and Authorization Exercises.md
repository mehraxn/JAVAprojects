# Application Layer and Authorization Exercises

## 1. Account transfer

Implement:

```java
void transfer(String fromAccountId, String toAccountId, int cents)
```

Requirements:

- both accounts must exist;
- amount must be positive;
- source account must have enough balance;
- failure leaves both balances unchanged.

## 2. Role-based approval

Implement request approval.

Requirements:

- `ADMIN` can approve;
- `VIEWER` cannot approve;
- missing request throws `NotFoundException`;
- already approved request throws `BusinessRuleException`;
- result is an immutable snapshot.

## 3. Audit fields

Add:

- `createdBy`
- `createdAt`
- `modifiedBy`
- `modifiedAt`

Use `Clock` so tests can use fixed time.

## 4. Workflow tests

Write tests for:

- success path;
- invalid input;
- unauthorized user;
- missing object;
- duplicate object;
- failed operation leaves state unchanged.
