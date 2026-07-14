package quizexamplatform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ranks quiz results, keeping the <strong>best score per participant</strong>.
 * If a participant is recorded more than once, only their highest score is kept;
 * a later, lower score does not replace an earlier, higher one.
 *
 * <p>Rankings are sorted by score descending, then by participant name ascending
 * (case-insensitive) to break ties.
 */
public final class ScoreBoard {
    private final Map<String, Integer> bestScoreByParticipant = new LinkedHashMap<>();

    public void recordScore(String participantName, int score) {
        if (participantName == null || participantName.trim().isEmpty()) {
            throw new IllegalArgumentException("Participant name must not be blank");
        }
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        String name = participantName.trim();
        Integer existing = bestScoreByParticipant.get(name);
        if (existing == null || score > existing) {
            bestScoreByParticipant.put(name, score);
        }
    }

    public void recordResult(QuizResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Quiz result must not be null");
        }
        recordScore(result.getParticipantName(), result.getPercentage());
    }

    public int getParticipantCount() {
        return bestScoreByParticipant.size();
    }

    /** The best score recorded for a participant, or -1 if they are not on the board. */
    public int getScore(String participantName) {
        if (participantName == null) {
            return -1;
        }
        Integer score = bestScoreByParticipant.get(participantName.trim());
        return score == null ? -1 : score;
    }

    public List<String> createResultsSummary() {
        List<Map.Entry<String, Integer>> entries =
                new ArrayList<>(bestScoreByParticipant.entrySet());
        entries.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                .thenComparing(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER)));
        List<String> summary = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : entries) {
            summary.add(entry.getKey() + ": " + entry.getValue() + "%");
        }
        return Collections.unmodifiableList(summary);
    }
}
