package com.weather.report.repositories;

import java.time.LocalDateTime;
import java.util.List;

import com.weather.report.model.entities.Measurement;
import com.weather.report.persistence.PersistenceManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * Repository for {@link Measurement} providing database-side (JPQL) filtering by
 * sensor / gateway / network code and by timestamp range.
 * <p>
 * These finders exist so that callers (reports, services) no longer need to load
 * <em>all</em> measurements with {@link CRUDRepository#read()} and filter them in
 * Java. All queries use {@link TypedQuery} with named parameters (no string
 * concatenation of caller input); the JPQL field name is always a fixed literal
 * chosen by this class, never external input.
 * <p>
 * Date-range convention (kept consistent across every method and matching the
 * previous Java-side filtering behaviour):
 * <ul>
 *   <li>bounds are <b>inclusive</b>: {@code timestamp >= start AND timestamp <= end};</li>
 *   <li>a {@code null} bound means "unbounded" on that side;</li>
 *   <li>if both bounds are given and {@code start} is after {@code end}, an
 *       {@link IllegalArgumentException} is thrown (the repository layer uses
 *       Java exceptions, not the project's checked exceptions).</li>
 * </ul>
 * Results are ordered by ascending timestamp and are never {@code null}.
 */
public class MeasurementRepository extends CRUDRepository<Measurement, Long> {

  public MeasurementRepository() {
    super(Measurement.class);
  }

  // ---------------------------------------------------------------------------
  // Date-range finders by code
  // ---------------------------------------------------------------------------

  /** Measurements for a sensor within an (optional) inclusive timestamp range. */
  public List<Measurement> findBySensorCodeAndDateRange(String sensorCode, LocalDateTime start, LocalDateTime end) {
    return findByCodeFieldAndDateRange("sensorCode", sensorCode, start, end);
  }

  /** Measurements for a gateway within an (optional) inclusive timestamp range. */
  public List<Measurement> findByGatewayCodeAndDateRange(String gatewayCode, LocalDateTime start, LocalDateTime end) {
    return findByCodeFieldAndDateRange("gatewayCode", gatewayCode, start, end);
  }

  /** Measurements for a network within an (optional) inclusive timestamp range. */
  public List<Measurement> findByNetworkCodeAndDateRange(String networkCode, LocalDateTime start, LocalDateTime end) {
    return findByCodeFieldAndDateRange("networkCode", networkCode, start, end);
  }

  // ---------------------------------------------------------------------------
  // Code-only finders (no date bounds)
  // ---------------------------------------------------------------------------

  public List<Measurement> findBySensorCode(String sensorCode) {
    return findBySensorCodeAndDateRange(sensorCode, null, null);
  }

  public List<Measurement> findByGatewayCode(String gatewayCode) {
    return findByGatewayCodeAndDateRange(gatewayCode, null, null);
  }

  public List<Measurement> findByNetworkCode(String networkCode) {
    return findByNetworkCodeAndDateRange(networkCode, null, null);
  }

  // ---------------------------------------------------------------------------
  // Date-only finder
  // ---------------------------------------------------------------------------

  /** All measurements within an (optional) inclusive timestamp range. */
  public List<Measurement> findByDateRange(LocalDateTime start, LocalDateTime end) {
    validateRange(start, end);
    EntityManager em = PersistenceManager.getEntityManager();
    try {
      StringBuilder jpql = new StringBuilder("SELECT m FROM Measurement m WHERE 1 = 1");
      if (start != null) {
        jpql.append(" AND m.timestamp >= :start");
      }
      if (end != null) {
        jpql.append(" AND m.timestamp <= :end");
      }
      jpql.append(" ORDER BY m.timestamp ASC");

      TypedQuery<Measurement> query = em.createQuery(jpql.toString(), Measurement.class);
      if (start != null) {
        query.setParameter("start", start);
      }
      if (end != null) {
        query.setParameter("end", end);
      }
      return query.getResultList();
    } finally {
      em.close();
    }
  }

  // ---------------------------------------------------------------------------
  // Count queries (aggregate, database-side)
  // ---------------------------------------------------------------------------

  public long countBySensorCode(String sensorCode) {
    return countByCodeField("sensorCode", sensorCode);
  }

  public long countByGatewayCode(String gatewayCode) {
    return countByCodeField("gatewayCode", gatewayCode);
  }

  public long countByNetworkCode(String networkCode) {
    return countByCodeField("networkCode", networkCode);
  }

  /** Number of measurements within an (optional) inclusive timestamp range. */
  public long countByDateRange(LocalDateTime start, LocalDateTime end) {
    validateRange(start, end);
    EntityManager em = PersistenceManager.getEntityManager();
    try {
      StringBuilder jpql = new StringBuilder("SELECT COUNT(m) FROM Measurement m WHERE 1 = 1");
      if (start != null) {
        jpql.append(" AND m.timestamp >= :start");
      }
      if (end != null) {
        jpql.append(" AND m.timestamp <= :end");
      }
      TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
      if (start != null) {
        query.setParameter("start", start);
      }
      if (end != null) {
        query.setParameter("end", end);
      }
      return query.getSingleResult();
    } finally {
      em.close();
    }
  }

  // ---------------------------------------------------------------------------
  // Backward-compatible alias (existing public API, used by SensorOperations)
  // ---------------------------------------------------------------------------

  /**
   * @deprecated kept for source compatibility; identical to
   *             {@link #findBySensorCodeAndDateRange(String, LocalDateTime, LocalDateTime)}.
   */
  @Deprecated
  public List<Measurement> findBySensorAndDateRange(String sensorCode, LocalDateTime start, LocalDateTime end) {
    return findBySensorCodeAndDateRange(sensorCode, start, end);
  }

  // ---------------------------------------------------------------------------
  // Internals
  // ---------------------------------------------------------------------------

  private List<Measurement> findByCodeFieldAndDateRange(String field, String code,
      LocalDateTime start, LocalDateTime end) {
    requireCode(code);
    validateRange(start, end);
    EntityManager em = PersistenceManager.getEntityManager();
    try {
      // 'field' is always a fixed literal supplied by this class, never caller input.
      StringBuilder jpql = new StringBuilder("SELECT m FROM Measurement m WHERE m.")
          .append(field).append(" = :code");
      if (start != null) {
        jpql.append(" AND m.timestamp >= :start");
      }
      if (end != null) {
        jpql.append(" AND m.timestamp <= :end");
      }
      jpql.append(" ORDER BY m.timestamp ASC");

      TypedQuery<Measurement> query = em.createQuery(jpql.toString(), Measurement.class);
      query.setParameter("code", code);
      if (start != null) {
        query.setParameter("start", start);
      }
      if (end != null) {
        query.setParameter("end", end);
      }
      return query.getResultList();
    } finally {
      em.close();
    }
  }

  private long countByCodeField(String field, String code) {
    requireCode(code);
    EntityManager em = PersistenceManager.getEntityManager();
    try {
      TypedQuery<Long> query = em.createQuery(
          "SELECT COUNT(m) FROM Measurement m WHERE m." + field + " = :code", Long.class);
      query.setParameter("code", code);
      return query.getSingleResult();
    } finally {
      em.close();
    }
  }

  private static void requireCode(String code) {
    if (code == null || code.isBlank()) {
      throw new IllegalArgumentException("Measurement query code must not be null or blank");
    }
  }

  private static void validateRange(LocalDateTime start, LocalDateTime end) {
    if (start != null && end != null && start.isAfter(end)) {
      throw new IllegalArgumentException("Start date must not be after end date");
    }
  }

}
