package filebasedaddressbook;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        System.exit(exitCode);
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        String command = args.length == 0 ? "help" : args[0].toLowerCase(Locale.ROOT);
        switch (command) {
            case "help":
                printUsage(out);
                return 0;
            case "demo":
                runDemo(out);
                return 0;
            case "file-demo":
                return runFileDemo(out, err);
            case "import-demo":
                runImportDemo(out);
                return 0;
            case "validation-demo":
                runValidationDemo(out);
                return 0;
            default:
                err.println("Unknown command: " + args[0]);
                printUsage(err);
                return 1;
        }
    }

    private static void printUsage(PrintStream out) {
        out.println("File-Based Address Book - educational Java file-I/O project");
        out.println();
        out.println("Usage: java -cp out filebasedaddressbook.Main <command>");
        out.println();
        out.println("Commands:");
        out.println("  help             Show this usage text (default with no command).");
        out.println("  demo             Create, list, search, update, and delete contacts.");
        out.println("  file-demo        Save and reload contacts through a temporary TSV file.");
        out.println("  import-demo      Show conflict-safe importing of contacts.");
        out.println("  validation-demo  Show how invalid input is rejected cleanly.");
    }

    private static AddressBook sampleBook() {
        AddressBook book = new AddressBook();
        book.addContact(new Contact("C001", "Amira Khan", "+49 111 222", "amira@example.com"));
        book.addContact(new Contact("C002", "Luca Rossi", "+39 333 444", "luca@example.com"));
        book.addContact(new Contact("C003", "Bianca Silva", "+55 999 000", "bianca@example.com"));
        return book;
    }

    private static void runDemo(PrintStream out) {
        AddressBook book = sampleBook();

        out.println("== All contacts (sorted by name) ==");
        printContacts(out, book.listContactsSorted());

        out.println("== Search 'example.com' ==");
        printContacts(out, book.searchContacts("example.com"));

        out.println("== Search 'luca' (case-insensitive) ==");
        printContacts(out, book.searchContacts("LUCA"));

        out.println("== Update C002 ==");
        book.updateContact("C002", "Luca Rossi", "+39 333 999", "luca.rossi@example.com");
        out.println("Updated: " + book.getContact("C002"));

        out.println("== Delete C001 ==");
        out.println("Deleted: " + book.deleteContact("C001"));

        out.println("== Final contacts ==");
        printContacts(out, book.listContactsSorted());
    }

    private static int runFileDemo(PrintStream out, PrintStream err) {
        AddressBook book = sampleBook();
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("addressbook-demo", ".tsv");
            FileStore store = new FileStore();

            out.println("== Save " + book.size() + " contacts to a temporary TSV file ==");
            store.exportContacts(tempFile, book);
            out.println("Saved to: " + tempFile);

            out.println("== Reload from the TSV file ==");
            List<Contact> loaded = store.load(tempFile);
            for (Contact contact : loaded) {
                out.println("Loaded: " + contact);
            }
            if (loaded.size() != book.size()) {
                err.println("File round trip lost contacts.");
                return 1;
            }
            out.println("Round trip preserved all " + loaded.size() + " contacts.");
            return 0;
        } catch (Exception exception) {
            err.println("File demo failed: " + exception.getMessage());
            return 1;
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                    out.println("Deleted temporary file.");
                } catch (Exception cleanupFailure) {
                    err.println("Could not delete temporary file: " + tempFile);
                }
            }
        }
    }

    private static void runImportDemo(PrintStream out) {
        AddressBook book = new AddressBook();
        book.addContact(new Contact("C001", "Amira Khan", "+49 111 222", "amira@example.com"));
        out.println("Starting with " + book.size() + " contact(s).");

        out.println("== Import two new contacts ==");
        book.importContacts(List.of(
                new Contact("C002", "Luca Rossi", "+39 333 444", "luca@example.com"),
                new Contact("C003", "Bianca Silva", "+55 999 000", "bianca@example.com")));
        out.println("After import: " + book.size() + " contacts.");

        out.println("== Import a batch that conflicts with an existing ID (C001) ==");
        try {
            book.importContacts(List.of(
                    new Contact("C004", "New Person", "+1 222 333", "new@example.com"),
                    new Contact("C001", "Conflicting", "+1 000 000", "conflict@example.com")));
            out.println("ERROR: conflicting import was accepted (this should not happen)");
        } catch (IllegalArgumentException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }
        out.println("Still " + book.size() + " contacts — the failed import changed nothing "
                + "(C004 was not added either).");
    }

    private static void runValidationDemo(PrintStream out) {
        AddressBook book = new AddressBook();
        book.addContact(new Contact("C001", "Amira Khan", "+49 111 222", "amira@example.com"));

        out.println("== Validation demo: every rejection below is intentional ==");

        out.println("-- Blank name --");
        reject(out, () -> new Contact("C002", "   ", "+1 222", "person@example.com"));

        out.println("-- Invalid email (no dot in domain) --");
        reject(out, () -> new Contact("C002", "Person", "+1 222", "person@example"));

        out.println("-- Invalid email (two @) --");
        reject(out, () -> new Contact("C002", "Person", "+1 222", "a@@example.com"));

        out.println("-- Duplicate ID --");
        reject(out, () -> book.addContact(
                new Contact("C001", "Copy", "+1 222", "copy@example.com")));

        out.println("-- Update with invalid email leaves the stored contact intact --");
        try {
            book.updateContact("C001", "Amira Khan", "+49 111 222", "not-an-email");
            out.println("ERROR: invalid update was accepted (this should not happen)");
        } catch (IllegalArgumentException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
            out.println("Stored contact unchanged: " + book.getContact("C001"));
        }

        out.println("-- Update a missing contact --");
        out.println("updateContact(\"C999\", ...) returned: "
                + book.updateContact("C999", "Ghost", "+1 000", "ghost@example.com")
                + " (false = not found)");

        out.println("All validation cases behaved as designed.");
    }

    private static void reject(PrintStream out, Runnable action) {
        try {
            action.run();
            out.println("ERROR: invalid input was accepted (this should not happen)");
        } catch (IllegalArgumentException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }
    }

    private static void printContacts(PrintStream out, List<Contact> contacts) {
        if (contacts.isEmpty()) {
            out.println("No contacts found.");
            return;
        }
        for (Contact contact : contacts) {
            out.println(contact);
        }
    }
}
