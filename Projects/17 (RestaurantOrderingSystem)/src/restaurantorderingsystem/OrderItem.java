package restaurantorderingsystem;

import java.math.BigDecimal;

public class OrderItem {
    private final MenuItem menuItem;
    private int quantity;

    public OrderItem(MenuItem menuItem, int quantity) {
        if (menuItem == null) {
            throw new IllegalArgumentException("Menu item must not be null");
        }
        this.menuItem = menuItem;
        setQuantity(quantity);
    }

    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }

    public void addQuantity(int additionalQuantity) {
        if (additionalQuantity <= 0 || quantity > Integer.MAX_VALUE - additionalQuantity) {
            throw new IllegalArgumentException("Additional quantity must be positive and valid");
        }
        quantity += additionalQuantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = quantity;
    }

    public BigDecimal calculateSubtotal() {
        return menuItem.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
