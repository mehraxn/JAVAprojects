package quizexamplatform;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public final class QuizTest {
    private QuizTest() {
    }

    private static Question q(String prompt) {
        return new Question(prompt, Arrays.asList("a", "b", "c"), 0);
    }

    static void run(Assert t) {
        // Valid creation and default threshold
        Quiz quiz = new Quiz("Java Basics");
        t.assertEquals("Java Basics", quiz.getTitle(), "title is stored");
        t.assertEquals(60, quiz.getPassingPercentage(), "default passing percentage is 60");
        t.assertEquals(0, quiz.getQuestionCount(), "new quiz has no questions");
        t.assertTrue(Modifier.isFinal(Quiz.class.getModifiers()), "Quiz is final");

        // Title validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new Quiz(null), "null title rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Quiz("  "), "blank title rejected");
        t.assertEquals("Trimmed", new Quiz("  Trimmed  ").getTitle(), "title is trimmed");

        // Custom threshold
        t.assertEquals(80, new Quiz("Hard", 80).getPassingPercentage(),
                "custom passing percentage is stored");
        t.assertEquals(0, new Quiz("Anything passes", 0).getPassingPercentage(),
                "passing percentage 0 is allowed");
        t.assertEquals(100, new Quiz("Perfect only", 100).getPassingPercentage(),
                "passing percentage 100 is allowed");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Quiz("Bad", -1), "passing percentage below 0 rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Quiz("Bad", 101), "passing percentage above 100 rejected");

        // Add questions
        quiz.addQuestion(q("Question one"));
        quiz.addQuestion(q("Question two"));
        t.assertEquals(2, quiz.getQuestionCount(), "questions are added");
        t.assertThrows(IllegalArgumentException.class,
                () -> quiz.addQuestion(null), "null question rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> quiz.addQuestion(q("Question one")), "duplicate prompt rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> quiz.addQuestion(q("QUESTION ONE")),
                "duplicate prompt rejected case-insensitively");

        // Returned questions list is unmodifiable and defensive
        t.assertThrows(UnsupportedOperationException.class,
                () -> quiz.getQuestions().add(q("Sneaky")), "questions list is unmodifiable");
        int before = quiz.getQuestionCount();
        quiz.getQuestions(); // obtaining the list must not change the quiz
        t.assertEquals(before, quiz.getQuestionCount(), "getQuestions does not mutate the quiz");

        // getQuestion range
        t.assertEquals("Question one", quiz.getQuestion(0).getPrompt(),
                "getQuestion returns the right question");
        t.assertThrows(IllegalArgumentException.class,
                () -> quiz.getQuestion(5), "getQuestion rejects out-of-range index");

        // Starting an empty quiz is rejected
        t.assertThrows(IllegalStateException.class,
                () -> new Quiz("Empty").startQuiz("Nobody"), "starting empty quiz rejected");

        // Starting a quiz with questions works
        QuizAttempt attempt = quiz.startQuiz("Mina");
        t.assertNotNull(attempt, "startQuiz returns an attempt");
        t.assertEquals(2, attempt.getQuestionCount(), "attempt sees the quiz's questions");
        t.assertEquals(60, attempt.getPassingPercentage(),
                "attempt inherits the quiz passing percentage");

        // Snapshot: adding a question after the attempt starts does not affect it
        quiz.addQuestion(q("Question three added later"));
        t.assertEquals(3, quiz.getQuestionCount(), "quiz grew to three questions");
        t.assertEquals(2, attempt.getQuestionCount(),
                "the already-started attempt still has its two-question snapshot");
        attempt.recordAnswer(0, 0);
        attempt.recordAnswer(1, 0);
        QuizResult result = attempt.finish();
        t.assertEquals(2, result.getTotalQuestions(),
                "the attempt finishes on its original snapshot, not the grown quiz");

        // Custom-threshold quiz flows the threshold into the result
        Quiz strict = new Quiz("Strict", 100);
        strict.addQuestion(new Question("Only right passes", Arrays.asList("a", "b"), 0));
        strict.addQuestion(new Question("Second", Arrays.asList("a", "b"), 0));
        QuizAttempt strictAttempt = strict.startQuiz("Sam");
        strictAttempt.recordAnswer(0, 0);
        strictAttempt.recordAnswer(1, 1); // wrong
        QuizResult strictResult = strictAttempt.finish();
        t.assertEquals(100, strictResult.getPassingPercentage(),
                "result carries the quiz's custom threshold");
        t.assertFalse(strictResult.hasPassed(), "50% fails a 100% threshold");
    }
}
