package testingcoveragequalitygate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class DiscountPolicyTest {
    @Test
    void appliesPercentageDiscount() {
        DiscountPolicy policy = new DiscountPolicy(new BigDecimal("10"));
        assertEquals(new BigDecimal("90.00"), policy.applyTo(new BigDecimal("100.00")));
    }

    @Test
    void supportsBoundaryPercentages() {
        assertEquals(new BigDecimal("20.00"),
                new DiscountPolicy(BigDecimal.ZERO).applyTo(new BigDecimal("20.00")));
        assertEquals(new BigDecimal("0.00"),
                new DiscountPolicy(new BigDecimal("100")).applyTo(new BigDecimal("20.00")));
    }

    @Test
    void rejectsInvalidPercentagesAndSubtotals() {
        assertThrows(IllegalArgumentException.class,
                () -> new DiscountPolicy(new BigDecimal("-1")));
        assertThrows(IllegalArgumentException.class,
                () -> new DiscountPolicy(new BigDecimal("101")));
        assertThrows(IllegalArgumentException.class,
                () -> new DiscountPolicy(null));
        DiscountPolicy policy = new DiscountPolicy(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class,
                () -> policy.applyTo(new BigDecimal("-0.01")));
    }
}
