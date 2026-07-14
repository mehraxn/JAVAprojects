package orderservice;

public interface DownstreamGateway {
    boolean reserveInventory(Order order);

    boolean releaseInventory(Order order);

    boolean authorizePayment(Order order);

    boolean sendNotification(Order order);
}
