package taskmanagerjdbc;

import java.time.LocalDate;

public class Task {
    public enum Status {
        OPEN,
        IN_PROGRESS,
        DONE
    }

    private final long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Status status;

    public Task(long id, String title, String description,
            LocalDate dueDate, Status status) {
        if (id < 0) {
            throw new IllegalArgumentException("Task ID cannot be negative.");
        }
        this.id = id;
        updateDetails(title, description, dueDate);
        setStatus(status);
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Status getStatus() {
        return status;
    }

    public void updateDetails(String title, String description, LocalDate dueDate) {
        this.title = requireText(title, "Title");
        this.description = description == null ? "" : description.trim();
        this.dueDate = dueDate;
    }

    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Task status cannot be null.");
        }
        this.status = status;
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        String due = dueDate == null ? "no due date" : "due " + dueDate;
        return "#" + id + " " + title + " [" + status + ", " + due + "]";
    }
}
