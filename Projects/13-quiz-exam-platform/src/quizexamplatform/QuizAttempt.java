package quizexamplatform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One participant's attempt at a quiz.
 *
 * <p>The attempt takes a snapshot of the quiz's questions and passing percentage
 * when it is created, so later changes to the quiz do not affect it. Answers may
 * be recorded and replaced freely until {@link #finish()} is called; after that
 * the attempt is locked and its result is stable.
 */
public final class QuizAttempt {
    private final String participantName;
    private final String quizTitle;
    private final int passingPercentage;
    private final List<Question> questions;
    private final Map<Integer, Integer> selectedAnswers = new LinkedHashMap<>();
    private QuizResult result;

    QuizAttempt(String participantName, String quizTitle, int passingPercentage,
            List<Question> questions) {
        if (participantName == null || participantName.trim().isEmpty()) {
            throw new IllegalArgumentException("Participant name must not be blank");
        }
        if (quizTitle == null || quizTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Quiz title must not be blank");
        }
        if (passingPercentage < 0 || passingPercentage > 100) {
            throw new IllegalArgumentException("Passing percentage must be between 0 and 100");
        }
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("A quiz attempt needs at least one question");
        }
        // Snapshot the questions so later quiz changes cannot affect this attempt.
        this.questions = Collections.unmodifiableList(new ArrayList<>(questions));
        this.participantName = participantName.trim();
        this.quizTitle = quizTitle.trim();
        this.passingPercentage = passingPercentage;
    }

    public String getParticipantName() { return participantName; }
    public String getQuizTitle() { return quizTitle; }
    public int getPassingPercentage() { return passingPercentage; }
    public int getQuestionCount() { return questions.size(); }
    public int getAnsweredQuestionCount() { return selectedAnswers.size(); }

    /**
     * Records (or replaces) the answer to a question. Replacing an answer before
     * finishing is allowed by design. Throws if the attempt is already finished
     * or the indexes are out of range.
     */
    public void recordAnswer(int questionIndex, int selectedOptionIndex) {
        ensureNotFinished();
        if (questionIndex < 0 || questionIndex >= questions.size()) {
            throw new IllegalArgumentException("Question index is out of range");
        }
        Question question = questions.get(questionIndex);
        question.getOption(selectedOptionIndex); // validates the option index
        selectedAnswers.put(questionIndex, selectedOptionIndex);
    }

    /** An unmodifiable view of the answers recorded so far (question index -> option index). */
    public Map<Integer, Integer> getSelectedAnswers() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(selectedAnswers));
    }

    public QuizResult finish() {
        ensureNotFinished();
        if (selectedAnswers.size() != questions.size()) {
            throw new IllegalStateException("Every question must be answered before finishing");
        }
        List<AnswerResult> answerResults = new ArrayList<>();
        for (int index = 0; index < questions.size(); index++) {
            answerResults.add(new AnswerResult(questions.get(index), selectedAnswers.get(index)));
        }
        result = new QuizResult(participantName, quizTitle, answerResults, passingPercentage);
        return result;
    }

    public boolean isFinished() {
        return result != null;
    }

    public QuizResult getResult() {
        if (result == null) {
            throw new IllegalStateException("Quiz attempt is not finished");
        }
        return result;
    }

    private void ensureNotFinished() {
        if (result != null) {
            throw new IllegalStateException("Quiz attempt is already finished");
        }
    }
}
