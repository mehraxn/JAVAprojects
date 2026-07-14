package testingcoveragequalitygate;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceCalculator {
    public BigDecimal calculateSubtotal(int quantity, BigDecimal unitPrice) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be null or negative.");
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotal(
            int quantity, BigDecimal unitPrice, DiscountPolicy discountPolicy) {
        if (discountPolicy == null) {
            throw new IllegalArgumentException("Discount policy is required.");
        }
        return discountPolicy.applyTo(calculateSubtotal(quantity, unitPrice));
    }
}
