package notificationservice;

public class Main {
    public static void main(String[] args) {
        NotificationService service = new NotificationService();
        service.registerChannel(new MockNotificationSender(Notification.ChannelType.EMAIL));
        service.registerChannel(new MockNotificationSender(Notification.ChannelType.SMS, 1));
        service.registerChannel(new MockNotificationSender(Notification.ChannelType.APP));

        service.enqueue("learner@example.com", "Welcome to the course.",
                Notification.ChannelType.EMAIL);
        Notification sms = service.enqueue("+49 123 4567", "Your code is 4821.",
                Notification.ChannelType.SMS);
        service.enqueue("user-42", "You have a new in-app message.",
                Notification.ChannelType.APP);

        while (!service.viewQueue().isEmpty()) {
            Notification result = service.processNext();
            if (result.getStatus() == Notification.Status.FAILED) {
                System.out.println("Mock failure: " + result.getLastError());
            }
        }
        if (service.retry(sms.getId(), 3)) {
            service.processNext();
        }

        System.out.println("Notification history:");
        for (Notification notification : service.getHistory()) {
            System.out.println(notification);
        }
        System.out.println("All deliveries were local mock demonstrations.");
    }
}
