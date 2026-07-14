package quizexamplatform;

import java.lang.reflect.Modifier;
import java.util.List;

public final class ScoreBoardTest {
    private ScoreBoardTest() {
    }

    static void run(Assert t) {
        t.assertTrue(Modifier.isFinal(ScoreBoard.class.getModifiers()), "ScoreBoard is final");

        // Empty scoreboard
        ScoreBoard board = new ScoreBoard();
        t.assertEquals(0, board.getParticipantCount(), "new scoreboard is empty");
        t.assertEquals(0, board.createResultsSummary().size(), "empty summary is empty");
        t.assertEquals(-1, board.getScore("Nobody"), "missing participant scores -1");

        // Add scores
        board.recordScore("Cleo", 75);
        board.recordScore("Ada", 90);
        board.recordScore("Bob", 60);
        t.assertEquals(3, board.getParticipantCount(), "three participants recorded");

        // Ranking is score descending
        List<String> summary = board.createResultsSummary();
        t.assertEquals("Ada: 90%", summary.get(0), "highest score first");
        t.assertEquals("Cleo: 75%", summary.get(1), "middle score second");
        t.assertEquals("Bob: 60%", summary.get(2), "lowest score last");

        // Ties broken by participant name ascending
        ScoreBoard ties = new ScoreBoard();
        ties.recordScore("Bob", 80);
        ties.recordScore("Ada", 80);
        ties.recordScore("Cleo", 80);
        List<String> tieSummary = ties.createResultsSummary();
        t.assertEquals("Ada: 80%", tieSummary.get(0), "tie: Ada first by name");
        t.assertEquals("Bob: 80%", tieSummary.get(1), "tie: Bob second by name");
        t.assertEquals("Cleo: 80%", tieSummary.get(2), "tie: Cleo third by name");

        // Best score per participant
        board.recordScore("Ada", 40); // lower than 90 -> ignored
        t.assertEquals(90, board.getScore("Ada"),
                "a lower repeated score does not replace the best score");
        board.recordScore("Ada", 100); // higher -> replaces
        t.assertEquals(100, board.getScore("Ada"),
                "a higher repeated score replaces the previous best");
        t.assertEquals(3, board.getParticipantCount(),
                "repeated participant does not add a new entry");

        // Null / range validation
        t.assertThrows(IllegalArgumentException.class,
                () -> board.recordResult(null), "null result rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> board.recordScore(null, 50), "null participant rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> board.recordScore("  ", 50), "blank participant rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> board.recordScore("Ada", -1), "negative score rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> board.recordScore("Ada", 101), "score above 100 rejected");

        // Returned ranking is unmodifiable
        t.assertThrows(UnsupportedOperationException.class,
                () -> board.createResultsSummary().add("Hacker: 999%"),
                "ranking list is unmodifiable");

        // recordResult wires a QuizResult's percentage in and keeps the best
        ScoreBoard fromResults = new ScoreBoard();
        Quiz quiz = new Quiz("Q");
        quiz.addQuestion(new Question("A", java.util.Arrays.asList("a", "b"), 0));
        quiz.addQuestion(new Question("B", java.util.Arrays.asList("a", "b"), 0));
        QuizAttempt attempt = quiz.startQuiz("Mina");
        attempt.recordAnswer(0, 0);
        attempt.recordAnswer(1, 0);
        fromResults.recordResult(attempt.finish()); // 100%
        t.assertEquals(100, fromResults.getScore("Mina"), "recordResult stores the percentage");
    }
}
