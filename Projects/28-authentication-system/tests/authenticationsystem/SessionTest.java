package authenticationsystem;

import static authenticationsystem.TestSupport.assertEquals;
import static authenticationsystem.TestSupport.assertFalse;
import static authenticationsystem.TestSupport.assertThrows;
import static authenticationsystem.TestSupport.assertTrue;
import static authenticationsystem.TestSupport.test;

import java.time.Instant;

final class SessionTest {
    private static final Instant EXPIRY = Instant.parse("2026-01-01T10:30:00Z");

    private SessionTest() {
    }

    static void run() {
        test("valid session exposes token, user ID, and expiry", () -> {
            Session session = new Session("token-abc", "U-1", EXPIRY);
            assertEquals("token-abc", session.getToken(), "token");
            assertEquals("U-1", session.getUserId(), "user ID");
            assertEquals(EXPIRY, session.getExpiresAt(), "expiry");
        });

        test("invalid constructions are rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Session("  ", "U-1", EXPIRY), "blank token");
            assertThrows(IllegalArgumentException.class,
                    () -> new Session("token", null, EXPIRY), "null user ID");
            assertThrows(IllegalArgumentException.class,
                    () -> new Session("token", "U-1", null), "null expiry");
        });

        test("session is valid strictly before expiry and expired from expiry on", () -> {
            Session session = new Session("token", "U-1", EXPIRY);
            assertFalse(session.isExpired(EXPIRY.minusSeconds(1)), "1s before expiry");
            assertTrue(session.isExpired(EXPIRY), "exactly at expiry");
            assertTrue(session.isExpired(EXPIRY.plusSeconds(1)), "1s after expiry");
            assertThrows(IllegalArgumentException.class,
                    () -> session.isExpired(null), "null current time");
        });

        test("copy carries the same values", () -> {
            Session copy = new Session("token", "U-1", EXPIRY).copy();
            assertEquals("token", copy.getToken(), "copied token");
            assertEquals(EXPIRY, copy.getExpiresAt(), "copied expiry");
        });
    }
}
