package dockerizedjavapostgres;

public class Task {
    private final long id;
    private final String title;
    private final boolean completed;

    public Task(long id, String title, boolean completed) {
        if (id < 0) {
            throw new IllegalArgumentException("Task ID cannot be negative.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty.");
        }
        if (title.trim().length() > 200) {
            throw new IllegalArgumentException("Task title cannot exceed 200 characters.");
        }
        this.id = id;
        this.title = title.trim();
        this.completed = completed;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public String toString() {
        return "Task{id=" + id + ", title='" + title + "', completed=" + completed + "}";
    }
}
