package expensetracker;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CsvExpenseStore implements ExpenseStore {
    private static final String HEADER = "id,title,amount,category,date";

    @Override
    public List<Expense> load(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null.");
        }
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }
        if (!Files.isRegularFile(path)) {
            throw new IOException("Expense CSV path is not a regular file: " + path);
        }
        if (Files.size(path) == 0) {
            return Collections.emptyList();
        }

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        int headerIndex = firstNonBlankLine(lines);
        if (headerIndex == -1) {
            return Collections.emptyList();
        }

        List<String> header = parseLine(lines.get(headerIndex), headerIndex + 1);
        if (!header.equals(parseLine(HEADER, 1))) {
            throw new IOException("Unexpected CSV header. Expected: " + HEADER);
        }

        List<Expense> expenses = new ArrayList<Expense>();
        Set<String> ids = new HashSet<String>();
        for (int index = headerIndex + 1; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.trim().isEmpty()) {
                continue;
            }
            List<String> fields = parseLine(line, index + 1);
            if (fields.size() != 5) {
                throw new IOException("Line " + (index + 1) + " must contain exactly 5 fields.");
            }

            try {
                Expense expense = new Expense(
                        fields.get(0),
                        fields.get(1),
                        new BigDecimal(fields.get(2).trim()),
                        fields.get(3),
                        LocalDate.parse(fields.get(4).trim()));
                if (!ids.add(expense.getId())) {
                    throw new IOException("Duplicate expense ID on line " + (index + 1) + ": " + expense.getId());
                }
                expenses.add(expense);
            } catch (NumberFormatException exception) {
                throw new IOException("Invalid amount on line " + (index + 1) + ".", exception);
            } catch (DateTimeParseException exception) {
                throw new IOException("Invalid date on line " + (index + 1) + ". Use yyyy-MM-dd.", exception);
            } catch (IllegalArgumentException exception) {
                throw new IOException("Invalid expense on line " + (index + 1) + ": " + exception.getMessage(), exception);
            }
        }
        return Collections.unmodifiableList(expenses);
    }

    @Override
    public void save(Path path, List<Expense> expenses) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null.");
        }
        if (expenses == null) {
            throw new IllegalArgumentException("Expense list cannot be null.");
        }
        if (Files.exists(path) && !Files.isRegularFile(path)) {
            throw new IOException("Expense CSV path is not a regular file: " + path);
        }

        List<String> lines = new ArrayList<String>();
        lines.add(HEADER);
        Set<String> ids = new HashSet<String>();
        for (Expense expense : expenses) {
            if (expense == null) {
                throw new IllegalArgumentException("Expense list cannot contain null values.");
            }
            if (!ids.add(expense.getId())) {
                throw new IllegalArgumentException("Duplicate expense ID: " + expense.getId());
            }
            lines.add(escape(expense.getId()) + ","
                    + escape(expense.getTitle()) + ","
                    + escape(expense.getAmount().toPlainString()) + ","
                    + escape(expense.getCategory()) + ","
                    + escape(expense.getDate().toString()));
        }

        Path target = path.toAbsolutePath();
        Path parent = target.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        writeAtomically(target, lines);
    }

    /**
     * Atomic-style save: write to a temporary file in the target directory,
     * then move it over the target. If anything fails halfway, the original
     * file is left untouched and the temporary file is cleaned up.
     */
    private void writeAtomically(Path target, List<String> lines) throws IOException {
        Path directory = target.getParent();
        Path tempFile = directory == null
                ? Files.createTempFile("expenses", ".csv.tmp")
                : Files.createTempFile(directory, "expenses", ".csv.tmp");
        try {
            Files.write(tempFile, lines, StandardCharsets.UTF_8);
            try {
                Files.move(tempFile, target,
                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(tempFile, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException cleanupFailure) {
                exception.addSuppressed(cleanupFailure);
            }
            throw exception;
        }
    }

    private int firstNonBlankLine(List<String> lines) {
        for (int index = 0; index < lines.size(); index++) {
            if (!lines.get(index).trim().isEmpty()) {
                return index;
            }
        }
        return -1;
    }

    private List<String> parseLine(String line, int lineNumber) throws IOException {
        List<String> fields = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        boolean quoteClosed = false;

        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (inQuotes) {
                if (character == '"') {
                    if (index + 1 < line.length() && line.charAt(index + 1) == '"') {
                        current.append('"');
                        index++;
                    } else {
                        inQuotes = false;
                        quoteClosed = true;
                    }
                } else {
                    current.append(character);
                }
            } else if (quoteClosed) {
                if (character != ',') {
                    throw new IOException("Unexpected text after a closing quote on line " + lineNumber + ".");
                }
                fields.add(current.toString());
                current.setLength(0);
                quoteClosed = false;
            } else if (character == ',') {
                fields.add(current.toString());
                current.setLength(0);
            } else if (character == '"') {
                if (current.length() != 0) {
                    throw new IOException("Unexpected quote on line " + lineNumber + ".");
                }
                inQuotes = true;
            } else {
                current.append(character);
            }
        }

        if (inQuotes) {
            throw new IOException("Unclosed quoted field on line " + lineNumber + ".");
        }
        fields.add(current.toString());
        return fields;
    }

    private String escape(String value) throws IOException {
        if (value.contains("\n") || value.contains("\r")) {
            throw new IOException("CSV values cannot contain line breaks.");
        }
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
