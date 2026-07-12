package contactsrestapi;

import java.util.ArrayList;
import java.util.List;

public final class JsonUtilTest {
    private JsonUtilTest() {
    }

    static void run(Assert t) {
        // Plain serialization
        Contact ada = new Contact("C-1", "Ada Lovelace", "ada@example.com", "+49 123", "note");
        t.assertEquals("{\"id\":\"C-1\",\"name\":\"Ada Lovelace\","
                + "\"email\":\"ada@example.com\",\"phone\":\"+49 123\",\"notes\":\"note\"}",
                JsonUtil.toJson(ada), "contact serializes to expected JSON");
        t.assertEquals("null", JsonUtil.toJson((Contact) null), "null contact serializes to null");

        // Escaping: quotes, backslashes, newlines, tabs, carriage returns, control chars
        Contact tricky = new Contact("C-2", "Quote \" Backslash \\ End",
                "", "", "line1\nline2\ttab\rreturn" + (char) 1 + "control");
        String json = JsonUtil.toJson(tricky);
        t.assertContains(json, "Quote \\\" Backslash \\\\ End", "quotes and backslashes escaped");
        t.assertContains(json, "line1\\nline2", "newline escaped as \\n");
        t.assertContains(json, "\\ttab", "tab escaped as \\t");
        t.assertContains(json, "\\rreturn", "carriage return escaped as \\r");
        t.assertContains(json, "\\u0001control", "control character escaped as unicode");
        t.assertFalse(json.contains("\n"), "no raw newline leaks into JSON output");
        t.assertFalse(json.contains("\t"), "no raw tab leaks into JSON output");
        t.assertFalse(json.contains("\r"), "no raw carriage return leaks into JSON output");
        t.assertFalse(json.indexOf((char) 1) >= 0,
                "no raw control character leaks into JSON output");

        // Every double quote in the output is either a field delimiter or escaped
        int unescapedInteriorQuotes = 0;
        boolean inString = false;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '\\') {
                i++;
            } else if (c == '"') {
                inString = !inString;
            } else if (inString && c == '"') {
                unescapedInteriorQuotes++;
            }
        }
        t.assertFalse(inString, "all JSON strings are properly closed");
        t.assertEquals(0, unescapedInteriorQuotes, "no unescaped quotes inside JSON strings");

        // List serialization
        List<Contact> contacts = new ArrayList<Contact>();
        t.assertEquals("[]", JsonUtil.toJson(contacts), "empty list serializes to []");
        contacts.add(ada);
        contacts.add(new Contact("C-3", "Grace", "", "", ""));
        String listJson = JsonUtil.toJson(contacts);
        t.assertTrue(listJson.startsWith("[{"), "list JSON starts with [{");
        t.assertTrue(listJson.endsWith("}]"), "list JSON ends with }]");
        t.assertContains(listJson, "},{", "list entries are comma-separated");
        t.assertContains(listJson, "\"id\":\"C-1\"", "list contains first contact");
        t.assertContains(listJson, "\"id\":\"C-3\"", "list contains second contact");
        t.assertThrows(IllegalArgumentException.class,
                () -> JsonUtil.toJson((List<Contact>) null), "null list rejected");

        // Error responses
        t.assertEquals("{\"error\":\"Contact not found.\"}",
                JsonUtil.error("Contact not found."), "error message serializes to error JSON");
        t.assertEquals("{\"error\":\"Bad \\\"input\\\"\"}",
                JsonUtil.error("Bad \"input\""), "error message is escaped");
        t.assertEquals("{\"error\":\"Request failed.\"}",
                JsonUtil.error(null), "null error message gets a default text");
    }
}
