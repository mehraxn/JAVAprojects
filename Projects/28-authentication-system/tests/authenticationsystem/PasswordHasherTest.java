package authenticationsystem;

import static authenticationsystem.TestSupport.assertEquals;
import static authenticationsystem.TestSupport.assertFalse;
import static authenticationsystem.TestSupport.assertNotEquals;
import static authenticationsystem.TestSupport.assertThrows;
import static authenticationsystem.TestSupport.assertTrue;
import static authenticationsystem.TestSupport.test;

final class PasswordHasherTest {
    private static final PasswordHasher HASHER = new PasswordHasher();
    private static final String DEMO_PASSWORD = "CorrectHorse1!";

    private PasswordHasherTest() {
    }

    static void run() {
        test("same password and salt always produce the same hash", () -> {
            String salt = HASHER.generateSalt();
            assertEquals(HASHER.hash(DEMO_PASSWORD.toCharArray(), salt),
                    HASHER.hash(DEMO_PASSWORD.toCharArray(), salt),
                    "deterministic hash");
        });

        test("same password with different salts produces different hashes", () -> {
            String saltOne = HASHER.generateSalt();
            String saltTwo = HASHER.generateSalt();
            assertNotEquals(saltOne, saltTwo, "salts are unique");
            assertNotEquals(HASHER.hash(DEMO_PASSWORD.toCharArray(), saltOne),
                    HASHER.hash(DEMO_PASSWORD.toCharArray(), saltTwo),
                    "per-salt hashes differ");
        });

        test("hash is never the raw password", () -> {
            String salt = HASHER.generateSalt();
            String hash = HASHER.hash(DEMO_PASSWORD.toCharArray(), salt);
            assertNotEquals(DEMO_PASSWORD, hash, "hash differs from raw password");
            assertFalse(hash.contains(DEMO_PASSWORD), "hash does not embed the password");
        });

        test("verify accepts the correct password and rejects a wrong one", () -> {
            String salt = HASHER.generateSalt();
            String hash = HASHER.hash(DEMO_PASSWORD.toCharArray(), salt);
            assertTrue(HASHER.verify(DEMO_PASSWORD.toCharArray(), salt, hash),
                    "correct password verifies");
            assertFalse(HASHER.verify("WrongGuess3?".toCharArray(), salt, hash),
                    "wrong password rejected");
        });

        test("invalid inputs are rejected cleanly", () -> {
            String salt = HASHER.generateSalt();
            assertThrows(IllegalArgumentException.class,
                    () -> HASHER.hash(null, salt), "null password");
            assertThrows(IllegalArgumentException.class,
                    () -> HASHER.hash(new char[0], salt), "empty password");
            assertThrows(IllegalArgumentException.class,
                    () -> HASHER.hash(DEMO_PASSWORD.toCharArray(), "  "), "blank salt");
            assertThrows(IllegalArgumentException.class,
                    () -> HASHER.hash(DEMO_PASSWORD.toCharArray(), "%%%not-base64%%%"),
                    "non-Base64 salt");
            assertThrows(IllegalArgumentException.class,
                    () -> HASHER.verify(DEMO_PASSWORD.toCharArray(), salt, "  "),
                    "blank expected hash");
        });
    }
}
