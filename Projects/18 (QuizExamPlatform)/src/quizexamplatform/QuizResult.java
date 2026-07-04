package quizexamplatform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizResult {
    public static final int PASSING_PERCENTAGE = 60;

    private final String participantName;
    private final String quizTitle;
    private final List<AnswerResult> answerResults;
    private final int correctAnswers;

    public QuizResult(String participantName, String quizTitle, List<AnswerResult> answerResults) {
        if (participantName == null || participantName.trim().isEmpty()
                || quizTitle == null || quizTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Participant and quiz title must not be blank");
        }
        if (answerResults == null || answerResults.isEmpty()) {
            throw new IllegalArgumentException("Quiz result must contain answers");
        }
        this.participantName = participantName.trim();
        this.quizTitle = quizTitle.trim();
        this.answerResults = Collections.unmodifiableList(new ArrayList<>(answerResults));
        int correctCount = 0;
        for (AnswerResult result : answerResults) {
            if (result == null) {
                throw new IllegalArgumentException("Answer results must not contain null");
            }
            if (result.isCorrect()) {
                correctCount++;
            }
        }
        this.correctAnswers = correctCount;
    }

    public String getParticipantName() { return participantName; }
    public String getQuizTitle() { return quizTitle; }
    public List<AnswerResult> getAnswerResults() { return answerResults; }
    public int getCorrectAnswers() { return correctAnswers; }
    public int getTotalQuestions() { return answerResults.size(); }
    public int getPercentage() { return correctAnswers * 100 / getTotalQuestions(); }
    public boolean hasPassed() { return getPercentage() >= PASSING_PERCENTAGE; }

    public String createFinalSummary() {
        return participantName + " scored " + correctAnswers + "/" + getTotalQuestions()
                + " (" + getPercentage() + "%) - " + (hasPassed() ? "PASS" : "FAIL");
    }
}
