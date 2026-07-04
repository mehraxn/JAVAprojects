package csvanalyticsengine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CsvReader {
    public DataSet read(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null.");
        }
        if (!Files.exists(path)) {
            throw new IOException("CSV file does not exist: " + path);
        }
        if (Files.size(path) == 0) {
            return new DataSet(Collections.<String>emptyList());
        }

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        int headerIndex = firstNonBlankLine(lines);
        if (headerIndex == -1) {
            return new DataSet(Collections.<String>emptyList());
        }

        List<String> columns = parseLine(lines.get(headerIndex), headerIndex + 1);
        try {
            DataSet dataSet = new DataSet(columns);
            for (int index = headerIndex + 1; index < lines.size(); index++) {
                String line = lines.get(index);
                if (line.trim().isEmpty()) {
                    continue;
                }
                List<String> values = parseLine(line, index + 1);
                if (values.size() != columns.size()) {
                    throw new IOException("Line " + (index + 1) + " has " + values.size()
                            + " values; expected " + columns.size() + ".");
                }

                DataRow row = new DataRow();
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    row.put(columns.get(columnIndex), values.get(columnIndex));
                }
                dataSet.addRow(row);
            }
            return dataSet;
        } catch (IllegalArgumentException exception) {
            throw new IOException("Invalid CSV structure: " + exception.getMessage(), exception);
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
        List<String> values = new ArrayList<String>();
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
                values.add(current.toString());
                current.setLength(0);
                quoteClosed = false;
            } else if (character == ',') {
                values.add(current.toString());
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
        values.add(current.toString());
        return values;
    }
}
