package authenticationsystem;

import java.time.Instant;

/**
 * Safe public view of an issued session: the token the caller needs for later
 * requests, the user it belongs to (ID, username, role), and the expiry time.
 * It never contains password material. Use {@link #getMaskedToken()} when
 * printing, so full tokens do not end up in demo output.
 */
public final class SessionInfo {
    private final String token;
    private final String userId;
    private final String username;
    private final User.Role role;
    private final Instant expiresAt;

    SessionInfo(String token, String userId, String username, User.Role role,
            Instant expiresAt) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Session token cannot be empty.");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session user ID cannot be empty.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Session username cannot be empty.");
        }
        if (role == null) {
            throw new IllegalArgumentException("Session role cannot be null.");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Session expiration cannot be null.");
        }
        this.token = token;
        this.userId = userId.trim();
        this.username = username.trim();
        this.role = role;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    /** First characters of the token only - safe for demo output. */
    public String getMaskedToken() {
        int visible = Math.min(6, token.length());
        return token.substring(0, visible) + "...";
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public User.Role getRole() {
        return role;
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

    @Override
    public String toString() {
        return "Session " + getMaskedToken() + " for " + username
                + " [" + role + "], expires " + expiresAt;
    }
}
