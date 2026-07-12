package quizexamplatform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The graded outcome of a finished quiz attempt. The percentage is the number of
 * correct answers out of the total (integer division), and pass/fail is decided
 * against the quiz's passing percentage.
 */
public final class QuizResult {
    public static final int DEFAULT_PASSING_PERCENTAGE = Quiz.DEFAULT_PASSING_PERCENTAGE;

    private final String participantName;
    private final String quizTitle;
    private final int passingPercentage;
    private final List<AnswerResult> answerResults;
    private final int correctAnswers;

    /** Uses the default passing percentage (60). */
    public QuizResult(String participantName, String quizTitle, List<AnswerResult> answerResults) {
        this(participantName, quizTitle, answerResults, DEFAULT_PASSING_PERCENTAGE);
    }

    public QuizResult(String participantName, String quizTitle,
            List<AnswerResult> answerResults, int passingPercentage) {
        if (participantName == null || participantName.trim().isEmpty()
                || quizTitle == null || quizTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Participant and quiz title must not be blank");
        }
        if (answerResults == null || answerResults.isEmpty()) {
            throw new IllegalArgumentException("Quiz result must contain answers");
        }
        if (passingPercentage < 0 || passingPercentage > 100) {
            throw new IllegalArgumentException("Passing percentage must be between 0 and 100");
        }
        int correctCount = 0;
        for (AnswerResult result : answerResults) {
            if (result == null) {
                throw new IllegalArgumentException("Answer results must not contain null");
            }
            if (result.isCorrect()) {
                correctCount++;
            }
        }
        this.participantName = participantName.trim();
        this.quizTitle = quizTitle.trim();
        this.passingPercentage = passingPercentage;
        this.answerResults = Collections.unmodifiableList(new ArrayList<>(answerResults));
        this.correctAnswers = correctCount;
    }

    public String getParticipantName() { return participantName; }
    public String getQuizTitle() { return quizTitle; }
    public int getPassingPercentage() { return passingPercentage; }
    public List<AnswerResult> getAnswerResults() { return answerResults; }
    public int getCorrectAnswers() { return correctAnswers; }
    public int getTotalQuestions() { return answerResults.size(); }
    public int getPercentage() { return correctAnswers * 100 / getTotalQuestions(); }
    public boolean hasPassed() { return getPercentage() >= passingPercentage; }

    public String createFinalSummary() {
        return participantName + " scored " + correctAnswers + "/" + getTotalQuestions()
                + " (" + getPercentage() + "%) - " + (hasPassed() ? "PASS" : "FAIL");
    }
}
