package notificationservice;

import java.time.LocalDateTime;

public class Notification {
    public enum ChannelType {
        EMAIL,
        SMS,
        APP
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
    private final LocalDateTime createdAt;
    private Status status;
    private int attemptCount;
    private String lastError;
    private LocalDateTime sentAt;

    public Notification(String id, String recipient,
            String message, ChannelType channelType) {
        this.id = requireText(id, "Notification ID");
        if (channelType == null) {
            throw new IllegalArgumentException("Notification type cannot be null.");
        }
        this.recipient = validateRecipient(recipient, channelType);
        this.message = requireText(message, "Message");
        if (this.message.length() > 5_000) {
            throw new IllegalArgumentException("Message cannot exceed 5000 characters.");
        }
        this.channelType = channelType;
        this.createdAt = LocalDateTime.now();
        this.status = Status.QUEUED;
        this.lastError = "";
    }

    private Notification(String id, String recipient, String message,
            ChannelType channelType, LocalDateTime createdAt, Status status,
            int attemptCount, String lastError, LocalDateTime sentAt) {
        this.id = id;
        this.recipient = recipient;
        this.message = message;
        this.channelType = channelType;
        this.createdAt = createdAt;
        this.status = status;
        this.attemptCount = attemptCount;
        this.lastError = lastError;
        this.sentAt = sentAt;
    }

    public String getId() {
        return id;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Status getStatus() {
        return status;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public String getLastError() {
        return lastError;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    void beginAttempt() {
        if (status != Status.QUEUED) {
            throw new IllegalStateException("Only queued notifications can be sent.");
        }
        if (attemptCount == Integer.MAX_VALUE) {
            throw new IllegalStateException("Attempt count has reached its maximum value.");
        }
        attemptCount++;
        status = Status.SENDING;
        lastError = "";
    }

    void markSent() {
        if (status != Status.SENDING) {
            throw new IllegalStateException("Only sending notifications can be marked sent.");
        }
        status = Status.SENT;
        sentAt = LocalDateTime.now();
        lastError = "";
    }

    void markFailed(String errorMessage) {
        if (status != Status.SENDING) {
            throw new IllegalStateException("Only sending notifications can be marked failed.");
        }
        status = Status.FAILED;
        lastError = errorMessage == null || errorMessage.trim().isEmpty()
                ? "Delivery failed." : errorMessage.trim();
    }

    void requeue() {
        if (status != Status.FAILED) {
            throw new IllegalStateException("Only failed notifications can be retried.");
        }
        status = Status.QUEUED;
    }

    public Notification copy() {
        return new Notification(id, recipient, message, channelType, createdAt,
                status, attemptCount, lastError, sentAt);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    private String validateRecipient(String recipient, ChannelType type) {
        String value = requireText(recipient, "Recipient");
        if (type == ChannelType.EMAIL
                && !value.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("EMAIL recipient must be a valid email address.");
        }
        if (type == ChannelType.SMS && !value.matches("[0-9+() .-]{3,30}")) {
            throw new IllegalArgumentException("SMS recipient must be a valid phone-like value.");
        }
        if (type == ChannelType.APP && value.length() > 100) {
            throw new IllegalArgumentException("APP recipient cannot exceed 100 characters.");
        }
        return value;
    }

    @Override
    public String toString() {
        return id + " | " + channelType + " | " + status
                + " | attempts=" + attemptCount;
    }
}
