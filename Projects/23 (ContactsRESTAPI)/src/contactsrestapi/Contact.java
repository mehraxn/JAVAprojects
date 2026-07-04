package contactsrestapi;

public class Contact {
    private final String id;
    private String name;
    private String email;
    private String phone;

    public Contact(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    public void updateDetails(String name, String email, String phone) {
        // TODO: Validate every value before changing contact state.
        throw new UnsupportedOperationException("TODO: update contact details");
    }
}
