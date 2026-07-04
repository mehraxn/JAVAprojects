package quizexamplatform;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private final String title;
    private final List<Question> questions = new ArrayList<>();
    private Duration timeLimit;

    public Quiz(String title) {
        this.title = title;
    }

    public String getTitle() { return title; }

    public void addQuestion(Question question) {
        // TODO: Validate and add the question to the quiz.
        throw new UnsupportedOperationException("TODO: add a question");
    }

    public int scoreAnswers(List<Integer> selectedAnswers) {
        // TODO: Compare each submitted answer with its question.
        throw new UnsupportedOperationException("TODO: score quiz answers");
    }
}
