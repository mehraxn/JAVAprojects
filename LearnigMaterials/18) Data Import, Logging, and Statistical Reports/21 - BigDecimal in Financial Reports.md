# BigDecimal in Financial Reports

## Learning goals

- Know when to use `BigDecimal`.
- Avoid `double` for money.
- Use scale and rounding mode.

## Why not double for money?

Floating-point numbers can represent decimal values imprecisely.

```java
System.out.println(0.1 + 0.2); // may not print exactly 0.3
```

For financial reports, use `BigDecimal`.

## BigDecimal example

```java
BigDecimal price = new BigDecimal("19.99");
BigDecimal quantity = new BigDecimal("3");
BigDecimal total = price.multiply(quantity);
```

Use string constructors for exact decimal input.

## Rounding

```java
BigDecimal average = total.divide(
        BigDecimal.valueOf(count),
        2,
        RoundingMode.HALF_UP);
```

## Scale

Scale is the number of digits after the decimal point. Money commonly uses scale 2, but currencies and business rules differ.

## Common mistakes

- Using `new BigDecimal(0.1)`.
- Dividing without specifying rounding.
- Mixing `double` calculations into money totals.
- Hiding rounding rules.

## Mini exercises

1. Calculate invoice total with `BigDecimal`.
2. Calculate average invoice value with rounding.
3. Explain why report formulas should document scale.

## Quick summary

Use `BigDecimal` for financial reports and always document rounding behavior.
