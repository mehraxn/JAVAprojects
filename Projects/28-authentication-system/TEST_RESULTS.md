# Test Results

Date: 2026-07-11

All commands below were actually executed on a Windows 11 machine from this project directory. No system-wide JDK was installed, so validation used a portable Eclipse Temurin JDK 21.0.11 (`javac`/`java` only — the project needs no build tool or libraries). Nothing in this file is estimated or assumed. Every password involved is a throwaway local demo value; no real credential exists anywhere in this project.

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK Temurin 21.0.11+10 (portable; no system JDK installed) |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/authenticationsystem/*.java` — clean |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/authenticationsystem/*.java` — clean |
| Automated tests | PASS | `TestRunner`: 46 tests passed, 0 failed, 139 assertion checks total, exit code 0 |
| Main demo | PASS | Register USER, seed demo ADMIN, login both, USER blocked from ADMIN action, ADMIN action succeeds, logout kills the token; only masked tokens printed; exit code 0 |
| Register/login demos | PASS | Valid + duplicate + weak-password registration; correct/wrong/unknown login; exit code 0 each |
| Authorization demo | PASS | Full role matrix: USER→USER true, USER→ADMIN false, ADMIN→USER true, ADMIN→ADMIN true, invalid token false; exit code 0 |
| Expiry demo | PASS | Injected Clock: session valid at 10:00 and 10:29, rejected and removed at 10:31 — instant, no sleeps; exit code 0 |
| Password cleanup tests | PASS | char[] verified zeroed after successful and failed registration and login |
| Hashing/salt tests | PASS | PBKDF2 deterministic per salt, unique salts, different hashes for same password, hash never contains raw password, constant-time verify accepts/rejects correctly |
| Session tests | PASS | Valid strictly before expiry, expired exactly at the deadline, logout revokes once, expired/revoked/invalid tokens cannot authorize, `removeExpiredSessions` clears only expired ones |
| Safe-view tests | PASS | Reflection check: `PublicUser` and `SessionInfo` declare no method whose name mentions password, hash, or salt; demo output contains no raw password or full token |
| CLI error handling | PASS | Unknown command and missing command print a clean stderr message and exit 1 (verified at the real process level too) |
| scripts/test.ps1 | PASS | Full pipeline (clean, strict compiles, 46 tests, 3 demos), exit code 0 |
| scripts/test.sh | PASS | Run via Git Bash on Windows; picks the `;` classpath separator automatically, exit code 0 |

Test breakdown: 9 `PasswordPolicy`, 5 `PasswordHasher`, 4 `User`, 4 `Session`, 17 `AuthService` (using the tests' `MutableClock` for expiry), 7 CLI smoke tests (calling `Main.run` directly with captured streams).

## Known limitations

- Educational local authentication system only.
- In-memory user/session storage.
- No database.
- No HTTP API.
- No MFA.
- No account lockout or rate limiting.
- No password reset flow.
- No real audit logging.
- Timing hardening for unknown usernames is educational, not full side-channel resistance.
- Not a production authentication provider.
