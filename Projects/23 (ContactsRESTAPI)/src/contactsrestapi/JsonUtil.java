package contactsrestapi;

import java.util.List;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static String toJson(Contact contact) {
        if (contact == null) {
            return "null";
        }
        return "{\"id\":\"" + escape(contact.getId())
                + "\",\"name\":\"" + escape(contact.getName())
                + "\",\"email\":\"" + escape(contact.getEmail())
                + "\",\"phone\":\"" + escape(contact.getPhone())
                + "\",\"notes\":\"" + escape(contact.getNotes()) + "\"}";
    }

    public static String toJson(List<Contact> contacts) {
        if (contacts == null) {
            throw new IllegalArgumentException("Contact list cannot be null.");
        }
        StringBuilder json = new StringBuilder("[");
        for (int index = 0; index < contacts.size(); index++) {
            if (index > 0) {
                json.append(',');
            }
            json.append(toJson(contacts.get(index)));
        }
        return json.append(']').toString();
    }

    public static String error(String message) {
        return "{\"error\":\"" + escape(message == null ? "Request failed." : message) + "\"}";
    }

    private static String escape(String value) {
        StringBuilder escaped = new StringBuilder();
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (character == '"') {
                escaped.append("\\\"");
            } else if (character == '\\') {
                escaped.append("\\\\");
            } else if (character == '\n') {
                escaped.append("\\n");
            } else if (character == '\r') {
                escaped.append("\\r");
            } else if (character == '\t') {
                escaped.append("\\t");
            } else if (character < 0x20) {
                escaped.append(String.format("\\u%04x", (int) character));
            } else {
                escaped.append(character);
            }
        }
        return escaped.toString();
    }
}
