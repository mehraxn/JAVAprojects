package testingcoveragequalitygate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PriceCalculatorTest {
    private final PriceCalculator calculator = new PriceCalculator();

    @ParameterizedTest(name = "{0} x {1} = {2}")
    @CsvSource({
            "1, 10.00, 10.00",
            "3, 10.00, 30.00",
            "2, 0.00, 0.00",
            "7, 19.99, 139.93",
            "100, 0.01, 1.00"
    })
    void calculatesSubtotalForNormalAndZeroPrices(
            int quantity, BigDecimal unitPrice, BigDecimal expected) {
        assertEquals(expected, calculator.calculateSubtotal(quantity, unitPrice));
    }

    @ParameterizedTest(name = "{0} x {1} rounds to {2}")
    @CsvSource({
            "2, 1.668, 3.34",   // 3.336 rounds down
            "3, 1.115, 3.35",   // 3.345 half rounds up
            "1, 0.005, 0.01",   // half at the smallest unit rounds up
            "1, 0.004, 0.00"    // below half rounds down
    })
    void roundsSubtotalHalfUpToTwoDecimalPlaces(
            int quantity, BigDecimal unitPrice, BigDecimal expected) {
        assertEquals(expected, calculator.calculateSubtotal(quantity, unitPrice));
    }

    @ParameterizedTest(name = "quantity {0} is rejected")
    @ValueSource(ints = {0, -1, -100})
    void rejectsZeroOrNegativeQuantity(int quantity) {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateSubtotal(quantity, BigDecimal.ONE));
    }

    @Test
    void rejectsNullOrNegativeUnitPrice() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateSubtotal(1, null));
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateSubtotal(1, new BigDecimal("-0.01")));
    }

    @Test
    void calculatesDiscountedTotal() {
        assertEquals(new BigDecimal("27.00"),
                calculator.calculateTotal(
                        3, new BigDecimal("10.00"),
                        new DiscountPolicy(new BigDecimal("10"))));
    }

    @Test
    void totalWithZeroDiscountEqualsSubtotal() {
        assertEquals(new BigDecimal("25.50"),
                calculator.calculateTotal(
                        1, new BigDecimal("25.50"),
                        new DiscountPolicy(BigDecimal.ZERO)));
    }

    @Test
    void rejectsMissingDiscountPolicy() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateTotal(1, BigDecimal.ONE, null));
    }
}
