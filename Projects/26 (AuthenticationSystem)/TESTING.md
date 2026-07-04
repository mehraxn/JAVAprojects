# Authentication System Testing

These are educational service tests. Never use real passwords while testing this project.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Register USER | Register valid username/password | User gets ID `U-1` and USER role |
| Register ADMIN | Register another valid account | User gets ADMIN role |
| Login | Submit correct credentials | Non-null session with future expiration |
| Authenticate | Authenticate a current token | Defensive user copy returned |
| Logout | Logout a current token | Returns `true`; authentication then returns `null` |
| USER action | Use a valid USER token | User-protected action succeeds |
| ADMIN action | Use a valid ADMIN token | Admin-protected action succeeds |
| Admin as user | Use ADMIN token for USER action | Succeeds |

## Password, edge-case, and invalid input test cases

| Test | Input or action | Expected result |
|---|---|---|
| Duplicate username | Register `Alice` then `alice` | Second registration rejected |
| Invalid username | Too short, spaces, or unsupported symbols | Rejected |
| Short password | Fewer than 10 characters | Rejected |
| Excessive password | More than 128 characters | Rejected |
| Missing character class | No uppercase, lowercase, digit, or special character | Rejected |
| Null role | Register without role | Rejected |
| Different salts | Register two users with identical passwords | Stored salts and hashes differ |
| Wrong password | Login with incorrect password | Returns `null` |
| Unknown username | Login unknown account | Returns `null` |
| Password cleanup | Inspect caller array after register/login | Every character is `\0` |
| No sensitive output | Run demonstration | No password, salt, hash, or token is printed |

## Session and authorization tests

| Test | Action | Expected result |
|---|---|---|
| Unknown token | Authenticate unused token | Returns `null` |
| Blank token | Authenticate/logout blank token | Rejected |
| Expired session | Use short configured duration and wait past expiry | Authentication returns `null` and removes session |
| Repeated logout | Logout same token twice | `true`, then `false` |
| USER admin attempt | Call admin action with USER token | `SecurityException` |
| Logged-out action | Call protected action after logout | `SecurityException` |
| Null required role | Call `canAccess` with null role | Rejected |
| Invalid session duration | Zero, negative, or null duration | Rejected |
| Random tokens | Log in repeatedly | Tokens are nonempty and distinct |

## Hashing tests

| Test | Action | Expected result |
|---|---|---|
| Correct verification | Hash then verify same password | `true` |
| Incorrect verification | Verify different password | `false` |
| Invalid salt | Use malformed Base64 salt | Rejected |
| Invalid expected hash | Use malformed Base64 hash | Rejected |
| Missing algorithm | Run on provider without PBKDF2-HMAC-SHA256 | `GeneralSecurityException`; no weak fallback |

## Manual testing checklist

- [ ] Compile all source files.
- [ ] Run `Main` and verify role and logout output.
- [ ] Register USER and ADMIN accounts.
- [ ] Test every password-strength rule separately.
- [ ] Test case-insensitive duplicate usernames.
- [ ] Test correct, incorrect, unknown, expired, and revoked authentication.
- [ ] Verify USER cannot perform the ADMIN action.
- [ ] Verify input password arrays are cleared on success and failure.
- [ ] Confirm no sensitive values appear in logs or output.

## Phase 2 validation review additions

| Test | Action | Expected result |
|---|---|---|
| Oversized login password | Submit more than 128 characters | Rejected and supplied password array is cleared |
| Unauthorized USER | Call admin action with a valid USER session | `SecurityException`; session remains valid for USER actions |
| Unknown or expired role check | Call `canAccess` | Returns `false`, never grants access |
