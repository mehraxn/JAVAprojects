package jobapplicationtracker;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReminderService {
    private final JdbcApplicationRepository repository;

    public ReminderService(JdbcApplicationRepository repository) {
        this.repository = repository;
    }

    public List<JobApplication> findDueReminders(LocalDate currentDate)
            throws SQLException {
        // TODO: Validate the date and return active due reminders.
        throw new UnsupportedOperationException("TODO: find reminders");
    }
}
