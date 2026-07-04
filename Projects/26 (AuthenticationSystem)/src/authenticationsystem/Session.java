package authenticationsystem;

import java.time.LocalDateTime;

public class Session {
    private final String token;
    private final String userId;
    private final LocalDateTime expiresAt;

    public Session(String token, String userId, LocalDateTime expiresAt) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }

    public String getToken() { return token; }
    public String getUserId() { return userId; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
}
