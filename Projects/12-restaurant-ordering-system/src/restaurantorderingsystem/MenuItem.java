package restaurantorderingsystem;

import java.math.BigDecimal;

/** An immutable menu item. Money is a {@link BigDecimal}; the price must be greater than zero. */
public final class MenuItem {
    private final String id;
    private final String name;
    private final BigDecimal price;

    public MenuItem(String id, String name, BigDecimal price) {
        this.id = requireText(id, "Menu item ID");
        this.name = requireText(name, "Menu item name");
        if (price == null) {
            throw new IllegalArgumentException("Menu item price must not be null");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Menu item price must be greater than zero");
        }
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }

    @Override
    public String toString() {
        return id + " " + name + " (" + Money.format(price) + ")";
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
