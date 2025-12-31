package com.weather.report.repositories;

import java.time.LocalDateTime;

import com.weather.report.model.entities.Measurement;
import com.weather.report.persistence.PersistenceManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class MeasurementRepository extends CRUDRepository<Measurement, Long> {

  public MeasurementRepository() {
    super(Measurement.class);
  }

  public List<Measurement> findBySensorAndDateRange(String sensorCode, LocalDateTime start, LocalDateTime end) {
    EntityManager em = PersistenceManager.getEntityManager();
    try {
      StringBuilder sb = new StringBuilder("SELECT m FROM Measurement m WHERE m.sensorCode = :code");

      if (start != null) {
        sb.append(" AND m.timestamp >= :start");
      }
      if (end != null) {
        sb.append(" AND m.timestamp <= :end");
      }
      sb.append(" ORDER BY m.timestamp ASC");

      TypedQuery<Measurement> query = em.createQuery(sb.toString(), Measurement.class);
      query.setParameter("code", sensorCode);

      if (start != null)
        query.setParameter("start", start);
      if (end != null)
        query.setParameter("end", end);

      return query.getResultList();
    } finally {
      em.close();
    }
  }

}
