package restaurantorderingsystem;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.List;

public final class RestaurantTest {
    private RestaurantTest() {
    }

    private static Restaurant sampleRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.addMenuItem(new MenuItem("M01", "Pasta", new BigDecimal("18.00")));
        restaurant.addMenuItem(new MenuItem("M02", "Salad", new BigDecimal("11.00")));
        restaurant.addMenuItem(new MenuItem("M03", "Juice", new BigDecimal("5.00")));
        return restaurant;
    }

    static void run(Assert t) {
        t.assertTrue(Modifier.isFinal(Restaurant.class.getModifiers()), "Restaurant is final");

        Restaurant restaurant = sampleRestaurant();

        // Menu
        t.assertEquals(3, restaurant.listMenu().size(), "three menu items");
        t.assertThrows(IllegalArgumentException.class,
                () -> restaurant.addMenuItem(null), "null menu item rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> restaurant.addMenuItem(new MenuItem("M01", "Other", BigDecimal.TEN)),
                "duplicate menu item ID rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> restaurant.addMenuItem(new MenuItem("M09", "pasta", BigDecimal.TEN)),
                "duplicate menu item name rejected case-insensitively");
        t.assertEquals("Pasta", restaurant.findMenuItem("M01").getName(),
                "findMenuItem returns the item");
        t.assertThrows(IllegalArgumentException.class,
                () -> restaurant.findMenuItem("NOPE"), "unknown menu item rejected");
        t.assertThrows(UnsupportedOperationException.class,
                () -> restaurant.listMenu().add(new MenuItem("X", "X", BigDecimal.TEN)),
                "menu list is unmodifiable");

        // Create order with sequential IDs
        Order first = restaurant.createOrder("Alina");
        Order second = restaurant.createOrder();
        t.assertEquals("O0001", first.getId(), "first order id is O0001");
        t.assertEquals("O0002", second.getId(), "order ids are sequential");
        t.assertEquals("Walk-in customer", second.getCustomerName(),
                "default customer name is used");

        // Add items through the restaurant
        restaurant.addItemToOrder("O0001", "M01", 2);
        restaurant.addItemToOrder("O0001", "M02", 1);
        restaurant.addItemToOrder("O0001", "M01", 1); // merges
        Order snapshot = restaurant.findOrder("O0001");
        t.assertEquals(2, snapshot.getItemCount(), "merged into two distinct lines");
        t.assertBigDecimalEquals(new BigDecimal("65.00"), snapshot.calculateSubtotal(),
                "subtotal via restaurant is correct");

        // Unknown lookups fail cleanly
        t.assertThrows(IllegalArgumentException.class,
                () -> restaurant.addItemToOrder("O9999", "M01", 1), "unknown order rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> restaurant.addItemToOrder("O0001", "NOPE", 1),
                "unknown menu item on order rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> restaurant.findOrder("O9999"), "unknown order lookup rejected");

        // List orders is unmodifiable
        List<Order> orders = restaurant.listOrders();
        t.assertEquals(2, orders.size(), "two orders listed");
        t.assertThrows(UnsupportedOperationException.class,
                () -> orders.add(new Order("X", "Y")), "orders list is unmodifiable");

        // Defensive copies: mutating a returned Order must not change restaurant state
        Order fromFind = restaurant.findOrder("O0001");
        fromFind.updateStatus(OrderStatus.PREPARING);
        t.assertEquals(OrderStatus.CREATED, restaurant.findOrder("O0001").getStatus(),
                "mutating a found order does not change the restaurant's order");
        Order fromFind2 = restaurant.findOrder("O0001"); // fresh CREATED copy
        fromFind2.addItem(restaurant.findMenuItem("M03"), 5);
        t.assertEquals(2, restaurant.findOrder("O0001").getItemCount(),
                "adding items to a found order does not change the restaurant's order");

        Order fromList = restaurant.listOrders().get(0);
        fromList.updateStatus(OrderStatus.CANCELLED);
        t.assertEquals(OrderStatus.CREATED, restaurant.findOrder("O0001").getStatus(),
                "mutating a listed order does not change the restaurant's order");

        Order fromCreate = restaurant.createOrder("Mutator");
        fromCreate.addItem(restaurant.findMenuItem("M01"), 9);
        t.assertEquals(0, restaurant.findOrder(fromCreate.getId()).getItemCount(),
                "mutating the order returned by createOrder does not change the restaurant");

        // Update status through the restaurant
        restaurant.updateOrderStatus("O0001", OrderStatus.PREPARING);
        t.assertEquals(OrderStatus.PREPARING, restaurant.findOrder("O0001").getStatus(),
                "updateOrderStatus changes the internal order");
        t.assertThrows(IllegalStateException.class,
                () -> restaurant.updateOrderStatus("O0001", OrderStatus.CREATED),
                "invalid status update rejected");

        // Cancel order
        Order cancelMe = restaurant.createOrder("Cancel");
        restaurant.addItemToOrder(cancelMe.getId(), "M01", 1);
        restaurant.cancelOrder(cancelMe.getId());
        t.assertEquals(OrderStatus.CANCELLED, restaurant.findOrder(cancelMe.getId()).getStatus(),
                "cancelOrder cancels the order");

        // End-to-end workflow
        Restaurant e2e = sampleRestaurant();
        Order order = e2e.createOrder("E2E");
        String id = order.getId();
        e2e.addItemToOrder(id, "M01", 3);
        e2e.updateOrderStatus(id, OrderStatus.PREPARING);
        e2e.updateOrderStatus(id, OrderStatus.READY);
        e2e.updateOrderStatus(id, OrderStatus.SERVED);
        Order finalOrder = e2e.findOrder(id);
        t.assertEquals(OrderStatus.SERVED, finalOrder.getStatus(), "end-to-end reaches SERVED");
        t.assertBigDecimalEquals(new BigDecimal("54.00"), finalOrder.calculateSubtotal(),
                "end-to-end subtotal is correct");
        t.assertBigDecimalEquals(new BigDecimal("48.60"), finalOrder.calculateTotal(),
                "end-to-end total applies the discount");
    }
}
