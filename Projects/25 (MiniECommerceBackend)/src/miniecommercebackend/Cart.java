package miniecommercebackend;

import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
    private final String id;
    private final Map<String, Integer> quantitiesByProduct = new LinkedHashMap<>();

    public Cart(String id) {
        this.id = id;
    }

    public String getId() { return id; }

    public void addProduct(String productId, int quantity) {
        // TODO: Validate quantity and add or increase the product.
        throw new UnsupportedOperationException("TODO: add a product to the cart");
    }

    public void removeProduct(String productId) {
        // TODO: Remove the product or report that it is absent.
        throw new UnsupportedOperationException("TODO: remove a product from the cart");
    }

    public Map<String, Integer> getItems() {
        // TODO: Return an unmodifiable item snapshot.
        throw new UnsupportedOperationException("TODO: list cart items");
    }
}
