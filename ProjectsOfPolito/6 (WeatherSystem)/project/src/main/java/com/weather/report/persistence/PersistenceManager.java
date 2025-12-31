package com.weather.report.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Manages JPA EntityManagerFactory and EntityManager instances.
 * Supports switching between production and test persistence units.
 */
public class PersistenceManager {
    
    private static final String TEST_PU_NAME = "weatherReportTestPU";
    private static final String PU_NAME = "weatherReportPU";

    private static EntityManagerFactory factory;
    private static String currentPUName = PU_NAME;

    /**
     * Switches to test mode, using in-memory H2 database.
     * The test persistence unit uses create-drop, so tables are
     * automatically created fresh on each factory initialization.
     */
    public static void setTestMode() {
        // Close existing factory if open
        if (factory != null && factory.isOpen()) {
            factory.close();
            factory = null;
        }
        // Switch to test persistence unit
        currentPUName = TEST_PU_NAME;
    }

    /**
     * Gets or creates the EntityManagerFactory for the current persistence unit.
     * Thread-safe using synchronized.
     */
    private static synchronized EntityManagerFactory getCurrentFactory() {
        if (factory == null || !factory.isOpen()) {
            factory = Persistence.createEntityManagerFactory(currentPUName);
        }
        return factory;
    }

    /**
     * Creates a new EntityManager for database operations.
     * Remember to close the EntityManager when done!
     * 
     * @return new EntityManager instance
     */
    public static EntityManager getEntityManager() {
        return getCurrentFactory().createEntityManager();
    }

    /**
     * Closes the EntityManagerFactory and releases resources.
     * Call this when shutting down the application.
     */
    public static void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
            factory = null;
        }
    }
}