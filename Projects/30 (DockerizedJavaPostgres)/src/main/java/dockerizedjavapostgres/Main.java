package dockerizedjavapostgres;

import java.sql.SQLException;
import java.util.List;

/**
 * One-shot CLI container job: connects to PostgreSQL, ensures a starter task
 * exists, lists all tasks, and exits. Exit code 0 means the whole workflow
 * succeeded; any configuration or database failure exits non-zero so Docker
 * Compose and CI can detect it.
 */
public class Main {
    public static void main(String[] args) {
        try {
            DatabaseConfig config = DatabaseConfig.fromEnvironment();
            System.out.println("Connecting to PostgreSQL using " + config.describeWithoutPassword());

            TaskRepository repository =
                    new JdbcTaskRepository(new DatabaseConnection(config));
            TaskService service = new TaskService(repository);

            List<Task> tasks = service.listTasks();
            if (tasks.isEmpty()) {
                Task created = service.addTask("First task created by the containerized Java app");
                System.out.println("Table was empty - inserted starter task with ID " + created.getId());
                tasks = service.listTasks();
            } else {
                System.out.println("Found existing tasks - skipping starter insert.");
            }

            System.out.println("Tasks in PostgreSQL (" + tasks.size() + " total):");
            for (Task task : tasks) {
                System.out.println("  " + task);
            }
            System.out.println("Task workflow completed successfully. Exiting.");
        } catch (IllegalArgumentException exception) {
            System.err.println("Configuration error: " + exception.getMessage());
            System.exit(1);
        } catch (SQLException exception) {
            System.err.println("Database operation failed: " + exception.getMessage());
            System.exit(1);
        }
    }
}
