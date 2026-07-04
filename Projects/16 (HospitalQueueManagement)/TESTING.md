# Testing Hospital Queue Management

The project has no external test dependencies. Use explicit arrival/current times for deterministic waiting-time checks.

## Manual test cases

1. Add patients at all four triage levels and verify emergency is served first.
2. Add patients with the same priority and verify earlier arrival is served first.
3. Add patients with equal priority and arrival time and verify patient ID breaks the tie.
4. Mark a waiting patient as emergency and verify queue order changes.
5. Update a waiting patient's priority and verify it is requeued correctly.
6. Serve a patient and verify status becomes `IN_TREATMENT` and the patient leaves the waiting queue.
7. Update the served patient to `DISCHARGED` and verify the record remains available.
8. Change a non-waiting patient back to `WAITING` and verify it returns to the queue.
9. Call `serveNextPatient()` on an empty queue; expect `IllegalStateException`.
10. Verify `viewQueue()` does not remove or reorder the actual queue.
11. Calculate average wait for known arrival/current times and verify the result.
12. Calculate average wait for an empty queue; expect `0.0`.
13. Use a current time before an arrival; expect `IllegalArgumentException`.
14. Add a duplicate patient ID or blank/null patient data; expect `IllegalArgumentException`.
15. Update an unknown patient or apply a null status/priority; expect `IllegalArgumentException`.
16. Try changing priority for a non-waiting patient; expect `IllegalStateException`.
17. Try modifying returned queue or record lists; expect `UnsupportedOperationException`.
