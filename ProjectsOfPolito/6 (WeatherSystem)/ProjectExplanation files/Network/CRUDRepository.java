package com.weather.report.repositories;

import java.util.List;

import org.hibernate.Hibernate;

import com.weather.report.persistence.PersistenceManager;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Generic repository exposing basic CRUD operations backed by the persistence
 * layer. Supports both EAGER and LAZY fetch strategies.
 *
 * @param <T>  entity type
 * @param <ID> identifier (primary key) type
 */
public class CRUDRepository<T, ID> {

    protected Class<T> entityClass;

    public CRUDRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected String getEntityName() {
        Entity ea = entityClass.getAnnotation(jakarta.persistence.Entity.class);
        if (ea == null)
            throw new IllegalArgumentException(
                "Class " + this.entityClass.getName() + " must be annotated as @Entity"
            );
        if (ea.name().isEmpty())
            return this.entityClass.getSimpleName();
        return ea.name();
    }

    /**
     * Persists a new entity instance.
     */
    public T create(T entity) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            tx.commit();
            return entity;
        } catch (EntityExistsException | jakarta.persistence.RollbackException e) {
            if (tx.isActive())
                tx.rollback();
            throw new RuntimeException("Entity already exists", e);
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw new RuntimeException("Error creating entity", e);
        } finally {
            em.close();
        }
    }

    /**
     * Reads a single entity by identifier (basic - no lazy initialization).
     */
    public T read(ID id) {
        if (id == null)
            return null;
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    /**
     * Reads a single entity and initializes specified lazy collections.
     * Use this method when you need access to lazy-loaded relationships.
     * 
     * @param id entity identifier
     * @param initializeCollections function to initialize needed collections
     * @return entity with initialized collections
     */
    public T readWithInitialization(ID id, CollectionInitializer<T> initializeCollections) {
        if (id == null)
            return null;
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            T entity = em.find(entityClass, id);
            if (entity != null && initializeCollections != null) {
                initializeCollections.initialize(entity);
            }
            return entity;
        } finally {
            em.close();
        }
    }

    /**
     * Reads an entity with a specific collection fetched eagerly via JOIN FETCH.
     * 
     * @param id entity identifier
     * @param fetchPaths collection paths to fetch (e.g., "operators", "gateways")
     * @return entity with specified collections loaded
     */
    public T readWithFetch(ID id, String... fetchPaths) {
        if (id == null)
            return null;
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT DISTINCT e FROM ")
                .append(getEntityName())
                .append(" e");
            
            for (String path : fetchPaths) {
                jpql.append(" LEFT JOIN FETCH e.").append(path);
            }
            
            jpql.append(" WHERE e.id = :id");
            
            TypedQuery<T> query = em.createQuery(jpql.toString(), entityClass);
            query.setParameter("id", id);
            
            return query.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    /**
     * Reads all entities with specified collections fetched.
     * 
     * @param fetchPaths collection paths to fetch
     * @return list of all entities with collections loaded
     */
    public List<T> readAllWithFetch(String... fetchPaths) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT DISTINCT e FROM ")
                .append(getEntityName())
                .append(" e");
            
            for (String path : fetchPaths) {
                jpql.append(" LEFT JOIN FETCH e.").append(path);
            }
            
            TypedQuery<T> query = em.createQuery(jpql.toString(), entityClass);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Reads all entities of the managed type (basic - no lazy initialization).
     */
    public List<T> read() {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            String jpql = "SELECT e FROM " + getEntityName() + " e";
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Updates an existing entity.
     */
    public T update(T entity) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T merged = em.merge(entity);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw new RuntimeException("Error updating entity", e);
        } finally {
            em.close();
        }
    }

    /**
     * Updates entity and initializes specified collections before returning.
     * Useful when you need the updated entity with lazy collections.
     */
    public T updateWithInitialization(T entity, CollectionInitializer<T> initializeCollections) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T merged = em.merge(entity);
            if (initializeCollections != null) {
                initializeCollections.initialize(merged);
            }
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw new RuntimeException("Error updating entity", e);
        } finally {
            em.close();
        }
    }

    /**
     * Deletes an entity by identifier.
     */
    public T delete(ID id) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw new RuntimeException("Error deleting entity", e);
        } finally {
            em.close();
        }
    }

    /**
     * Deletes entity and returns it with initialized collections.
     * Useful when you need to access relationships of deleted entity.
     */
    public T deleteWithInitialization(ID id, CollectionInitializer<T> initializeCollections) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                if (initializeCollections != null) {
                    initializeCollections.initialize(entity);
                }
                em.remove(entity);
            }
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw new RuntimeException("Error deleting entity", e);
        } finally {
            em.close();
        }
    }

    /**
     * Functional interface for initializing lazy collections.
     * Usage example:
     * <pre>
     * networkRepo.readWithInitialization("NET_01", network -> {
     *     network.getOperators().size();  // Forces initialization
     *     network.getGateways().size();   // Forces initialization
     * });
     * </pre>
     */
    @FunctionalInterface
    public interface CollectionInitializer<T> {
        void initialize(T entity);
    }

    public static void clearAll() {
        // Utility method
    }
}