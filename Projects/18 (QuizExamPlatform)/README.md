# Quiz Exam Platform

## Description

Quiz Exam Platform is an in-memory Java project for building multiple-choice quizzes, recording answers, grading attempts, and presenting results.

## Features

- Create questions with unique prompts and at least two options.
- Reject duplicate option text.
- Mark one valid option as correct.
- Build a quiz and start participant attempts.
- Record or replace answers before completion.
- Require every question to be answered.
- Show selected, correct, and incorrect answers.
- Calculate correct count, percentage, and pass/fail result.
- Record percentages in a sorted scoreboard.

## Java concepts practiced

- Classes and object composition
- List, Map, and Set collections
- Encapsulation of attempt state
- Validation of indexes and collection sizes
- Sorting and unmodifiable result data

## Main classes

- Question: stores prompt, answer options, and correct option.
- Quiz: owns the question set and starts attempts.
- QuizAttempt: records participant answers.
- AnswerResult: describes one graded response.
- QuizResult: calculates score, percentage, and final result.
- ScoreBoard: stores participant percentages.
- Main: demonstrates a complete quiz.

## How the program works

Questions use zero-based option indexes. Quiz starts a QuizAttempt, which accepts one answer per question until finish is called. Finishing requires a complete answer set and creates immutable answer details and a final pass/fail summary. The passing percentage is 60.

## Example usage

~~~powershell
javac -d out src\quizexamplatform\*.java
java -cp out quizexamplatform.Main
~~~

The demo answers three questions, prints correct/incorrect detail, shows the final result, and updates a scoreboard.

## Possible future improvements

- Add question categories and difficulty.
- Randomize question and option order.
- Add configurable passing percentages.
- Add timed attempts.
- Save result summaries to a file.
