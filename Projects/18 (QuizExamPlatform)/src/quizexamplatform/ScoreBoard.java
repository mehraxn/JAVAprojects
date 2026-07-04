package quizexamplatform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreBoard {
    private final Map<String, Integer> scoresByParticipant = new HashMap<>();

    public void recordScore(String participantName, int score) {
        // TODO: Validate and store the participant's score.
        throw new UnsupportedOperationException("TODO: record a score");
    }

    public List<String> createResultsSummary() {
        // TODO: Return a readable summary ordered by score.
        throw new UnsupportedOperationException("TODO: summarize results");
    }
}
