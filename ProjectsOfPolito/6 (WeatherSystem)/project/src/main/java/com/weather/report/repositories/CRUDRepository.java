package com.weather.report.repositories;

import java.util.List;

import com.weather.report.persistence.PersistenceManager;

import jakarta.persistence.Entity; //ADDED FOR R1
import jakarta.persistence.EntityManager; //ADDED FOR R1
import jakarta.persistence.EntityTransaction; //ADDED FOR R1

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
    this.entityClass = entityClass;                                             //ADDED FOR R1
  }

  /**
   * Given an entity class retrieves the name of the entity to be used in the
   * queries.
   * 
   * @return the name of the entity (to be used in queries)
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
    EntityManager entityManager = PersistenceManager.getEntityManager();        //ADDED FOR R1 //Entity manager is a standard JPA class used to interact with the persistence context.
    EntityTransaction transaction = entityManager.getTransaction();             //ADDED FOR R1 //EntityTransaction is a standard JPA class used to control transactions on the EntityManager.
    
    try {                                                                        //ADDED FOR R1
      transaction.begin();                                                       //ADDED FOR R1
      entityManager.persist(entity);                                             //ADDED FOR R1
      transaction.commit();                                                      //ADDED FOR R1
      return entity;                                                             //ADDED FOR R1
    } 
    catch (Exception exception) {                                                 //ADDED FOR R1
      if (transaction.isActive()) {                                               //ADDED FOR R1
        transaction.rollback();                                                  //ADDED FOR R1
      }                                                                         
      throw exception;                                                           //ADDED FOR R1
    }
     finally {                                                                    //ADDED FOR R1
      entityManager.close();                                                     //ADDED FOR R1
    }                                                                               
  }

  /**
   * Reads a single entity by identifier.
   *
   * @param id entity identifier (primary key)
   * @return found entity or {@code null} if absent
   */
  public T read(ID id) {
    EntityManager entityManager = PersistenceManager.getEntityManager();        //ADDED FOR R1
    try {                                                                        //ADDED FOR R1
      T foundEntity = entityManager.find(entityClass, id);                       //ADDED FOR R1
      return foundEntity;                                                        //ADDED FOR R1
    } finally {                                                                  //ADDED FOR R1
      entityManager.close();                                                     //ADDED FOR R1 //We close the EntityManager to free up resources ,without this, we could have resource leaks , But it is not part of the tests and test could pass without these too
    }                                                                            //ADDED FOR R1
  }

  /**
   * Reads all entities of the managed type.
   *
   * @return list of all entities
   */
  public List<T> read() {
    EntityManager entityManager = PersistenceManager.getEntityManager();                            //ADDED FOR R1
    try {                                                                                            //ADDED FOR R1
      String entityName = getEntityName();                                                           //ADDED FOR R1
      String jpqlQuery = "SELECT e FROM " + entityName + " e";                                       //ADDED FOR R1
      List<T> allEntities = entityManager.createQuery(jpqlQuery, entityClass).getResultList();      //ADDED FOR R1
      return allEntities;                                                                            //ADDED FOR R1
    } finally {                                                                                      //ADDED FOR R1
      entityManager.close();                                                                         //ADDED FOR R1 // We close the EntityManager to free up resources ,without this, we could have resource leaks , But it is not part of the tests and test could pass without these too
    }                                                                                                //ADDED FOR R1
  }

  /**
   * Updates an existing entity.
   *
   * @param entity entity with new state
   * @return updated entity
   */
  public T update(T entity) {
    EntityManager entityManager = PersistenceManager.getEntityManager();        //ADDED FOR R1
    EntityTransaction transaction = entityManager.getTransaction();             //ADDED FOR R1
    try {                                                                        //ADDED FOR R1
      transaction.begin();                                                       //ADDED FOR R1
      T mergedEntity = entityManager.merge(entity);                              //ADDED FOR R1
      transaction.commit();                                                      //ADDED FOR R1
      return mergedEntity;                                                       //ADDED FOR R1
    } catch (Exception exception) {                                              //ADDED FOR R1
      if (transaction.isActive()) {                                              //ADDED FOR R1
        transaction.rollback();                                                  //ADDED FOR R1
      }                                                                          //ADDED FOR R1
      throw exception;                                                           //ADDED FOR R1
    } finally {                                                                  //ADDED FOR R1
      entityManager.close();                                                     //ADDED FOR R1 //We close the EntityManager to free up resources ,without this, we could have resource leaks , But it is not part of the tests and test could pass without these too
    }                                                                            //ADDED FOR R1
  }

  /**
   * Deletes an entity by identifier (primary key).
   *
   * @param id entity identifier (primary key)
   * @return deleted entity
   */
  public T delete(ID id) {
    EntityManager entityManager = PersistenceManager.getEntityManager();        //ADDED FOR R1
    EntityTransaction transaction = entityManager.getTransaction();             //ADDED FOR R1
    try {                                                                        //ADDED FOR R1
      transaction.begin();                                                       //ADDED FOR R1
      T entityToDelete = entityManager.find(entityClass, id);                    //ADDED FOR R1
      if (entityToDelete != null) {                                              //ADDED FOR R1
        entityManager.remove(entityToDelete);                                    //ADDED FOR R1
      }                                                                          //ADDED FOR R1
      transaction.commit();                                                      //ADDED FOR R1
      return entityToDelete;                                                     //ADDED FOR R1
    } catch (Exception exception) {                                              //ADDED FOR R1
      if (transaction.isActive()) {                                              //ADDED FOR R1
        transaction.rollback();                                                  //ADDED FOR R1
      }                                                                          //ADDED FOR R1
      throw exception;                                                           //ADDED FOR R1
    } finally {                                                                  //ADDED FOR R1
      entityManager.close();                                                     //ADDED FOR R1 //We close the EntityManager to free up resources ,without this, we could have resource leaks , But it is not part of the tests and test could pass without these too
    }                                                                            //ADDED FOR R1
  }
}