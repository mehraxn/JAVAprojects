package jobapplicationtracker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CsvApplicationRepository implements ApplicationRepository {
    private static final String HEADER = "id,company,role,applicationDate,status,notes";

    @Override
    public List<JobApplication> load(Path path) throws IOException {
        requirePath(path);
        if (!Files.exists(path) || Files.size(path) == 0) {
            return Collections.emptyList();
        }

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        int headerIndex = firstNonBlankLine(lines);
        if (headerIndex == -1) {
            return Collections.emptyList();
        }
        if (!parseLine(lines.get(headerIndex), headerIndex + 1).equals(parseLine(HEADER, 1))) {
            throw new IOException("Unexpected CSV header. Expected: " + HEADER);
        }

        List<JobApplication> applications = new ArrayList<JobApplication>();
        Set<Long> ids = new HashSet<Long>();
        for (int index = headerIndex + 1; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.trim().isEmpty()) {
                continue;
            }
            List<String> fields = parseLine(line, index + 1);
            if (fields.size() != 6) {
                throw new IOException("Line " + (index + 1) + " must contain exactly 6 fields.");
            }

            try {
                long id = Long.parseLong(fields.get(0).trim());
                JobApplication application = new JobApplication(
                        id,
                        fields.get(1),
                        fields.get(2),
                        LocalDate.parse(fields.get(3).trim()),
                        JobApplication.Status.valueOf(fields.get(4).trim()),
                        fields.get(5));
                if (!ids.add(id)) {
                    throw new IOException("Duplicate application ID on line " + (index + 1) + ": " + id);
                }
                applications.add(application);
            } catch (NumberFormatException exception) {
                throw new IOException("Invalid application ID on line " + (index + 1) + ".", exception);
            } catch (DateTimeParseException exception) {
                throw new IOException("Invalid date on line " + (index + 1) + ". Use yyyy-MM-dd.", exception);
            } catch (IllegalArgumentException exception) {
                throw new IOException("Invalid application on line " + (index + 1) + ": "
                        + exception.getMessage(), exception);
            }
        }
        return Collections.unmodifiableList(applications);
    }

    @Override
    public void save(Path path, List<JobApplication> applications) throws IOException {
        requirePath(path);
        if (applications == null) {
            throw new IllegalArgumentException("Application list cannot be null.");
        }

        List<String> lines = new ArrayList<String>();
        lines.add(HEADER);
        Set<Long> ids = new HashSet<Long>();
        for (JobApplication application : applications) {
            if (application == null) {
                throw new IllegalArgumentException("Application list cannot contain null values.");
            }
            if (!ids.add(application.getId())) {
                throw new IllegalArgumentException("Duplicate application ID: " + application.getId());
            }
            lines.add(application.getId() + ","
                    + escape(application.getCompany()) + ","
                    + escape(application.getRole()) + ","
                    + application.getApplicationDate() + ","
                    + application.getStatus().name() + ","
                    + escape(application.getNotes()));
        }

        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    private void requirePath(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null.");
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
        if (value.contains(",") || value.contains("\"") || !value.equals(value.trim())) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
