package miniecommercebackend;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Order {
    public enum Status {
        CREATED,
        PAID,
        CANCELLED
    }

    private final String id;
    private final String cartId;
    private final Map<String, Integer> items;
    private final BigDecimal total;
    private final LocalDateTime createdAt;
    private Status status;

    public Order(String id, String cartId, Map<String, Integer> items,
            BigDecimal total, LocalDateTime createdAt, Status status) {
        this.id = requireText(id, "Order ID");
        this.cartId = requireText(cartId, "Cart ID");
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }
        Map<String, Integer> checkedItems = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> item : items.entrySet()) {
            String productId = requireText(item.getKey(), "Product ID");
            if (item.getValue() == null || item.getValue() <= 0) {
                throw new IllegalArgumentException("Order quantities must be positive.");
            }
            checkedItems.put(productId, item.getValue());
        }
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order total must be greater than zero.");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Order creation time cannot be null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null.");
        }
        this.items = Collections.unmodifiableMap(checkedItems);
        this.total = total;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getCartId() {
        return cartId;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Order status cannot be null.");
        }
        if (newStatus == status) {
            return;
        }
        if (status != Status.CREATED
                || !(newStatus == Status.PAID || newStatus == Status.CANCELLED)) {
            throw new IllegalStateException("Order status cannot change from "
                    + status + " to " + newStatus + ".");
        }
        status = newStatus;
    }

    public Order copy() {
        return new Order(id, cartId, items, total, createdAt, status);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        return id + " | " + status + " | " + total.toPlainString() + " | " + items;
    }
}
