# Testing Hospital Queue Management

## Testing approach

Use fixed arrival and current times for deterministic ordering and waiting-time results.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Add patients | Add unique patient IDs | Records and queue contain patients |
| Priority order | Add all triage levels | Emergency is served first |
| Arrival tie-break | Add equal priority at different times | Earlier arrival is first |
| Emergency override | Upgrade waiting patient | Patient moves to queue front |
| Serve | Serve next patient | Patient leaves queue and enters treatment |
| Discharge | Update treated patient | Status becomes discharged; record remains |

## Edge-case test cases

| Test | Action | Expected result |
|---|---|---|
| Empty queue | Calculate average waiting time | 0.0 |
| Equal time/priority | Add different IDs | ID determines order |
| Shared name | Add different IDs with same name | Both records are accepted |
| Requeue treatment | Change in-treatment patient to waiting | Patient returns in priority order |
| Queue snapshot | Call viewQueue repeatedly | Real queue remains unchanged |
| Same status | Apply current status again | No state change |

## Invalid input test cases

| Test | Action | Expected result |
|---|---|---|
| Serve empty | Call serveNextPatient with no waiting patients | IllegalStateException |
| Duplicate ID | Add same patient ID | IllegalArgumentException |
| Invalid data | Use blank identity, null priority, or null time | IllegalArgumentException |
| Invalid clock | Current time before patient arrival | IllegalArgumentException |
| Invalid priority update | Change non-waiting patient priority | IllegalStateException |
| Terminal status | Move discharged patient to another status | IllegalStateException |
| Unknown patient | Update missing patient ID | IllegalArgumentException |

## Expected results

Queue order must follow priority, arrival, and ID. Status and queue membership must remain consistent after both accepted and rejected transitions.

## Manual testing checklist

- [ ] Compile and run Main.
- [ ] Verify all four priority levels.
- [ ] Verify both tie-break rules.
- [ ] Verify emergency override reorders the queue.
- [ ] Verify serving changes status and preserves the record.
- [ ] Verify rejected transitions preserve status and queue membership.
- [ ] Verify returned queue and record lists cannot be modified.
