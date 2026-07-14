package restaurantorderingsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Owns the menu and the orders. Menu items are immutable, so they are handed out
 * directly. Orders are mutable aggregates, so every method that hands one out
 * returns a defensive <em>snapshot copy</em> — external code cannot reach into the
 * restaurant's live orders. All mutations go through the restaurant's own methods
 * ({@link #addItemToOrder}, {@link #updateOrderStatus}, {@link #cancelOrder}).
 */
public final class Restaurant {
    private final Map<String, MenuItem> menu = new LinkedHashMap<>();
    private final Map<String, Order> orders = new LinkedHashMap<>();
    private int nextOrderNumber = 1;

    public void addMenuItem(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Menu item must not be null");
        }
        if (menu.containsKey(item.getId())) {
            throw new IllegalArgumentException("Menu item ID already exists: " + item.getId());
        }
        for (MenuItem existingItem : menu.values()) {
            if (existingItem.getName().equalsIgnoreCase(item.getName())) {
                throw new IllegalArgumentException(
                        "Menu item name already exists: " + item.getName());
            }
        }
        menu.put(item.getId(), item);
    }

    public boolean removeMenuItem(String itemId) {
        return menu.remove(requireText(itemId, "Menu item ID")) != null;
    }

    /** Menu items are immutable, so returning the stored instance is safe. */
    public MenuItem findMenuItem(String itemId) {
        String validId = requireText(itemId, "Menu item ID");
        MenuItem item = menu.get(validId);
        if (item == null) {
            throw new IllegalArgumentException("Unknown menu item ID: " + validId);
        }
        return item;
    }

    public Order createOrder() {
        return createOrder("Walk-in customer");
    }

    public Order createOrder(String customerName) {
        String orderId = String.format("O%04d", nextOrderNumber++);
        Order order = new Order(orderId, customerName);
        orders.put(orderId, order);
        return order.copy();
    }

    /** Returns a snapshot copy of the order; mutating it does not affect the restaurant. */
    public Order findOrder(String orderId) {
        return findInternalOrder(orderId).copy();
    }

    public void addItemToOrder(String orderId, String menuItemId, int quantity) {
        findInternalOrder(orderId).addItem(findMenuItem(menuItemId), quantity);
    }

    public void removeItemFromOrder(String orderId, String menuItemId) {
        findInternalOrder(orderId).removeItem(menuItemId);
    }

    public void updateOrderStatus(String orderId, OrderStatus newStatus) {
        findInternalOrder(orderId).updateStatus(newStatus);
    }

    public void cancelOrder(String orderId) {
        findInternalOrder(orderId).updateStatus(OrderStatus.CANCELLED);
    }

    public List<MenuItem> listMenu() {
        return Collections.unmodifiableList(new ArrayList<>(menu.values()));
    }

    /** Returns snapshot copies of every order; mutating them does not affect the restaurant. */
    public List<Order> listOrders() {
        List<Order> snapshots = new ArrayList<>();
        for (Order order : orders.values()) {
            snapshots.add(order.copy());
        }
        return Collections.unmodifiableList(snapshots);
    }

    private Order findInternalOrder(String orderId) {
        String validId = requireText(orderId, "Order ID");
        Order order = orders.get(validId);
        if (order == null) {
            throw new IllegalArgumentException("Unknown order ID: " + validId);
        }
        return order;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
