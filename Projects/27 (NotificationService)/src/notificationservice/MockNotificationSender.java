package notificationservice;

public class MockNotificationSender implements NotificationChannel {
    private final Notification.ChannelType type;
    private int failuresRemaining;

    public MockNotificationSender(Notification.ChannelType type) {
        this(type, 0);
    }

    public MockNotificationSender(Notification.ChannelType type, int failuresRemaining) {
        if (type == null) {
            throw new IllegalArgumentException("Notification type cannot be null.");
        }
        if (failuresRemaining < 0) {
            throw new IllegalArgumentException("Failure count cannot be negative.");
        }
        this.type = type;
        this.failuresRemaining = failuresRemaining;
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
        System.out.println("[MOCK " + type + "] to " + notification.getRecipient()
                + ": " + notification.getMessage());
    }
}
