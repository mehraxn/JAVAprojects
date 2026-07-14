# Authentication System

An educational, local Java authentication system: registration, PBKDF2 password hashing, expiring sessions, and USER/ADMIN authorization — dependency-free and fully covered by automated tests.

> This project is for learning only. It is not a production authentication system, not a replacement for Spring Security or a real identity provider, and should never protect real accounts or sensitive data. Every password in the docs and demos is a throwaway local example.

## What it demonstrates

- User registration with username and password-policy validation
- PBKDF2 password hashing (`PBKDF2WithHmacSHA256`, 120,000 iterations, 256-bit keys)
- A fresh random salt per user
- Constant-time hash comparison (`MessageDigest.isEqual`)
- Password `char[]` cleanup — the service zeroes every password array it receives, even when the call fails
- Login/logout with random, URL-safe, expiring session tokens (32 random bytes)
- Session revocation and expired-session cleanup
- Role-based authorization (USER/ADMIN)
- An injectable `java.time.Clock`, so session expiry is tested deterministically with no `Thread.sleep`
- Safe public views: the API returns `PublicUser` (id, username, role) and `SessionInfo` (token, user, expiry) — password hashes and salts never leave the service
- Educational timing hardening: unknown usernames still run one dummy PBKDF2 verification, so login failure timing is similar whether or not the username exists (documented as a learning exercise, not full side-channel resistance)
- Dependency-free automated tests (46 tests, 139 checks) and strict compilation (`javac -Xlint:all -Werror`)

## Features

- Register normal USER accounts — public registration can never create an ADMIN
- Seed an ADMIN for local demos via the clearly named `seedAdminForDemo` (this is not a production admin-management system)
- Login with PBKDF2 verification; wrong password and unknown username are indistinguishable to the caller
- Logout / revoke sessions; expired and revoked tokens cannot authorize
- Authorize USER and ADMIN actions (`canAccess`, `performUserAction`, `performAdminAction`)
- Reject weak passwords (see policy below) and duplicate usernames case-insensitively

## Password policy

10–128 characters with at least one uppercase letter, one lowercase letter, one digit, and one symbol. Example of an accepted **local demo** password: `CorrectHorse1!`

## Quick start

```text
javac -Xlint:all -Werror -d out src/authenticationsystem/*.java

java -cp out authenticationsystem.Main help
java -cp out authenticationsystem.Main demo
java -cp out authenticationsystem.Main register-demo
java -cp out authenticationsystem.Main login-demo
java -cp out authenticationsystem.Main authorization-demo
java -cp out authenticationsystem.Main expiry-demo
```

The demos print only masked tokens (first characters + `...`) and never print passwords, hashes, or salts. The expiry demo uses the injected Clock, so it finishes instantly — no waiting.

## Project structure

```text
src/authenticationsystem/     AuthService, PasswordHasher, PasswordPolicy,
                              User, Session, PublicUser, SessionInfo, Main
tests/authenticationsystem/   Dependency-free tests, MutableClock, TestRunner
scripts/test.sh, test.ps1     One-command validation (clean, compile, test, demos)
README.md, TESTING.md         Documentation
TEST_RESULTS.md               Actual recorded validation results
```

## Authorization model

- Every authenticated session passes a USER-level check.
- Only ADMIN sessions pass an ADMIN-level check (so ADMIN can also do USER-level actions).
- Invalid, expired, and logged-out tokens fail every check; `performUserAction`/`performAdminAction` throw `SecurityException` in those cases.

## How to test

- `TESTING.md` — exact commands for strict compile, the test runner, and the CLI demos.
- `TEST_RESULTS.md` — the honest record of the validation actually performed.
- Quick version: `./scripts/test.sh` (Linux/macOS/Git Bash) or `.\scripts\test.ps1` (Windows PowerShell).

## Limitations

- Educational only — in-memory users and sessions, nothing persisted
- No database, no HTTP API
- No MFA, no password reset flow, no account recovery
- No rate limiting or account lockout
- No real audit logging, no compliance guarantee
- Timing hardening is a learning exercise, not full side-channel resistance

## Possible future improvements

- Account lockout after repeated failures
- Password reset flow with expiring reset tokens
- Rate limiting per username/IP
- Pluggable storage behind an interface
