package notificationservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationService {
    private final Map<String, Notification> notificationsByOrder = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public Notification send(String orderId, String message) {
        String cleanOrderId = requireText(orderId, "Order ID", 80);
        String cleanMessage = requireText(message, "Message", 300);
        return notificationsByOrder.computeIfAbsent(cleanOrderId,
                key -> new Notification("NOT-" + sequence.incrementAndGet(), key, cleanMessage));
    }

    public List<Notification> history() {
        return new ArrayList<>(notificationsByOrder.values());
    }

    private static String requireText(String value, String field, int maximumLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        String clean = value.trim();
        if (clean.length() > maximumLength) {
            throw new IllegalArgumentException(field + " is too long.");
        }
        return clean;
    }
}
