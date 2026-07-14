# Backend Java Interview Questions with Answers

1. **What is a service layer?**  
   A layer that coordinates business workflows, validation, authorization, repositories, and returned results.

2. **What belongs in a domain object?**  
   Rules and behavior about one business concept, such as account balance validation.

3. **Why use repositories?**  
   They hide persistence details and make services easier to test.

4. **What is dependency injection?**  
   Passing dependencies into a class, usually through a constructor.

5. **Why constructor injection?**  
   It makes required dependencies explicit and test-friendly.

6. **What is a DTO?**  
   A data transfer object used to carry data across boundaries.

7. **What is an immutable snapshot?**  
   A read-only view of state at a point in time.

8. **What is a domain invariant?**  
   A business rule that must always stay true.

9. **What is idempotency?**  
   Repeating an operation has the same final effect as running it once.

10. **Where should authorization checks happen?**  
    In or near the service workflow, often using a centralized authorization service.

11. **Why use `Clock`?**  
    It makes time-based code deterministic in tests.

12. **What is Maven?**  
    A build and dependency management tool.

13. **What is Maven Wrapper?**  
    A way to run a known Maven version without global installation.

14. **Surefire vs Failsafe?**  
    Surefire runs unit tests; Failsafe commonly runs integration tests.

15. **What is JaCoCo?**  
    A Java test coverage tool.

16. **What is ORM?**  
    Mapping Java objects to relational database tables.

17. **JPA vs Hibernate?**  
    JPA is a specification; Hibernate is an implementation.

18. **What does `@Entity` do?**  
    Marks a class as persistent.

19. **What is the owning side?**  
    The relationship side that controls the database relationship.

20. **What is `mappedBy`?**  
    It marks the inverse side and points to the owning-side field.

21. **What is cascade?**  
    Propagating entity operations to related entities.

22. **What is orphan removal?**  
    Deleting a child entity when it is removed from the parent relationship.

23. **Lazy vs eager?**  
    Lazy loads related data when accessed; eager loads immediately.

24. **What is N+1?**  
    One parent query plus one extra query for each parent row.

25. **What is `EntityManager`?**  
    JPA object for persisting, finding, merging, removing, and querying entities.

26. **What is persistence context?**  
    The set of managed entities tracked by an `EntityManager`.

27. **What is dirty checking?**  
    Automatic detection of changes to managed entities.

28. **What does `merge` return?**  
    A managed copy of the detached entity state.

29. **What does `flush` do?**  
    Sends SQL to the database without committing.

30. **Why rollback?**  
    To prevent partial database changes after failure.

31. **What is JPQL?**  
    Entity-oriented query language for JPA.

32. **JPQL vs SQL?**  
    JPQL uses entity and field names; SQL uses tables and columns.

33. **Why use `TypedQuery`?**  
    It gives type-safe query results.

34. **Why use parameters?**  
    They avoid unsafe query string concatenation.

35. **What is a DTO projection?**  
    A query that returns selected fields into a DTO instead of full entities.

36. **What is pagination?**  
    Returning a limited portion of sorted results.

37. **Why can simple CSV splitting fail?**  
    Quoted values can contain commas or escaped quotes.

38. **What is `ImportResult`?**  
    A structured summary of an import.

39. **Why use logging?**  
    Logs can be filtered, routed, formatted, and preserved.

40. **Why use `BigDecimal` for money?**  
    It avoids binary floating-point rounding issues and supports explicit scale.

41. **What report edge cases matter?**  
    Empty data, one value, equal values, zero division, and bucket boundaries.

42. **What is a histogram?**  
    A count of values grouped into ranges.
