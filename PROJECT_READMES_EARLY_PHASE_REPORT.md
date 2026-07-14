# Early Project README Phase Report

## Scope

Prompt 4 handled the 15 current project folders numbered 01–15 under `Projects/`. The work was limited to project README documentation and this phase report. The final folder order from `REPO_AUDIT_AND_ORDER_PLAN.md` and `REPO_RENAME_EXECUTION_REPORT.md` was used rather than pre-rename project numbers.

## Projects Processed

| # | Project folder | README status | Maven | Tests detected | TEST_RESULTS | Notes |
|---:|---|---|:---:|:---:|:---:|---|
| 01 | `01-simple-calculator` | UPDATED | YES | YES | NO | Replaced the raw lab-handout landing page with a concise project README; Java 25 requirement and unverified test status are explicit. |
| 02 | `02-university-management` | CREATED | YES | YES | YES | Added a root landing README; canonical Maven project remains in `project/`, with `Raw File/` preserved. |
| 03 | `03-hydraulic-network-simulator` | CREATED | YES | YES | YES | Added a root landing README; canonical Maven project remains in `Project/`, with `Raw files/` preserved. |
| 04 | `04-diet-takeaway-management` | CREATED | YES | YES | YES | Added a root landing README; canonical Maven project remains in `Project/`, with `Raw files/` preserved. |
| 05 | `05-student-grade-manager` | UPDATED | NO | YES | YES | Added explicit overview, concepts, tech stack, structure, evidence link, limitations, and resume value. |
| 06 | `06-product-inventory-manager` | UPDATED | NO | YES | YES | Preserved inventory rules and added missing portfolio sections. |
| 07 | `07-bank-account-simulator` | UPDATED | NO | YES | YES | Preserved money/transfer details and added missing portfolio sections. |
| 08 | `08-library-management-system` | UPDATED | NO | YES | YES | Preserved lending rules and added missing portfolio sections. |
| 09 | `09-hotel-room-booking` | UPDATED | NO | YES | YES | Expanded the short README with date rules, structure, commands, evidence, limitations, and resume value. |
| 10 | `10-event-registration-system` | UPDATED | NO | YES | YES | Expanded the short README with capacity/cancellation details and complete portfolio sections. |
| 11 | `11-hospital-queue-management` | UPDATED | NO | YES | YES | Expanded the short README while retaining explicit non-clinical limitations. |
| 12 | `12-restaurant-ordering-system` | UPDATED | NO | YES | YES | Preserved lifecycle and money details and added missing portfolio sections. |
| 13 | `13-quiz-exam-platform` | UPDATED | NO | YES | YES | Preserved attempt/grading behavior and added missing portfolio sections. |
| 14 | `14-parking-garage-system` | UPDATED | NO | YES | YES | Preserved allocation/billing details and added missing portfolio sections. |
| 15 | `15-movie-ticket-booking-system` | UPDATED | NO | YES | YES | Preserved atomic booking behavior and added missing portfolio sections. |

Totals: 3 READMEs created, 12 updated, 0 left as-is, and 0 skipped.

## Important Notes

- Java source code was not modified.
- Tests, Maven POMs, wrappers, scripts, and build configuration were not modified.
- Project folders were not renamed or moved.
- Generated files were not deleted or edited.
- Every README is based on inspected source classes, test layouts, scripts, build files, existing documentation, and available `TEST_RESULTS.md` evidence.
- Projects 02–04 intentionally retain nested canonical and raw course-material trees. Their new root READMEs identify the canonical build location without flattening or rewriting either tree.
- Project 01 contains automated test source, but no `TEST_RESULTS.md` or retained Surefire result establishes a current outcome. Its README therefore describes how tests can be run without claiming they passed.
- The root repository README did not require an update because its statement about project documentation remains accurate.

## Projects Needing Code Cleanup Later

### `01-simple-calculator`

- `pom.xml` targets Java 25, unlike the Java 21 baseline used by many later projects.
- A `target/` directory is retained.
- `TEST_RESULTS.md` is missing, so current validation status is uncertain.
- No reusable Bash or PowerShell test script is present.
- Input parsing and static error state may merit review in a later code-focused prompt; they were not changed here.

### `03-hydraulic-network-simulator`

- A generated `target/` exists inside the preserved `Raw files/` tree. Any cleanup must preserve the distinction between canonical and raw course material.

### `04-diet-takeaway-management`

- A generated `target/` exists inside the preserved `Raw files/` tree. Any cleanup must preserve the distinction between canonical and raw course material.

No project in this phase is missing test source. Projects 02–15 have a recorded `TEST_RESULTS.md`; only project 01 is missing one.
