package authenticationsystem;

import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AuthService {
    private final Map<String, User> usersByUsername = new LinkedHashMap<>();
    private final Map<String, Session> sessionsByToken = new LinkedHashMap<>();
    private final PasswordHasher passwordHasher;

    public AuthService(PasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }

    public User register(String username, char[] password, User.Role role)
            throws GeneralSecurityException {
        // TODO: Validate input, hash the password, and store a unique user.
        throw new UnsupportedOperationException("TODO: register a user");
    }

    public Session login(String username, char[] password)
            throws GeneralSecurityException {
        // TODO: Verify credentials and create a random expiring session.
        throw new UnsupportedOperationException("TODO: log in a user");
    }

    public User authenticate(String token) {
        // TODO: Validate the session token and expiration time.
        throw new UnsupportedOperationException("TODO: authenticate a session");
    }

    public void logout(String token) {
        // TODO: Remove the session token.
        throw new UnsupportedOperationException("TODO: log out a session");
    }
}
