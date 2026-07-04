package notificationservice;

public interface NotificationChannel {
    Notification.ChannelType getType();

    void deliver(Notification notification) throws Exception;
}
