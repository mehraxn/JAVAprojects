package urlshortenerbackend;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class FileUrlStoreTest {
    private FileUrlStoreTest() {
    }

    static void run(Assert t) throws Exception {
        FileUrlStore store = new FileUrlStore();
        LocalDateTime now = LocalDateTime.of(2026, 7, 12, 9, 30);

        t.assertThrows(IllegalArgumentException.class,
                () -> store.load(null), "null load path rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> store.save(null, new LinkedHashMap<String, UrlEntry>()),
                "null save path rejected");

        Path tempFile = Files.createTempFile("urlshortener-test", ".csv");
        try {
            t.assertThrows(IllegalArgumentException.class,
                    () -> store.save(tempFile, null), "null entry map rejected");

            // Round trip: entries, hit counts, and tricky characters survive
            Map<String, UrlEntry> entries = new LinkedHashMap<String, UrlEntry>();
            entries.put("plain1", new UrlEntry("plain1", "https://example.com/simple", now, 3));
            entries.put("comma1", new UrlEntry("comma1", "https://example.com/a,b,c", now, 0));
            entries.put("query1", new UrlEntry(
                    "query1", "https://example.com/x?q=1&r=2", now, 12));
            store.save(tempFile, entries);
            Map<String, UrlEntry> loaded = store.load(tempFile);
            t.assertEquals(3, loaded.size(), "save/load preserves all records");
            t.assertEquals("https://example.com/a,b,c",
                    loaded.get("comma1").getOriginalUrl(),
                    "comma in URL survives CSV quoting");
            t.assertEquals(3L, loaded.get("plain1").getHitCount(),
                    "hit counts are preserved");
            t.assertEquals(now, loaded.get("query1").getCreatedAt(),
                    "creation times are preserved");
            t.assertThrows(UnsupportedOperationException.class,
                    () -> loaded.remove("plain1"), "loaded map is unmodifiable");

            // CSV quoting of double quotes (valid in fields, verified via parser rules)
            List<String> quotedCsv = List.of(
                    "shortCode,originalUrl,createdAt,hitCount",
                    "\"abc123\",\"https://example.com/q\",\"2026-07-12T09:30\",\"4\"");
            Files.write(tempFile, quotedCsv, StandardCharsets.UTF_8);
            Map<String, UrlEntry> quoted = store.load(tempFile);
            t.assertEquals(4L, quoted.get("abc123").getHitCount(),
                    "fully quoted rows are parsed");

            // Malformed and invalid content
            writeAndExpectIOException(t, store, tempFile,
                    "wrong,header,entirely,here\nabc123,https://example.com,2026-07-12T09:30,0",
                    "unexpected CSV header rejected");
            writeAndExpectIOException(t, store, tempFile,
                    "shortCode,originalUrl,createdAt,hitCount\nabc123,https://example.com,2026-07-12T09:30",
                    "row with too few fields rejected");
            writeAndExpectIOException(t, store, tempFile,
                    "shortCode,originalUrl,createdAt,hitCount\n"
                            + "abc123,https://example.com,2026-07-12T09:30,0\n"
                            + "abc123,https://example.com/dup,2026-07-12T09:31,1",
                    "duplicate short code in file rejected");
            writeAndExpectIOException(t, store, tempFile,
                    "shortCode,originalUrl,createdAt,hitCount\n"
                            + "abc123,ftp://example.com/file,2026-07-12T09:30,0",
                    "invalid URL scheme in file rejected");
            writeAndExpectIOException(t, store, tempFile,
                    "shortCode,originalUrl,createdAt,hitCount\n"
                            + "abc123,https://example.com,2026-07-12T09:30,notanumber",
                    "invalid hit count rejected");
            writeAndExpectIOException(t, store, tempFile,
                    "shortCode,originalUrl,createdAt,hitCount\n"
                            + "abc123,https://example.com,2026-07-12T09:30,-5",
                    "negative hit count rejected");
            writeAndExpectIOException(t, store, tempFile,
                    "shortCode,originalUrl,createdAt,hitCount\n"
                            + "abc123,https://example.com,not-a-time,0",
                    "invalid creation time rejected");
            writeAndExpectIOException(t, store, tempFile,
                    "shortCode,originalUrl,createdAt,hitCount\n"
                            + "\"abc123,https://example.com,2026-07-12T09:30,0",
                    "unclosed quote rejected");

            // Empty and header-only files load as empty maps
            Files.write(tempFile, new byte[0]);
            t.assertEquals(0, store.load(tempFile).size(), "zero-byte file loads as empty map");
            Files.write(tempFile, List.of("shortCode,originalUrl,createdAt,hitCount"),
                    StandardCharsets.UTF_8);
            t.assertEquals(0, store.load(tempFile).size(), "header-only file loads as empty map");

            // Values with line breaks cannot be saved (documented limitation)
            // (URLs cannot contain raw newlines, so this is enforced at the entry level
            // and the store's escape() guards against it defensively.)
        } finally {
            Files.deleteIfExists(tempFile);
        }

        // Missing file loads as an empty map
        Path missing = tempFile.resolveSibling("urlshortener-missing-" + System.nanoTime() + ".csv");
        t.assertEquals(0, store.load(missing).size(), "missing file loads as empty map");

        // Directory paths are rejected
        Path tempDir = Files.createTempDirectory("urlshortener-dir");
        try {
            t.assertThrows(IOException.class, () -> store.load(tempDir),
                    "loading a directory path rejected");
            t.assertThrows(IOException.class,
                    () -> store.save(tempDir, new LinkedHashMap<String, UrlEntry>()),
                    "saving to a directory path rejected");
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }

    private static void writeAndExpectIOException(Assert t, FileUrlStore store,
            Path file, String content, String message) throws IOException {
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        t.assertThrows(IOException.class, () -> store.load(file), message);
    }
}
