package paymentservice;

import java.math.BigDecimal;

public class Payment {
    private final String id;
    private final String orderId;
    private final BigDecimal amount;
    private final String status;

    public Payment(String id, String orderId, BigDecimal amount, String status) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean isApproved() {
        return "APPROVED".equals(status);
    }

    public String toJson() {
        return "{\"id\":\"" + escape(id) + "\",\"orderId\":\"" + escape(orderId)
                + "\",\"amount\":" + amount + ",\"status\":\"" + status + "\"}";
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
