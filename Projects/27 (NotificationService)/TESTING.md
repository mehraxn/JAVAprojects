# Notification Service Testing

All senders used for these tests must be `MockNotificationSender` or another local test implementation. Do not connect real delivery services.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Register senders | Register EMAIL, SMS, and APP mocks | All types are available |
| Enqueue | Add three notifications | IDs `N-1` through `N-3`; all status `QUEUED` |
| FIFO processing | Process three queued items | Delivery order matches enqueue order |
| Successful send | Process using non-failing mock | Status `SENT`, attempts `1`, and sent time recorded |
| Queue inspection | View before processing | Ordered copies returned without consuming queue |
| History | Process several notifications | All appear in creation order with final states |
| Lookup | Find known notification ID | Matching snapshot returned |

## Failure and retry tests

| Test | Action | Expected result |
|---|---|---|
| Simulated failure | Mock configured to fail once | Status `FAILED`, attempts `1`, error recorded |
| Successful retry | Retry with maximum 3, then process | Status `SENT`, attempts `2` |
| Retry limit reached | Failure already has attempts equal to maximum | Returns `false`; item stays failed |
| Missing sender | Queue type with no registered sender | Status `FAILED` with missing-sender error |
| Retry missing sender | Requeue without adding sender | Another failed attempt is recorded |
| Retry sent item | Retry already sent notification | `IllegalStateException` |
| Retry queued item | Retry item still in queue | `IllegalStateException` |
| Unknown retry ID | Retry absent valid ID | Returns `false` |
| Duplicate retry prevention | Retry failed item twice before processing | Second call rejected because status is queued |

## Edge-case and invalid input test cases

| Test | Input or action | Expected result |
|---|---|---|
| Empty queue | Process next | Returns `null` |
| Blank recipient | Null, empty, or whitespace | Rejected |
| Blank message | Null, empty, or whitespace | Rejected |
| Null type | Enqueue without type | Rejected |
| Duplicate sender | Register second sender for same type | Rejected |
| Null sender | Register null | Rejected |
| Negative mock failures | Construct sender with negative count | Rejected |
| Invalid retry maximum | Zero or negative | Rejected |
| Blank lookup ID | Blank or null ID | Rejected |
| Unknown lookup | Find absent valid ID | Returns `null` |
| Sender type mismatch | Deliver notification through wrong mock directly | Rejected |
| Attempt overflow | Begin attempt at integer maximum | `IllegalStateException` |
| Returned list mutation | Modify queue/history list | `UnsupportedOperationException` |

## Manual testing checklist

- [ ] Compile all source files.
- [ ] Run `Main` and confirm the SMS mock fails once then succeeds.
- [ ] Queue EMAIL, SMS, and APP messages and verify FIFO order.
- [ ] Inspect queue before and after processing.
- [ ] Confirm attempt counts and timestamps.
- [ ] Test missing and duplicate sender registration.
- [ ] Test retry success, retry limits, and invalid retry states.
- [ ] Inspect history and confirm failed notifications are retained.
- [ ] Confirm output is explicitly labeled MOCK and no external service is contacted.

## Phase 2 validation review additions

| Test | Action | Expected result |
|---|---|---|
| Invalid EMAIL recipient | Queue missing/invalid `@` or domain | Rejected before entering queue/history |
| Invalid SMS recipient | Queue letters or unsupported phone characters | Rejected before entering queue/history |
| Oversized APP recipient | Use more than 100 characters | Rejected |
| Oversized message | Use more than 5000 characters | Rejected |
| Retry after failed validation | Inspect queue/history after rejected enqueue | Both collections remain unchanged |
