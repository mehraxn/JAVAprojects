package contactsrestapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ContactService {
    private final InMemoryContactRepository repository;
    private long nextId = 1;

    public ContactService(InMemoryContactRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Contact repository cannot be null.");
        }
        this.repository = repository;
    }

    public synchronized Contact createContact(String name, String email, String phone, String notes) {
        String id;
        do {
            id = "C-" + nextId;
            if (repository.containsId(id)) {
                nextId++;
            }
        } while (repository.containsId(id));
        Contact saved = repository.add(new Contact(id, name, email, phone, notes));
        nextId++;
        return saved;
    }

    public Contact findContact(String contactId) {
        return repository.findById(requireId(contactId));
    }

    public List<Contact> listContacts() {
        return repository.findAll();
    }

    public Contact updateContact(String contactId,
            String name, String email, String phone, String notes) {
        String id = requireId(contactId);
        if (!repository.containsId(id)) {
            return null;
        }
        return repository.update(new Contact(id, name, email, phone, notes));
    }

    public List<Contact> search(String searchText, int offset, int limit) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative.");
        }
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100.");
        }

        String query = searchText == null ? "" : searchText.trim().toLowerCase(Locale.ROOT);
        List<Contact> matches = new ArrayList<Contact>();
        for (Contact contact : repository.findAll()) {
            if (query.isEmpty() || contains(contact.getName(), query)
                    || contains(contact.getEmail(), query)
                    || contains(contact.getPhone(), query)
                    || contains(contact.getNotes(), query)) {
                matches.add(contact);
            }
        }
        if (offset >= matches.size()) {
            return Collections.emptyList();
        }
        int end = Math.min(matches.size(), offset + limit);
        return Collections.unmodifiableList(new ArrayList<Contact>(matches.subList(offset, end)));
    }

    public boolean deleteContact(String contactId) {
        return repository.deleteById(requireId(contactId));
    }

    private boolean contains(String value, String query) {
        return value.toLowerCase(Locale.ROOT).contains(query);
    }

    private String requireId(String contactId) {
        return Contact.validateId(contactId == null ? null : contactId.trim());
    }
}
