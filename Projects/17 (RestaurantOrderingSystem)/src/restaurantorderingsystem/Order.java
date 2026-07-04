package restaurantorderingsystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Order {
    public static final BigDecimal DISCOUNT_THRESHOLD = new BigDecimal("50.00");
    public static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.10");

    private final String id;
    private final String customerName;
    private final Map<String, OrderItem> items = new LinkedHashMap<>();
    private OrderStatus status = OrderStatus.CREATED;

    public Order(String id, String customerName) {
        this.id = requireText(id, "Order ID");
        this.customerName = requireText(customerName, "Customer name");
    }

    public String getId() { return id; }
    public String getCustomerName() { return customerName; }
    public OrderStatus getStatus() { return status; }

    public void addItem(MenuItem item, int quantity) {
        ensureEditable();
        if (item == null) {
            throw new IllegalArgumentException("Menu item must not be null");
        }
        OrderItem existingItem = items.get(item.getId());
        if (existingItem == null) {
            items.put(item.getId(), new OrderItem(item, quantity));
        } else {
            existingItem.addQuantity(quantity);
        }
    }

    public void removeItem(String menuItemId) {
        ensureEditable();
        String validId = requireText(menuItemId, "Menu item ID");
        if (items.remove(validId) == null) {
            throw new IllegalArgumentException("Item is not in the order: " + validId);
        }
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(items.values()));
    }

    public BigDecimal calculateSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem item : items.values()) {
            subtotal = subtotal.add(item.calculateSubtotal());
        }
        return subtotal;
    }

    public BigDecimal calculateDiscount() {
        BigDecimal subtotal = calculateSubtotal();
        if (subtotal.compareTo(DISCOUNT_THRESHOLD) >= 0) {
            return subtotal.multiply(DISCOUNT_RATE);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateTotal() {
        return calculateSubtotal().subtract(calculateDiscount());
    }

    public void updateStatus(OrderStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Order status must not be null");
        }
        if (status == OrderStatus.CREATED && newStatus == OrderStatus.PREPARING
                && items.isEmpty()) {
            throw new IllegalStateException("Cannot prepare an empty order");
        }
        if (!isValidTransition(status, newStatus)) {
            throw new IllegalStateException("Invalid order status transition: "
                    + status + " -> " + newStatus);
        }
        status = newStatus;
    }

    public String createSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Order ").append(id)
                .append(" for ").append(customerName)
                .append(" [").append(status).append("]\n");
        for (OrderItem item : items.values()) {
            summary.append("- ").append(item.getMenuItem().getName())
                    .append(" x").append(item.getQuantity())
                    .append(": ").append(item.calculateSubtotal()).append('\n');
        }
        summary.append("Subtotal: ").append(calculateSubtotal()).append('\n')
                .append("Discount: ").append(calculateDiscount()).append('\n')
                .append("Total: ").append(calculateTotal());
        return summary.toString();
    }

    private void ensureEditable() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED orders can be edited");
        }
    }

    private static boolean isValidTransition(OrderStatus current, OrderStatus next) {
        switch (current) {
            case CREATED:
                return next == OrderStatus.PREPARING || next == OrderStatus.CANCELLED;
            case PREPARING:
                return next == OrderStatus.READY || next == OrderStatus.CANCELLED;
            case READY:
                return next == OrderStatus.SERVED;
            case SERVED:
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
