package quizexamplatform;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class QuestionFileStore {
    public List<Question> loadQuestions(Path path) throws IOException {
        // TODO: Define a text format and parse questions with java.nio.file.Files.
        throw new UnsupportedOperationException("TODO: load questions from a file");
    }

    public void saveQuestions(Path path, List<Question> questions) throws IOException {
        // TODO: Serialize questions using only standard Java file APIs.
        throw new UnsupportedOperationException("TODO: save questions to a file");
    }
}
