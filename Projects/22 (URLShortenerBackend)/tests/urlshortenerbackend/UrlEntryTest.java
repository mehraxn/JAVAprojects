package urlshortenerbackend;

import java.time.LocalDateTime;

public final class UrlEntryTest {
    private UrlEntryTest() {
    }

    static void run(Assert t) {
        LocalDateTime now = LocalDateTime.of(2026, 7, 12, 10, 0);

        // Valid creation and getters
        UrlEntry entry = new UrlEntry("abc123", "https://example.com/page", now);
        t.assertEquals("abc123", entry.getShortCode(), "getShortCode returns constructor value");
        t.assertEquals("https://example.com/page", entry.getOriginalUrl(),
                "getOriginalUrl returns constructor value");
        t.assertEquals(now, entry.getCreatedAt(), "getCreatedAt returns constructor value");
        t.assertEquals(0L, entry.getHitCount(), "initial hit count is 0");

        // Hit counting
        entry.recordHit();
        entry.recordHit();
        t.assertEquals(2L, entry.getHitCount(), "recordHit increments the hit count");

        // Loaded entries preserve an existing hit count
        UrlEntry loaded = new UrlEntry("abc123", "https://example.com", now, 42);
        t.assertEquals(42L, loaded.getHitCount(), "loaded entry preserves existing hit count");
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("abc123", "https://example.com", now, -1),
                "negative hit count rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("abc123", "https://example.com", null),
                "null creation time rejected");

        // Hit overflow protection
        UrlEntry maxed = new UrlEntry("abc123", "https://example.com", now, Long.MAX_VALUE);
        t.assertThrows(IllegalStateException.class, maxed::recordHit,
                "hit count at Long.MAX_VALUE cannot be incremented");

        // Short code validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry(null, "https://example.com", now), "null short code rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("ab", "https://example.com", now),
                "too-short code rejected (min 3)");
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("x".repeat(21), "https://example.com", now),
                "too-long code rejected (max 20)");
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("bad code", "https://example.com", now),
                "code with space rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("bad/code", "https://example.com", now),
                "code with slash rejected");
        t.assertEquals("A-b_9", UrlEntry.validateShortCode("A-b_9"),
                "letters, digits, dash, underscore are allowed");

        // URL validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("abc123", null, now), "null URL rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("abc123", "", now), "empty URL rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("abc123", "   ", now), "whitespace-only URL rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("abc123", "example.com/page", now),
                "URL without scheme rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("abc123", "ftp://example.com/file", now),
                "ftp scheme rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("abc123", "file:///etc/passwd", now),
                "file scheme rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("abc123", "javascript:alert(1)", now),
                "javascript scheme rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("abc123", "https:///path-no-host", now),
                "URL without host rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("abc123", "https://example.com:70000/x", now),
                "URL port above 65535 rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlEntry("abc123", "https://exa mple.com", now),
                "malformed URL rejected");
        t.assertEquals("http://example.com",
                new UrlEntry("abc123", "http://example.com", now).getOriginalUrl(),
                "plain http URL accepted");
        t.assertEquals("https://example.com/a?b=c&d=e",
                new UrlEntry("abc123", " https://example.com/a?b=c&d=e ", now).getOriginalUrl(),
                "https URL with query accepted and trimmed");

        // copy() is an independent snapshot
        UrlEntry original = new UrlEntry("abc123", "https://example.com", now, 5);
        UrlEntry copy = original.copy();
        copy.recordHit();
        t.assertEquals(5L, original.getHitCount(), "mutating a copy leaves the original unchanged");
        t.assertEquals(6L, copy.getHitCount(), "the copy itself records hits");
    }
}
