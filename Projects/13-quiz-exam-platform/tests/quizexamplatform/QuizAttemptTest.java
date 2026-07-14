package quizexamplatform;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public final class QuizAttemptTest {
    private QuizAttemptTest() {
    }

    private static Quiz threeQuestionQuiz() {
        Quiz quiz = new Quiz("Java Basics");
        quiz.addQuestion(new Question("Q1 extends?",
                Arrays.asList("extends", "implements", "imports"), 0));
        quiz.addQuestion(new Question("Q2 unique?",
                Arrays.asList("List", "Set", "Queue"), 1));
        quiz.addQuestion(new Question("Q3 boolean?",
                Arrays.asList("int", "String", "boolean"), 2));
        return quiz;
    }

    static void run(Assert t) {
        t.assertTrue(Modifier.isFinal(QuizAttempt.class.getModifiers()), "QuizAttempt is final");

        // Valid creation via the quiz
        Quiz quiz = threeQuestionQuiz();
        QuizAttempt attempt = quiz.startQuiz("Mina");
        t.assertEquals("Mina", attempt.getParticipantName(), "participant name is stored");
        t.assertEquals("Java Basics", attempt.getQuizTitle(), "quiz title is stored");
        t.assertEquals(3, attempt.getQuestionCount(), "question count from snapshot");
        t.assertEquals(0, attempt.getAnsweredQuestionCount(), "no answers recorded yet");
        t.assertFalse(attempt.isFinished(), "new attempt is not finished");

        // Participant name validation
        t.assertThrows(IllegalArgumentException.class,
                () -> quiz.startQuiz(null), "null participant rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> quiz.startQuiz("   "), "blank participant rejected");

        // Record answers
        attempt.recordAnswer(0, 0);
        attempt.recordAnswer(1, 1);
        t.assertEquals(2, attempt.getAnsweredQuestionCount(), "answers are counted");

        // Replacing an answer before finish is allowed (by design)
        attempt.recordAnswer(1, 2);
        t.assertEquals(2, attempt.getAnsweredQuestionCount(),
                "replacing an answer does not add a new one");
        t.assertEquals(Integer.valueOf(2), attempt.getSelectedAnswers().get(1),
                "the replacement value is stored");

        // Invalid indexes rejected
        t.assertThrows(IllegalArgumentException.class,
                () -> attempt.recordAnswer(9, 0), "invalid question index rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> attempt.recordAnswer(0, 9), "invalid option index rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> attempt.recordAnswer(0, -1), "negative option index rejected");

        // Exposed answers map is unmodifiable and defensive
        t.assertThrows(UnsupportedOperationException.class,
                () -> attempt.getSelectedAnswers().put(0, 0), "answers map is unmodifiable");

        // Incomplete attempt cannot finish
        t.assertThrows(IllegalStateException.class,
                attempt::finish, "incomplete attempt cannot finish");
        t.assertFalse(attempt.isFinished(), "attempt stays open after a failed finish");
        t.assertThrows(IllegalStateException.class,
                attempt::getResult, "getResult before finish is rejected");

        // Complete and finish
        attempt.recordAnswer(1, 1); // back to correct
        attempt.recordAnswer(2, 2);
        QuizResult result = attempt.finish();
        t.assertNotNull(result, "finish returns a result");
        t.assertTrue(attempt.isFinished(), "attempt is finished");
        t.assertEquals(3, result.getCorrectAnswers(), "all three answers correct");
        t.assertEquals(100, result.getPercentage(), "perfect score is 100%");

        // Result is stable and re-fetchable
        t.assertTrue(result == attempt.getResult(), "getResult returns the same result object");

        // Finished attempt is locked
        t.assertThrows(IllegalStateException.class,
                () -> attempt.recordAnswer(0, 1), "cannot record after finish");
        t.assertThrows(IllegalStateException.class,
                attempt::finish, "cannot finish twice");
        t.assertEquals(100, attempt.getResult().getPercentage(),
                "result is stable after finish");

        // Snapshot independence: mutating the quiz after start does not change the attempt
        Quiz growing = threeQuestionQuiz();
        QuizAttempt snapAttempt = growing.startQuiz("Snap");
        growing.addQuestion(new Question("Q4 later", Arrays.asList("a", "b"), 0));
        t.assertEquals(3, snapAttempt.getQuestionCount(),
                "attempt keeps its 3-question snapshot after the quiz grows to 4");
        snapAttempt.recordAnswer(0, 0);
        snapAttempt.recordAnswer(1, 1);
        snapAttempt.recordAnswer(2, 2);
        QuizResult snapResult = snapAttempt.finish();
        t.assertEquals(3, snapResult.getTotalQuestions(),
                "attempt finishes on its snapshot, not the grown quiz");

        // Attempt respects a custom passing threshold
        Quiz strict = new Quiz("Strict", 90);
        strict.addQuestion(new Question("A", Arrays.asList("a", "b"), 0));
        strict.addQuestion(new Question("B", Arrays.asList("a", "b"), 0));
        strict.addQuestion(new Question("C", Arrays.asList("a", "b"), 0));
        QuizAttempt strictAttempt = strict.startQuiz("Strict Sam");
        strictAttempt.recordAnswer(0, 0);
        strictAttempt.recordAnswer(1, 0);
        strictAttempt.recordAnswer(2, 1); // 2/3 = 66%
        QuizResult strictResult = strictAttempt.finish();
        t.assertEquals(66, strictResult.getPercentage(), "2 of 3 is 66%");
        t.assertFalse(strictResult.hasPassed(), "66% fails a 90% threshold");
    }
}
