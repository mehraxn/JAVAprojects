# Notification Service

A standard-Java notification queue demonstrating interfaces, pluggable delivery senders, retries, status tracking, and history without sending any real email, SMS, or app notification.

## Features

- Create and queue notification messages.
- Support EMAIL, SMS, and APP notification types.
- Register one sender implementation per type.
- Process queued notifications in FIFO order.
- Track `QUEUED`, `SENDING`, `SENT`, and `FAILED` status.
- Record delivery-attempt count, last failure, creation time, and sent time.
- Retry failed notifications up to a caller-defined maximum attempt count.
- Inspect the queue without consuming it.
- Show complete notification history in creation order.
- Simulate predictable failures for retry demonstrations.

## Main classes and interface

- `Notification` — message, recipient, type, lifecycle status, timestamps, and attempt data.
- `NotificationChannel` — sender interface with `getType()` and `deliver()` operations.
- `MockNotificationSender` — configurable local sender for EMAIL, SMS, or APP.
- `NotificationService` — sender registry, FIFO queue, dispatch, retry, lookup, and history.
- `Main` — demonstration with one simulated SMS failure and successful retry.

## How the program works

Enqueueing creates a validated notification and appends it to the FIFO queue and history. Processing removes the queue head, records an attempt, and calls the sender registered for its type. Success marks it sent; an exception records failure details. Explicit retry requeues only eligible failed messages.

## Queue design

`NotificationService` stores pending objects in an `ArrayDeque` and all created notifications in an insertion-ordered history map. Enqueueing creates status `QUEUED`. Processing removes the head, changes it to `SENDING`, increments its attempt count, and delegates to the sender registered for its type.

- Successful delivery changes status to `SENT` and records `sentAt`.
- Missing senders or sender exceptions change status to `FAILED` and store a safe error message.
- A failed notification is not automatically retried.
- `retry(id, maximumAttempts)` changes an eligible failed item back to `QUEUED` and appends it to the queue.
- Sent or currently queued items cannot be retried.
- Queue and history methods return unmodifiable lists of copies.

All service operations are synchronized so queue/history transitions remain consistent.

## Mock senders

`MockNotificationSender` never contacts a mail server, SMS provider, device, network API, or external queue. It only prints a labeled local message. Its optional `failuresRemaining` value deliberately throws exceptions for the first deliveries, making retry behavior easy to demonstrate.

## Example usage

```text
javac -d out src/notificationservice/*.java
java -cp out notificationservice.Main
```

## Java concepts practiced

- Interfaces and interchangeable implementations
- Queues, maps, lists, and enums
- Controlled state transitions
- Exception handling and retry limits
- Defensive copies and unmodifiable collections
- Synchronization and integer-overflow checks

## Backend concepts practiced

- Queue-based asynchronous-style workflow modeled synchronously
- Interface-driven delivery adapters
- Retry limits, delivery attempts, failure history, and state transitions
- Non-destructive queue inspection and immutable history views

## Storage approach

The pending queue and complete history are held in memory. Mock senders print locally; they do not use a network service or external queue. All state disappears when the process exits.

## Limitations

- Processing is synchronous and single-process
- No scheduling, retry delay, persistence, priority, or dead-letter queue
- Mock senders do not represent provider-specific delivery guarantees
- Recipient validation is intentionally basic

## Possible future improvements

- Scheduled delivery and retry backoff
- Priority queues
- Per-channel retry policies
- Cancellation of queued messages
- File persistence
- Structured internal error codes
- Automated tests with injectable time and mock senders
