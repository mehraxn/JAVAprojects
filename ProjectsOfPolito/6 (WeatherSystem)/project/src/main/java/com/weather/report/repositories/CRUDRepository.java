package com.weather.report.repositories;

import java.util.List;

import com.weather.report.persistence.PersistenceManager; //ADDED FOR R1

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager; //ADDED FOR R1
import jakarta.persistence.EntityTransaction; //ADDED FOR R1
import jakarta.persistence.TypedQuery; //ADDED FOR R1

/**
 * Generic repository exposing basic CRUD operations backed by the persistence
 * layer.
 * <p>
 * Concrete repositories extend/compose this class to centralise common database
 * access
 * logic for all entities, as described in the README.
 *
 * @param <T>  entity type
 * @param <ID> identifier (primary key) type
 */
public class CRUDRepository<T, ID> {

  protected Class<T> entityClass;

  /**
   * Builds a repository for the given entity class.
   *
   * @param entityClass entity class handled by this repository
   */
  public CRUDRepository(Class<T> entityClass) {
    this.entityClass = entityClass; //ADDED FOR R1
  }

  /**
   * Given an entity class retrieves the name of the entity to be used in the
   * queries.
   * * @return the name of the entity (to be used in queries)
   */
  protected String getEntityName() {
    Entity ea = entityClass.getAnnotation(jakarta.persistence.Entity.class);
    if (ea == null)
      throw new IllegalArgumentException("Class " + this.entityClass.getName() + " must be annotated as @Entity");
    if (ea.name().isEmpty())
      return this.entityClass.getSimpleName();
    return ea.name();
  }

  /**
   * Persists a new entity instance.
   *
   * @param entity entity to persist
   * @return persisted entity
   */
  public T create(T entity) {
    EntityManager em = PersistenceManager.getEntityManager(); //ADDED FOR R1
    EntityTransaction tx = em.getTransaction(); //ADDED FOR R1
    try { //ADDED FOR R1
      tx.begin(); //ADDED FOR R1
      em.persist(entity); //ADDED FOR R1
      tx.commit(); //ADDED FOR R1
      return entity; //ADDED FOR R1
    } catch (Exception e) { //ADDED FOR R1
      if (tx.isActive()) { //ADDED FOR R1
        tx.rollback(); //ADDED FOR R1
      } //ADDED FOR R1
      throw e; //ADDED FOR R1
    } finally { //ADDED FOR R1
      em.close(); //ADDED FOR R1
    } //ADDED FOR R1
  }

  /**
   * Reads a single entity by identifier.
   *
   * @param id entity identifier (primary key)
   * @return found entity or {@code null} if absent
   */
  public T read(ID id) {
    EntityManager em = PersistenceManager.getEntityManager(); //ADDED FOR R1
    try { //ADDED FOR R1
      T entity = em.find(entityClass, id); //ADDED FOR R1
      return entity; //ADDED FOR R1
    } finally { //ADDED FOR R1
      em.close(); //ADDED FOR R1
    } //ADDED FOR R1
  }

  /**
   * Reads all entities of the managed type.
   *
   * @return list of all entities
   */
  public List<T> read() {
    EntityManager em = PersistenceManager.getEntityManager(); //ADDED FOR R1
    try { //ADDED FOR R1
      String jpql = "SELECT e FROM " + getEntityName() + " e"; //ADDED FOR R1
      TypedQuery<T> query = em.createQuery(jpql, entityClass); //ADDED FOR R1
      List<T> result = query.getResultList(); //ADDED FOR R1
      return result; //ADDED FOR R1
    } finally { //ADDED FOR R1
      em.close(); //ADDED FOR R1
    } //ADDED FOR R1
  }

  /**
   * Updates an existing entity.
   *
   * @param entity entity with new state
   * @return updated entity
   */
  public T update(T entity) {
    EntityManager em = PersistenceManager.getEntityManager(); //ADDED FOR R1
    EntityTransaction tx = em.getTransaction(); //ADDED FOR R1
    try { //ADDED FOR R1
      tx.begin(); //ADDED FOR R1
      T merged = em.merge(entity); //ADDED FOR R1
      tx.commit(); //ADDED FOR R1
      return merged; //ADDED FOR R1
    } catch (Exception e) { //ADDED FOR R1
      if (tx.isActive()) { //ADDED FOR R1
        tx.rollback(); //ADDED FOR R1
      } //ADDED FOR R1
      throw e; //ADDED FOR R1
    } finally { //ADDED FOR R1
      em.close(); //ADDED FOR R1
    } //ADDED FOR R1
  }

  /**
   * Deletes an entity by identifier (primary key).
   *
   * @param id entity identifier (primary key)
   * @return deleted entity
   */
  public T delete(ID id) {
    EntityManager em = PersistenceManager.getEntityManager(); //ADDED FOR R1
    EntityTransaction tx = em.getTransaction(); //ADDED FOR R1
    try { //ADDED FOR R1
      tx.begin(); //ADDED FOR R1
      T entity = em.find(entityClass, id); //ADDED FOR R1
      if (entity != null) { //ADDED FOR R1
        em.remove(entity); //ADDED FOR R1
      } //ADDED FOR R1
      tx.commit(); //ADDED FOR R1
      return entity; //ADDED FOR R1
    } catch (Exception e) { //ADDED FOR R1
      if (tx.isActive()) { //ADDED FOR R1
        tx.rollback(); //ADDED FOR R1
      } //ADDED FOR R1
      throw e; //ADDED FOR R1
    } finally { //ADDED FOR R1
      em.close(); //ADDED FOR R1
    } //ADDED FOR R1
  }

}