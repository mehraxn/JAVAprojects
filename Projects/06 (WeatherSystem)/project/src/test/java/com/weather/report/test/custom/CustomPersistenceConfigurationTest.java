package com.weather.report.test.custom;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Phase 6 persistence-configuration tests: both persistence units declared in
 * {@code META-INF/persistence.xml} (the test unit and the runtime/dev unit) can be
 * bootstrapped. This guards against the Phase-1 mismatch where the runtime unit name
 * did not exist in the configuration.
 */
class CustomPersistenceConfigurationTest {

  private static final String TEST_PU = "weatherReportTestPU";
  private static final String RUNTIME_PU = "weatherReportPU";

  @Test
  void testPersistenceUnitCanBeBootstrapped() {
    EntityManagerFactory factory = Persistence.createEntityManagerFactory(TEST_PU);
    try {
      assertNotNull(factory);
      assertTrue(factory.isOpen());
      try (EntityManager em = factory.createEntityManager()) {
        assertNotNull(em);
      }
    } finally {
      factory.close();
    }
  }

  @Test
  void runtimePersistenceUnitCanBeBootstrapped() {
    EntityManagerFactory factory = Persistence.createEntityManagerFactory(RUNTIME_PU);
    try {
      assertNotNull(factory);
      assertTrue(factory.isOpen());
      try (EntityManager em = factory.createEntityManager()) {
        assertNotNull(em);
      }
    } finally {
      factory.close();
    }
  }
}
