package jobapplicationtracker;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class JdbcApplicationRepository {
    private final DatabaseConnection databaseConnection;

    public JdbcApplicationRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public JobApplication create(JobApplication application) throws SQLException {
        // TODO: Insert with PreparedStatement and return the stored record.
        throw new UnsupportedOperationException("TODO: create an application");
    }

    public List<JobApplication> findAll() throws SQLException {
        // TODO: Query and map all applications.
        throw new UnsupportedOperationException("TODO: list applications");
    }

    public List<JobApplication> findByStage(JobApplication.Stage stage)
            throws SQLException {
        // TODO: Query applications using a stage parameter.
        throw new UnsupportedOperationException("TODO: filter by stage");
    }

    public List<JobApplication> findRemindersDueBy(LocalDate date)
            throws SQLException {
        // TODO: Query active reminders up to the requested date.
        throw new UnsupportedOperationException("TODO: find due reminders");
    }

    public boolean update(JobApplication application) throws SQLException {
        // TODO: Update application fields with PreparedStatement.
        throw new UnsupportedOperationException("TODO: update an application");
    }

    public boolean delete(long applicationId) throws SQLException {
        // TODO: Delete an application by ID.
        throw new UnsupportedOperationException("TODO: delete an application");
    }
}
