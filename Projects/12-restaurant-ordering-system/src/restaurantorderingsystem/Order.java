package restaurantorderingsystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An order: a set of {@link OrderItem}s and a lifecycle {@link OrderStatus}.
 *
 * <p><strong>Lifecycle:</strong> CREATED &rarr; PREPARING &rarr; READY &rarr; SERVED,
 * with CREATED &rarr; CANCELLED and PREPARING &rarr; CANCELLED also allowed. SERVED and
 * CANCELLED are terminal. Only CREATED orders are editable; a non-empty order is
 * required to move to PREPARING.
 *
 * <p><strong>Money:</strong> orders above {@value #DISCOUNT_THRESHOLD_TEXT} (inclusive)
 * receive a 10% discount. All amounts are {@link BigDecimal}, normalized to 2 decimals.
 */
public final class Order {
    static final String DISCOUNT_THRESHOLD_TEXT = "50.00";
    public static final BigDecimal DISCOUNT_THRESHOLD = new BigDecimal(DISCOUNT_THRESHOLD_TEXT);
    public static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.10");

    private final String id;
    private final String customerName;
    private final Map<String, OrderItem> items = new LinkedHashMap<>();
    private OrderStatus status = OrderStatus.CREATED;

    public Order(String id, String customerName) {
        this.id = requireText(id, "Order ID");
        this.customerName = requireText(customerName, "Customer name");
    }

    /** Deep copy: same id/customer/status with a copy of the items (items are immutable). */
    public Order(Order other) {
        if (other == null) {
            throw new IllegalArgumentException("Order to copy must not be null");
        }
        this.id = other.id;
        this.customerName = other.customerName;
        this.items.putAll(other.items);
        this.status = other.status;
    }

    public Order copy() {
        return new Order(this);
    }

    public String getId() { return id; }
    public String getCustomerName() { return customerName; }
    public OrderStatus getStatus() { return status; }
    public boolean isEditable() { return status == OrderStatus.CREATED; }
    public int getItemCount() { return items.size(); }

    /**
     * Adds a menu item with the given quantity. If the item is already in the
     * order, the quantities are merged rather than duplicating the line.
     */
    public void addItem(MenuItem item, int quantity) {
        ensureEditable();
        if (item == null) {
            throw new IllegalArgumentException("Menu item must not be null");
        }
        OrderItem existingItem = items.get(item.getId());
        if (existingItem == null) {
            items.put(item.getId(), new OrderItem(item, quantity));
        } else {
            items.put(item.getId(), existingItem.withAdditionalQuantity(quantity));
        }
    }

    public void removeItem(String menuItemId) {
        ensureEditable();
        String validId = requireText(menuItemId, "Menu item ID");
        if (items.remove(validId) == null) {
            throw new IllegalArgumentException("Item is not in the order: " + validId);
        }
    }

    /** An unmodifiable list of the order's items. The items themselves are immutable. */
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(items.values()));
    }

    public BigDecimal calculateSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem item : items.values()) {
            subtotal = subtotal.add(item.calculateSubtotal());
        }
        return Money.scale(subtotal);
    }

    public BigDecimal calculateDiscount() {
        BigDecimal subtotal = calculateSubtotal();
        if (subtotal.compareTo(DISCOUNT_THRESHOLD) >= 0) {
            return Money.scale(subtotal.multiply(DISCOUNT_RATE));
        }
        return Money.scale(BigDecimal.ZERO);
    }

    public BigDecimal calculateTotal() {
        return Money.scale(calculateSubtotal().subtract(calculateDiscount()));
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
                    .append(": ").append(Money.format(item.calculateSubtotal())).append('\n');
        }
        summary.append("Subtotal: ").append(Money.format(calculateSubtotal())).append('\n')
                .append("Discount: ").append(Money.format(calculateDiscount())).append('\n')
                .append("Total: ").append(Money.format(calculateTotal()));
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
