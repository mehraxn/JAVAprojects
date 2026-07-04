package notificationservice;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class NotificationService {
    private final Queue<Notification> queue = new ArrayDeque<Notification>();
    private final Map<String, Notification> history =
            new LinkedHashMap<String, Notification>();
    private final Map<Notification.ChannelType, NotificationChannel> channels =
            new LinkedHashMap<Notification.ChannelType, NotificationChannel>();
    private long nextNotificationId = 1;

    public synchronized void registerChannel(NotificationChannel channel) {
        if (channel == null || channel.getType() == null) {
            throw new IllegalArgumentException("Notification sender and type cannot be null.");
        }
        if (channels.containsKey(channel.getType())) {
            throw new IllegalArgumentException(
                    "A sender is already registered for " + channel.getType() + ".");
        }
        channels.put(channel.getType(), channel);
    }

    public synchronized Notification enqueue(String recipient, String message,
            Notification.ChannelType channelType) {
        Notification notification = new Notification(
                "N-" + nextNotificationId, recipient, message, channelType);
        queue.add(notification);
        history.put(notification.getId(), notification);
        nextNotificationId++;
        return notification.copy();
    }

    public synchronized Notification processNext() {
        Notification notification = queue.poll();
        if (notification == null) {
            return null;
        }
        notification.beginAttempt();
        NotificationChannel channel = channels.get(notification.getChannelType());
        if (channel == null) {
            notification.markFailed(
                    "No mock sender is registered for " + notification.getChannelType() + ".");
            return notification.copy();
        }

        try {
            channel.deliver(notification.copy());
            notification.markSent();
        } catch (Exception exception) {
            notification.markFailed(exception.getMessage());
        }
        return notification.copy();
    }

    public synchronized boolean retry(String notificationId, int maximumAttempts) {
        if (maximumAttempts <= 0) {
            throw new IllegalArgumentException("Maximum attempts must be positive.");
        }
        Notification notification = history.get(requireId(notificationId));
        if (notification == null) {
            return false;
        }
        if (notification.getStatus() != Notification.Status.FAILED) {
            throw new IllegalStateException("Only failed notifications can be retried.");
        }
        if (notification.getAttemptCount() >= maximumAttempts) {
            return false;
        }
        notification.requeue();
        queue.add(notification);
        return true;
    }

    public synchronized List<Notification> viewQueue() {
        List<Notification> result = new ArrayList<Notification>();
        for (Notification notification : queue) {
            result.add(notification.copy());
        }
        return Collections.unmodifiableList(result);
    }

    public synchronized List<Notification> getHistory() {
        List<Notification> result = new ArrayList<Notification>();
        for (Notification notification : history.values()) {
            result.add(notification.copy());
        }
        return Collections.unmodifiableList(result);
    }

    public synchronized Notification findNotification(String notificationId) {
        Notification notification = history.get(requireId(notificationId));
        return notification == null ? null : notification.copy();
    }

    private String requireId(String notificationId) {
        if (notificationId == null || notificationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification ID cannot be empty.");
        }
        return notificationId.trim();
    }
}
