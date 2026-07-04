package authenticationsystem;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class AuthService {
    private final Map<String, User> usersByUsername = new LinkedHashMap<String, User>();
    private final Map<String, User> usersById = new LinkedHashMap<String, User>();
    private final Map<String, Session> sessionsByToken = new LinkedHashMap<String, Session>();
    private final PasswordHasher passwordHasher;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Duration sessionDuration;
    private long nextUserId = 1;

    public AuthService(PasswordHasher passwordHasher) {
        this(passwordHasher, Duration.ofMinutes(30));
    }

    public AuthService(PasswordHasher passwordHasher, Duration sessionDuration) {
        if (passwordHasher == null) {
            throw new IllegalArgumentException("Password hasher cannot be null.");
        }
        if (sessionDuration == null || sessionDuration.isZero() || sessionDuration.isNegative()) {
            throw new IllegalArgumentException("Session duration must be positive.");
        }
        this.passwordHasher = passwordHasher;
        this.sessionDuration = sessionDuration;
    }

    public synchronized User register(String username, char[] password, User.Role role)
            throws GeneralSecurityException {
        try {
            String validUsername = validateUsername(username);
            validatePasswordStrength(password);
            if (role == null) {
                throw new IllegalArgumentException("Role cannot be null.");
            }
            String key = normalizeUsername(validUsername);
            if (usersByUsername.containsKey(key)) {
                throw new IllegalArgumentException("Username is already registered.");
            }

            String salt = passwordHasher.generateSalt();
            String hash = passwordHasher.hash(password, salt);
            User user = new User("U-" + nextUserId, validUsername, hash, salt, role);
            usersByUsername.put(key, user);
            usersById.put(user.getId(), user);
            nextUserId++;
            return user.copy();
        } finally {
            clearPassword(password);
        }
    }

    public synchronized Session login(String username, char[] password)
            throws GeneralSecurityException {
        try {
            String validUsername = validateUsername(username);
            if (password == null || password.length == 0) {
                throw new IllegalArgumentException("Password cannot be empty.");
            }
            if (password.length > 128) {
                throw new IllegalArgumentException("Password cannot exceed 128 characters.");
            }
            User user = usersByUsername.get(normalizeUsername(validUsername));
            if (user == null
                    || !passwordHasher.verify(password, user.getSalt(), user.getPasswordHash())) {
                return null;
            }

            String token = generateUniqueToken();
            Session session = new Session(token, user.getId(), Instant.now().plus(sessionDuration));
            sessionsByToken.put(token, session);
            return session.copy();
        } finally {
            clearPassword(password);
        }
    }

    public synchronized User authenticate(String token) {
        String validToken = requireToken(token);
        Session session = sessionsByToken.get(validToken);
        if (session == null) {
            return null;
        }
        if (session.isExpired(Instant.now())) {
            sessionsByToken.remove(validToken);
            return null;
        }
        User user = usersById.get(session.getUserId());
        return user == null ? null : user.copy();
    }

    public synchronized boolean logout(String token) {
        return sessionsByToken.remove(requireToken(token)) != null;
    }

    public synchronized boolean canAccess(String token, User.Role requiredRole) {
        if (requiredRole == null) {
            throw new IllegalArgumentException("Required role cannot be null.");
        }
        User user = authenticate(token);
        if (user == null) {
            return false;
        }
        return requiredRole == User.Role.USER || user.getRole() == User.Role.ADMIN;
    }

    public String performUserAction(String token) {
        if (!canAccess(token, User.Role.USER)) {
            throw new SecurityException("A valid user session is required.");
        }
        return "Protected user action completed.";
    }

    public String performAdminAction(String token) {
        if (!canAccess(token, User.Role.ADMIN)) {
            throw new SecurityException("An ADMIN session is required.");
        }
        return "Protected admin action completed.";
    }

    private String generateUniqueToken() {
        String token;
        do {
            byte[] bytes = new byte[32];
            secureRandom.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        } while (sessionsByToken.containsKey(token));
        return token;
    }

    private String validateUsername(String username) {
        if (username == null || !username.trim().matches("[A-Za-z0-9_.-]{3,30}")) {
            throw new IllegalArgumentException(
                    "Username must contain 3-30 letters, numbers, dots, underscores, or hyphens.");
        }
        return username.trim();
    }

    private void validatePasswordStrength(char[] password) {
        if (password == null || password.length < 10) {
            throw new IllegalArgumentException("Password must contain at least 10 characters.");
        }
        if (password.length > 128) {
            throw new IllegalArgumentException("Password cannot exceed 128 characters.");
        }
        boolean upper = false;
        boolean lower = false;
        boolean digit = false;
        boolean special = false;
        for (char character : password) {
            upper |= Character.isUpperCase(character);
            lower |= Character.isLowerCase(character);
            digit |= Character.isDigit(character);
            special |= !Character.isLetterOrDigit(character);
        }
        if (!(upper && lower && digit && special)) {
            throw new IllegalArgumentException(
                    "Password must include uppercase, lowercase, digit, and special characters.");
        }
    }

    private String normalizeUsername(String username) {
        return username.toLowerCase(Locale.ROOT);
    }

    private String requireToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Session token cannot be empty.");
        }
        return token.trim();
    }

    private void clearPassword(char[] password) {
        if (password != null) {
            Arrays.fill(password, '\0');
        }
    }
}
