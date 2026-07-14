package testingcoveragequalitygate;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        PriceCalculator calculator = new PriceCalculator();
        DiscountPolicy policy = new DiscountPolicy(new BigDecimal("10"));
        BigDecimal total = calculator.calculateTotal(2, new BigDecimal("25.00"), policy);
        System.out.println("Example total after discount: " + total);
    }
}
