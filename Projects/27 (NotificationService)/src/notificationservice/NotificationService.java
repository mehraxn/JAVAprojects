package notificationservice;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class NotificationService {
    private final Queue<Notification> queue = new ArrayDeque<>();
    private final Map<Notification.ChannelType, NotificationChannel> channels =
            new LinkedHashMap<>();

    public void registerChannel(NotificationChannel channel) {
        // TODO: Validate and register one channel per type.
        throw new UnsupportedOperationException("TODO: register a channel");
    }

    public Notification enqueue(String recipient, String message,
            Notification.ChannelType channelType) {
        // TODO: Validate values, generate an ID, and enqueue a notification.
        throw new UnsupportedOperationException("TODO: enqueue a notification");
    }

    public Notification processNext() {
        // TODO: Dispatch the next item and update status/attempt count.
        throw new UnsupportedOperationException("TODO: process a notification");
    }

    public void retry(Notification notification, int maximumAttempts) {
        // TODO: Requeue a failed notification within the retry limit.
        throw new UnsupportedOperationException("TODO: retry a notification");
    }

    public List<Notification> viewQueue() {
        // TODO: Return the queue in delivery order without removing items.
        throw new UnsupportedOperationException("TODO: view notification queue");
    }
}
