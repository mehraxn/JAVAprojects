package notificationservice;

import static notificationservice.TestSupport.assertEquals;
import static notificationservice.TestSupport.assertFalse;
import static notificationservice.TestSupport.assertThrows;
import static notificationservice.TestSupport.assertTrue;
import static notificationservice.TestSupport.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

final class NotificationServiceTest {

    private NotificationServiceTest() {
    }

    private static PrintStream silent() {
        return new PrintStream(new ByteArrayOutputStream());
    }

    private static NotificationService serviceWithEmailSender(int failures) {
        NotificationService service = new NotificationService();
        service.registerChannel(new MockNotificationSender(
                Notification.ChannelType.EMAIL, failures, silent()));
        return service;
    }

    static void run() {
        test("registering a sender enables delivery for its channel", () -> {
            NotificationService service = serviceWithEmailSender(0);
            service.enqueue("a@b.com", "Hi", Notification.ChannelType.EMAIL);
            assertEquals(Notification.Status.SENT, service.processNext().getStatus(),
                    "delivered via registered sender");
        });

        test("registering a duplicate sender for a channel is rejected", () -> {
            NotificationService service = serviceWithEmailSender(0);
            assertThrows(IllegalArgumentException.class,
                    () -> service.registerChannel(new MockNotificationSender(
                            Notification.ChannelType.EMAIL, 0, silent())),
                    "duplicate channel registration");
            assertThrows(IllegalArgumentException.class,
                    () -> service.registerChannel(null), "null sender");
        });

        test("enqueue adds to the queue and assigns sequential IDs", () -> {
            NotificationService service = serviceWithEmailSender(0);
            Notification first = service.enqueue("a@b.com", "one", Notification.ChannelType.EMAIL);
            Notification second = service.enqueue("c@d.com", "two", Notification.ChannelType.EMAIL);
            assertEquals("N-1", first.getId(), "first ID");
            assertEquals("N-2", second.getId(), "second ID");
            assertEquals(2, service.viewQueue().size(), "queue size");
        });

        test("queue processes in FIFO order and empties", () -> {
            NotificationService service = serviceWithEmailSender(0);
            service.enqueue("a@b.com", "first", Notification.ChannelType.EMAIL);
            service.enqueue("c@d.com", "second", Notification.ChannelType.EMAIL);
            assertEquals("first", service.processNext().getMessage(), "FIFO first");
            assertEquals("second", service.processNext().getMessage(), "FIFO second");
            assertTrue(service.viewQueue().isEmpty(), "queue empty after processing");
            assertEquals(null, service.processNext(), "processNext on empty queue");
        });

        test("successful send is recorded as SENT in history", () -> {
            NotificationService service = serviceWithEmailSender(0);
            Notification queued = service.enqueue("a@b.com", "Hi",
                    Notification.ChannelType.EMAIL);
            service.processNext();
            assertEquals(Notification.Status.SENT,
                    service.findNotification(queued.getId()).getStatus(), "SENT in history");
        });

        test("failed send is recorded as FAILED with the error", () -> {
            NotificationService service = serviceWithEmailSender(1);
            Notification queued = service.enqueue("a@b.com", "Hi",
                    Notification.ChannelType.EMAIL);
            Notification result = service.processNext();
            assertEquals(Notification.Status.FAILED, result.getStatus(), "FAILED status");
            assertTrue(result.getLastError().contains("Simulated"), "error recorded");
            assertEquals(1, service.findNotification(queued.getId()).getAttemptCount(),
                    "attempt counted");
        });

        test("retry after one failure succeeds", () -> {
            NotificationService service = serviceWithEmailSender(1);
            Notification queued = service.enqueue("a@b.com", "Hi",
                    Notification.ChannelType.EMAIL);
            service.processNext();
            assertTrue(service.retry(queued.getId(), 3), "retry accepted");
            assertEquals(Notification.Status.SENT, service.processNext().getStatus(),
                    "retry attempt succeeded");
            assertEquals(2, service.findNotification(queued.getId()).getAttemptCount(),
                    "two attempts total");
        });

        test("retry limit stops further attempts", () -> {
            NotificationService service = serviceWithEmailSender(99);
            Notification queued = service.enqueue("a@b.com", "Hi",
                    Notification.ChannelType.EMAIL);
            service.processNext();
            assertTrue(service.retry(queued.getId(), 2), "first retry accepted");
            service.processNext();
            assertFalse(service.retry(queued.getId(), 2),
                    "retry rejected once the attempt limit is reached");
            assertEquals(Notification.Status.FAILED,
                    service.findNotification(queued.getId()).getStatus(), "stays FAILED");
            assertThrows(IllegalArgumentException.class,
                    () -> service.retry(queued.getId(), 0), "non-positive limit rejected");
        });

        test("retry validates state and unknown IDs", () -> {
            NotificationService service = serviceWithEmailSender(0);
            Notification queued = service.enqueue("a@b.com", "Hi",
                    Notification.ChannelType.EMAIL);
            service.processNext();
            assertThrows(IllegalStateException.class,
                    () -> service.retry(queued.getId(), 3), "retrying a SENT notification");
            assertFalse(service.retry("N-999", 3), "unknown ID returns false");
            assertThrows(IllegalArgumentException.class,
                    () -> service.retry("  ", 3), "blank ID rejected");
        });

        test("missing sender fails cleanly and keeps the record", () -> {
            NotificationService service = new NotificationService();
            Notification queued = service.enqueue("a@b.com", "Hi",
                    Notification.ChannelType.EMAIL);
            Notification result = service.processNext();
            assertEquals(Notification.Status.FAILED, result.getStatus(), "FAILED status");
            assertTrue(result.getLastError().contains("No mock sender"),
                    "missing-sender error");
            assertEquals(Notification.Status.FAILED,
                    service.findNotification(queued.getId()).getStatus(), "history updated");
        });

        test("history contains every processed notification in order", () -> {
            NotificationService service = serviceWithEmailSender(0);
            service.enqueue("a@b.com", "one", Notification.ChannelType.EMAIL);
            service.enqueue("c@d.com", "two", Notification.ChannelType.EMAIL);
            service.processNext();
            service.processNext();
            List<Notification> history = service.getHistory();
            assertEquals(2, history.size(), "history size");
            assertEquals("N-1", history.get(0).getId(), "history order");
        });

        test("viewQueue and getHistory return defensive, unmodifiable snapshots", () -> {
            NotificationService service = serviceWithEmailSender(0);
            Notification queued = service.enqueue("a@b.com", "Hi",
                    Notification.ChannelType.EMAIL);
            assertThrows(UnsupportedOperationException.class,
                    () -> service.viewQueue().clear(), "queue list unmodifiable");
            assertThrows(UnsupportedOperationException.class,
                    () -> service.getHistory().clear(), "history list unmodifiable");
            // Mutating a snapshot copy must not change the stored record.
            service.viewQueue().get(0).beginAttempt();
            assertEquals(Notification.Status.QUEUED,
                    service.findNotification(queued.getId()).getStatus(),
                    "stored status unchanged after mutating a snapshot");
            assertEquals(0, service.findNotification(queued.getId()).getAttemptCount(),
                    "stored attempts unchanged after mutating a snapshot");
        });

        test("findNotification returns a copy, and null for unknown IDs", () -> {
            NotificationService service = serviceWithEmailSender(0);
            Notification queued = service.enqueue("a@b.com", "Hi",
                    Notification.ChannelType.EMAIL);
            service.findNotification(queued.getId()).beginAttempt();
            assertEquals(Notification.Status.QUEUED,
                    service.findNotification(queued.getId()).getStatus(),
                    "found copy is independent");
            assertEquals(null, service.findNotification("N-999"), "unknown ID is null");
            assertThrows(IllegalArgumentException.class,
                    () -> service.findNotification("   "), "blank ID rejected");
        });

        test("invalid enqueue input fails cleanly and adds nothing", () -> {
            NotificationService service = serviceWithEmailSender(0);
            assertThrows(IllegalArgumentException.class,
                    () -> service.enqueue("bad-email", "Hi", Notification.ChannelType.EMAIL),
                    "invalid recipient");
            assertThrows(IllegalArgumentException.class,
                    () -> service.enqueue("a@b.com", "  ", Notification.ChannelType.EMAIL),
                    "blank message");
            assertTrue(service.viewQueue().isEmpty(), "queue unchanged after rejects");
            assertTrue(service.getHistory().isEmpty(), "history unchanged after rejects");
        });
    }
}
