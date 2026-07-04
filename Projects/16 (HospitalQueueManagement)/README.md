# Hospital Queue Management

An in-memory Java application for triage priority, patient service, and simple records.

## Implemented features

- Add validated patients with unique IDs, arrival times, and triage levels.
- Order waiting patients with a `PriorityQueue`.
- Serve emergency patients first, followed by urgent, standard, and non-urgent patients.
- Resolve equal priority by arrival time and then patient ID.
- Update the priority of waiting patients, including emergency override.
- Show the waiting queue without changing it.
- Update status between waiting, in treatment, and discharged.
- Retain patient records after they leave the waiting queue.
- Calculate average waiting time, returning `0.0` for an empty queue.

## Structure

- `Patient` stores identity, priority, arrival time, and status.
- `TriageLevel` defines explicit service priority.
- `PatientStatus` defines the patient workflow.
- `TriageQueue` owns the priority queue and patient records.
- `Main` demonstrates priority update, service, and discharge.

Source files are under `src/hospitalqueuemanagement` and use only standard Java.

## Run

```powershell
javac -d out src\hospitalqueuemanagement\*.java
java -cp out hospitalqueuemanagement.Main
```

See `TESTING.md` for manual test cases.
