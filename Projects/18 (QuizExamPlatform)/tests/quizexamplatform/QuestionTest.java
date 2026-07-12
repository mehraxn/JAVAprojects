package quizexamplatform;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class QuestionTest {
    private QuestionTest() {
    }

    static void run(Assert t) {
        // Valid creation and getters
        Question question = new Question("Which keyword creates a subclass?",
                Arrays.asList("extends", "implements", "imports"), 0);
        t.assertEquals("Which keyword creates a subclass?", question.getPrompt(),
                "prompt is stored");
        t.assertEquals(3, question.getOptions().size(), "options are stored");
        t.assertEquals("extends", question.getOptions().get(0), "option order preserved");
        t.assertEquals(0, question.getCorrectOptionIndex(), "correct index is stored");
        t.assertEquals("extends", question.getCorrectOption(), "correct option resolved");
        t.assertTrue(question.isCorrect(0), "isCorrect true for correct index");
        t.assertFalse(question.isCorrect(1), "isCorrect false for wrong index");

        t.assertTrue(Modifier.isFinal(Question.class.getModifiers()), "Question is final");

        // Prompt validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new Question(null, Arrays.asList("a", "b"), 0), "null prompt rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Question("  ", Arrays.asList("a", "b"), 0), "blank prompt rejected");

        // Options validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new Question("Q", null, 0), "null options list rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Question("Q", new ArrayList<String>(), 0), "empty options rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Question("Q", Arrays.asList("only"), 0), "single option rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Question("Q", Arrays.asList("a", null), 0), "null option rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Question("Q", Arrays.asList("a", "  "), 0), "blank option rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Question("Q", Arrays.asList("a", "a"), 0),
                "duplicate options rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Question("Q", Arrays.asList("Yes", "yes"), 0),
                "duplicate options rejected case-insensitively");

        // Correct index range
        t.assertThrows(IllegalArgumentException.class,
                () -> new Question("Q", Arrays.asList("a", "b"), -1),
                "negative correct index rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Question("Q", Arrays.asList("a", "b"), 2),
                "too-large correct index rejected");

        // getOption / isCorrect index validation
        t.assertThrows(IllegalArgumentException.class,
                () -> question.getOption(-1), "getOption rejects negative index");
        t.assertThrows(IllegalArgumentException.class,
                () -> question.getOption(3), "getOption rejects out-of-range index");
        t.assertThrows(IllegalArgumentException.class,
                () -> question.isCorrect(9), "isCorrect rejects out-of-range index");

        // Returned options list is unmodifiable
        t.assertThrows(UnsupportedOperationException.class,
                () -> question.getOptions().add("new"), "options list is unmodifiable");

        // Mutating the original input list after construction does not affect the question
        List<String> mutableOptions = new ArrayList<>(Arrays.asList("a", "b", "c"));
        Question defensive = new Question("Q", mutableOptions, 0);
        mutableOptions.set(0, "hacked");
        mutableOptions.add("extra");
        t.assertEquals("a", defensive.getOptions().get(0),
                "mutating the input list does not change stored options");
        t.assertEquals(3, defensive.getOptions().size(),
                "adding to the input list does not change the question");

        // Prompt and options are trimmed
        t.assertEquals("Q", new Question("  Q  ", Arrays.asList("a", "b"), 0).getPrompt(),
                "prompt is trimmed");
        t.assertEquals("a", new Question("Q", Arrays.asList("  a  ", "b"), 0).getOptions().get(0),
                "options are trimmed");
    }
}
