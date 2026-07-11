package authenticationsystem;

/**
 * Safe public view of a stored user. Contains only ID, username, and role -
 * never the password hash, salt, or any raw password material. This is the
 * only user type returned by the public AuthService API; the internal User
 * record stays inside the service.
 */
public final class PublicUser {
    private final String id;
    private final String username;
    private final User.Role role;

    PublicUser(String id, String username, User.Role role) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }
        this.id = id.trim();
        this.username = username.trim();
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public User.Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return id + ": " + username + " [" + role + "]";
    }
}
