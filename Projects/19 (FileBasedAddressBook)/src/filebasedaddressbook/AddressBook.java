package filebasedaddressbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AddressBook {
    private final Map<String, Contact> contacts = new LinkedHashMap<>();

    public void addContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact must not be null");
        }
        if (contacts.containsKey(contact.getId())) {
            throw new IllegalArgumentException("Contact ID already exists: " + contact.getId());
        }
        // Store a defensive copy so the caller cannot mutate our stored contact.
        contacts.put(contact.getId(), contact.copy());
    }

    public Contact getContact(String contactId) {
        String validId = requireId(contactId);
        Contact contact = contacts.get(validId);
        if (contact == null) {
            throw new IllegalArgumentException("Unknown contact ID: " + validId);
        }
        return contact.copy();
    }

    public Optional<Contact> findById(String contactId) {
        Contact contact = contacts.get(requireId(contactId));
        return contact == null ? Optional.empty() : Optional.of(contact.copy());
    }

    public boolean containsContact(String contactId) {
        return contactId != null && contacts.containsKey(contactId.trim());
    }

    public int size() {
        return contacts.size();
    }

    /**
     * Updates an existing contact. Returns {@code false} if no contact has the
     * given ID. A failed validation throws and leaves the stored contact
     * unchanged (the update is applied to a copy first).
     */
    public boolean updateContact(String contactId, String name, String phone, String email) {
        Contact existing = contacts.get(requireId(contactId));
        if (existing == null) {
            return false;
        }
        Contact updated = existing.copy();
        updated.updateDetails(name, phone, email);
        contacts.put(updated.getId(), updated);
        return true;
    }

    public boolean deleteContact(String contactId) {
        return contacts.remove(requireId(contactId)) != null;
    }

    public List<Contact> searchContacts(String searchText) {
        if (searchText == null) {
            throw new IllegalArgumentException("Search text must not be null");
        }
        List<Contact> matches = new ArrayList<>();
        for (Contact contact : contacts.values()) {
            if (contact.matches(searchText)) {
                matches.add(contact.copy());
            }
        }
        sortContacts(matches);
        return Collections.unmodifiableList(matches);
    }

    public List<Contact> listContactsSorted() {
        List<Contact> sortedContacts = new ArrayList<>();
        for (Contact contact : contacts.values()) {
            sortedContacts.add(contact.copy());
        }
        sortContacts(sortedContacts);
        return Collections.unmodifiableList(sortedContacts);
    }

    /**
     * Conflict-safe import: verifies every incoming contact before changing any
     * state. If any contact is null, or any ID conflicts with an existing
     * contact or repeats within the incoming list, nothing is imported.
     */
    public void importContacts(List<Contact> incoming) {
        if (incoming == null) {
            throw new IllegalArgumentException("Imported contacts must not be null");
        }
        Set<String> incomingIds = new LinkedHashSet<>();
        for (Contact contact : incoming) {
            if (contact == null) {
                throw new IllegalArgumentException("Imported contacts must not contain null");
            }
            if (contacts.containsKey(contact.getId())) {
                throw new IllegalArgumentException(
                        "Contact ID already exists: " + contact.getId());
            }
            if (!incomingIds.add(contact.getId())) {
                throw new IllegalArgumentException(
                        "Duplicate contact ID in import: " + contact.getId());
            }
        }
        for (Contact contact : incoming) {
            contacts.put(contact.getId(), contact.copy());
        }
    }

    private static void sortContacts(List<Contact> contactList) {
        contactList.sort(Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Contact::getId));
    }

    private static String requireId(String contactId) {
        if (contactId == null || contactId.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact ID must not be blank");
        }
        return contactId.trim();
    }
}
