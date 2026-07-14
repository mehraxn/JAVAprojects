package filebasedaddressbook;

import java.lang.reflect.Modifier;

public final class ContactTest {
    private ContactTest() {
    }

    static void run(Assert t) {
        // Valid creation and getters
        Contact contact = new Contact("C001", "Amira Khan", "+49 111 222", "amira@example.com");
        t.assertEquals("C001", contact.getId(), "getId returns constructor value");
        t.assertEquals("Amira Khan", contact.getName(), "getName returns constructor value");
        t.assertEquals("+49 111 222", contact.getPhoneNumber(),
                "getPhoneNumber returns constructor value");
        t.assertEquals("amira@example.com", contact.getEmail(),
                "getEmail returns constructor value");

        t.assertTrue(Modifier.isFinal(Contact.class.getModifiers()),
                "Contact is final (fixes strict-compile this-escape warning)");

        // Trimming
        t.assertEquals("C002", new Contact("  C002 ", "Name", "123", "a@b.co").getId(),
                "ID is trimmed");
        t.assertEquals("Name", new Contact("C002", "  Name  ", "123", "a@b.co").getName(),
                "name is trimmed");

        // Blank/null field validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact(null, "Name", "123", "a@b.co"), "null ID rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("  ", "Name", "123", "a@b.co"), "blank ID rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", null, "123", "a@b.co"), "null name rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "  ", "123", "a@b.co"), "blank name rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Name", null, "a@b.co"), "null phone rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Name", "   ", "a@b.co"), "blank phone rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Name", "123", null), "null email rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Name", "123", "   "), "blank email rejected");

        // Tabs and line breaks rejected
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Na\tme", "123", "a@b.co"), "tab in name rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Name", "1\n2", "a@b.co"), "newline in phone rejected");

        // Email validation (simple/educational)
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Name", "123", "no-at-sign"), "email without @ rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Name", "123", "a@@b.co"),
                "email with two @ rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Name", "123", "a@b@c.co"),
                "email with multiple @ rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Name", "123", "@example.com"),
                "email without local part rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Name", "123", "user@"),
                "email without domain rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Name", "123", "user@example"),
                "email without a dot in the domain rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Name", "123", "user@.example.com"),
                "email domain starting with a dot rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Name", "123", "user@example.com."),
                "email domain ending with a dot rejected");
        t.assertEquals("user.name@sub.example.co.uk",
                new Contact("C1", "Name", "123", "user.name@sub.example.co.uk").getEmail(),
                "reasonable email accepted");

        // updateDetails
        Contact editable = new Contact("C001", "Old", "111", "old@example.com");
        editable.updateDetails("New Name", "999", "new@example.com");
        t.assertEquals("New Name", editable.getName(), "updateDetails changes name");
        t.assertEquals("999", editable.getPhoneNumber(), "updateDetails changes phone");
        t.assertEquals("new@example.com", editable.getEmail(), "updateDetails changes email");
        t.assertEquals("C001", editable.getId(), "updateDetails never changes the ID");

        // Failed update must not partially mutate the contact
        t.assertThrows(IllegalArgumentException.class,
                () -> editable.updateDetails("Newer Name", "888", "broken-email"),
                "update with invalid email rejected");
        t.assertEquals("New Name", editable.getName(), "failed update leaves name unchanged");
        t.assertEquals("999", editable.getPhoneNumber(), "failed update leaves phone unchanged");
        t.assertEquals("new@example.com", editable.getEmail(),
                "failed update leaves email unchanged");

        // copy() and copy constructor are independent
        Contact original = new Contact("C001", "Original", "111", "orig@example.com");
        Contact copy = original.copy();
        copy.updateDetails("Changed", "222", "changed@example.com");
        t.assertEquals("Original", original.getName(),
                "mutating a copy() leaves the original unchanged");
        Contact copy2 = new Contact(original);
        copy2.updateDetails("Also changed", "333", "also@example.com");
        t.assertEquals("Original", original.getName(),
                "mutating a copy-constructed contact leaves the original unchanged");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Contact((Contact) null), "copy constructor rejects null");

        // matches (case-insensitive across all fields)
        Contact searchable = new Contact("C001", "Amira Khan", "+49 111 222", "amira@example.com");
        t.assertTrue(searchable.matches("AMIRA"), "matches name case-insensitively");
        t.assertTrue(searchable.matches("111"), "matches phone substring");
        t.assertTrue(searchable.matches("EXAMPLE.COM"), "matches email case-insensitively");
        t.assertTrue(searchable.matches("c001"), "matches ID case-insensitively");
        t.assertFalse(searchable.matches("zzz"), "does not match unrelated text");
        t.assertThrows(IllegalArgumentException.class,
                () -> searchable.matches(null), "null search text rejected");
    }
}
