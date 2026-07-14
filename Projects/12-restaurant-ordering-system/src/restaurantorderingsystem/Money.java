package restaurantorderingsystem;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Small money helper. All monetary values are {@link BigDecimal} (never
 * {@code double}) to avoid floating-point rounding errors. Amounts are
 * normalized and displayed with exactly two decimal places, rounding half up.
 */
final class Money {
    static final int SCALE = 2;

    private Money() {
    }

    /** Rounds an amount to 2 decimal places (half up) for deterministic totals. */
    static BigDecimal scale(BigDecimal amount) {
        return amount.setScale(SCALE, RoundingMode.HALF_UP);
    }

    /** Formats an amount as a 2-decimal string, e.g. {@code "46.80"}. */
    static String format(BigDecimal amount) {
        return scale(amount).toPlainString();
    }
}
