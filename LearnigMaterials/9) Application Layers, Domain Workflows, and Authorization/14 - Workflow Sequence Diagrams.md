# Workflow Sequence Diagrams

## Learning goals

- Draw simple workflow sequence diagrams.
- Show service, repository, domain, and authorization interactions.
- Use diagrams to clarify all-or-nothing behavior.

## Create order

```mermaid
sequenceDiagram
    participant Caller
    participant OrderService
    participant ProductRepository
    participant OrderRepository
    Caller->>OrderService: createOrder(customerId, productIds)
    OrderService->>ProductRepository: findRequired(productIds)
    OrderService->>OrderService: validate request
    OrderService->>OrderRepository: save(order)
    OrderService-->>Caller: OrderSnapshot
```

## Transfer money

```mermaid
sequenceDiagram
    participant Caller
    participant TransferService
    participant AccountRepository
    Caller->>TransferService: transfer(sourceId, targetId, amount)
    TransferService->>AccountRepository: findRequired(sourceId)
    TransferService->>AccountRepository: findRequired(targetId)
    TransferService->>TransferService: validate amount and balance
    TransferService->>TransferService: withdraw and deposit
    TransferService->>AccountRepository: save both accounts
    TransferService-->>Caller: success
```

## Approve request

```mermaid
sequenceDiagram
    participant Caller
    participant RequestService
    participant AuthorizationService
    participant RequestRepository
    Caller->>RequestService: approve(requestId, user)
    RequestService->>AuthorizationService: requireAnyRole(user, ADMIN, MAINTAINER)
    RequestService->>RequestRepository: findRequired(requestId)
    RequestService->>RequestService: approve domain object
    RequestService->>RequestRepository: save(request)
    RequestService-->>Caller: RequestSnapshot
```

## Borrow and return item

```mermaid
sequenceDiagram
    participant Caller
    participant LoanService
    participant MemberRepository
    participant BookRepository
    participant LoanRepository
    Caller->>LoanService: borrow(memberId, bookId)
    LoanService->>MemberRepository: findRequired(memberId)
    LoanService->>BookRepository: findRequired(bookId)
    LoanService->>LoanRepository: ensureNoActiveLoan(bookId)
    LoanService->>LoanRepository: save(newLoan)
    LoanService-->>Caller: LoanSnapshot
```

## Common mistakes

- Drawing repository calls before validation when validation should happen first.
- Hiding authorization from the diagram.
- Showing implementation details that do not help understand the workflow.

## Mini exercises

1. Draw a sequence diagram for invoice payment.
2. Add a failure path for unauthorized approval.
3. Add rollback behavior to a transfer diagram.

## Quick summary

Sequence diagrams show the order of calls and make workflow responsibility easier to discuss.
