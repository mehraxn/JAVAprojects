package restaurantorderingsystem;

import java.util.HashMap;
import java.util.Map;

public class Restaurant {
    private final Map<String, MenuItem> menu = new HashMap<>();
    private final Map<String, Order> orders = new HashMap<>();

    public void addMenuItem(MenuItem item) {
        // TODO: Validate and add a uniquely identified menu item.
        throw new UnsupportedOperationException("TODO: add a menu item");
    }

    public boolean removeMenuItem(String itemId) {
        // TODO: Remove the menu item when present.
        throw new UnsupportedOperationException("TODO: remove a menu item");
    }

    public Order createOrder() {
        // TODO: Generate an identifier and store a new order.
        throw new UnsupportedOperationException("TODO: create an order");
    }

    public Order findOrder(String orderId) {
        // TODO: Return the order or report that it does not exist.
        throw new UnsupportedOperationException("TODO: find an order");
    }
}
