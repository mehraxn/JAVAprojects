package quizexamplatform;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class QuizResultTest {
    private QuizResultTest() {
    }

    private static Question question(int correctIndex) {
        return new Question("Prompt " + Math.random(),
                Arrays.asList("a", "b", "c"), correctIndex);
    }

    private static List<AnswerResult> answers(int correct, int total) {
        List<AnswerResult> results = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            Question q = question(0);
            // selected index 0 = correct, 1 = wrong
            results.add(new AnswerResult(q, i < correct ? 0 : 1));
        }
        return results;
    }

    static void run(Assert t) {
        t.assertTrue(Modifier.isFinal(QuizResult.class.getModifiers()), "QuizResult is final");
        t.assertTrue(Modifier.isFinal(AnswerResult.class.getModifiers()), "AnswerResult is final");

        // --- AnswerResult ---
        Question q = new Question("2 + 2 = ?", Arrays.asList("3", "4", "5"), 1);
        AnswerResult right = new AnswerResult(q, 1);
        AnswerResult wrong = new AnswerResult(q, 0);
        t.assertTrue(right.isCorrect(), "correct answer detected");
        t.assertFalse(wrong.isCorrect(), "wrong answer detected");
        t.assertEquals(1, right.getSelectedOptionIndex(), "selected index stored");
        t.assertEquals("4", right.getSelectedOption(), "selected option resolved");
        t.assertEquals("4", wrong.getCorrectOption(), "correct option exposed on a wrong answer");
        t.assertEquals("3", wrong.getSelectedOption(), "wrong selected option resolved");
        t.assertTrue(q == right.getQuestion(), "question reference is exposed");
        t.assertThrows(IllegalArgumentException.class,
                () -> new AnswerResult(null, 0), "null question rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new AnswerResult(q, 9), "out-of-range selected index rejected");
        // The exposed Question is immutable, so it cannot mutate result state
        t.assertThrows(UnsupportedOperationException.class,
                () -> right.getQuestion().getOptions().add("x"),
                "exposed question's options are unmodifiable");

        // --- QuizResult: score and percentage ---
        QuizResult perfect = new QuizResult("Ada", "Quiz", answers(4, 4));
        t.assertEquals(4, perfect.getCorrectAnswers(), "correct count is right");
        t.assertEquals(4, perfect.getTotalQuestions(), "total count is right");
        t.assertEquals(100, perfect.getPercentage(), "4/4 is 100%");
        t.assertTrue(perfect.hasPassed(), "100% passes the default threshold");

        QuizResult threeOfFive = new QuizResult("Bob", "Quiz", answers(3, 5));
        t.assertEquals(60, threeOfFive.getPercentage(), "3/5 is 60%");
        t.assertTrue(threeOfFive.hasPassed(), "exactly 60% passes the default 60 threshold");

        QuizResult twoOfFive = new QuizResult("Cleo", "Quiz", answers(2, 5));
        t.assertEquals(40, twoOfFive.getPercentage(), "2/5 is 40%");
        t.assertFalse(twoOfFive.hasPassed(), "40% fails the default threshold");

        // Custom threshold
        QuizResult strict = new QuizResult("Dan", "Quiz", answers(3, 5), 70);
        t.assertEquals(70, strict.getPassingPercentage(), "custom threshold stored");
        t.assertFalse(strict.hasPassed(), "60% fails a 70% threshold");
        QuizResult lenient = new QuizResult("Eve", "Quiz", answers(2, 5), 40);
        t.assertTrue(lenient.hasPassed(), "40% passes a 40% threshold (boundary)");

        // Default constructor uses 60
        t.assertEquals(60, perfect.getPassingPercentage(),
                "default constructor uses passing percentage 60");

        // Validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new QuizResult(null, "Quiz", answers(1, 2)), "null participant rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new QuizResult("Ada", "  ", answers(1, 2)), "blank title rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new QuizResult("Ada", "Quiz", null), "null answers rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new QuizResult("Ada", "Quiz", new ArrayList<AnswerResult>()),
                "empty answers rejected");
        List<AnswerResult> withNull = new ArrayList<>(answers(1, 2));
        withNull.add(null);
        t.assertThrows(IllegalArgumentException.class,
                () -> new QuizResult("Ada", "Quiz", withNull), "null answer entry rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new QuizResult("Ada", "Quiz", answers(1, 2), 150),
                "invalid threshold rejected");

        // Returned answer results list is unmodifiable and defensive
        List<AnswerResult> source = answers(2, 3);
        QuizResult result = new QuizResult("Ada", "Quiz", source);
        t.assertThrows(UnsupportedOperationException.class,
                () -> result.getAnswerResults().add(right),
                "answer results list is unmodifiable");
        int sizeBefore = result.getAnswerResults().size();
        source.clear(); // mutating the source list must not change the result
        t.assertEquals(sizeBefore, result.getAnswerResults().size(),
                "mutating the source list does not change the stored result");

        // Summary text
        t.assertContains(perfect.createFinalSummary(), "PASS", "summary shows PASS when passed");
        t.assertContains(twoOfFive.createFinalSummary(), "FAIL", "summary shows FAIL when failed");
    }
}
