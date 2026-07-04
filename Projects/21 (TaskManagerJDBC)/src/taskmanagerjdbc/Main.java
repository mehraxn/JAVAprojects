package taskmanagerjdbc;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskRepository repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);

        try {
            Task first = service.addTask("Review JDBC notes", "Study prepared statements",
                    LocalDate.now().plusDays(2));
            Task second = service.addTask("Plan weekly tasks", "", null);
            service.updateTaskStatus(first.getId(), Task.Status.IN_PROGRESS);

            System.out.println("All tasks:");
            printTasks(service.listAllTasks());
            System.out.println("\nOpen tasks:");
            printTasks(service.searchByStatus(Task.Status.OPEN));

            service.deleteTask(second.getId());
            System.out.println("\nTasks after deletion: " + service.listAllTasks().size());
            System.out.println("The demo used InMemoryTaskRepository; no database was contacted.");
        } catch (SQLException exception) {
            System.err.println("Repository operation failed: " + exception.getMessage());
        }
    }

    private static void printTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        for (Task task : tasks) {
            System.out.println(task);
        }
    }
}
