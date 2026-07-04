package quizexamplatform;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Quiz quiz = new Quiz("Java Basics");
        quiz.addQuestion(new Question(
                "Which keyword creates a subclass?",
                Arrays.asList("extends", "implements", "imports"), 0));
        quiz.addQuestion(new Question(
                "Which collection stores unique values?",
                Arrays.asList("List", "Set", "Queue"), 1));
        quiz.addQuestion(new Question(
                "Which type stores true or false?",
                Arrays.asList("int", "String", "boolean"), 2));

        QuizAttempt attempt = quiz.startQuiz("Mina");
        attempt.recordAnswer(0, 0);
        attempt.recordAnswer(1, 0);
        attempt.recordAnswer(2, 2);
        QuizResult result = attempt.finish();

        for (AnswerResult answer : result.getAnswerResults()) {
            System.out.println(answer.getQuestion().getPrompt());
            System.out.println("  Selected: " + answer.getSelectedOption());
            System.out.println("  " + (answer.isCorrect()
                    ? "Correct" : "Incorrect; correct answer: " + answer.getCorrectOption()));
        }
        System.out.println(result.createFinalSummary());

        ScoreBoard scoreBoard = new ScoreBoard();
        scoreBoard.recordResult(result);
        System.out.println("Scoreboard: " + scoreBoard.createResultsSummary());
    }
}
