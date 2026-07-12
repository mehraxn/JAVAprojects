package taskmanagerjdbc;

import java.io.PrintStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        System.exit(exitCode);
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        String command = args.length == 0 ? "help" : args[0].toLowerCase(Locale.ROOT);
        try {
            switch (command) {
                case "help":
                    printUsage(out);
                    return 0;
                case "demo":
                case "in-memory-demo":
                    runDemo(out);
                    return 0;
                case "validation-demo":
                    runValidationDemo(out);
                    return 0;
                case "jdbc-info":
                    printJdbcInfo(out);
                    return 0;
                default:
                    err.println("Unknown command: " + args[0]);
                    printUsage(err);
                    return 1;
            }
        } catch (SQLException exception) {
            err.println("Repository operation failed: " + exception.getMessage());
            return 1;
        }
    }

    private static void printUsage(PrintStream out) {
        out.println("Task Manager JDBC - educational repository-pattern project");
        out.println();
        out.println("Usage: java -cp out taskmanagerjdbc.Main <command>");
        out.println();
        out.println("Commands:");
        out.println("  help             Show this usage text (default with no command).");
        out.println("  demo             Run the in-memory CRUD/status-filter demo.");
        out.println("  in-memory-demo   Alias for demo.");
        out.println("  validation-demo  Show how invalid input is rejected cleanly.");
        out.println("  jdbc-info        Explain the JDBC repository and its requirements.");
    }

    private static void runDemo(PrintStream out) throws SQLException {
        TaskService service = new TaskService(new InMemoryTaskRepository());

        out.println("== Create tasks ==");
        Task review = service.addTask("Review JDBC notes", "Study prepared statements",
                LocalDate.now().plusDays(2));
        Task plan = service.addTask("Plan weekly tasks", "", null);
        Task write = service.addTask("Write summary", "One page", LocalDate.now().plusDays(7));
        out.println("Created: " + review);
        out.println("Created: " + plan);
        out.println("Created: " + write);

        out.println("== Update task details ==");
        service.updateTaskDetails(write.getId(), "Write project summary",
                "Two pages with examples", LocalDate.now().plusDays(5));
        out.println("Updated: " + service.findTask(write.getId()).orElseThrow(
                () -> new IllegalStateException("Updated task disappeared.")));

        out.println("== Update task status ==");
        service.updateTaskStatus(review.getId(), Task.Status.IN_PROGRESS);
        service.updateTaskStatus(plan.getId(), Task.Status.DONE);
        out.println("All tasks:");
        printTasks(out, service.listAllTasks());

        out.println("== Filter by status ==");
        out.println("OPEN tasks:");
        printTasks(out, service.searchByStatus(Task.Status.OPEN));
        out.println("IN_PROGRESS tasks:");
        printTasks(out, service.searchByStatus(Task.Status.IN_PROGRESS));

        out.println("== Delete a task ==");
        out.println("Deleted: " + service.deleteTask(plan.getId()));

        out.println("== Final task list ==");
        printTasks(out, service.listAllTasks());
        out.println("This demo used InMemoryTaskRepository; no database was contacted.");
    }

    private static void runValidationDemo(PrintStream out) throws SQLException {
        TaskService service = new TaskService(new InMemoryTaskRepository());
        service.addTask("Valid task", "Present so updates have a target", null);

        out.println("== Validation demo: every rejection below is intentional ==");

        out.println("-- Blank title --");
        try {
            service.addTask("   ", "description", null);
            out.println("ERROR: blank title was accepted (this should not happen)");
        } catch (IllegalArgumentException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }

        out.println("-- Null title --");
        try {
            service.addTask(null, "description", null);
            out.println("ERROR: null title was accepted (this should not happen)");
        } catch (IllegalArgumentException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }

        out.println("-- Null status --");
        try {
            service.updateTaskStatus(1, null);
            out.println("ERROR: null status was accepted (this should not happen)");
        } catch (IllegalArgumentException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }

        out.println("-- Invalid task ID --");
        try {
            service.deleteTask(0);
            out.println("ERROR: ID 0 was accepted (this should not happen)");
        } catch (IllegalArgumentException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }

        out.println("-- Missing task update --");
        out.println("updateTaskStatus(999, DONE) returned: "
                + service.updateTaskStatus(999, Task.Status.DONE) + " (false = not found)");

        out.println("-- Missing task delete --");
        out.println("deleteTask(999) returned: " + service.deleteTask(999)
                + " (false = not found)");

        out.println("All validation cases behaved as designed.");
    }

    private static void printJdbcInfo(PrintStream out) {
        out.println("JDBC repository information");
        out.println();
        out.println("JdbcTaskRepository is fully implemented with standard java.sql APIs:");
        out.println("  - PreparedStatement for every dynamic value (no string-concatenated SQL)");
        out.println("  - try-with-resources for Connection, PreparedStatement, and ResultSet");
        out.println("  - Statement.RETURN_GENERATED_KEYS with checked generated IDs");
        out.println("  - SQLException wrapped with clear context");
        out.println();
        out.println("Running it against a real database additionally requires:");
        out.println("  1. A database server or embedded database (none is bundled).");
        out.println("  2. Its JDBC driver on the runtime classpath (none is bundled).");
        out.println("  3. A 'tasks' table, for example:");
        out.println();
        out.println("     CREATE TABLE tasks (");
        out.println("         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,");
        out.println("         title VARCHAR(200) NOT NULL,");
        out.println("         description VARCHAR(2000) NOT NULL,");
        out.println("         due_date DATE,");
        out.println("         status VARCHAR(30) NOT NULL");
        out.println("     );");
        out.println();
        out.println("  4. A valid JDBC URL (and credentials if the database needs them), e.g.");
        out.println("     new DatabaseConnection(\"jdbc:postgresql://localhost/tasks\", user, password)");
        out.println();
        out.println("This command does not connect to any database. The demo commands use");
        out.println("InMemoryTaskRepository, and automated tests cover the in-memory stack;");
        out.println("real JDBC integration tests are not included.");
    }

    private static void printTasks(PrintStream out, List<Task> tasks) {
        if (tasks.isEmpty()) {
            out.println("No tasks found.");
            return;
        }
        for (Task task : tasks) {
            out.println(task);
        }
    }
}
