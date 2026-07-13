# Domain Workflows

## Learning goals

- Understand what a domain workflow is.
- Learn common workflow verbs.
- See how a service coordinates a workflow safely.

## What is a domain workflow?

A domain workflow is a meaningful business operation. It is usually described by a verb:

- create
- update
- delete
- connect
- disconnect
- approve
- cancel
- assign
- return
- transfer

A workflow often touches more than one object.

## Example: transfer money

```java
public final class TransferService {
    private final AccountRepository accounts;

    public TransferService(AccountRepository accounts) {
        this.accounts = accounts;
    }

    public void transfer(String fromId, String toId, int cents) {
        Account from = accounts.findRequired(fromId);
        Account to = accounts.findRequired(toId);

        from.withdraw(cents);
        to.deposit(cents);

        accounts.save(from);
        accounts.save(to);
    }
}
```

The domain objects protect their own balance rules. The service coordinates both accounts.

## Example: approve a request

```java
public RequestSnapshot approve(String requestId, User user) {
    requireRole(user, Role.ADMIN);
    Request request = requests.findRequired(requestId);
    request.approve(user.id());
    requests.save(request);
    return RequestSnapshot.from(request);
}
```

This workflow includes authorization, lookup, domain behavior, persistence, and a snapshot result.

## Workflow design checklist

- What input is required?
- Which objects must exist?
- Which duplicates are not allowed?
- Which relationships must be valid?
- Who is allowed to perform the operation?
- What state must change?
- What result should be returned?

## Common mistakes

- Updating one object and forgetting another.
- Allowing a workflow to continue after a validation failure.
- Returning internal mutable objects.
- Hiding important workflow rules inside UI code.

## Mini exercise

Design a `borrowItem(memberId, itemId)` workflow. Include success, not found, already borrowed, and unauthorized paths.

## Quick summary

Workflows are the real operations of an application. Services make those operations explicit and testable.
