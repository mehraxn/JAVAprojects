package notificationservice;

public class Main {
    public static void main(String[] args) {
        NotificationService service = new NotificationService();
        NotificationChannel consoleChannel = new ConsoleNotificationChannel();
        // TODO: Register the console channel and demonstrate queue processing.
        System.out.println("Notification Service skeleton ready.");
        System.out.println("No external notification was sent.");
    }
}
