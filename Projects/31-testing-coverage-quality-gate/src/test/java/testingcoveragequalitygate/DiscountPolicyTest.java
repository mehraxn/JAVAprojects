package testingcoveragequalitygate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class DiscountPolicyTest {

    @ParameterizedTest(name = "{0}% off {1} = {2}")
    @CsvSource({
            "0,    20.00,  20.00",   // boundary: no discount
            "10,   100.00, 90.00",
            "12.5, 80.00,  70.00",   // fractional percentage
            "25,   19.99,  14.99",   // 14.9925 rounds to 14.99
            "100,  20.00,  0.00",    // boundary: full discount
            "50,   0.00,   0.00"     // zero subtotal stays zero
    })
    void appliesPercentageDiscountWithHalfUpRounding(
            BigDecimal percentage, BigDecimal subtotal, BigDecimal expected) {
        DiscountPolicy policy = new DiscountPolicy(percentage);
        assertEquals(expected, policy.applyTo(subtotal));
    }

    @Test
    void roundsHalfCentUp() {
        // 15% off 0.10 = 0.085, which must round up to 0.09
        DiscountPolicy policy = new DiscountPolicy(new BigDecimal("15"));
        assertEquals(new BigDecimal("0.09"), policy.applyTo(new BigDecimal("0.10")));
    }

    @ParameterizedTest(name = "percentage {0} is rejected")
    @ValueSource(strings = {"-1", "-0.01", "100.01", "101"})
    void rejectsPercentagesOutsideZeroToHundred(String percentage) {
        assertThrows(IllegalArgumentException.class,
                () -> new DiscountPolicy(new BigDecimal(percentage)));
    }

    @Test
    void rejectsNullPercentage() {
        assertThrows(IllegalArgumentException.class,
                () -> new DiscountPolicy(null));
    }

    @Test
    void rejectsNullOrNegativeSubtotal() {
        DiscountPolicy policy = new DiscountPolicy(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> policy.applyTo(null));
        assertThrows(IllegalArgumentException.class,
                () -> policy.applyTo(new BigDecimal("-0.01")));
    }

    // getPercentage() is deliberately left without a dedicated test. A test that
    // only asserts a getter returns its constructor argument adds coverage but no
    // real verification, and the uncovered accessor keeps measured coverage below
    // 100% so the negative quality-gate demonstration (-Dcoverage.minimum=0.99)
    // can fail meaningfully. See docs/QUALITY_GATE.md.
}
