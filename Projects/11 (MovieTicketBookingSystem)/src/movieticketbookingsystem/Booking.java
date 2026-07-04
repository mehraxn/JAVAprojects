package movieticketbookingsystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Booking {
    private final String id;
    private final String showtimeId;
    private final List<String> seatLabels;
    private final BigDecimal totalPrice;

    public Booking(String id, String showtimeId, List<String> seatLabels, BigDecimal totalPrice) {
        if (id == null || id.trim().isEmpty() || showtimeId == null || showtimeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking and showtime IDs must not be blank");
        }
        if (seatLabels == null || seatLabels.isEmpty() || totalPrice == null
                || totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Booking seats and price must be valid");
        }
        List<String> validatedLabels = new ArrayList<>();
        Set<String> uniqueLabels = new LinkedHashSet<>();
        for (String label : seatLabels) {
            if (label == null || label.trim().isEmpty()) {
                throw new IllegalArgumentException("Booking seat labels must not be blank");
            }
            String trimmedLabel = label.trim();
            if (!uniqueLabels.add(trimmedLabel.toUpperCase(Locale.ROOT))) {
                throw new IllegalArgumentException("Duplicate booking seat: " + trimmedLabel);
            }
            validatedLabels.add(trimmedLabel);
        }
        this.id = id.trim();
        this.showtimeId = showtimeId.trim();
        this.seatLabels = Collections.unmodifiableList(validatedLabels);
        this.totalPrice = totalPrice;
    }

    public String getId() { return id; }
    public String getShowtimeId() { return showtimeId; }
    public List<String> getSeatLabels() { return seatLabels; }
    public BigDecimal getTotalPrice() { return totalPrice; }
}
