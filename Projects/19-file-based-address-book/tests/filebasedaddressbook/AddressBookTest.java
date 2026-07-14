package filebasedaddressbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class AddressBookTest {
    private AddressBookTest() {
    }

    static void run(Assert t) {
        AddressBook book = new AddressBook();

        // Empty book
        t.assertEquals(0, book.size(), "new address book is empty");
        t.assertEquals(0, book.listContactsSorted().size(), "empty sorted list");
        t.assertFalse(book.findById("C001").isPresent(),
                "findById on empty book returns empty Optional");
        t.assertFalse(book.deleteContact("C001"), "delete on empty book returns false");
        t.assertFalse(book.containsContact("C001"), "containsContact false on empty book");

        // Add
        book.addContact(new Contact("C002", "Luca Rossi", "+39 333 444", "luca@example.com"));
        book.addContact(new Contact("C001", "Amira Khan", "+49 111 222", "amira@example.com"));
        book.addContact(new Contact("C003", "bianca silva", "+55 999 000", "bianca@example.com"));
        t.assertEquals(3, book.size(), "three contacts added");
        t.assertThrows(IllegalArgumentException.class,
                () -> book.addContact(null), "adding null contact rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> book.addContact(new Contact("C001", "Dup", "1", "dup@example.com")),
                "duplicate ID rejected");
        t.assertEquals(3, book.size(), "failed add does not change size");

        // Find / get
        t.assertTrue(book.findById("C001").isPresent(), "findById finds existing contact");
        t.assertEquals("Amira Khan", book.findById("C001").get().getName(),
                "found contact has expected data");
        t.assertFalse(book.findById("C999").isPresent(),
                "findById returns empty Optional for missing ID");
        t.assertEquals("Amira Khan", book.getContact("C001").getName(),
                "getContact returns the contact");
        t.assertThrows(IllegalArgumentException.class,
                () -> book.getContact("C999"), "getContact throws for missing ID");
        t.assertThrows(IllegalArgumentException.class,
                () -> book.findById("  "), "blank ID rejected on find");

        // Sorted listing is deterministic (by name case-insensitive, then ID)
        List<Contact> sorted = book.listContactsSorted();
        t.assertEquals("C001", sorted.get(0).getId(), "sorted: Amira first");
        t.assertEquals("C003", sorted.get(1).getId(), "sorted: bianca second (case-insensitive)");
        t.assertEquals("C002", sorted.get(2).getId(), "sorted: Luca last");
        t.assertThrows(UnsupportedOperationException.class,
                () -> sorted.add(sorted.get(0)), "sorted list is unmodifiable");

        // Search (case-insensitive, all fields), returns unmodifiable list
        t.assertEquals(3, book.searchContacts("example.com").size(),
                "search matches email substring across all contacts");
        t.assertEquals(1, book.searchContacts("LUCA").size(),
                "search is case-insensitive on name");
        t.assertEquals(1, book.searchContacts("+39").size(), "search matches phone substring");
        t.assertEquals(1, book.searchContacts("C003").size(), "search matches ID");
        t.assertEquals(0, book.searchContacts("nomatch").size(),
                "unmatched search returns empty list");
        t.assertThrows(IllegalArgumentException.class,
                () -> book.searchContacts(null), "null search text rejected");
        t.assertThrows(UnsupportedOperationException.class,
                () -> book.searchContacts("a").add(sorted.get(0)),
                "search result is unmodifiable");

        // Update
        t.assertTrue(book.updateContact("C002", "Luca Rossi", "+39 333 999",
                "luca.rossi@example.com"), "update of existing contact returns true");
        t.assertEquals("+39 333 999", book.getContact("C002").getPhoneNumber(),
                "update is persisted");
        t.assertFalse(book.updateContact("C999", "Ghost", "1", "ghost@example.com"),
                "update of missing contact returns false");
        t.assertThrows(IllegalArgumentException.class,
                () -> book.updateContact("C002", "Luca Rossi", "+39 333 999", "broken-email"),
                "update with invalid email rejected");
        t.assertEquals("luca.rossi@example.com", book.getContact("C002").getEmail(),
                "failed update does not corrupt the stored contact");

        // Delete
        t.assertTrue(book.deleteContact("C003"), "delete of existing contact returns true");
        t.assertFalse(book.containsContact("C003"), "deleted contact is gone");
        t.assertFalse(book.deleteContact("C003"), "second delete returns false");
        t.assertEquals(2, book.size(), "two contacts remain after delete");

        // Defensive copies: mutating returned contacts must not change stored state
        Contact fromGet = book.getContact("C001");
        fromGet.updateDetails("Hacked", "000", "hacked@example.com");
        t.assertEquals("Amira Khan", book.getContact("C001").getName(),
                "mutating a getContact() result does not change stored state");

        Contact fromFind = book.findById("C001").get();
        fromFind.updateDetails("Hacked", "000", "hacked@example.com");
        t.assertEquals("Amira Khan", book.getContact("C001").getName(),
                "mutating a findById() result does not change stored state");

        Contact fromList = book.listContactsSorted().get(0);
        fromList.updateDetails("Hacked", "000", "hacked@example.com");
        t.assertEquals("Amira Khan", book.getContact("C001").getName(),
                "mutating a listContactsSorted() result does not change stored state");

        Contact fromSearch = book.searchContacts("amira").get(0);
        fromSearch.updateDetails("Hacked", "000", "hacked@example.com");
        t.assertEquals("Amira Khan", book.getContact("C001").getName(),
                "mutating a searchContacts() result does not change stored state");

        // Adding then mutating the original input contact does not change stored state
        Contact input = new Contact("C010", "Input", "111", "input@example.com");
        book.addContact(input);
        input.updateDetails("Changed After Add", "222", "changed@example.com");
        t.assertEquals("Input", book.getContact("C010").getName(),
                "mutating the input contact after add does not change stored state");
        book.deleteContact("C010");

        // Conflict-safe import
        AddressBook importer = new AddressBook();
        importer.addContact(new Contact("C001", "Existing", "1", "existing@example.com"));
        List<Contact> good = new ArrayList<>();
        good.add(new Contact("C002", "New Two", "2", "two@example.com"));
        good.add(new Contact("C003", "New Three", "3", "three@example.com"));
        importer.importContacts(good);
        t.assertEquals(3, importer.size(), "successful import adds all contacts");

        // Failed import (conflict with existing ID) leaves the book unchanged
        List<Contact> conflicting = new ArrayList<>();
        conflicting.add(new Contact("C004", "Would Add", "4", "four@example.com"));
        conflicting.add(new Contact("C001", "Conflict", "1", "conflict@example.com"));
        t.assertThrows(IllegalArgumentException.class,
                () -> importer.importContacts(conflicting),
                "import conflicting with an existing ID is rejected");
        t.assertEquals(3, importer.size(),
                "failed import leaves the address book unchanged");
        t.assertFalse(importer.containsContact("C004"),
                "no contact from a failed import is added");

        // Import with a duplicate ID inside the batch is rejected
        List<Contact> internalDup = new ArrayList<>();
        internalDup.add(new Contact("C005", "Five", "5", "five@example.com"));
        internalDup.add(new Contact("C005", "Five Again", "5", "five2@example.com"));
        t.assertThrows(IllegalArgumentException.class,
                () -> importer.importContacts(internalDup),
                "import with a duplicate ID inside the batch is rejected");
        t.assertEquals(3, importer.size(), "failed batch import changes nothing");
        t.assertThrows(IllegalArgumentException.class,
                () -> importer.importContacts(null), "null import list rejected");

        // Imported contacts are stored as copies
        List<Contact> single = new ArrayList<>();
        Contact importInput = new Contact("C006", "Six", "6", "six@example.com");
        single.add(importInput);
        importer.importContacts(single);
        importInput.updateDetails("Mutated", "999", "mutated@example.com");
        t.assertEquals("Six", importer.getContact("C006").getName(),
                "mutating an imported input contact does not change stored state");
    }
}
