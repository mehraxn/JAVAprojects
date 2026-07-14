package filebasedaddressbook;

import java.util.Locale;

public final class Contact {
    private final String id;
    private String name;
    private String phoneNumber;
    private String email;

    public Contact(String id, String name, String phoneNumber, String email) {
        this.id = requireField(id, "Contact ID");
        // Validate everything into locals first, then assign — the constructor
        // never leaves a half-built contact if a field is invalid.
        String validName = requireField(name, "Contact name");
        String validPhone = requireField(phoneNumber, "Phone number");
        String validEmail = requireEmail(email);
        this.name = validName;
        this.phoneNumber = validPhone;
        this.email = validEmail;
    }

    /** Copy constructor: produces an independent contact with the same fields. */
    public Contact(Contact other) {
        if (other == null) {
            throw new IllegalArgumentException("Contact to copy must not be null");
        }
        this.id = other.id;
        this.name = other.name;
        this.phoneNumber = other.phoneNumber;
        this.email = other.email;
    }

    /** Returns an independent copy of this contact. */
    public Contact copy() {
        return new Contact(this);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }

    public void updateDetails(String name, String phoneNumber, String email) {
        // Validate all new values before mutating anything, so a failed update
        // never leaves the contact partially changed.
        String validName = requireField(name, "Contact name");
        String validPhone = requireField(phoneNumber, "Phone number");
        String validEmail = requireEmail(email);
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

    /**
     * Simple, educational email check — NOT production-grade validation.
     * Rules: exactly one '@'; non-blank local part; non-blank domain that
     * contains a dot and does not start or end with a dot.
     */
    private static String requireEmail(String email) {
        String trimmed = requireField(email, "Email");
        int at = trimmed.indexOf('@');
        if (at < 0 || at != trimmed.lastIndexOf('@')) {
            throw new IllegalArgumentException("Email must contain exactly one @");
        }
        String local = trimmed.substring(0, at);
        String domain = trimmed.substring(at + 1);
        if (local.isEmpty()) {
            throw new IllegalArgumentException("Email must have a local part before @");
        }
        if (domain.isEmpty()) {
            throw new IllegalArgumentException("Email must have a domain after @");
        }
        if (!domain.contains(".")) {
            throw new IllegalArgumentException("Email domain must contain a dot");
        }
        if (domain.startsWith(".") || domain.endsWith(".")) {
            throw new IllegalArgumentException("Email domain must not start or end with a dot");
        }
        return trimmed;
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + phoneNumber + " | " + email;
    }
}
