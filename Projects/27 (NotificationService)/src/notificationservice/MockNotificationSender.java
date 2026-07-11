package notificationservice;

import java.io.PrintStream;

/**
 * Local mock delivery channel. It never contacts a real email, SMS, or push
 * provider - "delivery" prints one line to the configured stream. A fixed
 * number of initial failures can be simulated deterministically, e.g.
 * {@code new MockNotificationSender(ChannelType.SMS, 1)} fails the first
 * attempt and succeeds from the second attempt on.
 */
public class MockNotificationSender implements NotificationChannel {
    private final Notification.ChannelType type;
    private final PrintStream out;
    private int failuresRemaining;

    public MockNotificationSender(Notification.ChannelType type) {
        this(type, 0);
    }

    public MockNotificationSender(Notification.ChannelType type, int failuresRemaining) {
        this(type, failuresRemaining, System.out);
    }

    public MockNotificationSender(Notification.ChannelType type, int failuresRemaining,
            PrintStream out) {
        if (type == null) {
            throw new IllegalArgumentException("Notification type cannot be null.");
        }
        if (failuresRemaining < 0) {
            throw new IllegalArgumentException("Failure count cannot be negative.");
        }
        if (out == null) {
            throw new IllegalArgumentException("Output stream cannot be null.");
        }
        this.type = type;
        this.failuresRemaining = failuresRemaining;
        this.out = out;
    }

    @Override
    public Notification.ChannelType getType() {
        return type;
    }

    @Override
    public synchronized void deliver(Notification notification) throws Exception {
        if (notification == null) {
            throw new IllegalArgumentException("Notification cannot be null.");
        }
        if (notification.getChannelType() != type) {
            throw new IllegalArgumentException("Notification type does not match this sender.");
        }
        if (failuresRemaining > 0) {
            failuresRemaining--;
            throw new Exception("Simulated " + type + " delivery failure.");
        }
        out.println("[MOCK " + type + "] to " + notification.getRecipient()
                + ": " + notification.getMessage());
    }
}
