package notificationservice;

public class Notification {
    private final String id;
    private final String orderId;
    private final String message;
    private final String status;

    public Notification(String id, String orderId, String message) {
        this.id = id;
        this.orderId = orderId;
        this.message = message;
        this.status = "SENT_TO_MOCK_SENDER";
    }

    public String toJson() {
        return "{\"id\":\"" + escape(id) + "\",\"orderId\":\"" + escape(orderId)
                + "\",\"message\":\"" + escape(message) + "\",\"status\":\"" + status + "\"}";
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
