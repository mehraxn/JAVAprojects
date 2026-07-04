# Hospital Queue Management

## Description

Hospital Queue Management is an in-memory Java project for triage priority, patient service order, status updates, and simple patient records.

## Features

- Add patients with unique IDs and arrival times.
- Assign emergency, urgent, standard, or non-urgent priority.
- Serve patients using a PriorityQueue.
- Break equal-priority ties by arrival time and patient ID.
- Update priority for waiting patients.
- Apply an emergency override.
- Track waiting, in-treatment, and discharged status.
- Retain records after service.
- Calculate average waiting time.

## Java concepts practiced

- PriorityQueue and Comparator
- Map and List collections
- Enums and state transitions
- LocalDateTime and Duration
- Defensive queue snapshots and validation

## Main classes

- Patient: stores identity, triage level, arrival, and status.
- TriageLevel: defines service priority.
- PatientStatus: defines the patient workflow.
- TriageQueue: manages queue ordering and patient records.
- Main: demonstrates priority update, service, and discharge.

## How the program works

TriageQueue stores every patient record and separately keeps waiting patients in priority order. Serving removes the highest-priority patient and marks them in treatment. Discharged is terminal; in-treatment patients may be returned to waiting if needed.

## Example usage

~~~powershell
javac -d out src\hospitalqueuemanagement\*.java
java -cp out hospitalqueuemanagement.Main
~~~

The demo applies an emergency override, prints queue order, serves one patient, and records discharge.

## Possible future improvements

- Record treatment start and discharge times.
- Add departments or doctors.
- Add visit notes.
- Report served-patient statistics.
- Save patient records securely to a file.
