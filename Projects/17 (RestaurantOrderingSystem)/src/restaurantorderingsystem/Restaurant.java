package restaurantorderingsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Restaurant {
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
        return order;
    }

    public Order findOrder(String orderId) {
        String validId = requireText(orderId, "Order ID");
        Order order = orders.get(validId);
        if (order == null) {
            throw new IllegalArgumentException("Unknown order ID: " + validId);
        }
        return order;
    }

    public void addItemToOrder(String orderId, String menuItemId, int quantity) {
        findOrder(orderId).addItem(findMenuItem(menuItemId), quantity);
    }

    public void removeItemFromOrder(String orderId, String menuItemId) {
        findOrder(orderId).removeItem(menuItemId);
    }

    public List<MenuItem> listMenu() {
        return Collections.unmodifiableList(new ArrayList<>(menu.values()));
    }

    public List<Order> listOrders() {
        return Collections.unmodifiableList(new ArrayList<>(orders.values()));
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
