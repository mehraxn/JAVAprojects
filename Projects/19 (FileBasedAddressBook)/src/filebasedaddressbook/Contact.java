package filebasedaddressbook;

import java.util.Locale;

public class Contact {
    private final String id;
    private String name;
    private String phoneNumber;
    private String email;

    public Contact(String id, String name, String phoneNumber, String email) {
        this.id = requireField(id, "Contact ID");
        updateDetails(name, phoneNumber, email);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }

    public void updateDetails(String name, String phoneNumber, String email) {
        String validName = requireField(name, "Contact name");
        String validPhone = requireField(phoneNumber, "Phone number");
        String validEmail = requireField(email, "Email");
        if (!validEmail.contains("@")) {
            throw new IllegalArgumentException("Email must contain @");
        }
        this.name = validName;
        this.phoneNumber = validPhone;
        this.email = validEmail;
    }

    public boolean matches(String searchText) {
        if (searchText == null) {
            throw new IllegalArgumentException("Search text must not be null");
        }
        String query = searchText.trim().toLowerCase(Locale.ROOT);
        return id.toLowerCase(Locale.ROOT).contains(query)
                || name.toLowerCase(Locale.ROOT).contains(query)
                || phoneNumber.toLowerCase(Locale.ROOT).contains(query)
                || email.toLowerCase(Locale.ROOT).contains(query);
    }

    private static String requireField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        String trimmed = value.trim();
        if (trimmed.indexOf('\t') >= 0 || trimmed.indexOf('\n') >= 0
                || trimmed.indexOf('\r') >= 0) {
            throw new IllegalArgumentException(fieldName + " must not contain tabs or line breaks");
        }
        return trimmed;
    }
}
