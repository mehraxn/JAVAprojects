package testingcoveragequalitygate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PriceCalculatorTest {
    private final PriceCalculator calculator = new PriceCalculator();

    @Test
    void calculatesSubtotalAndDiscountedTotal() {
        assertEquals(new BigDecimal("30.00"),
                calculator.calculateSubtotal(3, new BigDecimal("10.00")));
        assertEquals(new BigDecimal("27.00"),
                calculator.calculateTotal(
                        3, new BigDecimal("10.00"),
                        new DiscountPolicy(new BigDecimal("10"))));
    }

    @Test
    void roundsMoneyToTwoDecimalPlaces() {
        assertEquals(new BigDecimal("3.34"),
                calculator.calculateSubtotal(2, new BigDecimal("1.668")));
    }

    @Test
    void rejectsInvalidQuantityPriceAndPolicy() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateSubtotal(0, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateSubtotal(1, new BigDecimal("-0.01")));
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateSubtotal(1, null));
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateTotal(1, BigDecimal.ONE, null));
    }
}
