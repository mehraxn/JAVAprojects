package restaurantorderingsystem;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        Restaurant restaurant = new Restaurant();
        restaurant.addMenuItem(new MenuItem("M01", "Pasta", new BigDecimal("18.00")));
        restaurant.addMenuItem(new MenuItem("M02", "Salad", new BigDecimal("11.00")));
        restaurant.addMenuItem(new MenuItem("M03", "Juice", new BigDecimal("5.00")));

        Order order = restaurant.createOrder("Alina");
        restaurant.addItemToOrder(order.getId(), "M01", 2);
        restaurant.addItemToOrder(order.getId(), "M02", 1);
        restaurant.addItemToOrder(order.getId(), "M03", 1);

        System.out.println(order.createSummary());
        order.updateStatus(OrderStatus.PREPARING);
        order.updateStatus(OrderStatus.READY);
        System.out.println("Updated status: " + order.getStatus());
    }
}
