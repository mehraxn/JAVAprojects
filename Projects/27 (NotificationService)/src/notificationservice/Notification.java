package notificationservice;

public class Notification {
    public enum ChannelType {
        CONSOLE,
        EMAIL,
        SMS
    }

    public enum Status {
        QUEUED,
        SENDING,
        SENT,
        FAILED
    }

    private final String id;
    private final String recipient;
    private final String message;
    private final ChannelType channelType;
    private Status status;
    private int attemptCount;

    public Notification(String id, String recipient,
            String message, ChannelType channelType) {
        this.id = id;
        this.recipient = recipient;
        this.message = message;
        this.channelType = channelType;
        this.status = Status.QUEUED;
    }

    public String getId() { return id; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public ChannelType getChannelType() { return channelType; }
    public Status getStatus() { return status; }
    public int getAttemptCount() { return attemptCount; }

    public void setStatus(Status status) {
        // TODO: Enforce legal notification status transitions.
        throw new UnsupportedOperationException("TODO: update notification status");
    }

    public void recordAttempt() {
        // TODO: Increment safely and define a maximum-attempt policy.
        throw new UnsupportedOperationException("TODO: record a delivery attempt");
    }
}
