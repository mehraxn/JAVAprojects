package contactsrestapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryContactRepository {
    private final Map<String, Contact> contacts = new LinkedHashMap<String, Contact>();

    public synchronized Contact add(Contact contact) {
        requireContact(contact);
        if (contacts.containsKey(contact.getId())) {
            throw new IllegalArgumentException("Contact ID already exists: " + contact.getId());
        }
        contacts.put(contact.getId(), contact.copy());
        return contact.copy();
    }

    public synchronized Contact update(Contact contact) {
        requireContact(contact);
        if (!contacts.containsKey(contact.getId())) {
            return null;
        }
        contacts.put(contact.getId(), contact.copy());
        return contact.copy();
    }

    public synchronized Contact findById(String contactId) {
        requireId(contactId);
        Contact contact = contacts.get(contactId);
        return contact == null ? null : contact.copy();
    }

    public synchronized boolean containsId(String contactId) {
        requireId(contactId);
        return contacts.containsKey(contactId);
    }

    public synchronized List<Contact> findAll() {
        List<Contact> result = new ArrayList<Contact>();
        for (Contact contact : contacts.values()) {
            result.add(contact.copy());
        }
        return Collections.unmodifiableList(result);
    }

    public synchronized boolean deleteById(String contactId) {
        requireId(contactId);
        return contacts.remove(contactId) != null;
    }

    private void requireContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact cannot be null.");
        }
    }

    private void requireId(String contactId) {
        if (contactId == null || contactId.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact ID cannot be empty.");
        }
    }
}
