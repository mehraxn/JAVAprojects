package filebasedaddressbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddressBook {
    private final Map<String, Contact> contacts = new LinkedHashMap<>();

    public void addContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact must not be null");
        }
        if (contacts.containsKey(contact.getId())) {
            throw new IllegalArgumentException("Contact ID already exists: " + contact.getId());
        }
        contacts.put(contact.getId(), contact);
    }

    public Contact getContact(String contactId) {
        String validId = requireId(contactId);
        Contact contact = contacts.get(validId);
        if (contact == null) {
            throw new IllegalArgumentException("Unknown contact ID: " + validId);
        }
        return contact;
    }

    public boolean containsContact(String contactId) {
        return contactId != null && contacts.containsKey(contactId.trim());
    }

    public void updateContact(String contactId, String name, String phone, String email) {
        getContact(contactId).updateDetails(name, phone, email);
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
                matches.add(contact);
            }
        }
        sortContacts(matches);
        return Collections.unmodifiableList(matches);
    }

    public List<Contact> listContactsSorted() {
        List<Contact> sortedContacts = new ArrayList<>(contacts.values());
        sortContacts(sortedContacts);
        return Collections.unmodifiableList(sortedContacts);
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
