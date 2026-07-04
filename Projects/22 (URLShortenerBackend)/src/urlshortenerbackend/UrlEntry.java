package urlshortenerbackend;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

public class UrlEntry {
    private final String shortCode;
    private final String originalUrl;
    private final LocalDateTime createdAt;
    private long hitCount;

    public UrlEntry(String shortCode, String originalUrl, LocalDateTime createdAt) {
        this(shortCode, originalUrl, createdAt, 0);
    }

    public UrlEntry(String shortCode, String originalUrl, LocalDateTime createdAt, long hitCount) {
        this.shortCode = validateShortCode(shortCode);
        this.originalUrl = validateUrl(originalUrl);
        if (createdAt == null) {
            throw new IllegalArgumentException("Creation time cannot be null.");
        }
        if (hitCount < 0) {
            throw new IllegalArgumentException("Hit count cannot be negative.");
        }
        this.createdAt = createdAt;
        this.hitCount = hitCount;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public long getHitCount() {
        return hitCount;
    }

    public void recordHit() {
        if (hitCount == Long.MAX_VALUE) {
            throw new IllegalStateException("Hit count has reached its maximum value.");
        }
        hitCount++;
    }

    public UrlEntry copy() {
        return new UrlEntry(shortCode, originalUrl, createdAt, hitCount);
    }

    public static String validateShortCode(String shortCode) {
        if (shortCode == null || !shortCode.matches("[A-Za-z0-9_-]{3,20}")) {
            throw new IllegalArgumentException(
                    "Short code must contain 3-20 letters, numbers, underscores, or hyphens.");
        }
        return shortCode;
    }

    public static String validateUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be empty.");
        }
        String value = originalUrl.trim();
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            if (scheme == null
                    || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    || uri.getHost() == null) {
                throw new IllegalArgumentException("URL must be an absolute HTTP or HTTPS URL with a host.");
            }
            if (uri.getPort() > 65_535) {
                throw new IllegalArgumentException("URL port cannot exceed 65535.");
            }
            return value;
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("URL is malformed: " + exception.getMessage(), exception);
        }
    }

    @Override
    public String toString() {
        return shortCode + " -> " + originalUrl + " (hits: " + hitCount + ")";
    }
}
