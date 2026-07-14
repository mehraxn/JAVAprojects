package paymentservice;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentService {
    private static final BigDecimal MOCK_APPROVAL_LIMIT = new BigDecimal("10000.00");
    private final Map<String, Payment> paymentsByOrder = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public synchronized Payment authorize(String orderId, BigDecimal amount) {
        String cleanOrderId = requireText(orderId, "Order ID");
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        Payment existing = paymentsByOrder.get(cleanOrderId);
        if (existing != null) {
            if (existing.getAmount().compareTo(amount) != 0) {
                throw new IllegalArgumentException("An authorization with a different amount already exists.");
            }
            return existing;
        }
        String status = amount.compareTo(MOCK_APPROVAL_LIMIT) <= 0 ? "APPROVED" : "DECLINED";
        Payment payment = new Payment("PAY-" + sequence.incrementAndGet(), cleanOrderId, amount, status);
        paymentsByOrder.put(cleanOrderId, payment);
        return payment;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }
}
