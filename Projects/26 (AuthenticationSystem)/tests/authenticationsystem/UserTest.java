package authenticationsystem;

import static authenticationsystem.TestSupport.assertEquals;
import static authenticationsystem.TestSupport.assertFalse;
import static authenticationsystem.TestSupport.assertThrows;
import static authenticationsystem.TestSupport.test;

final class UserTest {

    private UserTest() {
    }

    private static User sample() {
        return new User("U-1", "learner", "hash-value", "salt-value", User.Role.USER);
    }

    static void run() {
        test("valid user exposes id, username, and role", () -> {
            User user = sample();
            assertEquals("U-1", user.getId(), "ID");
            assertEquals("learner", user.getUsername(), "username");
            assertEquals(User.Role.USER, user.getRole(), "role");
        });

        test("blank fields and null role are rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new User(" ", "u", "h", "s", User.Role.USER), "blank ID");
            assertThrows(IllegalArgumentException.class,
                    () -> new User("U-1", null, "h", "s", User.Role.USER), "null username");
            assertThrows(IllegalArgumentException.class,
                    () -> new User("U-1", "u", "", "s", User.Role.USER), "empty hash");
            assertThrows(IllegalArgumentException.class,
                    () -> new User("U-1", "u", "h", "  ", User.Role.USER), "blank salt");
            assertThrows(IllegalArgumentException.class,
                    () -> new User("U-1", "u", "h", "s", null), "null role");
        });

        test("toString never contains the hash or salt", () -> {
            String text = sample().toString();
            assertFalse(text.contains("hash-value"), "no hash in toString");
            assertFalse(text.contains("salt-value"), "no salt in toString");
        });

        test("copy carries the same values", () -> {
            User copy = sample().copy();
            assertEquals("U-1", copy.getId(), "copied ID");
            assertEquals(User.Role.USER, copy.getRole(), "copied role");
        });
    }
}
