package notificationservice;

import static notificationservice.TestSupport.assertEquals;
import static notificationservice.TestSupport.assertThrows;
import static notificationservice.TestSupport.assertTrue;
import static notificationservice.TestSupport.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

final class MockNotificationSenderTest {

    private MockNotificationSenderTest() {
    }

    private static Notification email() {
        return new Notification("N-1", "learner@example.com", "Hello",
                Notification.ChannelType.EMAIL);
    }

    static void run() {
        test("successful sender delivers and prints to its stream only", () -> {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            MockNotificationSender sender = new MockNotificationSender(
                    Notification.ChannelType.EMAIL, 0,
                    new PrintStream(bytes, true, StandardCharsets.UTF_8));
            sender.deliver(email());
            String output = bytes.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("[MOCK EMAIL] to learner@example.com: Hello"),
                    "mock delivery line: " + output);
        });

        test("sender reports its configured channel", () -> {
            assertEquals(Notification.ChannelType.SMS,
                    new MockNotificationSender(Notification.ChannelType.SMS).getType(),
                    "channel type");
        });

        test("failure count makes failures deterministic: fail once, then succeed", () -> {
            MockNotificationSender sender = new MockNotificationSender(
                    Notification.ChannelType.EMAIL, 1,
                    new PrintStream(new ByteArrayOutputStream()));
            assertThrows(Exception.class, () -> sender.deliver(email()),
                    "first attempt fails");
            sender.deliver(email());
        });

        test("always-failing sender fails every attempt up to its count", () -> {
            MockNotificationSender sender = new MockNotificationSender(
                    Notification.ChannelType.EMAIL, 3,
                    new PrintStream(new ByteArrayOutputStream()));
            for (int attempt = 1; attempt <= 3; attempt++) {
                assertThrows(Exception.class, () -> sender.deliver(email()),
                        "attempt " + attempt + " fails");
            }
            sender.deliver(email());
        });

        test("sender rejects null notifications and wrong channels", () -> {
            MockNotificationSender sender = new MockNotificationSender(
                    Notification.ChannelType.SMS, 0,
                    new PrintStream(new ByteArrayOutputStream()));
            assertThrows(IllegalArgumentException.class, () -> sender.deliver(null),
                    "null notification");
            assertThrows(IllegalArgumentException.class, () -> sender.deliver(email()),
                    "EMAIL notification given to SMS sender");
        });

        test("invalid constructions are rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new MockNotificationSender(null), "null channel");
            assertThrows(IllegalArgumentException.class,
                    () -> new MockNotificationSender(Notification.ChannelType.EMAIL, -1),
                    "negative failure count");
            assertThrows(IllegalArgumentException.class,
                    () -> new MockNotificationSender(Notification.ChannelType.EMAIL, 0, null),
                    "null stream");
        });
    }
}
