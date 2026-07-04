# Authentication System

## Status

User, password-hashing, session, service, and optional HttpServer skeleton created.

## Planned features

- Register unique users.
- Hash passwords with PBKDF2WithHmacSHA256 and random salts.
- Verify login credentials.
- Create expiring random session tokens.
- Authenticate and revoke sessions.
- Apply simple user/admin roles.

## Current classes

- User: stored authentication identity and role.
- PasswordHasher: standard-Java hashing boundary.
- Session: token and expiration model.
- AuthService: registration, login, authentication, and logout.
- AuthHttpServer: optional local HTTP adapter.
- Main: safe runner that does not start a server.

## Security scope

This is an educational skeleton, not a production authentication system. It must not log or store plain-text passwords.

## Source layout

Source files are under src/authenticationsystem.
