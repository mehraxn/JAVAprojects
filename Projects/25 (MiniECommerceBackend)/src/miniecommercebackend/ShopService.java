package miniecommercebackend;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShopService {
    private final Map<String, Product> products = new LinkedHashMap<String, Product>();
    private final Map<String, Cart> carts = new LinkedHashMap<String, Cart>();
    private final Map<String, Order> orders = new LinkedHashMap<String, Order>();
    private long nextCartId = 1;
    private long nextOrderId = 1;

    public synchronized void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (products.containsKey(product.getId())) {
            throw new IllegalArgumentException("Product ID already exists: " + product.getId());
        }
        products.put(product.getId(), product.copy());
    }

    public synchronized List<Product> listProducts() {
        List<Product> result = new ArrayList<Product>();
        for (Product product : products.values()) {
            result.add(product.copy());
        }
        return Collections.unmodifiableList(result);
    }

    public synchronized List<Product> searchProducts(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            throw new IllegalArgumentException("Search text cannot be empty.");
        }
        String query = searchText.trim().toLowerCase(Locale.ROOT);
        List<Product> result = new ArrayList<Product>();
        for (Product product : products.values()) {
            if (product.getId().toLowerCase(Locale.ROOT).contains(query)
                    || product.getName().toLowerCase(Locale.ROOT).contains(query)) {
                result.add(product.copy());
            }
        }
        return Collections.unmodifiableList(result);
    }

    public synchronized Cart createCart() {
        Cart cart = new Cart("CART-" + nextCartId);
        carts.put(cart.getId(), cart);
        nextCartId++;
        return cart.copy();
    }

    public synchronized Cart findCart(String cartId) {
        Cart cart = carts.get(requireText(cartId, "Cart ID"));
        return cart == null ? null : cart.copy();
    }

    public synchronized void addToCart(String cartId, String productId, int quantity) {
        Cart cart = requireCart(cartId);
        Product product = requireProduct(productId);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Cart quantity must be positive.");
        }
        int existing = cart.getItems().containsKey(product.getId())
                ? cart.getItems().get(product.getId()) : 0;
        long requested = (long) existing + quantity;
        if (requested > product.getStockQuantity()) {
            throw new IllegalArgumentException("Not enough stock for product " + product.getId() + ".");
        }
        cart.addProduct(product.getId(), quantity);
    }

    public synchronized boolean removeFromCart(String cartId, String productId) {
        return requireCart(cartId).removeProduct(productId);
    }

    public synchronized BigDecimal calculateCartTotal(String cartId) {
        Cart cart = requireCart(cartId);
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> item : cart.getItems().entrySet()) {
            Product product = requireProduct(item.getKey());
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.getValue())));
        }
        return total;
    }

    public synchronized Order placeOrder(String cartId) {
        Cart cart = requireCart(cartId);
        Map<String, Integer> items = cart.getItems();
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot place an order from an empty cart.");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> item : items.entrySet()) {
            Product product = requireProduct(item.getKey());
            if (item.getValue() > product.getStockQuantity()) {
                throw new IllegalStateException("Not enough stock for product " + product.getId() + ".");
            }
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.getValue())));
        }

        Order order = new Order("ORDER-" + nextOrderId, cart.getId(), items,
                total, LocalDateTime.now(), Order.Status.CREATED);
        for (Map.Entry<String, Integer> item : items.entrySet()) {
            products.get(item.getKey()).adjustStock(-item.getValue());
        }
        orders.put(order.getId(), order);
        carts.remove(cart.getId());
        nextOrderId++;
        return order.copy();
    }

    public synchronized boolean updateOrderStatus(String orderId, Order.Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null.");
        }
        String id = requireText(orderId, "Order ID");
        Order order = orders.get(id);
        if (order == null) {
            return false;
        }
        if (status == Order.Status.CANCELLED && order.getStatus() == Order.Status.CREATED) {
            for (Map.Entry<String, Integer> item : order.getItems().entrySet()) {
                long restored = (long) products.get(item.getKey()).getStockQuantity() + item.getValue();
                if (restored > Integer.MAX_VALUE) {
                    throw new IllegalStateException("Cannot restore stock without overflow.");
                }
            }
            for (Map.Entry<String, Integer> item : order.getItems().entrySet()) {
                products.get(item.getKey()).adjustStock(item.getValue());
            }
        }
        order.setStatus(status);
        return true;
    }

    public synchronized Order findOrder(String orderId) {
        Order order = orders.get(requireText(orderId, "Order ID"));
        return order == null ? null : order.copy();
    }

    public synchronized List<Order> listOrders() {
        List<Order> result = new ArrayList<Order>();
        for (Order order : orders.values()) {
            result.add(order.copy());
        }
        return Collections.unmodifiableList(result);
    }

    private Cart requireCart(String cartId) {
        String id = requireText(cartId, "Cart ID");
        Cart cart = carts.get(id);
        if (cart == null) {
            throw new IllegalArgumentException("Cart does not exist: " + id);
        }
        return cart;
    }

    private Product requireProduct(String productId) {
        String id = requireText(productId, "Product ID");
        Product product = products.get(id);
        if (product == null) {
            throw new IllegalArgumentException("Product does not exist: " + id);
        }
        return product;
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }
}
