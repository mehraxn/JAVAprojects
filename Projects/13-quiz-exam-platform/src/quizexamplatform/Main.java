package quizexamplatform;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        System.exit(exitCode);
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        String command = args.length == 0 ? "help" : args[0].toLowerCase(Locale.ROOT);
        switch (command) {
            case "help":
                printUsage(out);
                return 0;
            case "demo":
                runDemo(out);
                return 0;
            case "attempt-demo":
                runAttemptDemo(out);
                return 0;
            case "validation-demo":
                runValidationDemo(out);
                return 0;
            case "scoreboard-demo":
                runScoreboardDemo(out);
                return 0;
            default:
                err.println("Unknown command: " + args[0]);
                printUsage(err);
                return 1;
        }
    }

    private static void printUsage(PrintStream out) {
        out.println("Quiz / Exam Platform - educational Java OOP project");
        out.println();
        out.println("Usage: java -cp out quizexamplatform.Main <command>");
        out.println();
        out.println("Commands:");
        out.println("  help             Show this usage text (default with no command).");
        out.println("  demo             Build a quiz, take an attempt, grade it, and rank it.");
        out.println("  attempt-demo     Show the attempt lifecycle (replace/finish/lock).");
        out.println("  validation-demo  Show how invalid input is rejected cleanly.");
        out.println("  scoreboard-demo  Show ranking, ties, and best-score-per-participant.");
    }

    private static Quiz sampleQuiz() {
        Quiz quiz = new Quiz("Java Basics", 60);
        quiz.addQuestion(new Question("Which keyword creates a subclass?",
                Arrays.asList("extends", "implements", "imports"), 0));
        quiz.addQuestion(new Question("Which collection stores unique values?",
                Arrays.asList("List", "Set", "Queue"), 1));
        quiz.addQuestion(new Question("Which type stores true or false?",
                Arrays.asList("int", "String", "boolean"), 2));
        return quiz;
    }

    private static void runDemo(PrintStream out) {
        Quiz quiz = sampleQuiz();
        out.println("== Quiz: " + quiz.getTitle() + " (pass at "
                + quiz.getPassingPercentage() + "%) ==");
        out.println(quiz.getQuestionCount() + " questions added.");

        out.println("== Start attempt and answer questions ==");
        QuizAttempt attempt = quiz.startQuiz("Mina");
        attempt.recordAnswer(0, 0);
        attempt.recordAnswer(1, 0); // wrong on purpose
        attempt.recordAnswer(2, 2);
        QuizResult result = attempt.finish();

        out.println("== Answer feedback ==");
        for (AnswerResult answer : result.getAnswerResults()) {
            out.println(answer.getQuestion().getPrompt());
            out.println("  Selected: " + answer.getSelectedOption());
            out.println("  " + (answer.isCorrect()
                    ? "Correct" : "Incorrect; correct answer: " + answer.getCorrectOption()));
        }

        out.println("== Result ==");
        out.println(result.createFinalSummary());

        out.println("== Scoreboard ==");
        ScoreBoard scoreBoard = new ScoreBoard();
        scoreBoard.recordResult(result);
        printSummary(out, scoreBoard.createResultsSummary());
    }

    private static void runAttemptDemo(PrintStream out) {
        Quiz quiz = sampleQuiz();
        QuizAttempt attempt = quiz.startQuiz("Sam");

        out.println("== Answer replacement before finish ==");
        attempt.recordAnswer(0, 1);
        out.println("First answer to Q1 (option index): " + attempt.getSelectedAnswers().get(0));
        attempt.recordAnswer(0, 0);
        out.println("Replaced answer to Q1 (option index): " + attempt.getSelectedAnswers().get(0));

        out.println("== Incomplete attempt cannot finish ==");
        try {
            attempt.finish();
            out.println("ERROR: incomplete attempt was allowed to finish");
        } catch (IllegalStateException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }

        out.println("== The attempt uses a snapshot: adding a question now is ignored ==");
        quiz.addQuestion(new Question("Added after start?",
                Arrays.asList("yes", "no"), 1));
        out.println("Attempt still has " + attempt.getQuestionCount()
                + " questions; quiz now has " + quiz.getQuestionCount() + ".");

        out.println("== Finish a complete attempt ==");
        attempt.recordAnswer(1, 1);
        attempt.recordAnswer(2, 2);
        QuizResult result = attempt.finish();
        out.println(result.createFinalSummary());

        out.println("== Editing after finish is rejected ==");
        try {
            attempt.recordAnswer(0, 1);
            out.println("ERROR: a finished attempt was edited");
        } catch (IllegalStateException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }

        out.println("== Finishing again is rejected ==");
        try {
            attempt.finish();
            out.println("ERROR: a finished attempt was finished again");
        } catch (IllegalStateException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }
    }

    private static void runValidationDemo(PrintStream out) {
        out.println("== Validation demo: every rejection below is intentional ==");

        out.println("-- Blank question prompt --");
        reject(out, () -> new Question("   ", Arrays.asList("a", "b"), 0));

        out.println("-- Duplicate options (case-insensitive) --");
        reject(out, () -> new Question("Pick one", Arrays.asList("Yes", "yes"), 0));

        out.println("-- Correct option index out of range --");
        reject(out, () -> new Question("Pick one", Arrays.asList("a", "b"), 5));

        out.println("-- Starting an empty quiz --");
        reject(out, () -> new Quiz("Empty").startQuiz("Nobody"));

        out.println("-- Passing percentage above 100 --");
        reject(out, () -> new Quiz("Bad threshold", 150));

        out.println("-- Recording an answer with an invalid option index --");
        Quiz quiz = sampleQuiz();
        QuizAttempt attempt = quiz.startQuiz("Tester");
        reject(out, () -> attempt.recordAnswer(0, 99));

        out.println("All validation cases behaved as designed.");
    }

    private static void runScoreboardDemo(PrintStream out) {
        ScoreBoard board = new ScoreBoard();

        out.println("== Record scores for several participants ==");
        board.recordScore("Ada", 90);
        board.recordScore("Bob", 90);   // ties with Ada -> name order breaks the tie
        board.recordScore("Cleo", 75);

        out.println("== Same participant scores again ==");
        board.recordScore("Cleo", 40);  // lower: best score (75) is kept
        board.recordScore("Ada", 100);  // higher: replaces 90
        out.println("Ada's best score: " + board.getScore("Ada") + "%");
        out.println("Cleo's best score (kept): " + board.getScore("Cleo") + "%");

        out.println("== Ranking (score desc, then name asc) ==");
        printSummary(out, board.createResultsSummary());
    }

    private static void reject(PrintStream out, Runnable action) {
        try {
            action.run();
            out.println("ERROR: invalid input was accepted (this should not happen)");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }
    }

    private static void printSummary(PrintStream out, List<String> lines) {
        if (lines.isEmpty()) {
            out.println("(empty)");
            return;
        }
        int rank = 1;
        for (String line : lines) {
            out.println(rank++ + ". " + line);
        }
    }
}
