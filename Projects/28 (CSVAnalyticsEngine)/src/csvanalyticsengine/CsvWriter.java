package csvanalyticsengine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvWriter {
    public void write(Path path, DataSet dataSet) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null.");
        }
        if (dataSet == null) {
            throw new IllegalArgumentException("Data set cannot be null.");
        }
        if (Files.exists(path) && !Files.isRegularFile(path)) {
            throw new IOException("CSV path is not a regular file: " + path);
        }

        List<String> lines = new ArrayList<String>();
        if (!dataSet.getColumns().isEmpty()) {
            lines.add(join(dataSet.getColumns()));
            for (DataRow row : dataSet.getRows()) {
                List<String> values = new ArrayList<String>();
                for (String column : dataSet.getColumns()) {
                    values.add(row.get(column));
                }
                lines.add(join(values));
            }
        }

        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    private String join(List<String> values) throws IOException {
        StringBuilder line = new StringBuilder();
        for (int index = 0; index < values.size(); index++) {
            if (index > 0) {
                line.append(',');
            }
            line.append(escape(values.get(index)));
        }
        return line.toString();
    }

    private String escape(String value) throws IOException {
        String safeValue = value == null ? "" : value;
        if (safeValue.contains("\n") || safeValue.contains("\r")) {
            throw new IOException("CSV values cannot contain line breaks.");
        }
        if (safeValue.contains(",") || safeValue.contains("\"")
                || !safeValue.equals(safeValue.trim())) {
            return "\"" + safeValue.replace("\"", "\"\"") + "\"";
        }
        return safeValue;
    }
}
