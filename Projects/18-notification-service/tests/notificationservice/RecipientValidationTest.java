package notificationservice;

import static notificationservice.TestSupport.assertEquals;
import static notificationservice.TestSupport.assertThrows;
import static notificationservice.TestSupport.test;

final class RecipientValidationTest {

    private RecipientValidationTest() {
    }

    private static Notification create(String recipient, Notification.ChannelType channel) {
        return new Notification("check", recipient, "probe", channel);
    }

    static void run() {
        test("EMAIL accepts a normal address and trims whitespace", () -> {
            assertEquals("learner@example.com",
                    create("learner@example.com", Notification.ChannelType.EMAIL).getRecipient(),
                    "normal email");
            assertEquals("learner@example.com",
                    create("  learner@example.com  ", Notification.ChannelType.EMAIL).getRecipient(),
                    "trimmed email");
        });

        test("EMAIL rejects malformed addresses", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> create("bad-email", Notification.ChannelType.EMAIL), "no @ sign");
            assertThrows(IllegalArgumentException.class,
                    () -> create("user@nodomain", Notification.ChannelType.EMAIL),
                    "missing domain dot");
            assertThrows(IllegalArgumentException.class,
                    () -> create("@example.com", Notification.ChannelType.EMAIL),
                    "missing local part");
            assertThrows(IllegalArgumentException.class,
                    () -> create("a b@example.com", Notification.ChannelType.EMAIL),
                    "space in address");
            assertThrows(IllegalArgumentException.class,
                    () -> create("   ", Notification.ChannelType.EMAIL), "blank email");
        });

        test("SMS accepts international and local phone formats", () -> {
            assertEquals("+393331112222",
                    create("+393331112222", Notification.ChannelType.SMS).getRecipient(),
                    "international format");
            assertEquals("3331112222",
                    create("3331112222", Notification.ChannelType.SMS).getRecipient(),
                    "local digits");
            assertEquals("+39 (333) 111-2222",
                    create("+39 (333) 111-2222", Notification.ChannelType.SMS).getRecipient(),
                    "spaces, parentheses, and dashes allowed");
        });

        test("SMS rejects letters, too-short, and blank values", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> create("abc123", Notification.ChannelType.SMS), "letters");
            assertThrows(IllegalArgumentException.class,
                    () -> create("12", Notification.ChannelType.SMS), "too short");
            assertThrows(IllegalArgumentException.class,
                    () -> create("   ", Notification.ChannelType.SMS), "blank phone");
            assertThrows(IllegalArgumentException.class,
                    () -> create("1".repeat(31), Notification.ChannelType.SMS), "too long");
        });

        test("APP accepts simple user identifiers", () -> {
            assertEquals("user-123",
                    create("user-123", Notification.ChannelType.APP).getRecipient(),
                    "app user ID");
        });

        test("APP rejects blank and oversized identifiers", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> create("   ", Notification.ChannelType.APP), "blank app recipient");
            assertThrows(IllegalArgumentException.class,
                    () -> create("u".repeat(101), Notification.ChannelType.APP),
                    "over 100 characters");
        });
    }
}
