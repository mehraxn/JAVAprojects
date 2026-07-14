package orderservice;

import java.math.BigDecimal;

public class Order {
    public enum Status {
        CREATED,
        INVENTORY_REJECTED,
        PAYMENT_REJECTED,
        CONFIRMED
    }

    private final String id;
    private final String sku;
    private final int quantity;
    private final BigDecimal unitPrice;
    private Status status;
    private String detail;

    public Order(String id, String sku, int quantity, BigDecimal unitPrice) {
        this.id = requireText(id, "Order ID");
        this.sku = requireText(sku, "SKU");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        if (unitPrice == null || unitPrice.signum() <= 0) {
            throw new IllegalArgumentException("Unit price must be positive.");
        }
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.status = Status.CREATED;
        this.detail = "Order created";
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }

    public synchronized void updateStatus(Status newStatus, String newDetail) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status is required.");
        }
        status = newStatus;
        detail = requireText(newDetail, "Status detail");
    }

    public String getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public synchronized String toJson() {
        return "{\"id\":\"" + escape(id) + "\",\"sku\":\"" + escape(sku)
                + "\",\"quantity\":" + quantity + ",\"unitPrice\":" + unitPrice.toPlainString()
                + ",\"total\":" + getTotal().toPlainString() + ",\"status\":\"" + status
                + "\",\"detail\":\"" + escape(detail) + "\"}";
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
