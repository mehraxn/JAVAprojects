package contactsrestapi;

import java.util.List;

public final class ContactServiceTest {
    private ContactServiceTest() {
    }

    static void run(Assert t) {
        t.assertThrows(IllegalArgumentException.class,
                () -> new ContactService(null), "null repository rejected");

        ContactService service = new ContactService(new InMemoryContactRepository());

        // Create with sequential IDs
        Contact ada = service.createContact("Ada Lovelace", "ada@example.com", "+49 123", "math");
        Contact grace = service.createContact("Grace Hopper", "grace@example.com", "", "compiler");
        Contact alan = service.createContact("Alan Turing", "alan@example.com", "", "crypto");
        t.assertEquals("C-1", ada.getId(), "first contact gets ID C-1");
        t.assertEquals("C-2", grace.getId(), "second contact gets ID C-2");
        t.assertEquals("C-3", alan.getId(), "third contact gets ID C-3");

        // Invalid create does not consume an ID slot silently
        t.assertThrows(IllegalArgumentException.class,
                () -> service.createContact("", "", "", ""), "create with blank name rejected");

        // Duplicate emails are allowed by design (no uniqueness rule)
        Contact adaTwin = service.createContact("Ada Twin", "ada@example.com", "", "");
        t.assertEquals("C-4", adaTwin.getId(), "duplicate email is allowed and gets next ID");
        t.assertTrue(service.deleteContact(adaTwin.getId()), "cleanup duplicate-email contact");

        // ID generation skips IDs already present in the repository
        InMemoryContactRepository seeded = new InMemoryContactRepository();
        seeded.add(new Contact("C-1", "Taken", "", "", ""));
        ContactService seededService = new ContactService(seeded);
        t.assertEquals("C-2", seededService.createContact("New", "", "", "").getId(),
                "ID generation skips IDs that already exist");

        // Find
        t.assertEquals("Ada Lovelace", service.findContact("C-1").getName(),
                "findContact returns stored contact");
        t.assertNull(service.findContact("C-99"), "findContact for missing ID returns null");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.findContact("bad id"), "malformed ID rejected on find");

        // List
        List<Contact> all = service.listContacts();
        t.assertEquals(3, all.size(), "listContacts returns all contacts");
        t.assertThrows(UnsupportedOperationException.class,
                () -> all.add(ada), "listContacts result is unmodifiable");
        all.get(0).updateDetails("Hacked", "", "", "");
        t.assertEquals("Ada Lovelace", service.findContact("C-1").getName(),
                "mutating a listed contact does not change stored state");

        // Update
        Contact updated = service.updateContact("C-1", "Ada Lovelace",
                "ada@example.com", "+49 456", "updated notes");
        t.assertNotNull(updated, "updateContact returns the updated contact");
        t.assertEquals("+49 456", service.findContact("C-1").getPhone(),
                "update is persisted");
        t.assertNull(service.updateContact("C-99", "Ghost", "", "", ""),
                "update of missing contact returns null");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.updateContact("C-1", "Ada", "broken-email", "", ""),
                "update with invalid email rejected");
        t.assertEquals("ada@example.com", service.findContact("C-1").getEmail(),
                "failed update does not corrupt the stored contact");
        t.assertEquals("updated notes", service.findContact("C-1").getNotes(),
                "failed update leaves previous notes intact");

        // Search
        t.assertEquals(3, service.search("", 0, 10).size(), "empty query matches everything");
        t.assertEquals(3, service.search(null, 0, 10).size(), "null query matches everything");
        t.assertEquals(1, service.search("lovelace", 0, 10).size(), "search by name matches");
        t.assertEquals(1, service.search("GRACE@EXAMPLE.COM", 0, 10).size(),
                "search by email is case-insensitive");
        t.assertEquals(1, service.search("+49 456", 0, 10).size(), "search by phone matches");
        t.assertEquals(1, service.search("crypto", 0, 10).size(), "search by notes matches");
        t.assertEquals(0, service.search("zzz-no-match", 0, 10).size(),
                "unmatched query returns empty list");
        t.assertThrows(UnsupportedOperationException.class,
                () -> service.search("", 0, 10).add(ada), "search result is unmodifiable");

        // Pagination
        List<Contact> page = service.search("", 1, 2);
        t.assertEquals(2, page.size(), "offset 1 limit 2 returns two contacts");
        t.assertEquals("C-2", page.get(0).getId(), "pagination skips the first contact");
        t.assertEquals("C-3", page.get(1).getId(), "pagination includes the third contact");
        t.assertEquals(1, service.search("", 2, 10).size(), "last page is shorter");
        t.assertEquals(0, service.search("", 50, 10).size(), "offset beyond results is empty");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.search("", -1, 10), "negative offset rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.search("", 0, 0), "zero limit rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.search("", 0, 101), "limit above 100 rejected");

        // Delete
        t.assertTrue(service.deleteContact("C-3"), "delete of existing contact returns true");
        t.assertFalse(service.deleteContact("C-3"), "delete of missing contact returns false");
        t.assertEquals(2, service.listContacts().size(), "two contacts remain");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.deleteContact("bad id"), "malformed ID rejected on delete");
    }
}
