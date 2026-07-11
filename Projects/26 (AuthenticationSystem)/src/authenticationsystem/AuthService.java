package authenticationsystem;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * In-memory authentication service for learning: registration, PBKDF2 login,
 * expiring sessions, and role-based authorization.
 *
 * Design notes:
 * - Public registration always creates USER accounts; ADMIN accounts exist
 *   only through {@link #seedAdminForDemo}, which is local-demo-only.
 * - All public methods return safe views (PublicUser, SessionInfo) that never
 *   contain password hashes or salts. The internal User records stay private.
 * - The service takes ownership of every password char[] passed in and zeroes
 *   it before returning, even on failure.
 * - Time comes from an injectable {@link Clock}, so session expiry is
 *   deterministic in tests (no Thread.sleep).
 * - Unknown usernames still run one PBKDF2 verification against dummy
 *   credentials, so a login failure takes a similar time whether or not the
 *   username exists (educational timing hardening, not full side-channel
 *   resistance).
 */
public class AuthService {
    private final Map<String, User> usersByUsername = new LinkedHashMap<>();
    private final Map<String, User> usersById = new LinkedHashMap<>();
    private final Map<String, Session> sessionsByToken = new LinkedHashMap<>();
    private final PasswordHasher passwordHasher;
    private final PasswordPolicy passwordPolicy = new PasswordPolicy();
    private final SecureRandom secureRandom = new SecureRandom();
    private final Duration sessionDuration;
    private final Clock clock;
    private long nextUserId = 1;
    private String dummySalt;
    private String dummyHash;

    public AuthService(PasswordHasher passwordHasher) {
        this(passwordHasher, Duration.ofMinutes(30));
    }

    public AuthService(PasswordHasher passwordHasher, Duration sessionDuration) {
        this(passwordHasher, sessionDuration, Clock.systemUTC());
    }

    public AuthService(PasswordHasher passwordHasher, Duration sessionDuration, Clock clock) {
        if (passwordHasher == null) {
            throw new IllegalArgumentException("Password hasher cannot be null.");
        }
        if (sessionDuration == null || sessionDuration.isZero() || sessionDuration.isNegative()) {
            throw new IllegalArgumentException("Session duration must be positive.");
        }
        if (clock == null) {
            throw new IllegalArgumentException("Clock cannot be null.");
        }
        this.passwordHasher = passwordHasher;
        this.sessionDuration = sessionDuration;
        this.clock = clock;
    }

    /**
     * Registers a normal USER account. Public registration can never create
     * an ADMIN. The password array is zeroed before this method returns.
     */
    public synchronized PublicUser registerUser(String username, char[] password)
            throws GeneralSecurityException {
        return register(username, password, User.Role.USER);
    }

    /**
     * Creates an ADMIN account for local demos and tests only. A real system
     * would manage administrators through a separate, audited process - this
     * method exists so the authorization demo has an ADMIN to work with.
     */
    public synchronized PublicUser seedAdminForDemo(String username, char[] password)
            throws GeneralSecurityException {
        return register(username, password, User.Role.ADMIN);
    }

    private PublicUser register(String username, char[] password, User.Role role)
            throws GeneralSecurityException {
        try {
            String validUsername = validateUsername(username);
            passwordPolicy.validate(password);
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
            return publicView(user);
        } finally {
            clearPassword(password);
        }
    }

    /**
     * Verifies the password and issues an expiring session. Returns null when
     * the username or password is wrong (the caller cannot tell which). The
     * password array is zeroed before this method returns.
     */
    public synchronized SessionInfo login(String username, char[] password)
            throws GeneralSecurityException {
        try {
            String validUsername = validateUsername(username);
            if (password == null || password.length == 0) {
                throw new IllegalArgumentException("Password cannot be empty.");
            }
            if (password.length > PasswordPolicy.MAXIMUM_LENGTH) {
                throw new IllegalArgumentException("Password cannot exceed "
                        + PasswordPolicy.MAXIMUM_LENGTH + " characters.");
            }
            User user = usersByUsername.get(normalizeUsername(validUsername));
            if (user == null) {
                // Educational timing hardening: hash anyway so an unknown
                // username does not return noticeably faster than a wrong
                // password for an existing username.
                verifyAgainstDummyCredentials(password);
                return null;
            }
            if (!passwordHasher.verify(password, user.getSalt(), user.getPasswordHash())) {
                return null;
            }

            String token = generateUniqueToken();
            Session session = new Session(token, user.getId(),
                    Instant.now(clock).plus(sessionDuration));
            sessionsByToken.put(token, session);
            return sessionView(session, user);
        } finally {
            clearPassword(password);
        }
    }

    /**
     * Returns the user for a valid, unexpired session token, or null.
     * Expired sessions are removed when they are noticed here.
     */
    public synchronized PublicUser authenticate(String token) {
        String validToken = requireToken(token);
        Session session = sessionsByToken.get(validToken);
        if (session == null) {
            return null;
        }
        if (session.isExpired(Instant.now(clock))) {
            sessionsByToken.remove(validToken);
            return null;
        }
        User user = usersById.get(session.getUserId());
        return user == null ? null : publicView(user);
    }

    /** Revokes a session. Returns false when the token was not active. */
    public synchronized boolean logout(String token) {
        return sessionsByToken.remove(requireToken(token)) != null;
    }

    /**
     * True when the token belongs to a live session whose role satisfies the
     * requirement: every authenticated user passes a USER check, only ADMIN
     * passes an ADMIN check.
     */
    public synchronized boolean canAccess(String token, User.Role requiredRole) {
        if (requiredRole == null) {
            throw new IllegalArgumentException("Required role cannot be null.");
        }
        PublicUser user = authenticate(token);
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

    /** Removes every expired session and returns how many were removed. */
    public synchronized int removeExpiredSessions() {
        Instant now = Instant.now(clock);
        int removed = 0;
        Iterator<Session> iterator = sessionsByToken.values().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isExpired(now)) {
                iterator.remove();
                removed++;
            }
        }
        return removed;
    }

    public synchronized int getActiveSessionCount() {
        return sessionsByToken.size();
    }

    private PublicUser publicView(User user) {
        return new PublicUser(user.getId(), user.getUsername(), user.getRole());
    }

    private SessionInfo sessionView(Session session, User user) {
        return new SessionInfo(session.getToken(), user.getId(), user.getUsername(),
                user.getRole(), session.getExpiresAt());
    }

    private void verifyAgainstDummyCredentials(char[] password)
            throws GeneralSecurityException {
        if (dummyHash == null) {
            char[] dummyPassword = "local-dummy-timing-password".toCharArray();
            dummySalt = passwordHasher.generateSalt();
            dummyHash = passwordHasher.hash(dummyPassword, dummySalt);
            clearPassword(dummyPassword);
        }
        passwordHasher.verify(password, dummySalt, dummyHash);
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
