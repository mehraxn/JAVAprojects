package blogapi;

public class User {
    private final String id;
    private final String name;

    public User(String id, String name) {
        this.id = requireText(id, "User ID");
        this.name = requireText(name, "User name");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User copy() {
        return new User(id, name);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        String result = value.trim();
        if (result.length() > 100) {
            throw new IllegalArgumentException(fieldName + " cannot exceed 100 characters.");
        }
        return result;
    }

    @Override
    public String toString() {
        return id + ": " + name;
    }
}
