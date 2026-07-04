package quizexamplatform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Question {
    private final String prompt;
    private final List<String> options;
    private final int correctOptionIndex;

    public Question(String prompt, List<String> options, int correctOptionIndex) {
        this.prompt = requireText(prompt, "Question prompt");
        if (options == null || options.size() < 2) {
            throw new IllegalArgumentException("A question must have at least two options");
        }
        List<String> optionCopy = new ArrayList<>();
        Set<String> uniqueOptions = new HashSet<>();
        for (String option : options) {
            String validOption = requireText(option, "Question option");
            if (!uniqueOptions.add(validOption.toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("Question options must be unique");
            }
            optionCopy.add(validOption);
        }
        if (correctOptionIndex < 0 || correctOptionIndex >= optionCopy.size()) {
            throw new IllegalArgumentException("Correct option index is out of range");
        }
        this.options = Collections.unmodifiableList(optionCopy);
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getPrompt() { return prompt; }
    public List<String> getOptions() { return options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public String getCorrectOption() { return options.get(correctOptionIndex); }

    public String getOption(int optionIndex) {
        validateOptionIndex(optionIndex);
        return options.get(optionIndex);
    }

    public boolean isCorrect(int selectedOptionIndex) {
        validateOptionIndex(selectedOptionIndex);
        return selectedOptionIndex == correctOptionIndex;
    }

    private void validateOptionIndex(int optionIndex) {
        if (optionIndex < 0 || optionIndex >= options.size()) {
            throw new IllegalArgumentException("Selected option index is out of range");
        }
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
