package filebasedaddressbook;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        AddressBook addressBook = new AddressBook();
        addressBook.addContact(new Contact(
                "C001", "Amira Khan", "+49 111 222", "amira@example.com"));
        addressBook.addContact(new Contact(
                "C002", "Luca Rossi", "+39 333 444", "luca@example.com"));

        addressBook.updateContact(
                "C002", "Luca Rossi", "+39 333 999", "luca@example.com");
        System.out.println("Search results for 'luca': "
                + addressBook.searchContacts("luca").size());

        if (args.length > 0) {
            Path path = Paths.get(args[0]);
            FileStore fileStore = new FileStore();
            fileStore.exportContacts(path, addressBook);
            List<Contact> loadedContacts = fileStore.load(path);
            System.out.println("Saved and loaded contacts: " + loadedContacts.size());
        } else {
            System.out.println("Pass a file path to demonstrate saving and loading.");
        }
    }
}
