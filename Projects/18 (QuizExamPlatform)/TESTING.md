# Testing Quiz Exam Platform

The project has no external test dependencies. Compile and run `Main`, or call the classes from a small Java driver.

## Manual test cases

1. Create a question with several unique options and a valid correct index.
2. Try fewer than two options, duplicate options, blank text, or an invalid correct index; expect `IllegalArgumentException`.
3. Add questions to a quiz and verify the question count and read-only question list.
4. Start a quiz with a participant name and record one answer for every question.
5. Change an answer before finishing and verify the latest answer is graded.
6. Try an invalid question or option index; expect `IllegalArgumentException`.
7. Finish with unanswered questions; expect `IllegalStateException`.
8. Start a quiz with no questions; expect `IllegalStateException`.
9. Finish a fully answered quiz and verify correct count and percentage.
10. Verify every answer result shows selected option, correct option, and correctness.
11. Verify 60% or more passes and below 60% fails.
12. Try recording another answer or finishing again after completion; expect `IllegalStateException`.
13. Call `getResult()` before finishing; expect `IllegalStateException`.
14. Record several scoreboard results and verify descending score order.
15. Record an invalid scoreboard percentage or blank participant; expect `IllegalArgumentException`.
16. Try modifying returned option, question, result, or summary lists; expect `UnsupportedOperationException`.
