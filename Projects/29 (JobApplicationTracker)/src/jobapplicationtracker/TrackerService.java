package jobapplicationtracker;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TrackerService {
    private final JdbcApplicationRepository repository;

    public TrackerService(JdbcApplicationRepository repository) {
        this.repository = repository;
    }

    public JobApplication addApplication(String company, String role,
            LocalDate appliedDate) throws SQLException {
        // TODO: Validate input and create an application record.
        throw new UnsupportedOperationException("TODO: add an application");
    }

    public void changeStage(long applicationId, JobApplication.Stage stage)
            throws SQLException {
        // TODO: Validate the stage transition and persist it.
        throw new UnsupportedOperationException("TODO: change application stage");
    }

    public List<JobApplication> search(String searchText,
            JobApplication.Stage stage) throws SQLException {
        // TODO: Filter applications by text and optional stage.
        throw new UnsupportedOperationException("TODO: search applications");
    }
}
