package miniecommercebackend;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    public enum Status {
        CREATED,
        PAID,
        CANCELLED
    }

    private final String id;
    private final String cartId;
    private final BigDecimal total;
    private final LocalDateTime createdAt;
    private Status status;

    public Order(String id, String cartId, BigDecimal total,
            LocalDateTime createdAt, Status status) {
        this.id = id;
        this.cartId = cartId;
        this.total = total;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getId() { return id; }
    public String getCartId() { return cartId; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Status getStatus() { return status; }
}
