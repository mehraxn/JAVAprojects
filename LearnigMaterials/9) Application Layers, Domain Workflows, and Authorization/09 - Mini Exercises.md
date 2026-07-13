# Mini Exercises

## 1. Account transfer

Implement a service method:

```java
void transfer(String fromAccountId, String toAccountId, int cents)
```

Requirements:

- amount must be positive;
- both accounts must exist;
- source account must have enough balance;
- failure must leave both balances unchanged.

## 2. Admin approval

Create a request approval workflow.

Requirements:

- only `ADMIN` and `MAINTAINER` can approve;
- missing request throws `NotFoundException`;
- already approved request throws `BusinessRuleException`;
- result is returned as an immutable snapshot.

## 3. Borrow and return

Create a library workflow with:

- `borrowItem(memberId, itemId)`;
- `returnItem(memberId, itemId)`;
- validation for missing member and missing item;
- validation for already borrowed items.

## 4. Audit fields

Add `createdBy`, `createdAt`, `modifiedBy`, and `modifiedAt` to a `Product` class. Use `Clock` in the service.

## 5. Workflow test plan

Choose one workflow and write a table with:

- success path;
- invalid input path;
- unauthorized path;
- not found path;
- duplicate path;
- unchanged-state path.

## Quick summary

Application-layer exercises are about complete operations, not isolated setters.
