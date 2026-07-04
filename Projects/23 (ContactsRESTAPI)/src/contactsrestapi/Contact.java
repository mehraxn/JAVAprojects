package contactsrestapi;

public class Contact {
    private final String id;
    private String name;
    private String email;
    private String phone;
    private String notes;

    public Contact(String id, String name, String email, String phone, String notes) {
        this.id = validateId(id);
        updateDetails(name, email, phone, notes);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getNotes() {
        return notes;
    }

    public void updateDetails(String name, String email, String phone, String notes) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("Name cannot exceed 100 characters.");
        }
        String validEmail = email == null ? "" : email.trim();
        if (validEmail.length() > 254) {
            throw new IllegalArgumentException("Email address cannot exceed 254 characters.");
        }
        if (!validEmail.isEmpty() && !validEmail.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Email address is invalid.");
        }
        String validPhone = phone == null ? "" : phone.trim();
        if (!validPhone.isEmpty() && !validPhone.matches("[0-9+() .-]{3,30}")) {
            throw new IllegalArgumentException("Phone number contains unsupported characters.");
        }
        String validNotes = notes == null ? "" : notes.trim();
        if (validNotes.length() > 5_000) {
            throw new IllegalArgumentException("Notes cannot exceed 5000 characters.");
        }
        this.name = name.trim();
        this.email = validEmail;
        this.phone = validPhone;
        this.notes = validNotes;
    }

    static String validateId(String id) {
        if (id == null || !id.matches("[A-Za-z0-9_-]{1,40}")) {
            throw new IllegalArgumentException(
                    "Contact ID must contain 1-40 letters, numbers, underscores, or hyphens.");
        }
        return id;
    }

    public Contact copy() {
        return new Contact(id, name, email, phone, notes);
    }

    @Override
    public String toString() {
        return id + ": " + name + " | " + email + " | " + phone;
    }
}
