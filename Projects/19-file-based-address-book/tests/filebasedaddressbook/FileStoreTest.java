package filebasedaddressbook;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class FileStoreTest {
    private FileStoreTest() {
    }

    static void run(Assert t) throws Exception {
        FileStore store = new FileStore();

        t.assertThrows(IllegalArgumentException.class,
                () -> store.load(null), "null load path rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> store.save(null, new ArrayList<Contact>()), "null save path rejected");

        Path tempFile = Files.createTempFile("addressbook-test", ".tsv");
        try {
            t.assertThrows(IllegalArgumentException.class,
                    () -> store.save(tempFile, null), "null contact list rejected");

            // Round trip preserves every field, including UTF-8 and sort order
            List<Contact> contacts = new ArrayList<>();
            contacts.add(new Contact("C001", "Amira Khan", "+49 111 222", "amira@example.com"));
            contacts.add(new Contact("C002", "Sofía Muñoz", "+34 600 700", "sofia@example.es"));
            contacts.add(new Contact("C003", "李雷", "+86 138 0000", "lilei@example.cn"));
            store.save(tempFile, contacts);

            List<Contact> loaded = store.load(tempFile);
            t.assertEquals(3, loaded.size(), "save/load preserves all contacts");
            t.assertEquals("Amira Khan", loaded.get(0).getName(), "names survive the round trip");
            t.assertEquals("+34 600 700", loaded.get(1).getPhoneNumber(),
                    "phone numbers survive the round trip");
            t.assertEquals("sofia@example.es", loaded.get(1).getEmail(),
                    "emails survive the round trip");
            t.assertEquals("Sofía Muñoz", loaded.get(1).getName(),
                    "accented UTF-8 names are preserved");
            t.assertEquals("李雷", loaded.get(2).getName(),
                    "non-Latin UTF-8 names are preserved");
            t.assertThrows(UnsupportedOperationException.class,
                    () -> loaded.add(loaded.get(0)), "loaded list is unmodifiable");

            // Duplicate IDs on save rejected
            List<Contact> dup = new ArrayList<>();
            dup.add(new Contact("C001", "One", "1", "one@example.com"));
            dup.add(new Contact("C001", "Two", "2", "two@example.com"));
            t.assertThrows(IllegalArgumentException.class,
                    () -> store.save(tempFile, dup), "duplicate IDs rejected on save");

            // Atomic-style save: replacing the file leaves no temp file behind
            List<Contact> smaller = new ArrayList<>();
            smaller.add(new Contact("C009", "Only", "9", "only@example.com"));
            store.save(tempFile, smaller);
            t.assertEquals(1, store.load(tempFile).size(),
                    "saving over an existing file replaces its contents");
            int strayTempFiles = 0;
            try (java.util.stream.Stream<Path> siblings =
                    Files.list(tempFile.toAbsolutePath().getParent())) {
                strayTempFiles = (int) siblings
                        .filter(p -> p.getFileName().toString().startsWith("addressbook")
                                && p.getFileName().toString().endsWith(".tsv.tmp"))
                        .count();
            }
            t.assertEquals(0, strayTempFiles, "atomic save leaves no temporary files behind");

            // Malformed and invalid content on load
            expectLoadFailure(t, store, tempFile,
                    "C1\tName\t123", "row with too few fields rejected");
            expectLoadFailure(t, store, tempFile,
                    "C1\tName\t123\ta@b.co\textra", "row with too many fields rejected");
            expectLoadFailure(t, store, tempFile,
                    "C1\tName\t123\tnot-an-email", "invalid email in file rejected");
            expectLoadFailure(t, store, tempFile,
                    "C1\t\t123\ta@b.co", "blank name in file rejected");
            expectLoadFailure(t, store, tempFile,
                    "\tName\t123\ta@b.co", "blank ID in file rejected");
            expectLoadFailure(t, store, tempFile,
                    "C1\tName\t123\ta@b.co\nC1\tOther\t456\tc@d.co",
                    "duplicate IDs in file rejected");

            // Empty, blank, and blank-line files
            Files.write(tempFile, new byte[0]);
            t.assertEquals(0, store.load(tempFile).size(), "zero-byte file loads as empty list");
            Files.write(tempFile, "\n   \n\n".getBytes(StandardCharsets.UTF_8));
            t.assertEquals(0, store.load(tempFile).size(),
                    "whitespace-only file loads as empty list");
            Files.write(tempFile,
                    "C1\tName\t123\ta@b.co\n\n\nC2\tOther\t456\tc@d.co\n"
                            .getBytes(StandardCharsets.UTF_8));
            t.assertEquals(2, store.load(tempFile).size(),
                    "blank lines between rows are ignored");
        } finally {
            Files.deleteIfExists(tempFile);
        }

        // Missing file loads as an empty list
        Path missing = tempFile.resolveSibling(
                "addressbook-missing-" + System.nanoTime() + ".tsv");
        t.assertEquals(0, store.load(missing).size(), "missing file loads as empty list");

        // Directory paths are rejected on load
        Path tempDir = Files.createTempDirectory("addressbook-dir");
        try {
            t.assertThrows(IOException.class, () -> store.load(tempDir),
                    "loading a directory path rejected");
        } finally {
            Files.deleteIfExists(tempDir);
        }

        // exportContacts / importContacts round trip through an AddressBook
        Path exportFile = Files.createTempFile("addressbook-export", ".tsv");
        try {
            AddressBook source = new AddressBook();
            source.addContact(new Contact("C001", "Amira Khan", "+49 111 222", "amira@example.com"));
            source.addContact(new Contact("C002", "Luca Rossi", "+39 333 444", "luca@example.com"));
            store.exportContacts(exportFile, source);

            AddressBook target = new AddressBook();
            store.importContacts(exportFile, target);
            t.assertEquals(2, target.size(), "export then import copies all contacts");
            t.assertEquals("Amira Khan", target.getContact("C001").getName(),
                    "imported contact data is correct");

            // Importing again into a book that already has C001 is rejected atomically
            AddressBook conflicting = new AddressBook();
            conflicting.addContact(new Contact("C001", "Existing", "1", "existing@example.com"));
            t.assertThrows(IllegalArgumentException.class,
                    () -> store.importContacts(exportFile, conflicting),
                    "import with a conflicting ID is rejected");
            t.assertEquals(1, conflicting.size(),
                    "failed file import leaves the address book unchanged");
            t.assertThrows(IllegalArgumentException.class,
                    () -> store.importContacts(exportFile, null),
                    "importing into a null address book rejected");
        } finally {
            Files.deleteIfExists(exportFile);
        }
    }

    private static void expectLoadFailure(Assert t, FileStore store,
            Path file, String content, String message) throws IOException {
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        t.assertThrows(IOException.class, () -> store.load(file), message);
    }
}
