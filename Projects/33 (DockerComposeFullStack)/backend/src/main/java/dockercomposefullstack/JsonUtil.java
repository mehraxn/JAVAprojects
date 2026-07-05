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

    private static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
