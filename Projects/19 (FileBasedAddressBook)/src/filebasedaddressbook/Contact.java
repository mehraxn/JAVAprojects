package filebasedaddressbook;

public class Contact {
    private final String id;
    private String name;
    private String phoneNumber;
    private String email;

    public Contact(String id, String name, String phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public void updateDetails(String name, String phoneNumber, String email) {
        // TODO: Validate and replace the editable contact details.
        throw new UnsupportedOperationException("TODO: update contact details");
    }

    public boolean matches(String searchText) {
        // TODO: Search case-insensitively across contact fields.
        throw new UnsupportedOperationException("TODO: match a contact");
    }
}
