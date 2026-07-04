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
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getDueDate() { return dueDate; }
    public Status getStatus() { return status; }
}
