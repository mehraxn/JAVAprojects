package authenticationsystem;

import java.time.Instant;

public class Session {
    private final String token;
    private final String userId;
    private final Instant expiresAt;

    public Session(String token, String userId, Instant expiresAt) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Session token cannot be empty.");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session user ID cannot be empty.");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Session expiration cannot be null.");
        }
        this.token = token;
        this.userId = userId.trim();
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired(Instant currentTime) {
        if (currentTime == null) {
            throw new IllegalArgumentException("Current time cannot be null.");
        }
        return !currentTime.isBefore(expiresAt);
    }

    public Session copy() {
        return new Session(token, userId, expiresAt);
    }
}
