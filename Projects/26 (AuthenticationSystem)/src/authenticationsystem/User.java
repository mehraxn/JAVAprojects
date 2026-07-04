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
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }
    public Role getRole() { return role; }
}
