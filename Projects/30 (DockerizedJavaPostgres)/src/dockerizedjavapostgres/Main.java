package dockerizedjavapostgres;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseConfig config = DatabaseConfig.fromEnvironment();
            TaskRepository repository =
                    new JdbcTaskRepository(new DatabaseConnection(config));
            TaskService service = new TaskService(repository);

            List<Task> tasks = service.listTasks();
            if (tasks.isEmpty()) {
                service.addTask("First task created by the containerized Java app");
                tasks = service.listTasks();
            }

            System.out.println("Connected using " + config.describeWithoutPassword());
            System.out.println("Tasks in PostgreSQL:");
            for (Task task : tasks) {
                System.out.println(task);
            }
        } catch (IllegalArgumentException exception) {
            System.err.println("Configuration error: " + exception.getMessage());
        } catch (SQLException exception) {
            System.err.println("Database operation failed: " + exception.getMessage());
        }
    }
}
