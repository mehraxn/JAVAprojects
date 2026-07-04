package quizexamplatform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Quiz {
    private final String title;
    private final List<Question> questions = new ArrayList<>();

    public Quiz(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Quiz title must not be blank");
        }
        this.title = title.trim();
    }

    public String getTitle() { return title; }
    public int getQuestionCount() { return questions.size(); }

    public void addQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question must not be null");
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

    public QuizAttempt startQuiz(String participantName) {
        if (questions.isEmpty()) {
            throw new IllegalStateException("Cannot start a quiz with no questions");
        }
        return new QuizAttempt(this, participantName);
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
