# Architecture and Design Patterns MCQs

Each question has five options. Some questions have more than one correct answer.

### Q1. What is the main goal of layered architecture?

A. Put all code in one class  
B. Separate responsibilities into understandable layers  
C. Remove the need for tests  
D. Force every class to use inheritance  
E. Make database code run in the UI

**Correct:** B  
**Explanation:** B is correct because layers separate UI, service, domain, repository, and persistence responsibilities. A, C, D, and E increase coupling or misunderstand architecture.

### Q2. Which responsibilities usually belong in a service layer?

A. Coordinating a workflow  
B. Validating use-case input  
C. Printing every menu line  
D. Calling repositories  
E. Owning database table definitions

**Correct:** A, B, D  
**Explanation:** Services coordinate workflows, validate use-case input, and call repositories. C belongs to UI/CLI code. E belongs to mapping/schema design, not service workflow logic.

### Q3. What does the facade pattern provide?

A. A simple public entry point over internal complexity  
B. A replacement for all domain classes  
C. A way to hide several focused services behind one API  
D. A database migration tool  
E. A required pattern for every Java class

**Correct:** A, C  
**Explanation:** A and C describe a facade. B, D, and E are wrong because a facade does not replace the model, manage schema, or belong everywhere.

### Q4. What is the factory pattern mainly about?

A. Centralizing object creation  
B. Running SQL queries  
C. Choosing object variants or defaults  
D. Replacing constructors in every situation  
E. Making creation intent clearer

**Correct:** A, C, E  
**Explanation:** Factories centralize creation and can express variants/defaults. B is unrelated. D is wrong because constructors are still useful.

### Q5. Why use the repository pattern?

A. To hide data-access details from business workflows  
B. To put all validation in SQL  
C. To make service code easier to test  
D. To provide CRUD-style access to domain objects  
E. To print reports directly from entities

**Correct:** A, C, D  
**Explanation:** A, C, and D are repository benefits. B and E mix responsibilities incorrectly.

### Q6. What problem do DTOs or snapshots solve?

A. They prevent callers from mutating internal domain state  
B. They can provide read-only views  
C. They make every object persistent  
D. They are useful at boundaries  
E. They replace all validation

**Correct:** A, B, D  
**Explanation:** Snapshots protect boundaries and read-only data. C and E are unrelated.

### Q7. Which statement best matches the Single Responsibility Principle?

A. A class should have one main reason to change  
B. A class should have one method only  
C. Every class must implement an interface  
D. Every class should be static  
E. All code should be in the service layer

**Correct:** A  
**Explanation:** A is the principle. B, C, D, and E are rigid or incorrect interpretations.

### Q8. What is constructor injection?

A. A class receives dependencies through its constructor  
B. A class creates all dependencies with `new` internally  
C. A database injects rows into entities  
D. A test cannot replace dependencies  
E. A subclass modifies the parent constructor

**Correct:** A  
**Explanation:** A is correct. B is the opposite. C, D, and E do not describe dependency injection.

### Q9. Which dependency direction is usually cleaner?

A. Service depends on repository interface  
B. Repository depends on CLI menu  
C. Domain object depends on console input  
D. UI depends on service  
E. Persistence implementation depends on repository port

**Correct:** A, D, E  
**Explanation:** A, D, and E follow clean boundaries. B and C make lower-level logic depend on entry-point details.

### Q10. What is a port in hexagonal architecture?

A. An interface used by or exposed by the core  
B. A UI color setting  
C. A required database table  
D. A replacement for all services  
E. A command-line argument parser

**Correct:** A  
**Explanation:** A defines a port. The other options are unrelated.

### Q11. What is an adapter in hexagonal architecture?

A. Code that connects the core to an outside mechanism  
B. A domain invariant  
C. A validation annotation only  
D. A concrete implementation of a port  
E. A SQL keyword

**Correct:** A, D  
**Explanation:** A and D describe adapters. B, C, and E do not.

### Q12. Which package organization styles are common?

A. By layer  
B. By feature  
C. By random file size  
D. By class creation date  
E. Hybrid feature plus layer

**Correct:** A, B, E  
**Explanation:** A, B, and E are real organization styles. C and D do not help design.

### Q13. What is a common architecture mistake?

A. Putting business logic in UI code  
B. Returning mutable internal lists  
C. Testing domain classes directly  
D. Mixing persistence code everywhere  
E. Giving classes unclear responsibilities

**Correct:** A, B, D, E  
**Explanation:** A, B, D, and E are mistakes. C is usually a good testing practice.

### Q14. What should architecture tests focus on?

A. Domain rules  
B. Service workflows with fake repositories  
C. Repository behavior separately  
D. Only launching the full application entry point  
E. CLI/controller behavior separately

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E create focused tests. D alone makes tests slow and hard to diagnose.

### Q15. Which SOLID warning signs are real?

A. One giant service class  
B. Interfaces forced everywhere without purpose  
C. Dependency direction from domain to UI  
D. A repository interface with focused CRUD methods  
E. A subclass that breaks parent behavior

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E indicate design problems. D can be a clean design.
