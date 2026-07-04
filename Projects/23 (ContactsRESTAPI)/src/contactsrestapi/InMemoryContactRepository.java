package contactsrestapi;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryContactRepository {
    private final Map<String, Contact> contacts = new LinkedHashMap<>();

    public Contact save(Contact contact) {
        // TODO: Validate and insert or update the contact.
        throw new UnsupportedOperationException("TODO: save a contact");
    }

    public Contact findById(String contactId) {
        // TODO: Return the contact or report that it does not exist.
        throw new UnsupportedOperationException("TODO: find a contact");
    }

    public List<Contact> findAll() {
        // TODO: Return a stable, read-only contact list.
        throw new UnsupportedOperationException("TODO: list contacts");
    }

    public boolean deleteById(String contactId) {
        // TODO: Delete a contact by ID.
        throw new UnsupportedOperationException("TODO: delete a contact");
    }
}
