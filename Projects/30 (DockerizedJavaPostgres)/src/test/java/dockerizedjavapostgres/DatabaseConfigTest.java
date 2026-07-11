package dockerizedjavapostgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class DatabaseConfigTest {
    private static final String URL = "jdbc:postgresql://database:5432/taskdb";
    private static final String USER = "task_user";
    private static final String PASSWORD = "some-local-test-password";

    @Test
    void acceptsValidConfiguration() {
        DatabaseConfig config = new DatabaseConfig(URL, USER, PASSWORD);
        assertEquals(URL, config.getUrl());
        assertEquals(USER, config.getUsername());
        assertEquals(PASSWORD, config.getPassword());
    }

    @Test
    void trimsUrlAndUsername() {
        DatabaseConfig config = new DatabaseConfig("  " + URL + "  ", "  " + USER + "  ", PASSWORD);
        assertEquals(URL, config.getUrl());
        assertEquals(USER, config.getUsername());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsMissingUrl(String url) {
        assertThrows(IllegalArgumentException.class,
                () -> new DatabaseConfig(url, USER, PASSWORD));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsMissingUsername(String username) {
        assertThrows(IllegalArgumentException.class,
                () -> new DatabaseConfig(URL, username, PASSWORD));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsMissingPassword(String password) {
        assertThrows(IllegalArgumentException.class,
                () -> new DatabaseConfig(URL, USER, password));
    }

    @ParameterizedTest
    @ValueSource(strings = {"CHANGE_ME", "change_me", "replace_with_a_local_learning_password"})
    void rejectsPlaceholderPasswords(String placeholder) {
        assertThrows(IllegalArgumentException.class,
                () -> new DatabaseConfig(URL, USER, placeholder));
    }

    @Test
    void descriptionNeverContainsPassword() {
        DatabaseConfig config = new DatabaseConfig(URL, USER, PASSWORD);
        assertFalse(config.describeWithoutPassword().contains(PASSWORD));
    }
}
