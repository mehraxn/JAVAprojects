# Validation Strategy

## Learning goals

- Separate different validation types.
- Decide where each validation should happen.
- Keep validation predictable.

## Common validation types

| Type | Example |
|---|---|
| Input validation | Name cannot be blank |
| Business rule validation | Account cannot overdraw |
| Existence validation | Product ID must exist |
| Duplicate validation | Course code must be unique |
| Relationship validation | Product can only be assigned to an existing category |

## Input validation

Input validation protects basic method requirements.

```java
private static String requireText(String value, String fieldName) {
    if (value == null || value.trim().isEmpty()) {
        throw new InvalidInputException(fieldName + " is required");
    }
    return value.trim();
}
```

## Business rule validation

Business rules usually belong in domain objects.

```java
public void approve() {
    if (approved) {
        throw new BusinessRuleException("Request is already approved");
    }
    approved = true;
}
```

## Existence and duplicate validation

Services usually ask repositories whether something exists.

```java
if (products.existsBySku(sku)) {
    throw new DuplicateIdException("SKU already exists");
}
```

## Relationship validation

When a workflow connects objects, the service should verify both sides.

```java
Product product = products.findRequired(productId);
Category category = categories.findRequired(categoryId);
product.assignTo(category.id());
```

## Common mistakes

- Checking the same rule in five places.
- Validating after state has already changed.
- Returning `null` for invalid input.
- Using one generic exception for every failure.

## Mini exercise

For creating a course enrollment, list input, existence, duplicate, and relationship validations.

## Quick summary

Validation is easier to maintain when each rule has one clear owner.
