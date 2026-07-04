# Testing Event Registration System

## Testing approach

Create events with small capacities so capacity and cancellation behavior can be checked directly.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Create event | Add valid unique event | Event appears in listEvents |
| Register | Add participant below capacity | Registration and participant are stored |
| Fill event | Register exactly capacity participants | Available places become zero |
| Cancel | Cancel registered attendee | Participant is removed and place is restored |
| Search name | Use partial name | Matching events are returned |
| Search date/category | Use exact date or partial category | Correct sorted results are returned |

## Edge-case test cases

| Test | Action | Expected result |
|---|---|---|
| Empty system | Run all valid searches | Empty unmodifiable lists |
| Shared attendee | Register same ID in different events | Both registrations succeed |
| Duplicate names | Register distinct IDs with same name | Both participants are accepted |
| Capacity boundary | Add exactly the final place | Registration succeeds |
| Full event | Add one participant beyond capacity | State remains unchanged |
| Search case | Change name/category letter case | Same events are returned |

## Invalid input test cases

| Test | Action | Expected result |
|---|---|---|
| Invalid capacity | Use zero or negative capacity | IllegalArgumentException |
| Duplicate event | Reuse ID or name/date/category definition | IllegalArgumentException |
| Duplicate participant | Register same attendee twice in one event | IllegalArgumentException |
| Invalid attendee | Use blank fields or email without @ | IllegalArgumentException |
| Null date | Create/search with null date | IllegalArgumentException |
| Unknown event/cancellation | Use missing IDs | IllegalArgumentException |
| Full registration | Register beyond capacity | IllegalStateException |

## Manual testing checklist

- [ ] Compile and run Main.
- [ ] Fill an event exactly to capacity.
- [ ] Verify the next registration is rejected.
- [ ] Verify cancellation restores one place.
- [ ] Test all three search modes.
- [ ] Verify search order is date then name.
- [ ] Verify returned lists cannot be modified.
