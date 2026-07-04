package miniecommercebackend;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
    private final String id;
    private final Map<String, Integer> quantitiesByProduct =
            new LinkedHashMap<String, Integer>();

    public Cart(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Cart ID cannot be empty.");
        }
        this.id = id.trim();
    }

    public String getId() {
        return id;
    }

    public void addProduct(String productId, int quantity) {
        String id = requireProductId(productId);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Cart quantity must be positive.");
        }
        int current = quantitiesByProduct.containsKey(id) ? quantitiesByProduct.get(id) : 0;
        long result = (long) current + quantity;
        if (result > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Cart quantity is too large.");
        }
        quantitiesByProduct.put(id, (int) result);
    }

    public boolean removeProduct(String productId) {
        return quantitiesByProduct.remove(requireProductId(productId)) != null;
    }

    public Map<String, Integer> getItems() {
        return Collections.unmodifiableMap(
                new LinkedHashMap<String, Integer>(quantitiesByProduct));
    }

    public Cart copy() {
        Cart copy = new Cart(id);
        for (Map.Entry<String, Integer> item : quantitiesByProduct.entrySet()) {
            copy.addProduct(item.getKey(), item.getValue());
        }
        return copy;
    }

    private String requireProductId(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty.");
        }
        return productId.trim();
    }
}
