package contactsrestapi;

import java.lang.reflect.Modifier;

public final class ContactTest {
    private ContactTest() {
    }

    static void run(Assert t) {
        Contact contact = new Contact("C-1", "Ada Lovelace",
                "ada@example.com", "+49 123 4567", "Programming contact");
        t.assertEquals("C-1", contact.getId(), "getId returns constructor value");
        t.assertEquals("Ada Lovelace", contact.getName(), "getName returns constructor value");
        t.assertEquals("ada@example.com", contact.getEmail(), "getEmail returns constructor value");
        t.assertEquals("+49 123 4567", contact.getPhone(), "getPhone returns constructor value");
        t.assertEquals("Programming contact", contact.getNotes(), "getNotes returns constructor value");

        t.assertTrue(Modifier.isFinal(Contact.class.getModifiers()),
                "Contact is final (fixes strict-compile this-escape warning)");

        // ID validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact(null, "Ada", "", "", ""), "null ID rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("bad id", "Ada", "", "", ""), "ID with space rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("a/b", "Ada", "", "", ""), "ID with slash rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("x".repeat(41), "Ada", "", "", ""), "over-long ID rejected");

        // Name validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C-2", null, "", "", ""), "null name rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C-2", "", "", "", ""), "empty name rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C-2", "   ", "", "", ""), "whitespace name rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C-2", "x".repeat(101), "", "", ""), "over-long name rejected");
        t.assertEquals("Ada", new Contact("C-2", "  Ada  ", "", "", "").getName(),
                "name is trimmed");

        // Email validation (blank allowed by design; nonempty must look like an address)
        t.assertEquals("", new Contact("C-3", "Ada", null, "", "").getEmail(),
                "null email stored as empty string");
        t.assertEquals("", new Contact("C-3", "Ada", "  ", "", "").getEmail(),
                "blank email stored as empty string");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C-3", "Ada", "not-an-email", "", ""),
                "email without @ rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C-3", "Ada", "ada@nodomain", "", ""),
                "email without a dot in the domain rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C-3", "Ada", "a b@example.com", "", ""),
                "email with space rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C-3", "Ada", "a@" + "x".repeat(250) + ".com", "", ""),
                "over-long email rejected");
        t.assertEquals("ada@example.com",
                new Contact("C-3", "Ada", " ada@example.com ", "", "").getEmail(),
                "valid email accepted and trimmed");

        // Phone validation (blank allowed by design)
        t.assertEquals("", new Contact("C-4", "Ada", "", null, "").getPhone(),
                "null phone stored as empty string");
        t.assertEquals("+49 (123) 456-78",
                new Contact("C-4", "Ada", "", "+49 (123) 456-78", "").getPhone(),
                "phone with digits, +, parentheses, space, dash accepted");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C-4", "Ada", "", "call me", ""),
                "phone with letters rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C-4", "Ada", "", "12", ""),
                "too-short phone rejected");

        // Notes validation (blank allowed by design)
        t.assertEquals("", new Contact("C-5", "Ada", "", "", null).getNotes(),
                "null notes stored as empty string");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C-5", "Ada", "", "", "x".repeat(5001)),
                "over-long notes rejected");

        // updateDetails
        Contact editable = new Contact("C-6", "Ada", "ada@example.com", "+49 123", "old");
        editable.updateDetails("Ada Lovelace", "ada.l@example.com", "+49 456", "new");
        t.assertEquals("Ada Lovelace", editable.getName(), "updateDetails changes name");
        t.assertEquals("ada.l@example.com", editable.getEmail(), "updateDetails changes email");
        t.assertEquals("+49 456", editable.getPhone(), "updateDetails changes phone");
        t.assertEquals("new", editable.getNotes(), "updateDetails changes notes");
        t.assertEquals("C-6", editable.getId(), "updateDetails never changes the ID");

        // Failed update must not partially mutate the contact
        t.assertThrows(IllegalArgumentException.class,
                () -> editable.updateDetails("New Name", "broken-email", "+49 456", "newer"),
                "update with invalid email rejected");
        t.assertEquals("Ada Lovelace", editable.getName(),
                "failed update leaves name unchanged");
        t.assertEquals("ada.l@example.com", editable.getEmail(),
                "failed update leaves email unchanged");
        t.assertEquals("new", editable.getNotes(),
                "failed update leaves notes unchanged");

        // copy() is independent
        Contact original = new Contact("C-7", "Ada", "ada@example.com", "", "note");
        Contact copy = original.copy();
        copy.updateDetails("Changed", "", "", "");
        t.assertEquals("Ada", original.getName(), "mutating a copy leaves the original unchanged");
        t.assertEquals("Changed", copy.getName(), "copy itself is mutable");
    }
}
