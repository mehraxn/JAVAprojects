# Testing Notification Service

## Planned normal tests

- Register a console channel.
- Enqueue and process notifications in FIFO order.
- Verify status and attempt count changes.
- Inspect the queue without consuming it.

## Planned failure tests

- Process with no registered channel.
- Simulate channel failure.
- Retry within and beyond the maximum attempt count.
- Verify failed processing does not lose the notification unexpectedly.

## Planned validation tests

- Reject blank recipient and message.
- Reject null channel type.
- Reject duplicate channel registration.
- Reject negative retry limits.

## Manual checklist

- [ ] Implement controlled status transitions.
- [ ] Keep queue inspection non-destructive.
- [ ] Implement retry limits.
- [ ] Demonstrate console delivery only.
- [ ] Do not add real external delivery without separate requirements.
