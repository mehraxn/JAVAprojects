package notificationservice;

public class ConsoleNotificationChannel implements NotificationChannel {
    @Override
    public Notification.ChannelType getType() {
        return Notification.ChannelType.CONSOLE;
    }

    @Override
    public void deliver(Notification notification) {
        // TODO: Validate and print a safe local notification demonstration.
        throw new UnsupportedOperationException("TODO: deliver a console notification");
    }
}
