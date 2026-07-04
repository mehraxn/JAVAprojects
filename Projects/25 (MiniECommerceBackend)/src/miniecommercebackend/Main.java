package miniecommercebackend;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        ShopService service = new ShopService();
        service.addProduct(new Product("P-100", "Mechanical Keyboard",
                new BigDecimal("79.90"), 5));
        service.addProduct(new Product("P-200", "USB-C Cable",
                new BigDecimal("12.50"), 10));

        Cart cart = service.createCart();
        service.addToCart(cart.getId(), "P-100", 1);
        service.addToCart(cart.getId(), "P-200", 2);
        System.out.println("Cart total: " + service.calculateCartTotal(cart.getId()));

        Order order = service.placeOrder(cart.getId());
        service.updateOrderStatus(order.getId(), Order.Status.PAID);
        System.out.println("Placed order: " + service.findOrder(order.getId()));
        System.out.println("Remaining products:");
        for (Product product : service.listProducts()) {
            System.out.println(product);
        }
    }
}
