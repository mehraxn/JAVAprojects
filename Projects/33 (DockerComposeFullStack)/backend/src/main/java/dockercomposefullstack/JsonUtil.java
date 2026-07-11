package dockercomposefullstack;

import java.util.List;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static String note(Note note) {
        return "{\"id\":" + note.getId()
                + ",\"text\":\"" + escape(note.getText()) + "\""
                + ",\"createdAt\":\"" + note.getCreatedAt() + "\"}";
    }

    public static String notes(List<Note> notes) {
        StringBuilder json = new StringBuilder("[");
        for (int index = 0; index < notes.size(); index++) {
            if (index > 0) {
                json.append(',');
            }
            json.append(note(notes.get(index)));
        }
        return json.append(']').toString();
    }

    public static String message(String key, String value) {
        return "{\"" + escape(key) + "\":\"" + escape(value) + "\"}";
    }

    /**
     * Minimal extractor for one string field from a small JSON object like
     * {"text":"..."}. Returns null when the body is not a JSON object, the
     * field is missing, or the value is not a valid JSON string.
     *
     * Deliberately tiny for this dependency-free demo — a real project should
     * use a JSON library instead of hand-parsing.
     */
    public static String extractStringField(String json, String field) {
        if (json == null || field == null) {
            return null;
        }
        String trimmed = json.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            return null;
        }
        int keyIndex = trimmed.indexOf("\"" + field + "\"");
        if (keyIndex < 0) {
            return null;
        }
        int colon = trimmed.indexOf(':', keyIndex + field.length() + 2);
        if (colon < 0) {
            return null;
        }
        int index = colon + 1;
        while (index < trimmed.length() && Character.isWhitespace(trimmed.charAt(index))) {
            index++;
        }
        if (index >= trimmed.length() || trimmed.charAt(index) != '"') {
            return null;
        }
        StringBuilder value = new StringBuilder();
        index++;
        while (index < trimmed.length()) {
            char current = trimmed.charAt(index);
            if (current == '"') {
                return value.toString();
            }
            if (current == '\\') {
                index++;
                if (index >= trimmed.length()) {
                    return null;
                }
                char escaped = trimmed.charAt(index);
                switch (escaped) {
                    case '"' -> value.append('"');
                    case '\\' -> value.append('\\');
                    case '/' -> value.append('/');
                    case 'n' -> value.append('\n');
                    case 'r' -> value.append('\r');
                    case 't' -> value.append('\t');
                    case 'b' -> value.append('\b');
                    case 'f' -> value.append('\f');
                    case 'u' -> {
                        if (index + 4 >= trimmed.length()) {
                            return null;
                        }
                        try {
                            value.append((char) Integer.parseInt(
                                    trimmed.substring(index + 1, index + 5), 16));
                        } catch (NumberFormatException invalid) {
                            return null;
                        }
                        index += 4;
                    }
                    default -> {
                        return null;
                    }
                }
            } else {
                value.append(current);
            }
            index++;
        }
        return null;
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
