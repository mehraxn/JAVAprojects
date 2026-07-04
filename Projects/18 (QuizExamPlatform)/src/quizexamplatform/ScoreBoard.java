package quizexamplatform;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScoreBoard {
    private final Map<String, Integer> scoresByParticipant = new LinkedHashMap<>();

    public void recordScore(String participantName, int score) {
        if (participantName == null || participantName.trim().isEmpty()) {
            throw new IllegalArgumentException("Participant name must not be blank");
        }
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        scoresByParticipant.put(participantName.trim(), score);
    }

    public void recordResult(QuizResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Quiz result must not be null");
        }
        recordScore(result.getParticipantName(), result.getPercentage());
    }

    public List<String> createResultsSummary() {
        List<Map.Entry<String, Integer>> entries =
                new ArrayList<>(scoresByParticipant.entrySet());
        entries.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                .thenComparing(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER)));
        List<String> summary = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : entries) {
            summary.add(entry.getKey() + ": " + entry.getValue() + "%");
        }
        return java.util.Collections.unmodifiableList(summary);
    }
}
