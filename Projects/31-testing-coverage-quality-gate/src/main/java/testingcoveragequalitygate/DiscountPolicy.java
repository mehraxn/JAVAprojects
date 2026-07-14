package testingcoveragequalitygate;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DiscountPolicy {
    private final BigDecimal percentage;

    public DiscountPolicy(BigDecimal percentage) {
        if (percentage == null) {
            throw new IllegalArgumentException("Discount percentage is required.");
        }
        if (percentage.compareTo(BigDecimal.ZERO) < 0
                || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100.");
        }
        this.percentage = percentage;
    }

    public BigDecimal applyTo(BigDecimal subtotal) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Subtotal cannot be null or negative.");
        }
        BigDecimal multiplier = new BigDecimal("100").subtract(percentage);
        return subtotal.multiply(multiplier)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getPercentage() {
        return percentage;
    }
}
