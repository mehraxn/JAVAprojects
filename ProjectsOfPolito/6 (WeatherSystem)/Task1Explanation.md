# R1 Network - Complete Requirement Breakdown

## Overview
**Requirement R1** concerns the management of `Network` and `Operator` entities, together with a reporting part at network level.

---

## Part 1: Data Import Implementation

### Task 1.1: Implement `storeMeasurements` Method

**Reference Text (Section R1 > Data import):**
> For this requirement it is necessary to complete the implementation of the `storeMeasurements` method in the `DataImportingService` class:
> ```
> public static void storeMeasurements(String filePath)
> ```
> The method must:
> - read measurements from CSV files;
> - create the corresponding `Measurement` instances and save them in the database;
> - after each save, invoke the private method:
>   ```
>   private static void checkMeasurement(Measurement measurement)
>   ```

**What You Need to Do:**
1. **Read CSV files** from the provided filepath
   - CSV files are located at: `src/main/resources/csv`
   - CSV structure (from Section 2.1 > Class Measurement):
     ```
     date, networkCode, gatewayCode, sensorCode, value
     ```
   - Each field meaning:
     - `date`: timestamp in `WeatherReport.DATE_FORMAT` format (`yyyy-MM-dd HH:mm:ss`)
     - `networkCode`: code of the network (format: `NET_##`)
     - `gatewayCode`: code of the gateway (format: `GW_####`)
     - `sensorCode`: code of the sensor (format: `S_######`)
     - `value`: numeric measurement value

2. **Create Measurement objects** for each CSV row
   - Use the `Measurement` constructor:
     ```java
     new Measurement(networkCode, gatewayCode, sensorCode, value, timestamp)
     ```

3. **Save each measurement** to the database
   - Use `MeasurementRepository` which extends `CRUDRepository<Measurement, Long>`
   - Call the `create()` method to persist

4. **Call `checkMeasurement()`** after EACH save
   - This is critical for threshold violation detection
   - Must be called immediately after persisting each measurement

---

### Task 1.2: Implement `checkMeasurement` Method

**Reference Text (Section R1 > Threshold violation check):**
> In the `checkMeasurement(Measurement measurement)` method, it is necessary to implement the check of the possible thresholds associated with the sensor that produced the measurement.
>
> In particular, the logic must:
> - obtain the sensor corresponding to the measurement;
> - verify whether a `Threshold` has been defined for that sensor;
> - determine whether the measured value violates the configured threshold;
> - in case of violation, invoke the method:
>   ```
>   AlertingService.notifyThresholdViolation(...)
>   ```
>   as described in [section 2.5](#25-services) on notification services.

**Critical Implementation Note (from same section):**
> At this point there would theoretically be a direct dependency on the implementation of `Sensor` and `Threshold`.  
> However, this dependency is eliminated by the fact that, in the tests, the interaction with `CRUDRepository<Sensor, String>` is _mocked_: mocking consists in replacing the real implementation with a test object that simulates the expected behaviour and returns controlled data.  
> For this mechanism to work correctly, it is important to use the sensor referenced by the `currentSensor` variable already present in the method, without modifying the structure of the provided code but adding to it the operational logic for checking the saved value.

**What You Need to Do:**

1. **Use the provided `currentSensor` variable** (DO NOT MODIFY THE MOCKING CODE)
   ```java
   // THIS CODE IS PROVIDED - DO NOT CHANGE
   CRUDRepository<Sensor, String> sensorRepository = new CRUDRepository<>(Sensor.class);
   Sensor currentSensor = sensorRepository.read().stream()
       .filter(s -> measurement.getSensorCode().equals(s.getCode()))
       .findFirst()
       .orElse(null);
   ```

2. **Check if sensor and threshold exist**
   - Check: `currentSensor != null`
   - Check: `currentSensor.getThreshold() != null`

3. **Determine if threshold is violated**
   - Get the threshold: `Threshold t = currentSensor.getThreshold();`
   - Get threshold value: `t.getValue()`
   - Get threshold type: `t.getType()` (returns `ThresholdType` enum)
   
   **Threshold Types (from Section 2.1 > Class ThresholdType):**
   - `LESS_THAN`: violation if `measurement.getValue() < threshold.getValue()`
   - `GREATER_THAN`: violation if `measurement.getValue() > threshold.getValue()`
   - `LESS_OR_EQUAL`: violation if `measurement.getValue() <= threshold.getValue()`
   - `GREATER_OR_EQUAL`: violation if `measurement.getValue() >= threshold.getValue()`
   - `EQUAL`: violation if `measurement.getValue() == threshold.getValue()`
   - `NOT_EQUAL`: violation if `measurement.getValue() != threshold.getValue()`

4. **Notify operators if threshold is violated**
   
   **Reference Text (Section 2.5 > Notification service):**
   > `public static void notifyThresholdViolation(Collection<Operator> operators, String sensorCode)`  
   > used to notify the operators of a network when an out-of-threshold value is detected by a sensor;

   Steps:
   - Get the Network associated with the measurement: `measurement.getNetworkCode()`
   - Retrieve the Network entity from database using `CRUDRepository<Network, String>`
   - Get the operators: `network.getOperators()`
   - Call: `AlertingService.notifyThresholdViolation(operators, sensorCode)`

---

## Part 2: NetworkOperations Implementation

### Task 2.1: Understand the Interface

**Reference Text (Section R1 > NetworkOperations):**
> The `NetworkOperations` interface groups the functionalities for managing networks and operators, as well as for producing the network report.  
> The exposed methods have self-describing names; to understand when to throw the various exceptions it is sufficient to refer to the [section 2.6](#26-exceptions) dedicated to the exception model.

**Methods to Implement:**
From the `NetworkOperations.java` interface:
1. `createNetwork(String code, String name, String description, String username)`
2. `updateNetwork(String code, String name, String description, String username)`
3. `deleteNetwork(String code, String username)`
4. `getNetworks(String... codes)`
5. `createOperator(String firstName, String lastName, String email, String phoneNumber, String username)`
6. `addOperatorToNetwork(String networkCode, String operatorEmail, String username)`
7. `getNetworkReport(String code, String startDate, String endDate)`

---

### Task 2.2: Implement Network Management Operations

#### 2.2.1 Create Network

**Reference Text (Multiple sections):**

**Network Code Format (Section 2.1 > Class Network):**
> The code of a network must be a string that starts with **"NET\_"** and is followed by **two decimal digits**.

**Exception Rules (Section 2.6):**
> **InvalidInputDataException**: It is thrown when invalid, missing or non-conforming data are provided for mandatory attributes. Values for optional fields must not trigger this exception.
>
> **IdAlreadyInUseException**: It is thrown when an attempt is made to create a new element using a unique code that is already present in the system.
>
> **UnauthorizedException**: It is thrown when the username passed to the operation:
> - does not correspond to any existing user, or
> - corresponds to a user who does not have the required permissions to execute the operation.

**User Permissions (Section 2.1 > Class User):**
> A **VIEWER** user can only perform _read_ operations (consulting data and reports).
> A **MAINTAINER** user can perform both _read_ and _write_ operations (creation, update and deletion of entities and configurations).

**Timestamped Metadata (Section R1 > NetworkOperations):**
> The concrete implementation of `NetworkOperations` must:
> - use the metadata inherited from `Timestamped` to record creation and modification information of networks;

**Timestamped Fields (Section 2.1 > Class Timestamped):**
> The `Timestamped` class, extended by `Network`, `Gateway` and `Sensor`, contains the metadata needed to track:
> - who created the instance;
> - when it was created;
> - who last modified it;
> - when it was last modified.

**What You Need to Do:**

1. **Validate the username (authorization)**
   - Check: `username != null`
   - Retrieve User from database: `CRUDRepository<User, String>`
   - Check: User exists AND `user.getType() == UserType.MAINTAINER`
   - Throw `UnauthorizedException` if validation fails

2. **Validate the network code (mandatory)**
   - Check: `code != null && !code.isEmpty()`
   - Check format: Must match pattern `NET_\d{2}` (NET_ followed by exactly 2 digits)
   - Throw `InvalidInputDataException` if validation fails

3. **Check for duplicate code**
   - Query database: `networkRepo.read(code)`
   - If network exists, throw `IdAlreadyInUseException`

4. **Create the Network entity**
   - Optional fields: `name` and `description` can be null (do NOT throw exception)
   - Set `createdBy` = username
   - Set `createdAt` = `LocalDateTime.now()`
   - Set `modifiedBy` = username (same as creator initially)
   - Set `modifiedAt` = `LocalDateTime.now()` (same as creation time initially)

5. **Persist and return**
   - Use `CRUDRepository<Network, String>` to persist
   - Return the persisted Network entity

---

#### 2.2.2 Update Network

**Reference Text (Section R1 > NetworkOperations):**
> The concrete implementation of `NetworkOperations` must:
> - create, update and delete `Network` and `Operator` entities;
> - use the metadata inherited from `Timestamped` to record creation and modification information of networks;

**What You Need to Do:**

1. **Validate authorization** (same as create)
   - Username must be MAINTAINER

2. **Validate code is provided**
   - Check: `code != null`
   - Throw `InvalidInputDataException` if null

3. **Check network exists**
   - Query database: `networkRepo.read(code)`
   - If not found, throw `ElementNotFoundException`

4. **Update fields**
   - Set `name` = new name (can be null, it's optional)
   - Set `description` = new description (can be null, it's optional)
   - Set `modifiedBy` = username
   - Set `modifiedAt` = `LocalDateTime.now()`
   - Do NOT modify `createdBy` or `createdAt`

5. **Persist and return**
   - Use `networkRepo.update(network)`
   - Return the updated Network entity

---

#### 2.2.3 Delete Network

**Reference Text (Section R1 > NetworkOperations):**
> The concrete implementation of `NetworkOperations` must:
> - notify the deletion of a `Network` through the call:
>   ```
>   AlertingService.notifyDeletion(...)
>   ```

**AlertingService.notifyDeletion (Section 2.5):**
> `public static void notifyDeletion(String username, String code, Class<?> elementClass)`  
> used to notify the deletion of an element of type `Network`, `Gateway` or `Sensor`.  
> The method receives:
> - the username of the user performing the deletion;
> - the unique code of the deleted element;
> - the class of the element, to distinguish the type of entity involved.

**What You Need to Do:**

1. **Validate authorization** (MAINTAINER only)

2. **Validate code provided**
   - Check: `code != null`
   - Throw `InvalidInputDataException` if null

3. **Check network exists**
   - Query database: `networkRepo.read(code)`
   - If not found, throw `ElementNotFoundException`

4. **Delete from database**
   - Use `networkRepo.delete(code)`
   - Store the deleted entity to return it

5. **Notify deletion**
   - Call: `AlertingService.notifyDeletion(username, code, Network.class)`
   - This must be called AFTER successful deletion

6. **Return the deleted entity**

---

#### 2.2.4 Get Networks

**Reference Text (Section R1 > NetworkOperations):**
> The method
> ```
> Collection<Network> getNetworks(String... codes)
> ```
> allows obtaining all the `Network` objects whose code is passed in the method's parameter list. If a code passed as input does not correspond to an element present in the system, it is simply ignored. If the method is invoked without any input parameters, it must return all `Network` elements present in the system.

**What You Need to Do:**

1. **Check if no codes provided**
   - If `codes == null || codes.length == 0`
   - Return ALL networks: `networkRepo.read()`

2. **If codes are provided**
   - Create result collection: `List<Network>`
   - For each code in the input:
     - Query: `networkRepo.read(code)`
     - If found (not null), add to result
     - If not found, ignore (do NOT throw exception)
   - Return the result collection

**NO AUTHORIZATION CHECK** - This is a read operation, available to all users

---

### Task 2.3: Implement Operator Management

#### 2.3.1 Create Operator

**Reference Text (Section 2.1 > Class Operator):**
> An _operator_ is an entity that receives notifications when a threshold violation is detected.  
> It is uniquely identified by its **email address**.  
> It has first name, last name and may also have a phone number.  
> The same operator may be responsible for multiple networks.

**Method Signature (from NetworkOperations.java):**
```java
Operator createOperator(String firstName, String lastName, String email, 
                       String phoneNumber, String username)
```

**What You Need to Do:**

1. **Validate authorization** (MAINTAINER only)

2. **Validate mandatory fields**
   - Check: `email != null && !email.isEmpty()`
   - Check: `firstName != null`
   - Check: `lastName != null`
   - Throw `InvalidInputDataException` if any mandatory field is invalid
   - Note: `phoneNumber` is optional, can be null

3. **Check for duplicate email**
   - Query database: `operatorRepo.read(email)`
   - If operator exists, throw `IdAlreadyInUseException`

4. **Create and persist**
   - Create: `new Operator(email, firstName, lastName, phoneNumber)`
   - Persist: `operatorRepo.create(operator)`
   - Return the persisted operator

---

#### 2.3.2 Add Operator to Network

**Method Signature:**
```java
Network addOperatorToNetwork(String networkCode, String operatorEmail, String username)
```

**What You Need to Do:**

1. **Validate authorization** (MAINTAINER only)

2. **Validate parameters**
   - Check: `networkCode != null`
   - Check: `operatorEmail != null`
   - Throw `InvalidInputDataException` if any is null

3. **Check network exists**
   - Query: `networkRepo.read(networkCode)`
   - If not found, throw `ElementNotFoundException`

4. **Check operator exists**
   - Query: `operatorRepo.read(operatorEmail)`
   - If not found, throw `ElementNotFoundException`

5. **Add operator to network's collection**
   - Get network's operators: `network.getOperators()`
   - Add operator to collection (check for duplicates first to avoid adding twice)
   - Update `modifiedBy` = username
   - Update `modifiedAt` = `LocalDateTime.now()`

6. **Persist and return**
   - Use `networkRepo.update(network)`
   - Return the updated network

---

## Part 3: NetworkReport Implementation

### Task 3.1: Understand Report Requirements

**Reference Text (Section 3.3 > Reporting):**
> The report returned as output by the functions that compute it is based on the `startDate` and `endDate` parameters. These parameters:
> - do not consider the timezone, i.e. they are expressed as absolute dates;
> - are optional (they may be _null_);
> - are in the `DATE_FORMAT` format defined in `WeatherReport`;
> - delimit the inclusive time interval of the measurements to consider;
> - if the value is null, the interval is not limited in that direction (for example: `startDate = null` means that there is no lower bound; `endDate = null` means that there is no upper bound).

**Date Format (from WeatherReport.java):**
```java
public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
```

---

### Task 3.2: Implement NetworkReport Interface

**Reference Text (Section R1 > NetworkReport):**
> The `NetworkReport` must contain:
> - `networkCode`: the code passed as input;
> - `startDate` / `endDate`: the received strings (possibly null);
> - `numberOfMeasurements`: total number of measurements of the network in the interval;
> - `mostActiveGateways`: gateways with the highest number of measurements;
> - `leastActiveGateways`: gateways with the lowest number of measurements;
> - `gatewaysLoadRatio`: map `<gatewayCode, ratio>`
>   - `ratio` is the percentage of measurements generated by a single gateway with respect to the total of the network.
> - `histogram`: map `<Range<LocalDateTime>, count>`.

---

#### 3.2.1 Filter Measurements

**What You Need to Do:**

1. **Get all measurements from database**
   - Use `CRUDRepository<Measurement, Long>` or `MeasurementRepository`
   - Call `read()` to get all measurements

2. **Filter by network code**
   - Keep only measurements where `measurement.getNetworkCode().equals(networkCode)`

3. **Filter by time interval**
   - Parse `startDate` and `endDate` using `DateTimeFormatter` with `DATE_FORMAT`
   - If `startDate != null`: keep only measurements where `timestamp >= startDate`
   - If `endDate != null`: keep only measurements where `timestamp <= endDate`
   - If both are null: include all measurements for this network

4. **Store filtered measurements** for use in other methods

---

#### 3.2.2 Implement Basic Report Methods

**Simple Methods:**

1. **getCode()**: Return the `networkCode` passed to constructor

2. **getStartDate()**: Return the `startDate` string (can be null)

3. **getEndDate()**: Return the `endDate` string (can be null)

4. **getNumberOfMeasurements()**: Return count of filtered measurements

---

#### 3.2.3 Calculate Most/Least Active Gateways

**What You Need to Do:**

1. **Count measurements per gateway**
   - Create a map: `Map<String, Long>` (gatewayCode -> count)
   - For each filtered measurement:
     - Get `measurement.getGatewayCode()`
     - Increment count for that gateway

2. **Find maximum count** (for mostActiveGateways)
   - Iterate through map values to find the maximum

3. **Collect all gateways with maximum count**
   - Create result collection
   - Add all gateway codes where count == maximum
   - Return collection

4. **Find minimum count** (for leastActiveGateways)
   - Same process but find minimum count instead

**Special Case:**
- If no measurements, return empty collection

---

#### 3.2.4 Calculate Gateways Load Ratio

**Reference Text:**
> `gatewaysLoadRatio`: map `<gatewayCode, ratio>`
> - `ratio` is the percentage of measurements generated by a single gateway with respect to the total of the network.

**What You Need to Do:**

1. **Get total measurements count**
   - Total = number of filtered measurements

2. **Count measurements per gateway**
   - Same as before: `Map<String, Long>`

3. **Calculate percentage for each gateway**
   - For each gateway:
     - ratio = (gatewayCount / total) * 100.0
     - Store in result map: `Map<String, Double>`

4. **Return the map**

**Special Case:**
- If total == 0, return empty map

---

#### 3.2.5 Implement Histogram

This is the most complex part of the report.

**Reference Text (Section R1 > NetworkReport > histogram):**
> `histogram`: map `<Range<LocalDateTime>, count>`.  
> The map groups the network's measurements into consecutive time buckets whose granularity (hourly or daily) depends on the duration of the requested interval or, if no interval is provided, on the effective range of available measurements.  
> Each `Range<LocalDateTime>` key contains the exact start and end instants of the bucket, together with its unit (`HOUR` or `DAY`).

**Complete Histogram Documentation (from NetworkReport.java):**
```
Returns the number of measurements included in this network report,
grouped into consecutive time buckets.

The method only considers measurements that belong to the current Network
and that fall within an effective interval [effectiveStart, effectiveEnd],
defined as follows:

- if startDate is non-null, effectiveStart is the parsed value of startDate;
  otherwise, effectiveStart is the timestamp of the earliest measurement
  available for this Network (or null if no measurements exist);
- if endDate is non-null, effectiveEnd is the parsed value of endDate;
  otherwise, effectiveEnd is the timestamp of the latest measurement
  available for this Network (or null if no measurements exist).

If either effectiveStart or effectiveEnd is null (i.e. there are no
measurements for this Network), the method returns an empty map.

Once [effectiveStart, effectiveEnd] has been determined, the bucket
granularity is selected as follows:

- if both effectiveStart and effectiveEnd are non-null and the duration
  between them is less than or equal to 48 hours, the interval is
  partitioned into hourly buckets;
- in all other cases, the interval is partitioned into daily buckets.

Buckets always cover sub-intervals of [effectiveStart, effectiveEnd] and
are built by intersecting the logical hour/day units with the effective
interval...
```

**Reference Text (Section 3.3 > Histogram range semantics):**
> All histograms in the Weather Report system are represented using `Report.Range<T>` keys. Unless otherwise stated:
> - ranges are **left-closed and right-open** (`[start, end)`): a value `v` belongs to a bucket if `start ≤ v < end`;
> - the **last bucket** of each histogram is **left and right closed** (`[start, end]` or `start ≤ v ≤ end`), so that the maximum value observed in the interval is always included in some bucket.

**Step-by-Step Implementation:**

**Step 1: Determine Effective Interval**

1. Find minimum and maximum timestamps in filtered measurements
2. Calculate `effectiveStart`:
   - If `startDate != null`: parse it
   - Else: use minimum timestamp from measurements
   - If no measurements: null
3. Calculate `effectiveEnd`:
   - If `endDate != null`: parse it  
   - Else: use maximum timestamp from measurements
   - If no measurements: null
4. If either is null, return empty map

**Step 2: Determine Granularity**

1. Calculate duration: `ChronoUnit.HOURS.between(effectiveStart, effectiveEnd)`
2. If duration <= 48 hours: use **hourly** buckets
3. Otherwise: use **daily** buckets

**Step 3: Create Buckets**

For **hourly buckets**:
- Start at `effectiveStart`
- Each bucket normally covers one full hour (e.g., 10:00:00 to 10:59:59)
- First bucket: starts at `effectiveStart`, ends at next hour boundary (or `effectiveEnd` if sooner)
- Middle buckets: full hours
- Last bucket: starts at last hour boundary, ends at `effectiveEnd` (inclusive)

For **daily buckets**:
- Start at `effectiveStart`
- Each bucket normally covers one full day (00:00:00 to 23:59:59)
- First bucket: starts at `effectiveStart`, ends at next day boundary (or `effectiveEnd` if sooner)
- Middle buckets: full days
- Last bucket: starts at last day boundary, ends at `effectiveEnd` (inclusive)

**Step 4: Count Measurements in Each Bucket**

For each bucket (Range<LocalDateTime>):
1. Count measurements where bucket.contains(measurement.getTimestamp())
2. Remember: all buckets except last are `[start, end)`
3. Last bucket is `[start, end]` (inclusive on both ends)

**Step 5: Return SortedMap**

- Use `TreeMap` with comparator based on start time
- Buckets must be in ascending chronological order
- Each entry: `Range<LocalDateTime>` -> count

**Step 6: Create Range Objects**

You need to create Range<LocalDateTime> objects that:
- Store `start` and `end` timestamps
- Implement `getStart()`, `getEnd()`, and `contains(LocalDateTime)` methods
- `contains()` must respect the histogram convention (left-closed, right-open, except last)

---

## Part 4: OperationsFactory Update

### Task 4.1: Update Factory Method

**Reference Text (Section R1 > OperationsFactory):**
> To complete requirement R1 it is necessary to:
> - provide a concrete implementation of `NetworkOperations`;
> - update the `OperationsFactory` class so that the method:
>   ```
>   public static NetworkOperations getNetworkOperations()
>   ```
>   returns an instance of this implementation.

**What You Need to Do:**

1. **Create a concrete class implementing NetworkOperations**
   - Example: `NetworkOperationsImpl implements NetworkOperations`

2. **Update OperationsFactory.java**
   ```java
   public static NetworkOperations getNetworkOperations() {
       return new NetworkOperationsImpl();
   }
   ```

---

## Summary: Complete Task Checklist for R1

### Data Import
- [ ] Implement `storeMeasurements()` to read CSV, create Measurements, persist, and call checkMeasurement
- [ ] Implement `checkMeasurement()` to detect threshold violations and notify operators

### Network Operations
- [ ] Implement `createNetwork()` with validation, authorization, and Timestamped tracking
- [ ] Implement `updateNetwork()` with validation and Timestamped updates
- [ ] Implement `deleteNetwork()` with notification
- [ ] Implement `getNetworks()` with optional filtering
- [ ] Implement `createOperator()` with validation
- [ ] Implement `addOperatorToNetwork()` with proper associations

### Network Report
- [ ] Filter measurements by network code and time interval
- [ ] Implement basic getters (code, dates, count)
- [ ] Calculate most/least active gateways
- [ ] Calculate gateways load ratio
- [ ] Implement complex histogram with hourly/daily granularity

### Factory
- [ ] Create concrete implementation class
- [ ] Update `OperationsFactory.getNetworkOperations()`

---

## Key Design Principles

**From Section 2.6 (Exceptions):**
> The described exceptions have no priority among them: the order in which they may be thrown is not relevant when an operation violates multiple conditions simultaneously.

**From Section 2.1 (User Permissions):**
- ALL write operations (create, update, delete) require MAINTAINER permission
- Read operations can be performed by anyone

**From Section 3.3 (Reporting - Statistics):**
> If the number of available measurements is less than 2, the values of variance and standard deviation are not meaningful and must be set to `0`, and the possible set of outliers is empty.

**From Section 2.1 (Timestamped):**
- ALWAYS set `createdBy` and `createdAt` when creating
- ALWAYS set `modifiedBy` and `modifiedAt` when updating
- NEVER modify `createdBy` or `createdAt` during updates