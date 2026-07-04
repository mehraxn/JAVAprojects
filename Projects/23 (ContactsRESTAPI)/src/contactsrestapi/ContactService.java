package contactsrestapi;

import java.util.List;

public class ContactService {
    private final InMemoryContactRepository repository;

    public ContactService(InMemoryContactRepository repository) {
        this.repository = repository;
    }

    public Contact createContact(String name, String email, String phone) {
        // TODO: Validate values, generate an ID, and save a contact.
        throw new UnsupportedOperationException("TODO: create a contact");
    }

    public Contact updateContact(String contactId,
            String name, String email, String phone) {
        // TODO: Validate and update the requested contact.
        throw new UnsupportedOperationException("TODO: update a contact");
    }

    public List<Contact> search(String searchText, int offset, int limit) {
        // TODO: Search contacts and apply simple pagination.
        throw new UnsupportedOperationException("TODO: search contacts");
    }

    public boolean deleteContact(String contactId) {
        // TODO: Delegate deletion after validating the ID.
        throw new UnsupportedOperationException("TODO: delete a contact");
    }
}
