# Authorization and Roles

## Learning goals

- Understand authorization in service-layer workflows.
- Learn simple role checks.
- Avoid scattering permission checks everywhere.

## Authentication vs authorization

Authentication asks: who is the user?

Authorization asks: what is this user allowed to do?

This chapter focuses on authorization.

## Example roles

- `ADMIN`
- `MAINTAINER`
- `OPERATOR`
- `VIEWER`

The exact names depend on the application.

## Role check example

```java
public enum Role {
    ADMIN,
    MAINTAINER,
    OPERATOR,
    VIEWER
}

public record UserContext(String userId, Role role) {
}
```

```java
private static void requireAdmin(UserContext user) {
    if (user.role() != Role.ADMIN) {
        throw new UnauthorizedException("Admin role required");
    }
}
```

## Service-layer authorization

```java
public ProductSnapshot deleteProduct(String productId, UserContext user) {
    requireAdmin(user);
    Product product = products.findRequired(productId);
    products.delete(product.id());
    return ProductSnapshot.from(product);
}
```

The check is close to the workflow, not scattered across every UI command.

## Permission matrix

| Operation | ADMIN | MAINTAINER | OPERATOR | VIEWER |
|---|---:|---:|---:|---:|
| View report | yes | yes | yes | yes |
| Create product | yes | yes | yes | no |
| Approve request | yes | yes | no | no |
| Delete product | yes | no | no | no |

## Common mistakes

- Only hiding UI buttons but not checking service permissions.
- Copy-pasting role checks into every class.
- Using strings for roles without validation.
- Forgetting tests for unauthorized paths.

## Mini exercise

Create a permission table for an order application with `create`, `cancel`, `approve`, and `view` operations.

## Quick summary

Authorization belongs near the workflow. A user should not be able to bypass rules by calling a different entry point.
