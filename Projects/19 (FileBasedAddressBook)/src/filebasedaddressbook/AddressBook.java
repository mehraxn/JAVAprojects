package filebasedaddressbook;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressBook {
    private final Map<String, Contact> contacts = new HashMap<>();

    public void addContact(Contact contact) {
        // TODO: Validate and add a contact with a unique identifier.
        throw new UnsupportedOperationException("TODO: add a contact");
    }

    public void updateContact(String contactId, String name, String phone, String email) {
        // TODO: Find the contact and delegate its update.
        throw new UnsupportedOperationException("TODO: update a contact");
    }

    public boolean deleteContact(String contactId) {
        // TODO: Remove the contact when present.
        throw new UnsupportedOperationException("TODO: delete a contact");
    }

    public List<Contact> searchContacts(String searchText) {
        // TODO: Return contacts matching the search text.
        throw new UnsupportedOperationException("TODO: search contacts");
    }

    public List<Contact> listContactsSorted() {
        // TODO: Return contacts sorted by name.
        throw new UnsupportedOperationException("TODO: sort contacts");
    }
}
