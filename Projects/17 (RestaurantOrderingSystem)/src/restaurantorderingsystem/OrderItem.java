package restaurantorderingsystem;

import java.math.BigDecimal;

public class OrderItem {
    private final MenuItem menuItem;
    private int quantity;

    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }

    public BigDecimal calculateSubtotal() {
        // TODO: Multiply the menu-item price by the quantity.
        throw new UnsupportedOperationException("TODO: calculate item subtotal");
    }
}
