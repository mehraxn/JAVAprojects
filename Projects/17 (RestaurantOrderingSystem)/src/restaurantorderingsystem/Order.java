package restaurantorderingsystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String id;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.CREATED;

    public Order(String id) {
        this.id = id;
    }

    public String getId() { return id; }
    public OrderStatus getStatus() { return status; }

    public void addItem(MenuItem item, int quantity) {
        // TODO: Validate the quantity and add or update the order item.
        throw new UnsupportedOperationException("TODO: add an order item");
    }

    public void removeItem(String menuItemId) {
        // TODO: Remove the matching item from the order.
        throw new UnsupportedOperationException("TODO: remove an order item");
    }

    public BigDecimal calculateTotal() {
        // TODO: Sum all order-item subtotals.
        throw new UnsupportedOperationException("TODO: calculate order total");
    }

    public void updateStatus(OrderStatus newStatus) {
        // TODO: Validate the requested status transition.
        throw new UnsupportedOperationException("TODO: update order status");
    }
}
