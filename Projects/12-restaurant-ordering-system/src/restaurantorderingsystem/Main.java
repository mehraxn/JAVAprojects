package restaurantorderingsystem;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Locale;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        System.exit(exitCode);
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        String command = args.length == 0 ? "help" : args[0].toLowerCase(Locale.ROOT);
        switch (command) {
            case "help":
                printUsage(out);
                return 0;
            case "demo":
                runDemo(out);
                return 0;
            case "order-demo":
                runOrderDemo(out);
                return 0;
            case "discount-demo":
                runDiscountDemo(out);
                return 0;
            case "status-demo":
                runStatusDemo(out);
                return 0;
            case "validation-demo":
                runValidationDemo(out);
                return 0;
            default:
                err.println("Unknown command: " + args[0]);
                printUsage(err);
                return 1;
        }
    }

    private static void printUsage(PrintStream out) {
        out.println("Restaurant Ordering System - educational Java OOP project");
        out.println();
        out.println("Usage: java -cp out restaurantorderingsystem.Main <command>");
        out.println();
        out.println("Commands:");
        out.println("  help             Show this usage text (default with no command).");
        out.println("  demo             Full walkthrough: menu, order, totals, lifecycle.");
        out.println("  order-demo       Order creation, item merging, and totals.");
        out.println("  discount-demo    Discount boundary and 2-decimal money formatting.");
        out.println("  status-demo      Valid and invalid order status transitions.");
        out.println("  validation-demo  Show how invalid input is rejected cleanly.");
    }

    private static Restaurant sampleRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.addMenuItem(new MenuItem("M01", "Pasta", new BigDecimal("18.00")));
        restaurant.addMenuItem(new MenuItem("M02", "Salad", new BigDecimal("11.00")));
        restaurant.addMenuItem(new MenuItem("M03", "Juice", new BigDecimal("5.00")));
        return restaurant;
    }

    private static void runDemo(PrintStream out) {
        Restaurant restaurant = sampleRestaurant();
        out.println("== Menu ==");
        for (MenuItem item : restaurant.listMenu()) {
            out.println(item);
        }

        out.println("== Create order and add items ==");
        Order order = restaurant.createOrder("Alina");
        String id = order.getId();
        restaurant.addItemToOrder(id, "M01", 2);
        restaurant.addItemToOrder(id, "M02", 1);
        restaurant.addItemToOrder(id, "M01", 1); // merges with the first Pasta line
        out.println("Added Pasta x2, Salad x1, then Pasta x1 again (merges).");

        out.println("== Order summary ==");
        out.println(restaurant.findOrder(id).createSummary());

        out.println("== Move through the lifecycle ==");
        restaurant.updateOrderStatus(id, OrderStatus.PREPARING);
        restaurant.updateOrderStatus(id, OrderStatus.READY);
        restaurant.updateOrderStatus(id, OrderStatus.SERVED);
        out.println("Final status: " + restaurant.findOrder(id).getStatus());
    }

    private static void runOrderDemo(PrintStream out) {
        Restaurant restaurant = sampleRestaurant();
        Order order = restaurant.createOrder("Bruno");
        String id = order.getId();

        out.println("== Add the same item twice (quantities merge) ==");
        restaurant.addItemToOrder(id, "M03", 2);
        restaurant.addItemToOrder(id, "M03", 3);
        Order snapshot = restaurant.findOrder(id);
        out.println("Juice line quantity: " + snapshot.getItems().get(0).getQuantity()
                + " (2 + 3, one line, not two)");
        out.println("Distinct line items: " + snapshot.getItemCount());

        out.println("== Totals ==");
        restaurant.addItemToOrder(id, "M01", 1);
        Order updated = restaurant.findOrder(id);
        out.println("Subtotal: " + Money.format(updated.calculateSubtotal()));
        out.println("Total:    " + Money.format(updated.calculateTotal()));
    }

    private static void runDiscountDemo(PrintStream out) {
        out.println("Discount rule: orders of " + Money.format(Order.DISCOUNT_THRESHOLD)
                + " or more get 10% off (the threshold itself qualifies).");
        out.println();

        // Below threshold: 2 x 18.00 = 36.00 -> no discount
        printOrderMoney(out, "Below threshold", 2, 0, 0);
        // Exactly threshold: 5 x 5.00 + ... build exactly 50.00 -> discount applies
        printExactThreshold(out);
        // Above threshold: 3 x 18.00 = 54.00 -> discount applies
        printOrderMoney(out, "Above threshold", 3, 0, 0);
    }

    private static void printOrderMoney(PrintStream out, String label,
            int pasta, int salad, int juice) {
        Restaurant restaurant = sampleRestaurant();
        Order order = restaurant.createOrder(label);
        String id = order.getId();
        if (pasta > 0) {
            restaurant.addItemToOrder(id, "M01", pasta);
        }
        if (salad > 0) {
            restaurant.addItemToOrder(id, "M02", salad);
        }
        if (juice > 0) {
            restaurant.addItemToOrder(id, "M03", juice);
        }
        Order snapshot = restaurant.findOrder(id);
        out.println(label + ":");
        out.println("  Subtotal: " + Money.format(snapshot.calculateSubtotal()));
        out.println("  Discount: " + Money.format(snapshot.calculateDiscount()));
        out.println("  Total:    " + Money.format(snapshot.calculateTotal()));
    }

    private static void printExactThreshold(PrintStream out) {
        // 10 x Juice (5.00) = exactly 50.00
        printOrderMoney(out, "Exactly threshold", 0, 0, 10);
    }

    private static void runStatusDemo(PrintStream out) {
        Restaurant restaurant = sampleRestaurant();

        out.println("== Empty order cannot move to PREPARING ==");
        Order empty = restaurant.createOrder("Empty");
        reject(out, () -> restaurant.updateOrderStatus(empty.getId(), OrderStatus.PREPARING));

        out.println("== Valid lifecycle: CREATED -> PREPARING -> READY -> SERVED ==");
        Order order = restaurant.createOrder("Cleo");
        String id = order.getId();
        restaurant.addItemToOrder(id, "M01", 1);
        restaurant.updateOrderStatus(id, OrderStatus.PREPARING);
        out.println("Now: " + restaurant.findOrder(id).getStatus());
        restaurant.updateOrderStatus(id, OrderStatus.READY);
        out.println("Now: " + restaurant.findOrder(id).getStatus());
        restaurant.updateOrderStatus(id, OrderStatus.SERVED);
        out.println("Now: " + restaurant.findOrder(id).getStatus());

        out.println("== Editing after preparation starts is rejected ==");
        Order order2 = restaurant.createOrder("Dana");
        String id2 = order2.getId();
        restaurant.addItemToOrder(id2, "M01", 1);
        restaurant.updateOrderStatus(id2, OrderStatus.PREPARING);
        reject(out, () -> restaurant.addItemToOrder(id2, "M02", 1));

        out.println("== Invalid backwards transition is rejected ==");
        reject(out, () -> restaurant.updateOrderStatus(id, OrderStatus.PREPARING));

        out.println("== Terminal SERVED cannot be cancelled ==");
        reject(out, () -> restaurant.cancelOrder(id));
    }

    private static void runValidationDemo(PrintStream out) {
        out.println("== Validation demo: every rejection below is intentional ==");

        out.println("-- Blank menu item name --");
        reject(out, () -> new MenuItem("M99", "  ", new BigDecimal("5.00")));

        out.println("-- Zero / negative price --");
        reject(out, () -> new MenuItem("M99", "Free", BigDecimal.ZERO));
        reject(out, () -> new MenuItem("M99", "Owed", new BigDecimal("-1.00")));

        out.println("-- Duplicate menu item ID --");
        Restaurant restaurant = sampleRestaurant();
        reject(out, () -> restaurant.addMenuItem(new MenuItem("M01", "Different", BigDecimal.TEN)));

        out.println("-- Invalid quantity --");
        Order order = restaurant.createOrder("Tester");
        reject(out, () -> restaurant.addItemToOrder(order.getId(), "M01", 0));

        out.println("-- Unknown order and unknown menu item --");
        reject(out, () -> restaurant.findOrder("O9999"));
        reject(out, () -> restaurant.addItemToOrder(order.getId(), "NOPE", 1));

        out.println("All validation cases behaved as designed.");
    }

    private static void reject(PrintStream out, Runnable action) {
        try {
            action.run();
            out.println("ERROR: invalid operation was accepted (this should not happen)");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }
    }
}
