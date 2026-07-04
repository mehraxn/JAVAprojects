# Testing Quiz Exam Platform

## Testing approach

Use small quizzes with known answers. Remember that question and option indexes are zero-based.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Create question | Use unique prompt/options and valid answer | Question is created |
| Build quiz | Add several unique questions | Question count matches |
| Start attempt | Start populated quiz | Open QuizAttempt is returned |
| Record answers | Answer every question | Answer count matches quiz size |
| Finish | Complete attempt | QuizResult is created |
| Scoreboard | Record several results | Highest percentage appears first |

## Edge-case test cases

| Test | Action | Expected result |
|---|---|---|
| Change answer | Record same question twice before finish | Latest answer is graded |
| Passing boundary | Produce exactly 60% | Result is PASS |
| Empty quiz | Start before adding questions | IllegalStateException |
| Incomplete attempt | Finish before all answers | IllegalStateException; attempt remains open |
| Duplicate prompt | Add prompt with different letter case | IllegalArgumentException |
| Score tie | Record equal percentages | Participant name orders the tie |

## Invalid input test cases

| Test | Action | Expected result |
|---|---|---|
| Invalid options | Use fewer than two, blank, or duplicate options | IllegalArgumentException |
| Invalid correct index | Use index outside options | IllegalArgumentException |
| Invalid answer index | Use unknown question/option index | IllegalArgumentException |
| Invalid answer list | Use wrong size, null, or null elements | IllegalArgumentException |
| Repeat finish/edit | Finish or answer after completion | IllegalStateException |
| Invalid score | Record below 0 or above 100 | IllegalArgumentException |
| Blank participant/title | Use empty identity text | IllegalArgumentException |

## Expected results

Each graded answer must show the selected and correct option. Final counts, percentage, pass/fail status, and scoreboard entry must agree.

## Manual testing checklist

- [ ] Compile and run Main.
- [ ] Verify zero-based option indexing.
- [ ] Test all-correct, all-incorrect, and mixed results.
- [ ] Verify selected and correct option text.
- [ ] Verify incomplete attempts can still be corrected.
- [ ] Verify pass/fail around 60%.
- [ ] Verify returned options, questions, and results cannot be modified.
