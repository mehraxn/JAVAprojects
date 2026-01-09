# JPA FetchType Guide: EAGER vs LAZY Loading

## Table of Contents
1. [Introduction to FetchType](#1-introduction-to-fetchtype)
2. [EAGER Loading Explained](#2-eager-loading-explained)
3. [LAZY Loading Explained](#3-lazy-loading-explained)
4. [Comparison: EAGER vs LAZY](#4-comparison-eager-vs-lazy)
5. [Role in the Weather Report Project](#5-role-in-the-weather-report-project)
6. [The LazyInitializationException Problem](#6-the-lazyinitializationexception-problem)
7. [Migration Guide: EAGER to LAZY](#7-migration-guide-eager-to-lazy)
8. [Modified CRUDRepository Implementation](#8-modified-crudrepository-implementation)
9. [Best Practices](#9-best-practices)

---

## 1. Introduction to FetchType

In JPA (Java Persistence API), **FetchType** determines **when** related entities are loaded from the database. When you have relationships between entities (like `@OneToMany`, `@ManyToMany`, `@ManyToOne`, `@OneToOne`), you need to decide:

- Should the related data be loaded **immediately** when the parent entity is fetched?
- Or should it be loaded **on-demand** when you actually access it?

JPA provides two strategies:
- `FetchType.EAGER` - Load immediately
- `FetchType.LAZY` - Load on-demand

---

## 2. EAGER Loading Explained

### What is EAGER Loading?

With `FetchType.EAGER`, JPA loads the related entities **at the same time** as the parent entity. The data is fetched in a single query (or a few queries) regardless of whether you need it.

### Example in Your Code

```java
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "NETWORK_OPERATORS",
    joinColumns = @JoinColumn(name = "network_code"),
    inverseJoinColumns = @JoinColumn(name = "operator_email")
)
private Collection<Operator> operators = new ArrayList<>();
```

### What Happens with EAGER?

```java
// When you call this:
Network network = entityManager.find(Network.class, "NET_01");

// JPA executes something like:
// SELECT n.*, o.* FROM NETWORKS n 
// LEFT JOIN NETWORK_OPERATORS no ON n.code = no.network_code
// LEFT JOIN OPERATORS o ON no.operator_email = o.email
// WHERE n.code = 'NET_01'

// The operators collection is ALREADY populated
// Even if you never call network.getOperators()
```

### Pros of EAGER Loading
- ✅ Simple to use - no special handling required
- ✅ No risk of `LazyInitializationException`
- ✅ All data available immediately
- ✅ Works outside of transaction/session context

### Cons of EAGER Loading
- ❌ **Performance overhead** - loads data you might not need
- ❌ **N+1 query problem** - can cause multiple queries
- ❌ **Memory consumption** - keeps unnecessary data in memory
- ❌ **Cascading fetches** - can load entire object graphs

---

## 3. LAZY Loading Explained

### What is LAZY Loading?

With `FetchType.LAZY`, JPA creates a **proxy** (placeholder) for the related entities. The actual data is only fetched from the database **when you access it**.

### Example (After Migration)

```java
@ManyToMany(fetch = FetchType.LAZY)  // Changed from EAGER
@JoinTable(
    name = "NETWORK_OPERATORS",
    joinColumns = @JoinColumn(name = "network_code"),
    inverseJoinColumns = @JoinColumn(name = "operator_email")
)
private Collection<Operator> operators = new ArrayList<>();
```

### What Happens with LAZY?

```java
// When you call this:
Network network = entityManager.find(Network.class, "NET_01");

// JPA executes ONLY:
// SELECT * FROM NETWORKS WHERE code = 'NET_01'

// The operators collection is a PROXY (not loaded yet)

// Later, when you call:
Collection<Operator> ops = network.getOperators();
ops.size(); // This triggers the actual query!

// NOW JPA executes:
// SELECT o.* FROM OPERATORS o 
// JOIN NETWORK_OPERATORS no ON o.email = no.operator_email
// WHERE no.network_code = 'NET_01'
```

### Pros of LAZY Loading
- ✅ **Better performance** - only loads what you need
- ✅ **Reduced memory usage** - doesn't keep unused data
- ✅ **Faster initial queries** - simpler SELECT statements
- ✅ **Recommended by JPA** for collections

### Cons of LAZY Loading
- ❌ Requires **active session/transaction** when accessing data
- ❌ Risk of `LazyInitializationException`
- ❌ More complex code handling
- ❌ Can cause N+1 if not handled properly

---

## 4. Comparison: EAGER vs LAZY

| Aspect | EAGER | LAZY |
|--------|-------|------|
| **When data loads** | Immediately with parent | On first access |
| **Default for** | `@ManyToOne`, `@OneToOne` | `@OneToMany`, `@ManyToMany` |
| **Performance** | Can be slower | Generally faster |
| **Memory** | Higher consumption | Lower consumption |
| **Session requirement** | Not needed after load | Required for access |
| **Exception risk** | None | `LazyInitializationException` |
| **Use case** | Always need the data | Rarely need the data |

### Visual Representation

```
EAGER Loading:
┌─────────────┐     ┌─────────────┐
│   Network   │────▶│  Operators  │  ← Both loaded at once
└─────────────┘     └─────────────┘
      │
      ▼
  One Query (or JOIN)

LAZY Loading:
┌─────────────┐     ┌─────────────┐
│   Network   │────▶│   [PROXY]   │  ← Proxy created, not loaded
└─────────────┘     └─────────────┘
      │                    │
      ▼                    ▼ (when accessed)
  Query 1              Query 2
```

---

## 5. Role in the Weather Report Project

### Current State (EAGER)

In your Network entity, you have:

```java
@ManyToMany(fetch = FetchType.EAGER)
private Collection<Operator> operators = new ArrayList<>();

@OneToMany(mappedBy = "network")  // Default is LAZY for collections
private Collection<Gateway> gateways = new ArrayList<>();
```

### Where EAGER is Used

1. **Network ↔ Operator relationship** - Currently EAGER
   - Every time you load a Network, ALL operators are loaded
   - Used in `AlertingService.notifyThresholdViolation()` when checking thresholds

2. **Potential issues in your project:**
   ```java
   // In NetworkOperationsImpl.getNetworks()
   // Loading all networks also loads ALL their operators
   Collection<Network> networks = networkRepo.read();
   // Even if you just want to list network names!
   ```

### Impact on Your CRUDRepository

Your current `CRUDRepository.read()` works fine with EAGER:

```java
public T read(ID id) {
    EntityManager em = PersistenceManager.getEntityManager();
    try {
        return em.find(entityClass, id);  // Returns entity with all EAGER relations loaded
    } finally {
        em.close();  // Session closed, but EAGER data is already there
    }
}
```

**The problem with LAZY:** After `em.close()`, accessing lazy collections throws an exception!

---

## 6. The LazyInitializationException Problem

### What is it?

```java
org.hibernate.LazyInitializationException: 
failed to lazily initialize a collection of role: 
com.weather.report.model.entities.Network.operators, 
could not initialize proxy - no Session
```

### Why Does it Happen?

```java
// In CRUDRepository
public T read(ID id) {
    EntityManager em = PersistenceManager.getEntityManager();
    try {
        return em.find(entityClass, id);  // Network loaded, operators = PROXY
    } finally {
        em.close();  // Session CLOSED!
    }
}

// Later in your service code
Network network = networkRepo.read("NET_01");
// Session is already closed here!

Collection<Operator> ops = network.getOperators();  // Tries to load operators
// BOOM! LazyInitializationException - no active session!
```

### The Core Issue

```
Timeline:
─────────────────────────────────────────────────────────────────▶
     │                    │                        │
  Session             Session                  Access
   Opens               Closes                 getOperators()
     │                    │                        │
     ▼                    ▼                        ▼
  [Entity              [Proxy                 [EXCEPTION!
   Loaded]              Ready]                 No Session]
```

---

## 7. Migration Guide: EAGER to LAZY

### Step 1: Change the Entity Annotation

```java
// Before
@ManyToMany(fetch = FetchType.EAGER)
private Collection<Operator> operators = new ArrayList<>();

// After
@ManyToMany(fetch = FetchType.LAZY)
private Collection<Operator> operators = new ArrayList<>();
```

### Step 2: Modify CRUDRepository

You have **three options** to handle lazy loading:

#### Option A: Initialize Within Transaction (Recommended)

Add methods that explicitly initialize lazy collections:

```java
public T readWithCollections(ID id, String... collectionNames) {
    EntityManager em = PersistenceManager.getEntityManager();
    try {
        T entity = em.find(entityClass, id);
        if (entity != null) {
            // Force initialization of specified collections
            for (String collectionName : collectionNames) {
                // Use Hibernate.initialize() or access the collection
                initializeCollection(entity, collectionName);
            }
        }
        return entity;
    } finally {
        em.close();
    }
}
```

#### Option B: Use JOIN FETCH Queries

Create specialized read methods with fetch joins:

```java
public T readWithFetch(ID id, String fetchPath) {
    EntityManager em = PersistenceManager.getEntityManager();
    try {
        String jpql = "SELECT e FROM " + getEntityName() + " e " +
                      "LEFT JOIN FETCH e." + fetchPath + " " +
                      "WHERE e.id = :id";
        TypedQuery<T> query = em.createQuery(jpql, entityClass);
        query.setParameter("id", id);
        return query.getResultStream().findFirst().orElse(null);
    } finally {
        em.close();
    }
}
```

#### Option C: Open Session in View Pattern

Keep the session open longer (NOT recommended for this project):

```java
// This pattern keeps session open across the entire request
// but couples persistence to presentation layer
```

### Step 3: Update Service/Operations Code

Identify where lazy collections are accessed and ensure proper initialization:

```java
// Before (with EAGER, this just worked)
public void notifyOperators(String networkCode) {
    Network network = networkRepo.read(networkCode);
    Collection<Operator> operators = network.getOperators(); // Was already loaded
    AlertingService.notifyThresholdViolation(operators, sensorCode);
}

// After (with LAZY, need explicit fetch)
public void notifyOperators(String networkCode) {
    Network network = networkRepo.readWithOperators(networkCode); // New method
    Collection<Operator> operators = network.getOperators(); // Now initialized
    AlertingService.notifyThresholdViolation(operators, sensorCode);
}
```

---

## 8. Modified CRUDRepository Implementation

Here's the complete modified `CRUDRepository` that supports both EAGER and LAZY loading:

```java
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
```

---

## 9. Best Practices

### When to Use EAGER
- Small, always-needed collections
- One-to-one relationships where you always need the related entity
- Collections with few items that are accessed in every use case

### When to Use LAZY (Recommended Default)
- Large collections
- Collections rarely accessed
- To-many relationships (`@OneToMany`, `@ManyToMany`)
- When you want to optimize query performance

### For Your Weather Report Project

1. **Keep LAZY for `@OneToMany` and `@ManyToMany`** (like operators)
2. **Use `readWithFetch()` when you need the collections**
3. **Create specialized repository methods for common use cases:**

```java
// In a NetworkRepository extending CRUDRepository
public class NetworkRepository extends CRUDRepository<Network, String> {
    
    public NetworkRepository() {
        super(Network.class);
    }
    
    /**
     * Loads a network with its operators (for alerting)
     */
    public Network readWithOperators(String code) {
        return readWithFetch(code, "operators");
    }
    
    /**
     * Loads a network with all relationships (for full reports)
     */
    public Network readFull(String code) {
        return readWithFetch(code, "operators", "gateways");
    }
}
```

### Summary of Changes Required

| Component | Change Required |
|-----------|-----------------|
| `Network.java` | Change `FetchType.EAGER` → `FetchType.LAZY` |
| `CRUDRepository.java` | Add `readWithFetch()`, `readWithInitialization()` methods |
| `NetworkOperationsImpl` | Use new repository methods where operators are needed |
| `DataImportingService` | Use fetch methods when accessing operators for alerting |

---

## Quick Reference: Method Selection

```
Do you need lazy collections?
│
├─ NO → Use read(id) or read()
│
└─ YES → Which collections?
         │
         ├─ Known at compile time → Use readWithFetch(id, "collectionName")
         │
         └─ Dynamic/multiple → Use readWithInitialization(id, entity -> {
                                   entity.getCollection1().size();
                                   entity.getCollection2().size();
                               })
```