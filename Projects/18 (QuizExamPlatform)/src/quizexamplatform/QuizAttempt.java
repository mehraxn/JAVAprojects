package quizexamplatform;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuizAttempt {
    private final Quiz quiz;
    private final String participantName;
    private final Map<Integer, Integer> selectedAnswers = new LinkedHashMap<>();
    private QuizResult result;

    public QuizAttempt(Quiz quiz, String participantName) {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz must not be null");
        }
        if (participantName == null || participantName.trim().isEmpty()) {
            throw new IllegalArgumentException("Participant name must not be blank");
        }
        this.quiz = quiz;
        this.participantName = participantName.trim();
    }

    public void recordAnswer(int questionIndex, int selectedOptionIndex) {
        ensureNotFinished();
        Question question = quiz.getQuestion(questionIndex);
        question.isCorrect(selectedOptionIndex);
        selectedAnswers.put(questionIndex, selectedOptionIndex);
    }

    public int getAnsweredQuestionCount() {
        return selectedAnswers.size();
    }

    public QuizResult finish() {
        ensureNotFinished();
        if (selectedAnswers.size() != quiz.getQuestionCount()) {
            throw new IllegalStateException("Every question must be answered before finishing");
        }
        List<AnswerResult> answerResults = new ArrayList<>();
        for (int index = 0; index < quiz.getQuestionCount(); index++) {
            answerResults.add(new AnswerResult(
                    quiz.getQuestion(index), selectedAnswers.get(index)));
        }
        result = new QuizResult(participantName, quiz.getTitle(), answerResults);
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
