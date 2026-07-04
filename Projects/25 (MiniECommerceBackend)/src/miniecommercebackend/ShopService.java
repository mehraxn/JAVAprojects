package miniecommercebackend;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShopService {
    private final Map<String, Product> products = new LinkedHashMap<>();
    private final Map<String, Cart> carts = new LinkedHashMap<>();
    private final Map<String, Order> orders = new LinkedHashMap<>();

    public void addProduct(Product product) {
        // TODO: Validate product data and reject duplicate IDs.
        throw new UnsupportedOperationException("TODO: add a product");
    }

    public Cart createCart() {
        // TODO: Generate and store a new empty cart.
        throw new UnsupportedOperationException("TODO: create a cart");
    }

    public void addToCart(String cartId, String productId, int quantity) {
        // TODO: Validate the cart, product, quantity, and available stock.
        throw new UnsupportedOperationException("TODO: add an item to a cart");
    }

    public Order checkout(String cartId) {
        // TODO: Validate the entire cart, reduce stock, and create an order.
        throw new UnsupportedOperationException("TODO: check out a cart");
    }

    public List<Order> listOrders() {
        // TODO: Return an unmodifiable order history.
        throw new UnsupportedOperationException("TODO: list orders");
    }
}
