package quizexamplatform;

import java.util.List;

public class Question {
    private final String prompt;
    private final List<String> options;
    private final int correctOptionIndex;

    public Question(String prompt, List<String> options, int correctOptionIndex) {
        this.prompt = prompt;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getPrompt() { return prompt; }
    public List<String> getOptions() { return options; }

    public boolean isCorrect(int selectedOptionIndex) {
        // TODO: Validate the selected index and compare it with the answer.
        throw new UnsupportedOperationException("TODO: check an answer");
    }
}
