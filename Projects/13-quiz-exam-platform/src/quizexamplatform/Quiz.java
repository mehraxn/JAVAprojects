package quizexamplatform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A quiz / question bank with a configurable passing percentage.
 *
 * <p>The default passing percentage is 60. Starting an attempt takes a snapshot
 * of the current questions, so adding questions later does not affect an
 * already-started attempt.
 */
public final class Quiz {
    public static final int DEFAULT_PASSING_PERCENTAGE = 60;

    private final String title;
    private final int passingPercentage;
    private final List<Question> questions = new ArrayList<>();

    public Quiz(String title) {
        this(title, DEFAULT_PASSING_PERCENTAGE);
    }

    public Quiz(String title, int passingPercentage) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Quiz title must not be blank");
        }
        if (passingPercentage < 0 || passingPercentage > 100) {
            throw new IllegalArgumentException("Passing percentage must be between 0 and 100");
        }
        this.title = title.trim();
        this.passingPercentage = passingPercentage;
    }

    public String getTitle() { return title; }
    public int getPassingPercentage() { return passingPercentage; }
    public int getQuestionCount() { return questions.size(); }

    public void addQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question must not be null");
        }
        for (Question existingQuestion : questions) {
            if (existingQuestion.getPrompt().equalsIgnoreCase(question.getPrompt())) {
                throw new IllegalArgumentException(
                        "Question prompt already exists: " + question.getPrompt());
            }
        }
        questions.add(question);
    }

    public Question getQuestion(int questionIndex) {
        if (questionIndex < 0 || questionIndex >= questions.size()) {
            throw new IllegalArgumentException("Question index is out of range");
        }
        return questions.get(questionIndex);
    }

    public List<Question> getQuestions() {
        return Collections.unmodifiableList(new ArrayList<>(questions));
    }

    /**
     * Starts an attempt with a snapshot of the current questions and this quiz's
     * passing percentage. Later changes to this quiz do not affect the attempt.
     */
    public QuizAttempt startQuiz(String participantName) {
        if (questions.isEmpty()) {
            throw new IllegalStateException("Cannot start a quiz with no questions");
        }
        return new QuizAttempt(participantName, title, passingPercentage, questions);
    }

    public int scoreAnswers(List<Integer> selectedAnswers) {
        if (selectedAnswers == null || selectedAnswers.size() != questions.size()) {
            throw new IllegalArgumentException("One answer is required for every question");
        }
        int score = 0;
        for (int index = 0; index < questions.size(); index++) {
            Integer answer = selectedAnswers.get(index);
            if (answer == null) {
                throw new IllegalArgumentException("Answers must not contain null");
            }
            if (questions.get(index).isCorrect(answer)) {
                score++;
            }
        }
        return score;
    }
}
