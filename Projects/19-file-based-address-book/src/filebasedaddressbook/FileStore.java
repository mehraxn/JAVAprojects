package filebasedaddressbook;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileStore {
    private static final String DELIMITER = "\t";

    public List<Contact> load(Path path) throws IOException {
        requirePath(path);
        if (Files.notExists(path)) {
            return Collections.emptyList();
        }
        if (!Files.isRegularFile(path)) {
            throw new IOException("Contact path is not a regular file: " + path);
        }

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<Contact> contacts = new ArrayList<>();
        Set<String> contactIds = new HashSet<>();
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] fields = line.split(DELIMITER, -1);
            if (fields.length != 4) {
                throw new IOException("Invalid contact data on line " + (index + 1));
            }
            try {
                Contact contact = new Contact(fields[0], fields[1], fields[2], fields[3]);
                if (!contactIds.add(contact.getId())) {
                    throw new IOException("Duplicate contact ID on line " + (index + 1));
                }
                contacts.add(contact);
            } catch (IllegalArgumentException exception) {
                throw new IOException("Invalid contact data on line " + (index + 1), exception);
            }
        }
        return Collections.unmodifiableList(contacts);
    }

    public void save(Path path, List<Contact> contacts) throws IOException {
        requirePath(path);
        if (contacts == null) {
            throw new IllegalArgumentException("Contacts must not be null");
        }
        List<String> lines = new ArrayList<>();
        Set<String> contactIds = new HashSet<>();
        for (Contact contact : contacts) {
            if (contact == null) {
                throw new IllegalArgumentException("Contacts must not contain null");
            }
            if (!contactIds.add(contact.getId())) {
                throw new IllegalArgumentException("Duplicate contact ID: " + contact.getId());
            }
            lines.add(contact.getId() + DELIMITER
                    + contact.getName() + DELIMITER
                    + contact.getPhoneNumber() + DELIMITER
                    + contact.getEmail());
        }

        Path absolutePath = path.toAbsolutePath();
        Path parent = absolutePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        writeAtomically(absolutePath, lines);
    }

    /**
     * Atomic-style save: write to a temporary file in the target directory, then
     * move it over the target. If anything fails halfway, the original file is
     * left untouched and the temporary file is cleaned up.
     */
    private void writeAtomically(Path target, List<String> lines) throws IOException {
        Path directory = target.getParent();
        Path tempFile = directory == null
                ? Files.createTempFile("addressbook", ".tsv.tmp")
                : Files.createTempFile(directory, "addressbook", ".tsv.tmp");
        try {
            Files.write(tempFile, lines, StandardCharsets.UTF_8);
            try {
                Files.move(tempFile, target,
                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(tempFile, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException cleanupFailure) {
                exception.addSuppressed(cleanupFailure);
            }
            throw exception;
        }
    }

    public void importContacts(Path path, AddressBook addressBook) throws IOException {
        if (addressBook == null) {
            throw new IllegalArgumentException("Address book must not be null");
        }
        // Load fully first, then hand off to the address book's conflict-safe
        // import so a conflict leaves the existing contacts unchanged.
        addressBook.importContacts(load(path));
    }

    public void exportContacts(Path path, AddressBook addressBook) throws IOException {
        if (addressBook == null) {
            throw new IllegalArgumentException("Address book must not be null");
        }
        save(path, addressBook.listContactsSorted());
    }

    private static void requirePath(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("File path must not be null");
        }
    }
}
