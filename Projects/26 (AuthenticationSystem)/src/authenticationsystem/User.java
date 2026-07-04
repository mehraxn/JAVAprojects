package authenticationsystem;

public class User {
    public enum Role {
        USER,
        ADMIN
    }

    private final String id;
    private final String username;
    private final String passwordHash;
    private final String salt;
    private final Role role;

    public User(String id, String username,
            String passwordHash, String salt, Role role) {
        this.id = requireText(id, "User ID");
        this.username = requireText(username, "Username");
        this.passwordHash = requireText(passwordHash, "Password hash");
        this.salt = requireText(salt, "Password salt");
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    String getPasswordHash() {
        return passwordHash;
    }

    String getSalt() {
        return salt;
    }

    public User copy() {
        return new User(id, username, passwordHash, salt, role);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        return id + ": " + username + " [" + role + "]";
    }
}
