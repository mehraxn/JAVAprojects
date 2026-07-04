# Notification Service

## Status

Notification model, channel interface, queue service, console channel, and Main skeleton created.

## Planned features

- Enqueue notifications.
- Dispatch through a registered channel.
- Track queued, sending, sent, and failed status.
- Count delivery attempts.
- Retry failed notifications within a limit.
- Inspect queue order without removing items.

## Current classes

- Notification: message, channel, status, and attempt model.
- NotificationChannel: delivery-channel contract.
- ConsoleNotificationChannel: local demonstration channel.
- NotificationService: queueing, dispatch, and retry logic.
- Main: non-delivering demonstration entry point.

## Scope

No real email, SMS, network service, or external queue is used. Additional channels remain abstractions until explicitly implemented.

## Source layout

Source files are under src/notificationservice.
