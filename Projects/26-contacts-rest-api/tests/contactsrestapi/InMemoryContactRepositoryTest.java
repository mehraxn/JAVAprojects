package contactsrestapi;

import java.util.List;

public final class InMemoryContactRepositoryTest {
    private InMemoryContactRepositoryTest() {
    }

    static void run(Assert t) {
        InMemoryContactRepository repository = new InMemoryContactRepository();

        // Empty repository
        t.assertEquals(0, repository.findAll().size(), "new repository is empty");
        t.assertNull(repository.findById("C-1"), "find on empty repository returns null");
        t.assertFalse(repository.deleteById("C-1"), "delete on empty repository returns false");
        t.assertFalse(repository.containsId("C-1"), "containsId on empty repository is false");

        // Save and find
        Contact ada = new Contact("C-1", "Ada", "ada@example.com", "+49 123", "first");
        Contact saved = repository.add(ada);
        t.assertEquals("C-1", saved.getId(), "add returns the saved contact");
        t.assertTrue(repository.containsId("C-1"), "containsId finds saved contact");
        Contact found = repository.findById("C-1");
        t.assertNotNull(found, "findById returns saved contact");
        t.assertEquals("Ada", found.getName(), "found contact has saved data");
        t.assertNull(repository.findById("C-99"), "findById for missing ID returns null");

        // ID lookups are trimmed and validated
        t.assertNotNull(repository.findById(" C-1 "), "IDs are trimmed before lookup");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.findById("bad id"), "malformed ID rejected on find");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.findById(null), "null ID rejected on find");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.deleteById("a/b"), "malformed ID rejected on delete");

        // Null and duplicate protection
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.add(null), "adding null contact rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.add(new Contact("C-1", "Copy", "", "", "")),
                "duplicate ID rejected");

        // List preserves insertion order and is unmodifiable
        repository.add(new Contact("C-2", "Grace", "grace@example.com", "", ""));
        List<Contact> all = repository.findAll();
        t.assertEquals(2, all.size(), "findAll returns all contacts");
        t.assertEquals("C-1", all.get(0).getId(), "findAll preserves insertion order (first)");
        t.assertEquals("C-2", all.get(1).getId(), "findAll preserves insertion order (second)");
        t.assertThrows(UnsupportedOperationException.class,
                () -> all.add(new Contact("C-3", "X", "", "", "")),
                "findAll list is unmodifiable");

        // Defensive copies: mutating returned contacts must not change stored state
        found.updateDetails("Hacked", "", "", "");
        t.assertEquals("Ada", repository.findById("C-1").getName(),
                "mutating a returned contact does not change the stored record");
        ada.updateDetails("Changed after save", "", "", "");
        t.assertEquals("Ada", repository.findById("C-1").getName(),
                "mutating the original contact after add does not change the stored record");

        // Update
        Contact updated = repository.update(
                new Contact("C-1", "Ada Lovelace", "ada@example.com", "", "updated"));
        t.assertNotNull(updated, "update of existing contact returns the contact");
        t.assertEquals("Ada Lovelace", repository.findById("C-1").getName(),
                "update replaces stored data");
        t.assertNull(repository.update(new Contact("C-99", "Ghost", "", "", "")),
                "update of missing contact returns null");

        // Delete
        t.assertTrue(repository.deleteById("C-2"), "delete of existing contact returns true");
        t.assertFalse(repository.containsId("C-2"), "deleted contact is gone");
        t.assertFalse(repository.deleteById("C-2"), "second delete returns false");
        t.assertEquals(1, repository.findAll().size(), "one contact remains after delete");
    }
}
