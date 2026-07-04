package movieticketbookingsystem;

import java.math.BigDecimal;
import java.util.List;

public class Booking {
    private final String id;
    private final String showtimeId;
    private final List<String> seatLabels;
    private final BigDecimal totalPrice;

    public Booking(String id, String showtimeId, List<String> seatLabels, BigDecimal totalPrice) {
        this.id = id;
        this.showtimeId = showtimeId;
        this.seatLabels = seatLabels;
        this.totalPrice = totalPrice;
    }

    public String getId() { return id; }
}
