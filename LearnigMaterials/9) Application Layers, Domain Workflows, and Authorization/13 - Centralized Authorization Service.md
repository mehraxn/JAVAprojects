# Centralized Authorization Service

## Learning goals

- Centralize authorization decisions.
- Use roles and permission checks clearly.
- Avoid scattered role logic.

## Why centralize authorization?

If every service writes its own role checks, rules become inconsistent. A central authorization service makes permission decisions easier to review and test.

## Role and user objects

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

## Authorization service

```java
public final class AuthorizationService {
    public void requireRole(UserContext user, Role required) {
        if (user.role() != required) {
            throw new UnauthorizedException(required + " role required");
        }
    }

    public void requireAnyRole(UserContext user, Role... allowedRoles) {
        for (Role role : allowedRoles) {
            if (user.role() == role) {
                return;
            }
        }
        throw new UnauthorizedException("User is not allowed to perform this operation");
    }
}
```

## Permission matrix

| Operation | ADMIN | MAINTAINER | OPERATOR | VIEWER |
|---|---:|---:|---:|---:|
| View report | yes | yes | yes | yes |
| Create order | yes | yes | yes | no |
| Approve request | yes | yes | no | no |
| Delete product | yes | no | no | no |

## Service usage

```java
public RequestSnapshot approve(String requestId, UserContext user) {
    authorization.requireAnyRole(user, Role.ADMIN, Role.MAINTAINER);
    Request request = requests.findRequired(requestId);
    request.approve(user.userId());
    requests.save(request);
    return RequestSnapshot.from(request);
}
```

## Common mistakes

- Checking permissions only in UI.
- Copy-pasting role checks everywhere.
- Using unvalidated role strings.
- Forgetting unauthorized test cases.

## Mini exercises

1. Add `requireAnyRole` to an authorization service.
2. Create a permission matrix for invoice approval.
3. Test that a viewer cannot delete a product.

## Quick summary

Centralized authorization keeps protected workflows consistent and testable.
