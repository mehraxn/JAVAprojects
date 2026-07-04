# Authentication System

An educational, in-memory authentication example using standard-Java PBKDF2 password hashing, expiring sessions, logout, and simple USER/ADMIN authorization.

> This project is for learning only. It is not a production authentication system and should not protect real accounts or sensitive data.

## Features

- Register users with generated IDs.
- Prevent duplicate usernames using case-insensitive comparison.
- Validate username format and password strength.
- Hash passwords with `PBKDF2WithHmacSHA256`.
- Generate a different random salt for every user.
- Compare derived hashes using `MessageDigest.isEqual`.
- Log in with username and password.
- Generate random, expiring session tokens.
- Log out by revoking a session token.
- Support USER and ADMIN roles.
- Demonstrate user-protected and admin-protected actions.
- Clear caller-provided password arrays after registration and login attempts.

## Main classes

- `User` — stored ID, username, password hash, salt, and role. Hash/salt getters are package-private.
- `PasswordHasher` — salt generation, PBKDF2 derivation, and constant-time verification.
- `Session` — random token, user ID, and expiration time.
- `AuthService` — registration, login, authentication, logout, and authorization.
- `Main` — local demonstration that does not print passwords, hashes, salts, or tokens.

## How the program works: authentication flow

1. Registration validates a username, strong password, and role.
2. The service checks the normalized username for duplicates.
3. `PasswordHasher` generates a 16-byte random salt.
4. PBKDF2-HMAC-SHA256 derives a 256-bit hash using 120,000 iterations.
5. The user record stores only the encoded salt and hash, never the plain password.
6. Login derives a hash from the submitted password and stored salt.
7. A successful comparison creates a 256-bit URL-safe random session token valid for 30 minutes.
8. Authentication rejects unknown, expired, or logged-out tokens.
9. Role checks allow both roles to perform USER actions, while only ADMIN can perform ADMIN actions.

`register` and `login` overwrite their supplied `char[]` password arrays with null characters before returning, including failure paths. Callers should therefore pass a dedicated mutable array rather than a shared value.

## Password rules

- Between 10 and 128 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one digit
- At least one special character

Usernames must contain 3–30 letters, numbers, dots, underscores, or hyphens.

## Example usage

```text
javac -d out src/authenticationsystem/*.java
java -cp out authenticationsystem.Main
```

PBKDF2-HMAC-SHA256 is provided by modern JDKs. If the runtime provider does not support it, operations report a `GeneralSecurityException`; the project does not fall back to a weaker hash.

## Backend concepts practiced

- Credential registration and verification flow
- Expiring server-side sessions and token revocation
- Role-based authorization checks
- Case-insensitive identity uniqueness and failure-safe password cleanup

## Storage approach

Users and sessions are stored in synchronized in-memory maps. Passwords are represented only by PBKDF2 hashes and per-user salts after registration. Nothing persists across process restarts.

## Limitations

This example has no persistent user store, transport security, rate limiting, account lockout, password reset, email verification, multi-factor authentication, audit log, CSRF protection, secure cookies, secret rotation, breached-password checking, or distributed session management. It also keeps sessions and credentials in process memory. The `register` method accepts a role only to demonstrate both roles locally; a public production registration path must never let users assign themselves the ADMIN role. Production systems should use a reviewed security framework and current organization-specific policies.

## Java concepts practiced

- `SecureRandom`, PBKDF2, Base64, and constant-time comparison
- Character-array cleanup and exception-safe `finally` blocks
- Maps, enums, durations, and instants
- Session lifecycle and role-based authorization
- Validation and defensive copies

## Possible future improvements

- Persistent storage designed for credentials
- Configurable hashing parameters and hash upgrades
- Login throttling and account lockout
- Refresh-token rotation
- Audit logging without sensitive values
- Automated tests with an injectable clock
