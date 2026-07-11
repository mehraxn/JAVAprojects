package miniecommercebackend;

import java.io.PrintStream;
import java.math.BigDecimal;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        System.exit(run(args, System.out, System.err));
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        if (args == null || args.length != 1) {
            err.println("Expected exactly one command. Run 'help' for usage.");
            return 2;
        }
        if (out == null || err == null) throw new IllegalArgumentException("Output streams cannot be null.");
        try {
            switch (args[0]) {
                case "help": printHelp(out); break;
                case "demo": completeDemo(out); break;
                case "catalog-demo": catalogDemo(out); break;
                case "checkout-demo": checkoutDemo(out); break;
                case "cancel-demo": cancelDemo(out); break;
                case "failure-demo": failureDemo(out); break;
                default:
                    err.println("Unknown command: " + args[0]);
                    err.println("Run 'help' for usage.");
                    return 2;
            }
            return 0;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            err.println("Command failed: " + exception.getMessage());
            return 1;
        }
    }

    private static ShopService shop() {
        ShopService service = new ShopService();
        service.addProduct(new Product("P-100", "Mechanical Keyboard", new BigDecimal("79.90"), 5));
        service.addProduct(new Product("P-200", "USB-C Cable", new BigDecimal("12.50"), 10));
        return service;
    }

    private static void printHelp(PrintStream out) {
        out.println("Mini E-Commerce Backend");
        out.println("Usage: java -cp out miniecommercebackend.Main <command>");
        out.println("Commands: help, demo, catalog-demo, checkout-demo, cancel-demo, failure-demo");
    }

    private static void completeDemo(PrintStream out) {
        ShopService service = shop();
        Cart cart = service.createCart();
        service.addToCart(cart.getId(), "P-100", 1);
        service.addToCart(cart.getId(), "P-200", 2);
        out.println("Cart total: " + service.calculateCartTotal(cart.getId()).toPlainString());
        Order order = service.placeOrder(cart.getId());
        service.updateOrderStatus(order.getId(), Order.Status.PAID);
        out.println("Order summary: " + service.findOrder(order.getId()));
        printCatalog(service, out);
    }

    private static void catalogDemo(PrintStream out) {
        ShopService service = shop();
        out.println("Catalog demo");
        printCatalog(service, out);
    }

    private static void checkoutDemo(PrintStream out) {
        ShopService service = shop();
        Cart cart = service.createCart();
        service.addToCart(cart.getId(), "P-200", 3);
        out.println("Checkout total: " + service.calculateCartTotal(cart.getId()).toPlainString());
        out.println("Checkout successful: " + service.placeOrder(cart.getId()).getId());
    }

    private static void cancelDemo(PrintStream out) {
        ShopService service = shop();
        Cart cart = service.createCart();
        service.addToCart(cart.getId(), "P-100", 2);
        Order order = service.placeOrder(cart.getId());
        int afterCheckout = stock(service, "P-100");
        service.updateOrderStatus(order.getId(), Order.Status.CANCELLED);
        out.println("Order cancelled: " + order.getId());
        out.println("Stock restored: " + afterCheckout + " -> " + stock(service, "P-100"));
    }

    private static void failureDemo(PrintStream out) {
        ShopService service = shop();
        Cart cart = service.createCart();
        try {
            service.addToCart(cart.getId(), "P-100", 99);
            throw new IllegalStateException("Expected failure did not occur.");
        } catch (IllegalArgumentException expected) {
            out.println("Expected failure handled: " + expected.getMessage());
            out.println("Cart unchanged: " + service.findCart(cart.getId()).getItems().isEmpty());
        }
    }

    private static int stock(ShopService service, String id) {
        for (Product product : service.listProducts()) {
            if (product.getId().equals(id)) return product.getStockQuantity();
        }
        throw new IllegalArgumentException("Product does not exist: " + id);
    }

    private static void printCatalog(ShopService service, PrintStream out) {
        out.println("Catalog:");
        for (Product product : service.listProducts()) out.println("- " + product);
    }
}
