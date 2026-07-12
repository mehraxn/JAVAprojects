package eventregistrationsystem;

public final class Attendee {
    private final String id;
    private final String name;
    private final String email;

    public Attendee(String id, String name, String email) {
        this.id = requireText(id, "Attendee ID");
        this.name = requireText(name, "Attendee name");
        this.email = requireText(email, "Email");
        validateEmail(this.email);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    private static void validateEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 0 || at != email.lastIndexOf('@') || at == email.length() - 1) {
            throw new IllegalArgumentException("Email must contain exactly one @ with text on both sides");
        }
        String domain = email.substring(at + 1);
        if (!domain.contains(".") || domain.startsWith(".") || domain.endsWith(".")) {
            throw new IllegalArgumentException("Email domain must contain an internal dot");
        }
    }
}
