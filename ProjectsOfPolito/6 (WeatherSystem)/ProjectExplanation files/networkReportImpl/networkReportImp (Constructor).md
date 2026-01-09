# NetworkReportImpl Constructor Analysis

## Table of Contents
1. [Overview](#1-overview)
2. [Constructor Signature](#2-constructor-signature)
3. [Strategy Pattern Used](#3-strategy-pattern-used)
4. [Line-by-Line Analysis](#4-line-by-line-analysis)
5. [Data Flow Diagram](#5-data-flow-diagram)
6. [Time Complexity Analysis](#6-time-complexity-analysis)
7. [Why Initialize Inside the Constructor?](#7-why-initialize-inside-the-constructor)
8. [Potential Improvements](#8-potential-improvements)

---

## 1. Overview

The `NetworkReportImpl` constructor is responsible for:
1. **Storing report metadata** (network code, date range)
2. **Loading all measurements** from the database
3. **Parsing date strings** into `LocalDateTime` objects
4. **Filtering measurements** based on network and time constraints

The constructor follows an **"Eager Loading with Filtering"** strategy, where all data processing happens at construction time rather than when methods are called.

---

## 2. Constructor Signature

```java
public NetworkReportImpl(String networkCode, String startDate, String endDate)
```

| Parameter | Type | Description | Nullable |
|-----------|------|-------------|----------|
| `networkCode` | `String` | The network to generate report for (e.g., "NET_01") | No |
| `startDate` | `String` | Lower bound of time interval in format `yyyy-MM-dd HH:mm:ss` | Yes |
| `endDate` | `String` | Upper bound of time interval in format `yyyy-MM-dd HH:mm:ss` | Yes |

---

## 3. Strategy Pattern Used

### Eager Initialization Strategy

```
┌─────────────────────────────────────────────────────────────────┐
│                    CONSTRUCTOR EXECUTION                        │
├─────────────────────────────────────────────────────────────────┤
│  1. Store Parameters                                            │
│  2. Load ALL Measurements from DB                               │
│  3. Parse Date Strings → LocalDateTime                          │
│  4. Filter Measurements (network + date range)                  │
│  5. Store Filtered Results                                      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    METHOD CALLS (LATER)                         │
├─────────────────────────────────────────────────────────────────┤
│  • getMostActiveGateways()  → Uses pre-filtered measurements    │
│  • getLeastActiveGateways() → Uses pre-filtered measurements    │
│  • getGatewaysLoadRatio()   → Uses pre-filtered measurements    │
│  • getHistogram()           → Uses pre-filtered measurements    │
└─────────────────────────────────────────────────────────────────┘
```

### Why This Strategy?

| Advantage | Explanation |
|-----------|-------------|
| **Single DB Query** | Database is accessed only once in constructor |
| **Fast Method Calls** | All subsequent methods work on in-memory data |
| **Consistency** | All methods see the same snapshot of data |
| **Simplicity** | No need to manage lazy loading or caching |

| Disadvantage | Explanation |
|--------------|-------------|
| **Memory Usage** | All filtered measurements kept in memory |
| **Construction Time** | Constructor does heavy lifting |
| **Data Freshness** | Data snapshot taken at construction time |

---

## 4. Line-by-Line Analysis

### Phase 1: Store Input Parameters

```java
public NetworkReportImpl(String networkCode, String startDate, String endDate) {
    this.code = networkCode;
    this.startDateStr = startDate;
    this.endDateStr = endDate;
```

#### Line: `this.code = networkCode;`
- **Purpose**: Store the network code for later use
- **Used by**: `getCode()` method
- **Example**: `"NET_01"` → stored in `this.code`

#### Line: `this.startDateStr = startDate;`
- **Purpose**: Store the original start date string
- **Used by**: `getStartDate()` method
- **Note**: Kept as string to return exactly what was passed in

#### Line: `this.endDateStr = endDate;`
- **Purpose**: Store the original end date string
- **Used by**: `getEndDate()` method
- **Note**: Can be `null` (means no upper bound)

---

### Phase 2: Load All Measurements from Database

```java
    CRUDRepository<Measurement, Long> repo = new CRUDRepository<>(Measurement.class);
    List<Measurement> allMeasurements = repo.read();
```

#### Line: `CRUDRepository<Measurement, Long> repo = new CRUDRepository<>(Measurement.class);`

```
┌─────────────────────────────────────────────────────────┐
│           CRUDRepository<Measurement, Long>             │
├─────────────────────────────────────────────────────────┤
│  Generic Type T = Measurement                           │
│  Generic Type ID = Long (primary key type)              │
│  entityClass = Measurement.class                        │
└─────────────────────────────────────────────────────────┘
```

- **Purpose**: Create a repository instance to access measurements
- **Why here?**: Constructor needs data access to filter measurements
- **Alternative**: Could inject repository as parameter (better for testing)

#### Line: `List<Measurement> allMeasurements = repo.read();`

```
Database Query Executed:
┌─────────────────────────────────────────────────────────┐
│  SELECT * FROM Measurement                              │
│                                                         │
│  Returns: ALL measurements in the entire system         │
│  Example: 10,000 measurements across all networks       │
└─────────────────────────────────────────────────────────┘
```

- **Purpose**: Fetch ALL measurements from database
- **Returns**: Complete list regardless of network or date
- **Performance Note**: This could be optimized with a filtered query

---

### Phase 3: Parse Start Date String

```java
    LocalDateTime startDateTime = null;
    if (startDate != null) {
        int year = Integer.parseInt(startDate.substring(0, 4));
        int month = Integer.parseInt(startDate.substring(5, 7));
        int day = Integer.parseInt(startDate.substring(8, 10));
        int hour = Integer.parseInt(startDate.substring(11, 13));
        int minute = Integer.parseInt(startDate.substring(14, 16));
        int second = Integer.parseInt(startDate.substring(17, 19));
        startDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
    }
```

#### Line: `LocalDateTime startDateTime = null;`
- **Purpose**: Initialize variable to hold parsed date
- **Default**: `null` means "no lower bound" for filtering

#### Line: `if (startDate != null) {`
- **Purpose**: Only parse if a date was provided
- **Why check?**: `startDate` is optional (nullable)

#### Parsing Block - String Extraction

```
Date String Format: "yyyy-MM-dd HH:mm:ss"
Example:            "2024-03-15 14:30:45"

Position Analysis:
┌─────────────────────────────────────────────────────────┐
│  Index:  0123456789...                                  │
│  String: 2024-03-15 14:30:45                            │
│          ││││ ││ ││ ││ ││ ││                            │
│          year mo da hr mi se                            │
└─────────────────────────────────────────────────────────┘
```

| Line | Extraction | Indices | Example Result |
|------|------------|---------|----------------|
| `int year = Integer.parseInt(startDate.substring(0, 4));` | Year | [0,4) | `"2024"` → `2024` |
| `int month = Integer.parseInt(startDate.substring(5, 7));` | Month | [5,7) | `"03"` → `3` |
| `int day = Integer.parseInt(startDate.substring(8, 10));` | Day | [8,10) | `"15"` → `15` |
| `int hour = Integer.parseInt(startDate.substring(11, 13));` | Hour | [11,13) | `"14"` → `14` |
| `int minute = Integer.parseInt(startDate.substring(14, 16));` | Minute | [14,16) | `"30"` → `30` |
| `int second = Integer.parseInt(startDate.substring(17, 19));` | Second | [17,19) | `"45"` → `45` |

#### Line: `startDateTime = LocalDateTime.of(year, month, day, hour, minute, second);`

```java
// Creates immutable LocalDateTime object
LocalDateTime.of(2024, 3, 15, 14, 30, 45)
// Result: 2024-03-15T14:30:45
```

- **Purpose**: Combine parsed components into a `LocalDateTime`
- **Returns**: Immutable date-time object for comparisons

---

### Phase 4: Parse End Date String

```java
    LocalDateTime endDateTime = null;
    if (endDate != null) {
        int year = Integer.parseInt(endDate.substring(0, 4));
        int month = Integer.parseInt(endDate.substring(5, 7));
        int day = Integer.parseInt(endDate.substring(8, 10));
        int hour = Integer.parseInt(endDate.substring(11, 13));
        int minute = Integer.parseInt(endDate.substring(14, 16));
        int second = Integer.parseInt(endDate.substring(17, 19));
        endDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
    }
```

- **Purpose**: Same as Phase 3, but for end date
- **Pattern**: Identical parsing logic (code duplication - could be refactored)
- **Result**: `endDateTime` or `null` if no upper bound

---

### Phase 5: Initialize Measurements Collection

```java
    this.measurements = new ArrayList<>();
```

- **Purpose**: Create empty list to store filtered measurements
- **Type**: `ArrayList` for efficient random access and iteration
- **Initial State**: Empty, will be populated in filtering phase

---

### Phase 6: Filter Measurements (Main Loop)

```java
    for (Measurement measurement : allMeasurements) {
```

- **Purpose**: Iterate through ALL measurements to filter
- **Type**: Enhanced for-loop (foreach)
- **Iterations**: Equal to total measurements in database

---

#### Step 6.1: Check Network Membership

```java
        boolean belongsToNetwork = measurement.getNetworkCode().equals(networkCode);
        if (!belongsToNetwork) {
            continue;
        }
```

##### Line: `boolean belongsToNetwork = measurement.getNetworkCode().equals(networkCode);`

```
Filter Logic:
┌─────────────────────────────────────────────────────────┐
│  measurement.getNetworkCode() == "NET_01"               │
│  networkCode (parameter)      == "NET_01"               │
│                                                         │
│  Result: true (belongs to this network)                 │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│  measurement.getNetworkCode() == "NET_02"               │
│  networkCode (parameter)      == "NET_01"               │
│                                                         │
│  Result: false (different network - SKIP)               │
└─────────────────────────────────────────────────────────┘
```

- **Purpose**: Check if measurement belongs to requested network
- **Method**: String equality comparison

##### Line: `if (!belongsToNetwork) { continue; }`
- **Purpose**: Skip measurements from other networks
- **Effect**: Move to next iteration without further processing
- **Optimization**: Early exit prevents unnecessary date checks

---

#### Step 6.2: Check Start Date Constraint

```java
        boolean isAfterStart = true;
        if (startDateTime != null) {
            if (measurement.getTimestamp().isBefore(startDateTime)) {
                isAfterStart = false;
            }
        }
```

##### Line: `boolean isAfterStart = true;`
- **Purpose**: Assume measurement passes start date check
- **Default**: `true` (include unless proven otherwise)
- **Why default true?**: If `startDateTime` is null, no lower bound exists

##### Line: `if (startDateTime != null) {`
- **Purpose**: Only check if a start date was specified
- **Effect**: Skip check if startDate parameter was null

##### Line: `if (measurement.getTimestamp().isBefore(startDateTime)) {`

```
Timeline Visualization:
────────────────────────────────────────────────────────▶ Time
        │                    │
   startDateTime         measurement
        │                    │
        ▼                    ▼
   [2024-01-01]         [2024-02-15]
        
   measurement.isBefore(startDateTime) = false ✓ INCLUDE

────────────────────────────────────────────────────────▶ Time
        │                    │
   measurement          startDateTime
        │                    │
        ▼                    ▼
   [2023-12-15]         [2024-01-01]
        
   measurement.isBefore(startDateTime) = true ✗ EXCLUDE
```

- **Purpose**: Check if measurement is before the start bound
- **Comparison**: Uses `LocalDateTime.isBefore()`
- **Result**: If before start, mark as failing the filter

##### Line: `isAfterStart = false;`
- **Purpose**: Mark measurement as outside valid range
- **Effect**: Will be excluded from final results

---

#### Step 6.3: Check End Date Constraint

```java
        boolean isBeforeEnd = true;
        if (endDateTime != null) {
            if (measurement.getTimestamp().isAfter(endDateTime)) {
                isBeforeEnd = false;
            }
        }
```

##### Line: `boolean isBeforeEnd = true;`
- **Purpose**: Assume measurement passes end date check
- **Default**: `true` (no upper bound if null)

##### Line: `if (endDateTime != null) {`
- **Purpose**: Only check if an end date was specified

##### Line: `if (measurement.getTimestamp().isAfter(endDateTime)) {`

```
Timeline Visualization:
────────────────────────────────────────────────────────▶ Time
        │                    │
   measurement           endDateTime
        │                    │
        ▼                    ▼
   [2024-02-15]         [2024-03-01]
        
   measurement.isAfter(endDateTime) = false ✓ INCLUDE

────────────────────────────────────────────────────────▶ Time
        │                    │
   endDateTime          measurement
        │                    │
        ▼                    ▼
   [2024-03-01]         [2024-04-15]
        
   measurement.isAfter(endDateTime) = true ✗ EXCLUDE
```

- **Purpose**: Check if measurement is after the end bound
- **Comparison**: Uses `LocalDateTime.isAfter()`

##### Line: `isBeforeEnd = false;`
- **Purpose**: Mark measurement as outside valid range

---

#### Step 6.4: Add to Filtered Collection

```java
        if (isAfterStart && isBeforeEnd) {
            this.measurements.add(measurement);
        }
    }
}
```

##### Line: `if (isAfterStart && isBeforeEnd) {`

```
Decision Matrix:
┌──────────────┬─────────────┬──────────────────┐
│ isAfterStart │ isBeforeEnd │ Action           │
├──────────────┼─────────────┼──────────────────┤
│    true      │    true     │ ADD to list ✓    │
│    true      │    false    │ SKIP (too late)  │
│    false     │    true     │ SKIP (too early) │
│    false     │    false    │ SKIP (both fail) │
└──────────────┴─────────────┴──────────────────┘
```

- **Purpose**: Only include measurements that pass BOTH date checks
- **Logic**: AND condition - must be within [startDate, endDate]

##### Line: `this.measurements.add(measurement);`
- **Purpose**: Add qualifying measurement to filtered list
- **Result**: Only measurements matching network AND date range

---

## 5. Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         CONSTRUCTOR INPUT                               │
├─────────────────────────────────────────────────────────────────────────┤
│  networkCode: "NET_01"                                                  │
│  startDate:   "2024-01-01 00:00:00"                                     │
│  endDate:     "2024-12-31 23:59:59"                                     │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         DATABASE QUERY                                  │
├─────────────────────────────────────────────────────────────────────────┤
│  SELECT * FROM Measurement                                              │
│                                                                         │
│  Returns: 10,000 measurements (all networks, all dates)                 │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         DATE PARSING                                    │
├─────────────────────────────────────────────────────────────────────────┤
│  "2024-01-01 00:00:00" → LocalDateTime(2024, 1, 1, 0, 0, 0)             │
│  "2024-12-31 23:59:59" → LocalDateTime(2024, 12, 31, 23, 59, 59)        │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         FILTERING LOOP                                  │
├─────────────────────────────────────────────────────────────────────────┤
│  For each of 10,000 measurements:                                       │
│                                                                         │
│  ┌─ Filter 1: Network ──────────────────────────────────────────────┐   │
│  │  measurement.networkCode == "NET_01"?                            │   │
│  │  5,000 pass → 5,000 fail (other networks)                        │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                           │                                             │
│                           ▼                                             │
│  ┌─ Filter 2: Start Date ───────────────────────────────────────────┐   │
│  │  measurement.timestamp >= 2024-01-01 00:00:00?                   │   │
│  │  4,500 pass → 500 fail (before start)                            │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                           │                                             │
│                           ▼                                             │
│  ┌─ Filter 3: End Date ─────────────────────────────────────────────┐   │
│  │  measurement.timestamp <= 2024-12-31 23:59:59?                   │   │
│  │  4,200 pass → 300 fail (after end)                               │   │
│  └──────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         FINAL RESULT                                    │
├─────────────────────────────────────────────────────────────────────────┤
│  this.measurements = ArrayList with 4,200 measurements                  │
│                                                                         │
│  All measurements where:                                                │
│  • networkCode = "NET_01"                                               │
│  • timestamp >= 2024-01-01 00:00:00                                     │
│  • timestamp <= 2024-12-31 23:59:59                                     │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 6. Time Complexity Analysis

| Phase | Operation | Time Complexity |
|-------|-----------|-----------------|
| Store parameters | Assignment | O(1) |
| Database query | `repo.read()` | O(N) where N = total measurements |
| Parse start date | String parsing | O(1) |
| Parse end date | String parsing | O(1) |
| Filter loop | Iterate all measurements | O(N) |
| Add to list | ArrayList add | O(1) amortized |

**Overall: O(N)** where N is the total number of measurements in the database.

### Memory Complexity

| Component | Memory Usage |
|-----------|--------------|
| `allMeasurements` | O(N) - temporarily holds all measurements |
| `this.measurements` | O(M) - holds filtered measurements (M ≤ N) |
| Date variables | O(1) - constant |

**Overall: O(N)** during construction, then O(M) after garbage collection.

---

## 7. Why Initialize Inside the Constructor?

### Your Question Explained

**Q: Why don't we initialize the measurements OUTSIDE the constructor?**

This is an excellent design question! Let's explore both approaches:

---

### Current Approach: Initialize IN Constructor

```java
public class NetworkReportImpl implements NetworkReport {
    private List<Measurement> measurements;
    
    // Everything happens in constructor
    public NetworkReportImpl(String networkCode, String startDate, String endDate) {
        // Load and filter measurements HERE
        this.measurements = filterMeasurements(networkCode, startDate, endDate);
    }
    
    public long getNumberOfMeasurements() {
        return measurements.size();  // Data already ready!
    }
}
```

---

### Alternative Approach: Initialize OUTSIDE Constructor

```java
public class NetworkReportImpl implements NetworkReport {
    private String networkCode;
    private String startDate;
    private String endDate;
    private List<Measurement> measurements;  // NOT initialized in constructor
    
    // Constructor only stores parameters
    public NetworkReportImpl(String networkCode, String startDate, String endDate) {
        this.networkCode = networkCode;
        this.startDate = startDate;
        this.endDate = endDate;
        // measurements NOT loaded here
    }
    
    // Separate method to load data
    public void loadMeasurements() {
        this.measurements = filterMeasurements(networkCode, startDate, endDate);
    }
    
    // Or load lazily when first accessed
    public long getNumberOfMeasurements() {
        if (measurements == null) {
            loadMeasurements();  // Load on first access
        }
        return measurements.size();
    }
}
```

---

### Comparison: Why Constructor Initialization Was Chosen

| Aspect | IN Constructor (Current) | OUTSIDE Constructor |
|--------|--------------------------|---------------------|
| **Object State** | Always valid and complete | May be incomplete/invalid |
| **Usage Simplicity** | Just create and use | Must remember to call `load()` |
| **Thread Safety** | Safer (immutable after creation) | Risk of race conditions |
| **Error Handling** | Fails fast at creation | Errors delayed until access |
| **Testing** | Harder to mock data | Easier to inject test data |
| **Performance** | Upfront cost | Deferred cost (lazy) |

---

### Detailed Reasons for Constructor Initialization

#### Reason 1: Object Consistency (Most Important)

```java
// WITH constructor initialization - SAFE
NetworkReportImpl report = new NetworkReportImpl("NET_01", start, end);
// Object is IMMEDIATELY ready to use
long count = report.getNumberOfMeasurements();  // Works!
Map<String, Double> ratios = report.getGatewaysLoadRatio();  // Works!

// WITHOUT constructor initialization - RISKY
NetworkReportImpl report = new NetworkReportImpl("NET_01", start, end);
// Object is NOT ready yet!
long count = report.getNumberOfMeasurements();  // NullPointerException! 💥
// Forgot to call loadMeasurements()
```

#### Reason 2: Fail-Fast Principle

```java
// WITH constructor initialization
try {
    NetworkReportImpl report = new NetworkReportImpl("NET_01", start, end);
    // If database fails, exception thrown HERE
} catch (Exception e) {
    // Handle error immediately
}

// WITHOUT constructor initialization
NetworkReportImpl report = new NetworkReportImpl("NET_01", start, end);  // Seems OK
// ... later in code ...
report.loadMeasurements();  // Error happens HERE - harder to trace
```

#### Reason 3: Immutability Pattern

```java
// Constructor initialization enables immutability
public class NetworkReportImpl {
    private final List<Measurement> measurements;  // Can be final!
    
    public NetworkReportImpl(...) {
        this.measurements = Collections.unmodifiableList(
            filterMeasurements(...)
        );
        // Now measurements can NEVER change
    }
}
```

#### Reason 4: No "Zombie Objects"

```
WITH Constructor Init:
┌─────────────────────────┐
│   NetworkReportImpl     │
│   ✓ Fully initialized   │
│   ✓ Ready to use        │
│   ✓ Valid state         │
└─────────────────────────┘

WITHOUT Constructor Init:
┌─────────────────────────┐
│   NetworkReportImpl     │
│   ? Partially created   │  ← "Zombie" object
│   ? May not be ready    │
│   ? Unknown state       │
└─────────────────────────┘
```

---

### When WOULD You Initialize Outside Constructor?

There are valid cases for NOT initializing in constructor:

#### Case 1: Lazy Loading (Performance)

```java
// If loading is expensive and might not be needed
public class LazyReport {
    private List<Measurement> measurements;
    
    public List<Measurement> getMeasurements() {
        if (measurements == null) {
            measurements = loadExpensiveData();  // Only load if actually needed
        }
        return measurements;
    }
}
```

#### Case 2: Dependency Injection (Testing)

```java
// Allow injecting mock data for tests
public class TestableReport {
    private List<Measurement> measurements;
    
    public TestableReport(String networkCode, ...) {
        // Don't load in constructor
    }
    
    // Setter for testing
    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }
}

// In test:
TestableReport report = new TestableReport("NET_01", ...);
report.setMeasurements(mockMeasurements);  // Inject test data
```

#### Case 3: Builder Pattern

```java
// More flexible construction
NetworkReport report = NetworkReportBuilder.create()
    .forNetwork("NET_01")
    .from("2024-01-01")
    .to("2024-12-31")
    .loadMeasurements()  // Explicit load step
    .build();
```

---

### Summary: Why This Project Uses Constructor Initialization

```
┌────────────────────────────────────────────────────────────────────┐
│                  DESIGN DECISION RATIONALE                         │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  1. SIMPLICITY                                                     │
│     → Create object, use immediately                               │
│     → No need to remember extra steps                              │
│                                                                    │
│  2. SAFETY                                                         │
│     → Object always in valid state                                 │
│     → No null pointer risks from uninitialized data                │
│                                                                    │
│  3. ENCAPSULATION                                                  │
│     → Loading logic hidden from users                              │
│     → Users don't need to know HOW data is loaded                  │
│                                                                    │
│  4. CONSISTENCY                                                    │
│     → All methods see same data snapshot                           │
│     → No race conditions or stale data                             │
│                                                                    │
│  5. PROJECT REQUIREMENTS                                           │
│     → Report represents a point-in-time snapshot                   │
│     → Data should not change after creation                        │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

The trade-off is that construction is slower (loads data immediately), but the object is **guaranteed** to be in a valid, usable state.

---

## 8. Potential Improvements

### 1. Database-Level Filtering

**Current approach:**
```java
List<Measurement> allMeasurements = repo.read();  // Gets ALL measurements
// Then filter in Java
```

**Improved approach:**
```java
// Add to MeasurementRepository
public List<Measurement> findByNetworkAndDateRange(
    String networkCode, 
    LocalDateTime start, 
    LocalDateTime end
) {
    String jpql = "SELECT m FROM Measurement m WHERE m.networkCode = :network";
    if (start != null) jpql += " AND m.timestamp >= :start";
    if (end != null) jpql += " AND m.timestamp <= :end";
    // Execute parameterized query
}
```

### 2. Extract Date Parsing Method

**Current approach (duplicated code):**
```java
int year = Integer.parseInt(startDate.substring(0, 4));
int month = Integer.parseInt(startDate.substring(5, 7));
// ... same pattern repeated for endDate
```

**Improved approach:**
```java
private LocalDateTime parseDate(String dateStr) {
    if (dateStr == null) return null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return LocalDateTime.parse(dateStr, formatter);
}

// Usage in constructor
startDateTime = parseDate(startDate);
endDateTime = parseDate(endDate);
```

### 3. Use Stream API for Filtering

**Current approach:**
```java
for (Measurement measurement : allMeasurements) {
    boolean belongsToNetwork = ...;
    if (!belongsToNetwork) continue;
    // ... more checks
    this.measurements.add(measurement);
}
```

**Improved approach:**
```java
this.measurements = allMeasurements.stream()
    .filter(m -> m.getNetworkCode().equals(networkCode))
    .filter(m -> startDateTime == null || !m.getTimestamp().isBefore(startDateTime))
    .filter(m -> endDateTime == null || !m.getTimestamp().isAfter(endDateTime))
    .collect(Collectors.toList());
```

### 4. Dependency Injection for Repository

**Current approach:**
```java
CRUDRepository<Measurement, Long> repo = new CRUDRepository<>(Measurement.class);
```

**Improved approach:**
```java
public NetworkReportImpl(
    String networkCode, 
    String startDate, 
    String endDate,
    CRUDRepository<Measurement, Long> measurementRepo  // Injected
) {
    // ... use injected repository
}
```

Benefits:
- Easier unit testing (can mock repository)
- Follows Dependency Injection principle
- Better separation of concerns

---

## Summary

The `NetworkReportImpl` constructor implements a straightforward **eager-loading filter pattern**:

1. **Load everything** from database
2. **Parse input parameters** into usable types
3. **Filter in memory** using simple boolean conditions
4. **Store results** for later method calls

This approach prioritizes **simplicity and consistency** over **performance optimization**, making it suitable for moderate data volumes but potentially problematic at scale.