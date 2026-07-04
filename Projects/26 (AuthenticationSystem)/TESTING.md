# Testing Authentication System

## Planned service tests

- Register and log in a user.
- Reject duplicate usernames.
- Authenticate valid, expired, and revoked sessions.
- Verify user and admin roles.

## Planned security tests

- Verify identical passwords use different salts.
- Verify wrong passwords fail.
- Verify plain-text passwords are never stored or printed.
- Verify session tokens are unpredictable and expire.

## Planned validation tests

- Reject blank usernames and weak/empty passwords.
- Reject null roles and tokens.
- Reject unknown sessions without revealing user details.

## Manual checklist

- [ ] Implement hashing with standard Java security APIs.
- [ ] Clear temporary password arrays after use.
- [ ] Implement session expiration.
- [ ] Avoid detailed login-failure information.
- [ ] Treat HttpServer endpoints as local educational demonstrations only.
