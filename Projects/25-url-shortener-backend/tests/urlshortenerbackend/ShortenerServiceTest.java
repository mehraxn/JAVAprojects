package urlshortenerbackend;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class ShortenerServiceTest {
    private ShortenerServiceTest() {
    }

    static void run(Assert t) {
        t.assertThrows(IllegalArgumentException.class,
                () -> new ShortenerService(null), "null code generator rejected");

        ShortenerService service = new ShortenerService(new CodeGenerator());

        // Generated links
        UrlEntry generated = service.shorten("https://example.com/articles/java");
        t.assertNotNull(generated, "shorten returns the created entry");
        t.assertTrue(generated.getShortCode().matches("[0-9a-zA-Z]{6}"),
                "generated code has the expected shape");
        t.assertEquals("https://example.com/articles/java", generated.getOriginalUrl(),
                "generated entry stores the original URL");
        t.assertEquals(0L, generated.getHitCount(), "new entry starts with 0 hits");

        // Custom links
        UrlEntry custom = service.shorten("https://openjdk.org/", "openjdk");
        t.assertEquals("openjdk", custom.getShortCode(), "custom code is preserved");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.shorten("https://example.com/x", "openjdk"),
                "duplicate custom code rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.shorten("https://example.com/x", "a"),
                "too-short custom code rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.shorten("https://example.com/x", "bad code"),
                "custom code with space rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.shorten("ftp://example.com/x", "ftplink"),
                "invalid URL rejected for custom link");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.shorten("not a url"),
                "invalid URL rejected for generated link");
        t.assertEquals(2, service.listEntries().size(),
                "failed creations do not add entries");

        // Resolve and hit counting
        t.assertEquals("https://example.com/articles/java",
                service.resolve(generated.getShortCode()), "resolve returns the original URL");
        service.resolve(generated.getShortCode());
        t.assertEquals(2L, service.find(generated.getShortCode()).getHitCount(),
                "each successful resolve increments the hit count");
        t.assertEquals(0L, service.find("openjdk").getHitCount(),
                "resolving one code does not touch other counts");
        t.assertThrows(NoSuchElementException.class,
                () -> service.resolve("nosuch"), "resolving a missing code fails cleanly");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.resolve("bad code"), "resolving an invalid code fails cleanly");
        t.assertEquals(2L, service.find(generated.getShortCode()).getHitCount(),
                "failed resolves do not increment any hit count");

        // find
        t.assertNull(service.find("nosuch"), "find returns null for a missing code");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.find("bad code"), "find validates the code format");

        // Defensive snapshots
        List<UrlEntry> listed = service.listEntries();
        t.assertEquals(2, listed.size(), "listEntries returns all entries");
        t.assertThrows(UnsupportedOperationException.class,
                () -> listed.add(custom), "listEntries result is unmodifiable");
        listed.get(0).recordHit();
        t.assertEquals(2L, service.find(generated.getShortCode()).getHitCount(),
                "mutating a listed entry does not change stored state");
        service.find("openjdk").recordHit();
        t.assertEquals(0L, service.find("openjdk").getHitCount(),
                "mutating a found entry does not change stored state");

        Map<String, UrlEntry> snapshot = service.snapshot();
        t.assertEquals(2, snapshot.size(), "snapshot contains all entries");
        t.assertThrows(UnsupportedOperationException.class,
                () -> snapshot.remove("openjdk"), "snapshot map is unmodifiable");
        snapshot.get("openjdk").recordHit();
        t.assertEquals(0L, service.find("openjdk").getHitCount(),
                "mutating a snapshot entry does not change stored state");

        // Generated codes stay unique across a sample
        ShortenerService bulk = new ShortenerService(new CodeGenerator());
        Set<String> codes = new HashSet<String>();
        for (int i = 0; i < 200; i++) {
            codes.add(bulk.shorten("https://example.com/page/" + i).getShortCode());
        }
        t.assertEquals(200, codes.size(), "200 generated links have 200 distinct codes");

        // Generator skips codes already taken by custom links
        ShortenerService collision = new ShortenerService(new CodeGenerator());
        collision.shorten("https://example.com/taken", "000001");
        String next = collision.shorten("https://example.com/free").getShortCode();
        t.assertNotEquals("000001", next,
                "generated code skips a custom code that already exists");

        // replaceEntries (load/import)
        LocalDateTime now = LocalDateTime.of(2026, 7, 12, 9, 0);
        Map<String, UrlEntry> imported = new LinkedHashMap<String, UrlEntry>();
        imported.put("aaa111", new UrlEntry("aaa111", "https://example.com/one", now, 7));
        ShortenerService target = new ShortenerService(new CodeGenerator());
        target.shorten("https://example.com/old");
        target.replaceEntries(imported);
        t.assertEquals(1, target.listEntries().size(), "replaceEntries replaces all entries");
        t.assertEquals(7L, target.find("aaa111").getHitCount(),
                "replaceEntries preserves imported hit counts");
        t.assertThrows(IllegalArgumentException.class,
                () -> target.replaceEntries(null), "null import map rejected");
        Map<String, UrlEntry> mismatched = new LinkedHashMap<String, UrlEntry>();
        mismatched.put("wrongkey", new UrlEntry("aaa111", "https://example.com/one", now));
        t.assertThrows(IllegalArgumentException.class,
                () -> target.replaceEntries(mismatched),
                "import map with mismatched key rejected");
        t.assertEquals(1, target.listEntries().size(),
                "failed import leaves existing entries untouched");
        imported.get("aaa111").recordHit();
        t.assertEquals(7L, target.find("aaa111").getHitCount(),
                "mutating the imported map does not change stored state");
    }
}
