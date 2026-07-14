package orderservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class OrderService {
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private final DownstreamGateway gateway;

    public OrderService(DownstreamGateway gateway) {
        if (gateway == null) {
            throw new IllegalArgumentException("Downstream gateway is required.");
        }
        this.gateway = gateway;
    }

    public Order create(String sku, int quantity, BigDecimal unitPrice) {
        Order order = new Order("ORD-" + sequence.incrementAndGet(), sku, quantity, unitPrice);
        orders.put(order.getId(), order);

        if (!gateway.reserveInventory(order)) {
            order.updateStatus(Order.Status.INVENTORY_REJECTED,
                    "Inventory was unavailable or the inventory service could not be reached");
            return order;
        }

        if (!gateway.authorizePayment(order)) {
            boolean released = gateway.releaseInventory(order);
            String detail = released
                    ? "Mock payment was rejected; inventory was released"
                    : "Mock payment was rejected; inventory release could not be confirmed";
            order.updateStatus(Order.Status.PAYMENT_REJECTED, detail);
            return order;
        }

        boolean notified = gateway.sendNotification(order);
        order.updateStatus(Order.Status.CONFIRMED, notified
                ? "Order confirmed and mock notification recorded"
                : "Order confirmed; mock notification could not be confirmed");
        return order;
    }

    public Optional<Order> find(String id) {
        return Optional.ofNullable(orders.get(id));
    }

    public List<Order> findAll() {
        List<Order> result = new ArrayList<>(orders.values());
        result.sort(Comparator.comparing(Order::getId));
        return result;
    }
}
