package restaurantorderingsystem;

import java.math.BigDecimal;

/**
 * An immutable line in an order: a {@link MenuItem} and a positive quantity.
 *
 * <p>Because it is immutable, "changing" the quantity produces a new instance via
 * {@link #withAdditionalQuantity(int)}; the original is never mutated. This means
 * an {@code OrderItem} handed out by an {@link Order} cannot be used to mutate the
 * order's internal state.
 */
public final class OrderItem {
    private final MenuItem menuItem;
    private final int quantity;

    public OrderItem(MenuItem menuItem, int quantity) {
        if (menuItem == null) {
            throw new IllegalArgumentException("Menu item must not be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }

    /** Returns a new order item with {@code additionalQuantity} more of the same menu item. */
    public OrderItem withAdditionalQuantity(int additionalQuantity) {
        if (additionalQuantity <= 0) {
            throw new IllegalArgumentException("Additional quantity must be positive");
        }
        if (quantity > Integer.MAX_VALUE - additionalQuantity) {
            throw new IllegalArgumentException("Quantity would overflow");
        }
        return new OrderItem(menuItem, quantity + additionalQuantity);
    }

    /** The line total: unit price multiplied by quantity, normalized to 2 decimals. */
    public BigDecimal calculateSubtotal() {
        return Money.scale(menuItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
    }

    @Override
    public String toString() {
        return menuItem.getName() + " x" + quantity + ": " + Money.format(calculateSubtotal());
    }
}
