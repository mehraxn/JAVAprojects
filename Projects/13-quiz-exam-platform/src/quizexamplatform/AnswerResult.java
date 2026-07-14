package quizexamplatform;

public final class AnswerResult {
    private final Question question;
    private final int selectedOptionIndex;
    private final boolean correct;

    public AnswerResult(Question question, int selectedOptionIndex) {
        if (question == null) {
            throw new IllegalArgumentException("Question must not be null");
        }
        this.question = question;
        this.selectedOptionIndex = selectedOptionIndex;
        this.correct = question.isCorrect(selectedOptionIndex);
    }

    public Question getQuestion() { return question; }
    public int getSelectedOptionIndex() { return selectedOptionIndex; }
    public String getSelectedOption() { return question.getOption(selectedOptionIndex); }
    public String getCorrectOption() { return question.getCorrectOption(); }
    public boolean isCorrect() { return correct; }
}
