package notificationservice;

import static notificationservice.TestSupport.assertEquals;
import static notificationservice.TestSupport.assertThrows;
import static notificationservice.TestSupport.assertTrue;
import static notificationservice.TestSupport.test;

final class NotificationTest {

    private NotificationTest() {
    }

    private static Notification sample() {
        return new Notification("N-1", "learner@example.com", "Hello",
                Notification.ChannelType.EMAIL);
    }

    static void run() {
        test("valid notification starts QUEUED with 0 attempts", () -> {
            Notification notification = sample();
            assertEquals("N-1", notification.getId(), "ID");
            assertEquals("learner@example.com", notification.getRecipient(), "recipient");
            assertEquals("Hello", notification.getMessage(), "message");
            assertEquals(Notification.ChannelType.EMAIL, notification.getChannelType(), "channel");
            assertEquals(Notification.Status.QUEUED, notification.getStatus(), "initial status");
            assertEquals(0, notification.getAttemptCount(), "initial attempts");
            assertEquals("", notification.getLastError(), "initial error");
            assertTrue(notification.getCreatedAt() != null, "created timestamp exists");
            assertEquals(null, notification.getSentAt(), "not sent yet");
        });

        test("blank ID, recipient, and message are rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Notification("  ", "a@b.com", "Hi", Notification.ChannelType.EMAIL),
                    "blank ID");
            assertThrows(IllegalArgumentException.class,
                    () -> new Notification("N-1", null, "Hi", Notification.ChannelType.EMAIL),
                    "null recipient");
            assertThrows(IllegalArgumentException.class,
                    () -> new Notification("N-1", "a@b.com", "   ", Notification.ChannelType.EMAIL),
                    "blank message");
            assertThrows(IllegalArgumentException.class,
                    () -> new Notification("N-1", "a@b.com", null, Notification.ChannelType.EMAIL),
                    "null message");
        });

        test("null channel is rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Notification("N-1", "a@b.com", "Hi", null), "null channel");
        });

        test("messages over 5000 characters are rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Notification("N-1", "a@b.com", "x".repeat(5001),
                            Notification.ChannelType.EMAIL),
                    "oversized message");
        });

        test("beginAttempt moves QUEUED to SENDING and increments attempts", () -> {
            Notification notification = sample();
            notification.beginAttempt();
            assertEquals(Notification.Status.SENDING, notification.getStatus(), "SENDING");
            assertEquals(1, notification.getAttemptCount(), "attempt counted");
        });

        test("markSent moves SENDING to SENT and records the send time", () -> {
            Notification notification = sample();
            notification.beginAttempt();
            notification.markSent();
            assertEquals(Notification.Status.SENT, notification.getStatus(), "SENT");
            assertTrue(notification.getSentAt() != null, "sentAt recorded");
        });

        test("markFailed moves SENDING to FAILED and stores the error", () -> {
            Notification notification = sample();
            notification.beginAttempt();
            notification.markFailed("mock outage");
            assertEquals(Notification.Status.FAILED, notification.getStatus(), "FAILED");
            assertEquals("mock outage", notification.getLastError(), "error text");
        });

        test("requeue moves FAILED back to QUEUED for a retry", () -> {
            Notification notification = sample();
            notification.beginAttempt();
            notification.markFailed("mock outage");
            notification.requeue();
            assertEquals(Notification.Status.QUEUED, notification.getStatus(), "requeued");
            assertEquals(1, notification.getAttemptCount(), "attempts preserved on requeue");
            notification.beginAttempt();
            assertEquals(2, notification.getAttemptCount(), "second attempt counted");
        });

        test("illegal status transitions are rejected", () -> {
            Notification notification = sample();
            assertThrows(IllegalStateException.class, notification::markSent,
                    "markSent while QUEUED");
            assertThrows(IllegalStateException.class,
                    () -> notification.markFailed("x"), "markFailed while QUEUED");
            assertThrows(IllegalStateException.class, notification::requeue,
                    "requeue while QUEUED");
            notification.beginAttempt();
            assertThrows(IllegalStateException.class, notification::beginAttempt,
                    "beginAttempt while SENDING");
            notification.markSent();
            assertThrows(IllegalStateException.class, notification::requeue,
                    "requeue after SENT");
        });

        test("copy is an independent snapshot", () -> {
            Notification original = sample();
            Notification copy = original.copy();
            original.beginAttempt();
            assertEquals(Notification.Status.QUEUED, copy.getStatus(),
                    "copy unchanged after original transition");
            assertEquals(0, copy.getAttemptCount(), "copy attempts unchanged");
        });

        test("toString reports ID, channel, status, and attempts", () -> {
            String text = sample().toString();
            assertTrue(text.contains("N-1"), "toString ID");
            assertTrue(text.contains("EMAIL"), "toString channel");
            assertTrue(text.contains("QUEUED"), "toString status");
            assertTrue(text.contains("attempts=0"), "toString attempts");
        });
    }
}
