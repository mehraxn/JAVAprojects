package eventregistrationsystem;

public class Attendee {
    private final String id;
    private final String name;
    private final String email;

    public Attendee(String id, String name, String email) {
        this.id = requireText(id, "Attendee ID");
        this.name = requireText(name, "Attendee name");
        this.email = requireText(email, "Email");
        if (!this.email.contains("@")) {
            throw new IllegalArgumentException("Email must contain @");
        }
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
}
