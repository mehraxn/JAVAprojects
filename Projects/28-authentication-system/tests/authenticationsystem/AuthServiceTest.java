package authenticationsystem;

import static authenticationsystem.TestSupport.assertEquals;
import static authenticationsystem.TestSupport.assertFalse;
import static authenticationsystem.TestSupport.assertNotEquals;
import static authenticationsystem.TestSupport.assertNotNull;
import static authenticationsystem.TestSupport.assertNull;
import static authenticationsystem.TestSupport.assertThrows;
import static authenticationsystem.TestSupport.assertTrue;
import static authenticationsystem.TestSupport.test;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

final class AuthServiceTest {
    private static final Instant START = Instant.parse("2026-01-01T10:00:00Z");
    private static final String DEMO_PASSWORD = "LearnJava10!";

    private AuthServiceTest() {
    }

    private static char[] password() {
        return DEMO_PASSWORD.toCharArray();
    }

    private static AuthService service() {
        return new AuthService(new PasswordHasher());
    }

    private static boolean isZeroed(char[] array) {
        for (char character : array) {
            if (character != '\0') {
                return false;
            }
        }
        return true;
    }

    static void run() {
        test("constructor rejects null hasher, bad duration, and null clock", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new AuthService(null), "null hasher");
            assertThrows(IllegalArgumentException.class,
                    () -> new AuthService(new PasswordHasher(), Duration.ZERO),
                    "zero duration");
            assertThrows(IllegalArgumentException.class,
                    () -> new AuthService(new PasswordHasher(), Duration.ofMinutes(5), null),
                    "null clock");
        });

        test("public registration always creates USER accounts", () -> {
            PublicUser user = service().registerUser("learner", password());
            assertEquals(User.Role.USER, user.getRole(), "registered role");
            assertEquals("learner", user.getUsername(), "registered username");
            assertEquals("U-1", user.getId(), "first user ID");
        });

        test("demo admin seeding creates ADMIN accounts", () -> {
            PublicUser admin = service().seedAdminForDemo("demo-admin", password());
            assertEquals(User.Role.ADMIN, admin.getRole(), "seeded role");
        });

        test("duplicate usernames are rejected case-insensitively without leaking details", () -> {
            AuthService auth = service();
            auth.registerUser("learner", password());
            try {
                auth.registerUser("LEARNER", "OtherPass99?".toCharArray());
                throw new AssertionError("duplicate registration should fail");
            } catch (IllegalArgumentException expected) {
                String message = expected.getMessage().toLowerCase(Locale.ROOT);
                assertTrue(message.contains("already registered"), "clear duplicate message");
                assertFalse(message.contains("otherpass"), "no password in error");
                assertFalse(message.contains("hash"), "no hash talk in error");
            }
        });

        test("weak passwords and invalid usernames are rejected at registration", () -> {
            AuthService auth = service();
            assertThrows(IllegalArgumentException.class,
                    () -> auth.registerUser("learner", "weak".toCharArray()), "weak password");
            assertThrows(IllegalArgumentException.class,
                    () -> auth.registerUser("x", password()), "too-short username");
            assertThrows(IllegalArgumentException.class,
                    () -> auth.registerUser("bad name!", password()), "invalid characters");
        });

        test("password char[] is zeroed after registration, even on failure", () -> {
            AuthService auth = service();
            char[] good = password();
            auth.registerUser("learner", good);
            assertTrue(isZeroed(good), "cleared after successful registration");
            char[] weak = "weak".toCharArray();
            assertThrows(IllegalArgumentException.class,
                    () -> auth.registerUser("second", weak), "weak rejected");
            assertTrue(isZeroed(weak), "cleared after failed registration");
        });

        test("login succeeds with the right password and returns a session view", () -> {
            AuthService auth = service();
            auth.registerUser("learner", password());
            SessionInfo session = auth.login("learner", password());
            assertNotNull(session, "session issued");
            assertEquals("learner", session.getUsername(), "session username");
            assertEquals(User.Role.USER, session.getRole(), "session role");
            assertNotNull(session.getToken(), "token present");
            assertTrue(session.getToken().length() >= 40,
                    "token is long (32 random bytes, URL-safe Base64)");
            assertTrue(session.getMaskedToken().endsWith("..."), "masked token");
            assertFalse(session.getMaskedToken().equals(session.getToken()),
                    "masked token hides the full value");
        });

        test("password char[] is zeroed after login attempts", () -> {
            AuthService auth = service();
            auth.registerUser("learner", password());
            char[] correct = password();
            auth.login("learner", correct);
            assertTrue(isZeroed(correct), "cleared after successful login");
            char[] wrong = "WrongGuess3?".toCharArray();
            auth.login("learner", wrong);
            assertTrue(isZeroed(wrong), "cleared after failed login");
        });

        test("wrong password and unknown username both return null", () -> {
            AuthService auth = service();
            auth.registerUser("learner", password());
            assertNull(auth.login("learner", "WrongGuess3?".toCharArray()), "wrong password");
            assertNull(auth.login("nobody", "WrongGuess3?".toCharArray()),
                    "unknown username (dummy hash still runs)");
        });

        test("authenticate returns the user for a live token and null otherwise", () -> {
            AuthService auth = service();
            auth.registerUser("learner", password());
            SessionInfo session = auth.login("learner", password());
            PublicUser user = auth.authenticate(session.getToken());
            assertNotNull(user, "live token authenticates");
            assertEquals("learner", user.getUsername(), "authenticated username");
            assertNull(auth.authenticate("made-up-token"), "unknown token");
            assertThrows(IllegalArgumentException.class,
                    () -> auth.authenticate("   "), "blank token rejected");
            assertThrows(IllegalArgumentException.class,
                    () -> auth.authenticate(null), "null token rejected");
        });

        test("public views expose no password, hash, or salt methods", () -> {
            for (Class<?> viewClass : new Class<?>[] {PublicUser.class, SessionInfo.class}) {
                for (Method method : viewClass.getMethods()) {
                    if (method.getDeclaringClass() == Object.class) {
                        continue; // hashCode etc. come from Object, not from the view.
                    }
                    String name = method.getName().toLowerCase(Locale.ROOT);
                    assertFalse(name.contains("password") || name.contains("hash")
                            || name.contains("salt"),
                            viewClass.getSimpleName() + " exposes " + method.getName());
                }
            }
        });

        test("logout revokes the session exactly once", () -> {
            AuthService auth = service();
            auth.registerUser("learner", password());
            SessionInfo session = auth.login("learner", password());
            assertTrue(auth.logout(session.getToken()), "logout succeeds");
            assertNull(auth.authenticate(session.getToken()), "token dead after logout");
            assertFalse(auth.logout(session.getToken()), "second logout returns false");
            assertFalse(auth.canAccess(session.getToken(), User.Role.USER),
                    "logged-out token cannot authorize");
        });

        test("sessions expire exactly at their deadline (injected clock, no sleeps)", () -> {
            MutableClock clock = new MutableClock(START);
            AuthService auth = new AuthService(new PasswordHasher(),
                    Duration.ofMinutes(30), clock);
            auth.registerUser("learner", password());
            SessionInfo session = auth.login("learner", password());
            assertEquals(START.plus(Duration.ofMinutes(30)), session.getExpiresAt(),
                    "expiry uses the injected clock");

            clock.advance(Duration.ofMinutes(29));
            assertNotNull(auth.authenticate(session.getToken()), "valid before expiry");
            clock.advance(Duration.ofMinutes(1));
            assertNull(auth.authenticate(session.getToken()), "invalid at expiry");
            assertFalse(auth.canAccess(session.getToken(), User.Role.USER),
                    "expired token cannot authorize");
            assertEquals(0, auth.getActiveSessionCount(),
                    "expired session removed when noticed");
        });

        test("removeExpiredSessions clears only expired sessions", () -> {
            MutableClock clock = new MutableClock(START);
            AuthService auth = new AuthService(new PasswordHasher(),
                    Duration.ofMinutes(10), clock);
            auth.registerUser("learner", password());
            auth.login("learner", password());
            clock.advance(Duration.ofMinutes(5));
            SessionInfo fresh = auth.login("learner", password());
            clock.advance(Duration.ofMinutes(6));
            assertEquals(1, auth.removeExpiredSessions(), "one session expired");
            assertEquals(1, auth.getActiveSessionCount(), "one session still live");
            assertNotNull(auth.authenticate(fresh.getToken()), "fresh session survives");
        });

        test("role checks: USER passes USER, only ADMIN passes ADMIN", () -> {
            AuthService auth = service();
            auth.registerUser("learner", password());
            auth.seedAdminForDemo("demo-admin", "AdminStudy2!".toCharArray());
            SessionInfo userSession = auth.login("learner", password());
            SessionInfo adminSession = auth.login("demo-admin", "AdminStudy2!".toCharArray());

            assertTrue(auth.canAccess(userSession.getToken(), User.Role.USER),
                    "USER passes USER check");
            assertFalse(auth.canAccess(userSession.getToken(), User.Role.ADMIN),
                    "USER fails ADMIN check");
            assertTrue(auth.canAccess(adminSession.getToken(), User.Role.USER),
                    "ADMIN passes USER check");
            assertTrue(auth.canAccess(adminSession.getToken(), User.Role.ADMIN),
                    "ADMIN passes ADMIN check");
            assertFalse(auth.canAccess("made-up-token", User.Role.USER),
                    "invalid token fails");
            assertThrows(IllegalArgumentException.class,
                    () -> auth.canAccess(userSession.getToken(), null), "null role");
        });

        test("protected actions succeed or throw SecurityException by role", () -> {
            AuthService auth = service();
            auth.registerUser("learner", password());
            auth.seedAdminForDemo("demo-admin", "AdminStudy2!".toCharArray());
            SessionInfo userSession = auth.login("learner", password());
            SessionInfo adminSession = auth.login("demo-admin", "AdminStudy2!".toCharArray());

            assertNotNull(auth.performUserAction(userSession.getToken()), "user action");
            assertThrows(SecurityException.class,
                    () -> auth.performAdminAction(userSession.getToken()),
                    "USER blocked from admin action");
            assertNotNull(auth.performAdminAction(adminSession.getToken()), "admin action");
            assertThrows(SecurityException.class,
                    () -> auth.performUserAction("made-up-token"),
                    "invalid token blocked from user action");
        });

        test("two users with the same password get different session tokens", () -> {
            AuthService auth = service();
            auth.registerUser("first-user", password());
            auth.registerUser("second-user", password());
            SessionInfo one = auth.login("first-user", password());
            SessionInfo two = auth.login("second-user", password());
            assertNotEquals(one.getToken(), two.getToken(), "unique tokens");
        });
    }
}
