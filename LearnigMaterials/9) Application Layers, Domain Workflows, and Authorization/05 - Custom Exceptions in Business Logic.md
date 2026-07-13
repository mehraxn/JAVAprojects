# Custom Exceptions in Business Logic

## Learning goals

- Understand why custom exceptions can make workflows clearer.
- Learn common business exception types.
- Avoid overusing exceptions for normal decisions.

## Why custom exceptions?

`IllegalArgumentException` is useful, but larger applications often need more precise failures.

Examples:

- `InvalidInputException`
- `NotFoundException`
- `DuplicateIdException`
- `UnauthorizedException`
- `BusinessRuleException`

## Simple exception hierarchy

```java
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

public final class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(message);
    }
}
```

## Usage example

```java
public Product findRequired(String productId) {
    return products.get(productId)
            .orElseThrow(() -> new NotFoundException("Product not found: " + productId));
}
```

## When to use each type

| Exception | Use when |
|---|---|
| `InvalidInputException` | Input is null, blank, malformed, or out of range |
| `NotFoundException` | Required object does not exist |
| `DuplicateIdException` | A unique value already exists |
| `UnauthorizedException` | User lacks permission |
| `BusinessRuleException` | Operation violates a domain rule |

## Common mistakes

- Creating dozens of exception classes before they add clarity.
- Catching exceptions and ignoring them.
- Showing raw internal exception messages to users.
- Using exceptions for simple `if` decisions inside loops.

## Mini exercise

Create custom exceptions for a library borrowing workflow. Which exception should represent an already borrowed item?

## Quick summary

Custom exceptions make failure paths readable when they describe real business outcomes.
