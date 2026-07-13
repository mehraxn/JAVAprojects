# Factory Pattern

## Learning goals

- Understand why object creation sometimes deserves its own method or class.
- Learn static factory methods and simple factory classes.
- Recognize factory trade-offs.

## Why factories exist

Constructors are useful, but sometimes object creation needs validation, defaults, or a decision about which subtype to create. A factory centralizes that creation logic.

## Static factory method

```java
public final class Money {
    private final String currency;
    private final int cents;

    private Money(String currency, int cents) {
        this.currency = currency;
        this.cents = cents;
    }

    public static Money euros(int cents) {
        return new Money("EUR", cents);
    }

    public static Money dollars(int cents) {
        return new Money("USD", cents);
    }
}
```

The method names explain intent better than overloaded constructors.

## Simple factory class

```java
public final class DiscountPolicyFactory {
    public DiscountPolicy create(String customerType) {
        if ("VIP".equalsIgnoreCase(customerType)) {
            return new PercentageDiscount(15);
        }
        return new NoDiscount();
    }
}
```

The service can ask the factory for a policy without knowing every implementation.

## Benefits

- Creation rules live in one place.
- Constructors can stay simple.
- Tests can create consistent objects.
- Names can explain intent, such as `newDraftOrder` or `fromCsvRow`.

## Trade-offs

- Too many factories can make a small program harder to read.
- A factory with many `if` statements may need refactoring later.
- Factories should create objects, not perform complete workflows.

## Common mistakes

- Calling a class `Factory` when it performs business operations.
- Hiding invalid defaults inside factory methods.
- Making every object require a factory.

## Mini exercise

Create a `ProductFactory` with methods `standardProduct` and `discountedProduct`. Decide which validation belongs in the factory and which belongs in `Product`.

## Quick summary

Factories are useful when object creation has meaningful rules, names, or subtype decisions.
