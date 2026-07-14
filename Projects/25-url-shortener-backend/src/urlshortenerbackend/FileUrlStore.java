package urlshortenerbackend;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FileUrlStore {
    private static final String HEADER = "shortCode,originalUrl,createdAt,hitCount";

    public Map<String, UrlEntry> load(Path path) throws IOException {
        requirePath(path);
        if (!Files.exists(path)) {
            return Collections.emptyMap();
        }
        if (!Files.isRegularFile(path)) {
            throw new IOException("URL storage path is not a regular file: " + path);
        }
        if (Files.size(path) == 0) {
            return Collections.emptyMap();
        }
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        int headerIndex = firstNonBlankLine(lines);
        if (headerIndex == -1) {
            return Collections.emptyMap();
        }
        if (!parseLine(lines.get(headerIndex), headerIndex + 1).equals(parseLine(HEADER, 1))) {
            throw new IOException("Unexpected CSV header. Expected: " + HEADER);
        }

        Map<String, UrlEntry> entries = new LinkedHashMap<String, UrlEntry>();
        for (int index = headerIndex + 1; index < lines.size(); index++) {
            if (lines.get(index).trim().isEmpty()) {
                continue;
            }
            List<String> fields = parseLine(lines.get(index), index + 1);
            if (fields.size() != 4) {
                throw new IOException("Line " + (index + 1) + " must contain exactly 4 fields.");
            }
            try {
                UrlEntry entry = new UrlEntry(
                        fields.get(0), fields.get(1), LocalDateTime.parse(fields.get(2).trim()),
                        Long.parseLong(fields.get(3).trim()));
                if (entries.put(entry.getShortCode(), entry) != null) {
                    throw new IOException("Duplicate short code on line " + (index + 1) + ".");
                }
            } catch (NumberFormatException exception) {
                throw new IOException("Invalid hit count on line " + (index + 1) + ".", exception);
            } catch (DateTimeParseException exception) {
                throw new IOException("Invalid creation time on line " + (index + 1) + ".", exception);
            } catch (IllegalArgumentException exception) {
                throw new IOException("Invalid URL entry on line " + (index + 1) + ": "
                        + exception.getMessage(), exception);
            }
        }
        return Collections.unmodifiableMap(entries);
    }

    public void save(Path path, Map<String, UrlEntry> entries) throws IOException {
        requirePath(path);
        if (entries == null) {
            throw new IllegalArgumentException("Entry map cannot be null.");
        }
        if (Files.exists(path) && !Files.isRegularFile(path)) {
            throw new IOException("URL storage path is not a regular file: " + path);
        }
        List<String> lines = new ArrayList<String>();
        lines.add(HEADER);
        for (Map.Entry<String, UrlEntry> item : entries.entrySet()) {
            UrlEntry entry = item.getValue();
            if (entry == null || !item.getKey().equals(entry.getShortCode())) {
                throw new IllegalArgumentException("Every map key must match a non-null URL entry.");
            }
            lines.add(escape(entry.getShortCode()) + ","
                    + escape(entry.getOriginalUrl()) + ","
                    + escape(entry.getCreatedAt().toString()) + ","
                    + entry.getHitCount());
        }
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    private void requirePath(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null.");
        }
    }

    private int firstNonBlankLine(List<String> lines) {
        for (int index = 0; index < lines.size(); index++) {
            if (!lines.get(index).trim().isEmpty()) {
                return index;
            }
        }
        return -1;
    }

    private List<String> parseLine(String line, int lineNumber) throws IOException {
        List<String> fields = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        boolean quoteClosed = false;
        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (inQuotes) {
                if (character == '"') {
                    if (index + 1 < line.length() && line.charAt(index + 1) == '"') {
                        current.append('"');
                        index++;
                    } else {
                        inQuotes = false;
                        quoteClosed = true;
                    }
                } else {
                    current.append(character);
                }
            } else if (quoteClosed) {
                if (character != ',') {
                    throw new IOException("Unexpected text after a closing quote on line " + lineNumber + ".");
                }
                fields.add(current.toString());
                current.setLength(0);
                quoteClosed = false;
            } else if (character == ',') {
                fields.add(current.toString());
                current.setLength(0);
            } else if (character == '"') {
                if (current.length() != 0) {
                    throw new IOException("Unexpected quote on line " + lineNumber + ".");
                }
                inQuotes = true;
            } else {
                current.append(character);
            }
        }
        if (inQuotes) {
            throw new IOException("Unclosed quoted field on line " + lineNumber + ".");
        }
        fields.add(current.toString());
        return fields;
    }

    private String escape(String value) throws IOException {
        if (value.contains("\r") || value.contains("\n")) {
            throw new IOException("CSV values cannot contain line breaks.");
        }
        if (value.contains(",") || value.contains("\"") || !value.equals(value.trim())) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
