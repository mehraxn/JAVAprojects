package urlshortenerbackend;

import java.time.LocalDateTime;

public class UrlEntry {
    private final String shortCode;
    private final String originalUrl;
    private final LocalDateTime createdAt;
    private long hitCount;

    public UrlEntry(String shortCode, String originalUrl, LocalDateTime createdAt) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
    }

    public String getShortCode() { return shortCode; }
    public String getOriginalUrl() { return originalUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public long getHitCount() { return hitCount; }

    public void recordHit() {
        // TODO: Increment safely and define overflow behavior.
        throw new UnsupportedOperationException("TODO: record a URL hit");
    }
}
