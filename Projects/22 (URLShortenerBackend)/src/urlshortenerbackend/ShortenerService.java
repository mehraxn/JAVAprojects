package urlshortenerbackend;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class ShortenerService {
    private final Map<String, UrlEntry> entries = new LinkedHashMap<String, UrlEntry>();
    private final CodeGenerator codeGenerator;

    public ShortenerService(CodeGenerator codeGenerator) {
        if (codeGenerator == null) {
            throw new IllegalArgumentException("Code generator cannot be null.");
        }
        this.codeGenerator = codeGenerator;
    }

    public synchronized UrlEntry shorten(String originalUrl) {
        String code = codeGenerator.generate(entries.keySet());
        return createEntry(originalUrl, code);
    }

    public synchronized UrlEntry shorten(String originalUrl, String customShortCode) {
        String code = UrlEntry.validateShortCode(customShortCode);
        if (entries.containsKey(code)) {
            throw new IllegalArgumentException("Short code already exists: " + code);
        }
        return createEntry(originalUrl, code);
    }

    public synchronized String resolve(String shortCode) {
        String code = UrlEntry.validateShortCode(shortCode);
        UrlEntry entry = entries.get(code);
        if (entry == null) {
            throw new NoSuchElementException("Unknown short code: " + code);
        }
        entry.recordHit();
        return entry.getOriginalUrl();
    }

    public synchronized UrlEntry find(String shortCode) {
        String code = UrlEntry.validateShortCode(shortCode);
        UrlEntry entry = entries.get(code);
        return entry == null ? null : entry.copy();
    }

    public synchronized List<UrlEntry> listEntries() {
        List<UrlEntry> result = new ArrayList<UrlEntry>();
        for (UrlEntry entry : entries.values()) {
            result.add(entry.copy());
        }
        return Collections.unmodifiableList(result);
    }

    public synchronized Map<String, UrlEntry> snapshot() {
        Map<String, UrlEntry> result = new LinkedHashMap<String, UrlEntry>();
        for (Map.Entry<String, UrlEntry> item : entries.entrySet()) {
            result.put(item.getKey(), item.getValue().copy());
        }
        return Collections.unmodifiableMap(result);
    }

    public synchronized void replaceEntries(Map<String, UrlEntry> loadedEntries) {
        if (loadedEntries == null) {
            throw new IllegalArgumentException("Loaded entries cannot be null.");
        }
        Map<String, UrlEntry> checked = new LinkedHashMap<String, UrlEntry>();
        for (Map.Entry<String, UrlEntry> item : loadedEntries.entrySet()) {
            if (item.getValue() == null || !item.getKey().equals(item.getValue().getShortCode())) {
                throw new IllegalArgumentException("Every map key must match a non-null URL entry.");
            }
            if (checked.put(item.getKey(), item.getValue().copy()) != null) {
                throw new IllegalArgumentException("Duplicate short code: " + item.getKey());
            }
        }
        entries.clear();
        entries.putAll(checked);
    }

    private UrlEntry createEntry(String originalUrl, String code) {
        UrlEntry entry = new UrlEntry(code, originalUrl, LocalDateTime.now());
        entries.put(code, entry);
        return entry.copy();
    }
}
