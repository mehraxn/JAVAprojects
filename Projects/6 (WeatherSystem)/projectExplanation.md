# WEATHER REPORT PROJECT - COMPLETE EXPLANATION

---

## TABLE OF CONTENTS

1. [Project Overview - What is This?](#1-project-overview---what-is-this)
2. [System Architecture - How It's Built](#2-system-architecture---how-its-built)
3. [Data Model - All The Entities](#3-data-model---all-the-entities)
4. [Technical Infrastructure](#4-technical-infrastructure)
5. [Your Tasks - The 4 Requirements](#5-your-tasks---the-4-requirements)
6. [Requirement R1: Network Management](#6-requirement-r1-network-management)
7. [Requirement R2: Gateway Management](#7-requirement-r2-gateway-management)
8. [Requirement R3: Sensor Management](#8-requirement-r3-sensor-management)
9. [Requirement R4: Topology Integration](#9-requirement-r4-topology-integration)
10. [Git Workflow and Code Review](#10-git-workflow-and-code-review)
11. [Exception Handling Rules](#11-exception-handling-rules)
12. [Statistics and Mathematical Formulas](#12-statistics-and-mathematical-formulas)
13. [Critical Implementation Details](#13-critical-implementation-details)

---

## 1. PROJECT OVERVIEW - WHAT IS THIS?

### Simple Explanation

Imagine you have hundreds of weather sensors scattered around a city. These sensors are organized into groups (gateways), and these groups belong to larger networks. The sensors constantly send measurements like temperature, humidity, or air quality.

**Your job**: Build a Java system that:
- Stores all this data in a database
- Organizes sensors, gateways, and networks
- Detects when sensors report unusual values (outliers)
- Creates reports showing what's happening
- Notifies people when something goes wrong

### Real-World Scenario

Think of it like this:
- **Network**: The entire weather monitoring system for a city district
- **Gateway**: A control station that manages sensors on one street
- **Sensor**: A single device measuring temperature
- **Operator**: The person who gets an alert when sensors detect dangerous values

---

## 2. SYSTEM ARCHITECTURE - HOW IT'S BUILT

### The Façade Pattern

The system uses a **WeatherReport** class as the main entry point. This is called a "façade" - one simple interface that hides all the complexity behind it.

```
User → WeatherReport → Operations Interfaces → Repositories → Database
```

### Main Components

1. **WeatherReport (Façade)**
   - The single entry point for external code
   - Provides utility methods (create users, import data)
   - Gives access to 4 operation interfaces

2. **Operations Interfaces**
   - NetworkOperations: Manage networks and operators
   - GatewayOperations: Manage gateways and parameters
   - SensorOperations: Manage sensors and thresholds
   - TopologyOperations: Connect everything together

3. **OperationsFactory**
   - Returns the concrete implementations of each operations interface
   - Central configuration point

4. **Repositories**
   - CRUDRepository: Generic database operations (Create, Read, Update, Delete)
   - MeasurementRepository: Specific operations for measurements

5. **Services**
   - AlertingService: Sends notifications
   - DataImportingService: Loads measurements from CSV files

---

## 3. DATA MODEL - ALL THE ENTITIES

### Network

**What it is**: A logical grouping of monitoring equipment, like "Downtown Weather Network"

**Attributes**:
- `code` (unique identifier): Must start with "NET_" followed by exactly 2 digits
  - Valid: "NET_01", "NET_99"
  - Invalid: "NET_1", "NET_100", "NETWORK_01"
- `name` (optional): Human-readable name
- `description` (optional): What this network monitors
- `operators` (list): People who receive alerts

**Important Notes**:
- The code is the PRIMARY KEY
- Networks extend `Timestamped` (tracks who created/modified and when)
- When deleted, must call `AlertingService.notifyDeletion()`

---

### Operator

**What it is**: A person responsible for monitoring, receives alerts

**Attributes**:
- `email` (unique identifier): The operator's email address (PRIMARY KEY)
- `firstName` (required): First name
- `lastName` (required): Last name
- `phoneNumber` (optional): Contact number

**Important Notes**:
- One operator can be assigned to multiple networks
- Email is the unique identifier
- Operators receive notifications when thresholds are violated

---

### Gateway

**What it is**: Groups sensors that measure the same type of thing (e.g., all temperature sensors on one street)

**Attributes**:
- `code` (unique identifier): Must start with "GW_" followed by exactly 4 digits
  - Valid: "GW_0001", "GW_9999"
  - Invalid: "GW_001", "GW_10000"
- `name` (optional): Human-readable name
- `description` (optional): What this gateway does
- `parameters` (list): Configuration values

**Important Notes**:
- Extends `Timestamped`
- When deleted, must call `AlertingService.notifyDeletion()`
- Can have special parameters (see below)

---

### Parameter

**What it is**: Configuration or state information for a gateway

**Attributes**:
- `code` (unique within the gateway): Identifies the parameter
- `name` (optional): Description
- `description` (optional): More details
- `value` (numeric): The parameter's value

**Special Reserved Parameters**:

1. **EXPECTED_MEAN**: The expected average value for sensors
2. **EXPECTED_STD_DEV**: The expected standard deviation
3. **BATTERY_CHARGE**: Current battery level (percentage)

**Important Notes**:
- Parameter codes must be unique WITHIN each gateway
- Different gateways can have parameters with the same code
- Special parameters are used in report calculations

---

### Sensor

**What it is**: A physical device that measures something and sends data

**Attributes**:
- `code` (unique identifier): Must start with "S_" followed by exactly 6 digits
  - Valid: "S_000001", "S_999999"
  - Invalid: "S_00001", "S_1000000"
- `name` (optional): Human-readable name
- `description` (optional): What this sensor measures
- `threshold` (optional): Alert limit for measurements

**Important Notes**:
- Extends `Timestamped`
- When deleted, must call `AlertingService.notifyDeletion()`
- May have one threshold defined

---

### Threshold

**What it is**: Defines when a sensor's measurement is considered abnormal

**Attributes**:
- `value` (numeric, required): The limit value
- `comparisonType` (required): How to compare measurements to the threshold
  - Possible types: GREATER_THAN, LESS_THAN, EQUAL_TO, etc.

**Important Notes**:
- ALWAYS has both value and comparison type
- Used to detect anomalous measurements
- When violated, operators are notified

---

### Measurement

**What it is**: A single data point recorded by a sensor

**Attributes (ALL REQUIRED)**:
- `networkCode`: Which network this belongs to
- `gatewayCode`: Which gateway produced it
- `sensorCode`: Which sensor measured it
- `timestamp`: When it was recorded
- `value`: The measured value (numeric)

**Important Notes**:
- Read from CSV files in `src/main/resources/csv`
- CSV format: `date, networkCode, gatewayCode, sensorCode, value`
- Date format is defined by `WeatherReport.DATE_FORMAT`
- After saving each measurement, must call `checkMeasurement()`

---

### Timestamped (Abstract Base Class)

**What it is**: Base class for Network, Gateway, and Sensor

**Attributes**:
- `createdBy`: Username who created this
- `createdAt`: When it was created
- `lastModifiedBy`: Username who last changed this
- `lastModifiedAt`: When it was last changed

**Important Notes**:
- Automatically tracked by the system
- Updated on create and update operations

---

### User

**What it is**: Someone who uses the Weather Report system

**Attributes (BOTH REQUIRED)**:
- `username` (unique identifier): Login name
- `type`: Permission level

**User Types**:
1. **VIEWER**: Can only READ (view data, see reports)
2. **MAINTAINER**: Can READ and WRITE (create, update, delete)

**Important Notes**:
- Username is the PRIMARY KEY
- Type determines what operations are allowed
- Unauthorized operations throw `UnauthorizedException`

---

## 4. TECHNICAL INFRASTRUCTURE

### Persistence Layer

**Technologies**:
- **JPA/Hibernate**: Object-Relational Mapping
- **H2 Database**: In-memory relational database

**Why This Matters**:
- All entities are stored in database tables
- Hibernate automatically converts Java objects to SQL
- No direct SQL queries - use JPQL instead

### Repository Pattern

**CRUDRepository**:
```java
- save(entity): Create or update
- findById(id): Get one by ID
- findAll(): Get all entities
- delete(entity): Remove from database
```

**MeasurementRepository**:
- Extends CRUDRepository
- Specialized for Measurement entities
- Place for future complex queries

---

## 5. YOUR TASKS - THE 4 REQUIREMENTS

### Overview

You have **4 requirements (R1, R2, R3, R4)** to implement:

**R1, R2, R3** are independent - can be done in parallel by different team members:
- R1: Networks and Operators
- R2: Gateways and Parameters
- R3: Sensors and Thresholds

**R4** is integration - combines everything after R1-R3 are done:
- R4: Topology (connecting networks, gateways, sensors)

### Branch Strategy

Each requirement gets its own Git branch:

**Branch Naming**: `X-rN` where:
- X = any number (usually issue number)
- N = requirement number (1, 2, 3, or 4)

**Examples**:
- `1-r1` (minimal)
- `1-r1-network-management` (with description)
- `5-r2-gateway-features` (issue 5, requirement 2)

**Rules**:
- R1, R2, R3: Only ONE person commits to each branch
- R4: ALL team members can commit (integration phase)

---

## 6. REQUIREMENT R1: NETWORK MANAGEMENT

### What You Build

Implement everything related to Networks and Operators.

### Task 1: Data Import Service (Part 1 of 3)

**File**: `DataImportingService.java`

**Method to implement**: `storeMeasurements(String filePath)`

**What it does**:
1. Read CSV file from the given path
2. Parse each line into a Measurement object
3. Save each measurement to the database using MeasurementRepository
4. After EACH save, call `checkMeasurement(measurement)`

**CSV File Format**:
```
date, networkCode, gatewayCode, sensorCode, value
2024-01-15T10:30:00, NET_01, GW_0001, S_000001, 23.5
```

**Important Notes**:
- Files are in `src/main/resources/csv`
- Date format: Use `WeatherReport.DATE_FORMAT`
- Must call `checkMeasurement()` after each measurement is saved

---

### Task 2: Threshold Violation Check

**File**: `DataImportingService.java`

**Method to implement**: `checkMeasurement(Measurement measurement)` (private)

**What it does**:
1. Get the sensor that produced this measurement
2. Check if the sensor has a threshold defined
3. If yes, check if the measurement value violates the threshold
4. If violated, call `AlertingService.notifyThresholdViolation(operators, sensorCode)`

**Threshold Violation Logic**:
```
Example:
Threshold: value = 30, comparisonType = GREATER_THAN
Measurement value = 35
Result: VIOLATION (35 > 30)
```

**Important Notes**:
- Use the `currentSensor` variable already in the method
- Don't modify the method structure, just add logic
- Tests use mocking - this is why the structure matters
- The operators to notify are from the NETWORK that owns the sensor

---

### Task 3: NetworkOperations Implementation

**File**: Create a new class (e.g., `NetworkOperationsImpl.java`)

**Interface to implement**: `NetworkOperations`

**Methods to implement**:

#### 3.1 Create Network
```java
Network createNetwork(String username, String code, String name, String description)
```

**What it does**:
- Create a new Network object
- Validate code format (NET_XX where XX are 2 digits)
- Validate username (must exist and be MAINTAINER)
- Set Timestamped fields (createdBy, createdAt)
- Save to database

**Throws**:
- `InvalidInputDataException`: Bad code format or missing required data
- `IdAlreadyInUseException`: Code already exists
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 3.2 Update Network
```java
Network updateNetwork(String username, String code, String name, String description)
```

**What it does**:
- Find existing network by code
- Update name and/or description
- Update Timestamped fields (lastModifiedBy, lastModifiedAt)
- Save to database

**Throws**:
- `ElementNotFoundException`: Network code doesn't exist
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 3.3 Delete Network
```java
void deleteNetwork(String username, String code)
```

**What it does**:
- Find network by code
- Delete from database
- Call `AlertingService.notifyDeletion(username, code, Network.class)`

**Throws**:
- `ElementNotFoundException`: Network doesn't exist
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 3.4 Get Networks
```java
Collection<Network> getNetworks(String... codes)
```

**What it does**:
- If no codes provided: Return ALL networks
- If codes provided: Return networks matching those codes
- If a code doesn't exist: Ignore it (don't throw exception)

**Example**:
```java
getNetworks()  // Returns all networks
getNetworks("NET_01", "NET_02", "NET_99")  // Returns these 3 if they exist
getNetworks("NET_01", "NET_XX")  // Returns only NET_01 (NET_XX ignored)
```

---

#### 3.5 Add Operator to Network
```java
void addOperator(String username, String networkCode, Operator operator)
```

**What it does**:
- Validate user permissions
- Find the network
- Create operator if doesn't exist (use email as ID)
- Add operator to network's operator list
- Save

**Throws**:
- `InvalidInputDataException`: Invalid operator data
- `ElementNotFoundException`: Network doesn't exist
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 3.6 Remove Operator from Network
```java
void removeOperator(String username, String networkCode, String operatorEmail)
```

**What it does**:
- Validate user permissions
- Find network
- Remove operator from network's list
- Save

**Throws**:
- `ElementNotFoundException`: Network or operator doesn't exist
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 3.7 Generate Network Report
```java
NetworkReport generateNetworkReport(String networkCode, String startDate, String endDate)
```

**What it does**: Create a comprehensive report about the network's activity

**Report Contents**:

1. **networkCode**: The code you're reporting on

2. **startDate / endDate**: The dates received (can be null)

3. **numberOfMeasurements**: Count all measurements in the time range

4. **mostActiveGateways**: Gateway codes with the MOST measurements
   - If multiple gateways have the same max count, include all of them
   - Return as a Collection<String>

5. **leastActiveGateways**: Gateway codes with the LEAST measurements
   - If multiple gateways have the same min count, include all of them
   - Return as Collection<String>

6. **gatewaysLoadRatio**: Map<String, Double>
   - Key: Gateway code
   - Value: Percentage of total measurements
   - Formula: (gateway measurements / total measurements) × 100
   - Example: Gateway GW_0001 has 50 measurements out of 200 total = 25.0

7. **histogram**: Map<Range<LocalDateTime>, Long>
   - Groups measurements into time buckets
   - Bucket size depends on time range

**Histogram Rules**:

**Granularity**:
- **HOURLY buckets** if time range ≤ 24 hours
- **DAILY buckets** if time range > 24 hours

**Bucket Boundaries**:
- All buckets are **[start, end)** (left-closed, right-open)
- EXCEPT the last bucket which is **[start, end]** (both closed)
- This ensures the maximum timestamp is included

**Example Hourly Histogram**:
```
Time range: 2024-01-15T10:00 to 2024-01-15T13:00

Buckets:
[2024-01-15T10:00, 2024-01-15T11:00) → 15 measurements
[2024-01-15T11:00, 2024-01-15T12:00) → 23 measurements
[2024-01-15T12:00, 2024-01-15T13:00] → 18 measurements (last bucket, includes 13:00)
```

**Example Daily Histogram**:
```
Time range: 2024-01-10 to 2024-01-12

Buckets:
[2024-01-10T00:00, 2024-01-11T00:00) → 450 measurements
[2024-01-11T00:00, 2024-01-12T00:00) → 523 measurements
[2024-01-12T00:00, 2024-01-13T00:00] → 498 measurements
```

**Important Notes**:
- Must return a SortedMap ordered by start time
- If startDate is null: no lower bound
- If endDate is null: no upper bound
- Date format: `WeatherReport.DATE_FORMAT`
- Time interval is INCLUSIVE of both boundaries

---

### Task 4: Update OperationsFactory

**File**: `OperationsFactory.java`

**Method to modify**:
```java
public static NetworkOperations getNetworkOperations()
```

**What it does**: Return an instance of your NetworkOperations implementation

**Example**:
```java
public static NetworkOperations getNetworkOperations() {
    return new NetworkOperationsImpl();
}
```

---

### R1 Summary Checklist

- [ ] Implement `storeMeasurements()` in DataImportingService
- [ ] Implement `checkMeasurement()` in DataImportingService
- [ ] Create NetworkOperations implementation class
- [ ] Implement all 7 NetworkOperations methods
- [ ] Generate correct NetworkReport with all 7 fields
- [ ] Update OperationsFactory to return your implementation
- [ ] Handle all exceptions correctly
- [ ] Use Timestamped fields properly
- [ ] Call AlertingService.notifyDeletion() on delete
- [ ] Call AlertingService.notifyThresholdViolation() on violations

---

## 7. REQUIREMENT R2: GATEWAY MANAGEMENT

### What You Build

Implement everything related to Gateways and Parameters.

### Task 1: Data Import Service (Part 2 of 3)

**File**: `DataImportingService.java`

**Method to implement**: `storeMeasurements(String filePath)`

**What it does**:
1. Read CSV file
2. Parse each line into Measurement
3. Save to database
4. Call `checkMeasurement(measurement)` after EACH save

**Important Notes**:
- Same as R1, but you implement it independently in your branch
- You MUST call `checkMeasurement()` even though you don't implement its body
- This is for consistency - R1 branch implements the body

---

### Task 2: GatewayOperations Implementation

**File**: Create a new class (e.g., `GatewayOperationsImpl.java`)

**Interface to implement**: `GatewayOperations`

**Methods to implement**:

#### 2.1 Create Gateway
```java
Gateway createGateway(String username, String code, String name, String description)
```

**What it does**:
- Create new Gateway object
- Validate code format (GW_XXXX where XXXX are 4 digits)
- Validate username (must exist and be MAINTAINER)
- Set Timestamped fields
- Save to database

**Throws**:
- `InvalidInputDataException`: Bad code format
- `IdAlreadyInUseException`: Code already exists
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 2.2 Update Gateway
```java
Gateway updateGateway(String username, String code, String name, String description)
```

**What it does**:
- Find existing gateway
- Update name and/or description
- Update Timestamped fields
- Save

**Throws**:
- `ElementNotFoundException`: Gateway doesn't exist
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 2.3 Delete Gateway
```java
void deleteGateway(String username, String code)
```

**What it does**:
- Find gateway
- Delete from database
- Call `AlertingService.notifyDeletion(username, code, Gateway.class)`

**Throws**:
- `ElementNotFoundException`: Gateway doesn't exist
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 2.4 Get Gateways
```java
Collection<Gateway> getGateways(String... codes)
```

**What it does**:
- If no codes: Return ALL gateways
- If codes provided: Return matching gateways
- Ignore non-existent codes

---

#### 2.5 Set Parameter
```java
void setParameter(String username, String gatewayCode, String parameterCode, 
                  String name, String description, Double value)
```

**What it does**:
- Validate user permissions
- Find gateway
- If parameter exists: Update it
- If parameter doesn't exist: Create new one
- Save

**Important Notes**:
- Parameter code must be unique WITHIN the gateway
- Different gateways can have same parameter code
- Value is required (cannot be null)

**Throws**:
- `InvalidInputDataException`: Missing required data
- `ElementNotFoundException`: Gateway doesn't exist
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 2.6 Remove Parameter
```java
void removeParameter(String username, String gatewayCode, String parameterCode)
```

**What it does**:
- Validate user permissions
- Find gateway
- Remove parameter from gateway
- Save

**Throws**:
- `ElementNotFoundException`: Gateway or parameter doesn't exist
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 2.7 Generate Gateway Report
```java
GatewayReport generateGatewayReport(String gatewayCode, String startDate, String endDate)
```

**What it does**: Create a comprehensive report about the gateway

**Report Contents**:

1. **code**: Gateway code

2. **startDate / endDate**: Input dates (can be null)

3. **numberOfMeasurements**: Total count in time range

4. **mostActiveSensors**: Sensor codes with MOST measurements
   - Collection<String>
   - Include all sensors tied for maximum

5. **leastActiveSensors**: Sensor codes with LEAST measurements
   - Collection<String>
   - Include all sensors tied for minimum

6. **sensorsLoadRatio**: Map<String, Double>
   - Key: Sensor code
   - Value: Percentage of gateway's total measurements
   - Formula: (sensor measurements / gateway total) × 100

7. **outlierSensors**: List<String>
   - Sensor codes whose MEAN value is anomalous
   - See formula below

8. **batteryChargePercentage**: Double
   - Current value of BATTERY_CHARGE parameter
   - Does NOT depend on time range
   - Return null if parameter doesn't exist

9. **histogram**: Map<Range<Duration>, Long>
   - Distribution of inter-arrival times
   - See detailed explanation below

---

### Outlier Sensors Calculation

A sensor is an **outlier** if its mean differs significantly from expected.

**Formula**:
```
|sensor_mean - EXPECTED_MEAN| ≥ 2 × EXPECTED_STD_DEV
```

**Where**:
- `sensor_mean`: Average of sensor's measurements in time range
- `EXPECTED_MEAN`: Value of gateway's EXPECTED_MEAN parameter
- `EXPECTED_STD_DEV`: Value of gateway's EXPECTED_STD_DEV parameter

**Example**:
```
Gateway parameters:
- EXPECTED_MEAN = 25.0
- EXPECTED_STD_DEV = 2.0

Sensor S_000001 mean = 30.0
|30.0 - 25.0| = 5.0
2 × 2.0 = 4.0
5.0 ≥ 4.0 → OUTLIER!

Sensor S_000002 mean = 26.5
|26.5 - 25.0| = 1.5
2 × 2.0 = 4.0
1.5 < 4.0 → Not an outlier
```

**Important Notes**:
- Only check if both EXPECTED_MEAN and EXPECTED_STD_DEV exist
- If either parameter is missing, outlierSensors is empty
- Use measurements only within the specified time range

---

### Inter-Arrival Time Histogram

This is the most complex part of the gateway report.

**What is Inter-Arrival Time?**
The time duration between consecutive measurements.

**Example**:
```
Measurement 1: 2024-01-15T10:00:00
Measurement 2: 2024-01-15T10:15:30
Measurement 3: 2024-01-15T10:28:45

Inter-arrival times:
Time1→2: 15 minutes 30 seconds (Duration)
Time2→3: 13 minutes 15 seconds (Duration)
```

**Histogram Requirements**:

1. **Calculate all inter-arrival durations** between consecutive measurements
2. **Find min and max** durations
3. **Create 20 equal-width buckets** spanning [min, max]
4. **Count** how many durations fall in each bucket

**Bucket Rules**:
- All buckets: **[start, end)** (left-closed, right-open)
- Last bucket: **[start, end]** (both closed to include maximum)
- Buckets must be contiguous (no gaps)
- Must be a SortedMap ordered by start duration

**Example**:
```
Inter-arrival times: 10s, 15s, 12s, 18s, 20s, 11s, 14s, ...
Min duration: 10 seconds
Max duration: 20 seconds
Range: 10 seconds
Bucket width: 10 / 20 = 0.5 seconds

Buckets (simplified for clarity):
[10.0s, 10.5s) → 1 measurement
[10.5s, 11.0s) → 0 measurements
[11.0s, 11.5s) → 1 measurement
...
[19.5s, 20.0s] → 1 measurement (last bucket, includes 20.0)
```

**Important Notes**:
- Need at least 2 measurements to calculate inter-arrivals
- If fewer than 2 measurements, histogram can be empty
- Use Duration type for ranges
- Order by ascending bucket start

---

### Task 3: Update OperationsFactory

**File**: `OperationsFactory.java`

**Method to modify**:
```java
public static GatewayOperations getGatewayOperations()
```

**What it does**: Return your GatewayOperations implementation

---

### R2 Summary Checklist

- [ ] Implement `storeMeasurements()` in DataImportingService
- [ ] Call `checkMeasurement()` after each save
- [ ] Create GatewayOperations implementation class
- [ ] Implement all 7 GatewayOperations methods
- [ ] Generate complete GatewayReport with all 9 fields
- [ ] Correctly calculate outlier sensors formula
- [ ] Build inter-arrival time histogram with 20 buckets
- [ ] Handle special parameters (EXPECTED_MEAN, EXPECTED_STD_DEV, BATTERY_CHARGE)
- [ ] Update OperationsFactory
- [ ] Handle all exceptions
- [ ] Use Timestamped fields
- [ ] Call AlertingService.notifyDeletion() on delete

---

## 8. REQUIREMENT R3: SENSOR MANAGEMENT

### What You Build

Implement everything related to Sensors and Thresholds.

### Task 1: Data Import Service (Part 3 of 3)

**File**: `DataImportingService.java`

**Method to implement**: `storeMeasurements(String filePath)`

**What it does**:
1. Read CSV file
2. Parse measurements
3. Save to database
4. Call `checkMeasurement(measurement)`

**Important Notes**:
- Same as R1 and R2
- You implement independently in R3 branch
- Must call `checkMeasurement()` after EACH save
- This ensures compatibility when merged

---

### Task 2: SensorOperations Implementation

**File**: Create a new class (e.g., `SensorOperationsImpl.java`)

**Interface to implement**: `SensorOperations`

**Methods to implement**:

#### 2.1 Create Sensor
```java
Sensor createSensor(String username, String code, String name, String description)
```

**What it does**:
- Create new Sensor object
- Validate code format (S_XXXXXX where XXXXXX are 6 digits)
- Validate username (must exist and be MAINTAINER)
- Set Timestamped fields
- Save to database

**Throws**:
- `InvalidInputDataException`: Bad code format
- `IdAlreadyInUseException`: Code already exists
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 2.2 Update Sensor
```java
Sensor updateSensor(String username, String code, String name, String description)
```

**What it does**:
- Find existing sensor
- Update name and/or description
- Update Timestamped fields
- Save

**Throws**:
- `ElementNotFoundException`: Sensor doesn't exist
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 2.3 Delete Sensor
```java
void deleteSensor(String username, String code)
```

**What it does**:
- Find sensor
- Delete from database
- Call `AlertingService.notifyDeletion(username, code, Sensor.class)`

**Throws**:
- `ElementNotFoundException`: Sensor doesn't exist
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 2.4 Get Sensors
```java
Collection<Sensor> getSensors(String... codes)
```

**What it does**:
- If no codes: Return ALL sensors
- If codes provided: Return matching sensors
- Ignore non-existent codes

---

#### 2.5 Set Threshold
```java
void setThreshold(String username, String sensorCode, Double value, ComparisonType comparisonType)
```

**What it does**:
- Validate user permissions
- Find sensor
- Create/update threshold with value and comparison type
- Save

**Important Notes**:
- Threshold ALWAYS has both value AND comparisonType
- If sensor already has threshold: Replace it
- If sensor has no threshold: Create new one

**Comparison Types**:
- GREATER_THAN: value > threshold
- LESS_THAN: value < threshold
- EQUAL_TO: value == threshold
- GREATER_THAN_OR_EQUAL: value ≥ threshold
- LESS_THAN_OR_EQUAL: value ≤ threshold

**Throws**:
- `InvalidInputDataException`: Missing value or comparisonType
- `ElementNotFoundException`: Sensor doesn't exist
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 2.6 Remove Threshold
```java
void removeThreshold(String username, String sensorCode)
```

**What it does**:
- Validate user permissions
- Find sensor
- Remove threshold from sensor
- Save

**Throws**:
- `ElementNotFoundException`: Sensor doesn't exist
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 2.7 Generate Sensor Report
```java
SensorReport generateSensorReport(String sensorCode, String startDate, String endDate)
```

**What it does**: Create detailed statistical report for the sensor

**Report Contents**:

1. **code**: Sensor code

2. **startDate / endDate**: Input dates (can be null)

3. **numberOfMeasurements**: Total count in time range

4. **mean**: Average of all measurement values
   - Formula: mean = (sum of all values) / count
   - If 0 measurements: return 0

5. **variance**: Sample variance
   - Formula: variance = Σ(xi - mean)² / (n - 1)
   - If fewer than 2 measurements: return 0
   - Use SAMPLE variance (divide by n-1, not n)

6. **stdDev**: Standard deviation
   - Formula: stdDev = √variance
   - If fewer than 2 measurements: return 0

7. **minimumMeasuredValue**: Lowest value recorded
   - If 0 measurements: return 0 or null

8. **maximumMeasuredValue**: Highest value recorded
   - If 0 measurements: return 0 or null

9. **outliers**: Collection<Measurement>
   - Measurements that deviate significantly from mean
   - See outlier formula below
   - If fewer than 2 measurements: empty collection

10. **histogram**: Map<Range<Double>, Long>
    - Distribution of measurement values
    - Built using ONLY non-outlier measurements
    - See detailed explanation below

---

### Outlier Detection Formula

A measurement is an **outlier** if it differs from the mean by at least 2 standard deviations.

**Formula**:
```
|measurement_value - mean| ≥ 2 × stdDev
```

**Example**:
```
Measurements: 20, 22, 21, 23, 50, 19, 24
Mean = 25.57
StdDev = 11.18

Value = 50:
|50 - 25.57| = 24.43
2 × 11.18 = 22.36
24.43 ≥ 22.36 → OUTLIER!

Value = 22:
|22 - 25.57| = 3.57
2 × 11.18 = 22.36
3.57 < 22.36 → Not an outlier
```

**Important Notes**:
- Need at least 2 measurements to calculate outliers
- If fewer than 2 measurements: empty outliers collection
- Outliers are entire Measurement objects, not just values

---

### Measurement Value Histogram

Shows distribution of sensor readings.

**Requirements**:

1. **Use ONLY non-outlier measurements**
2. **Find min and max** among non-outlier values
3. **Create 20 equal-width buckets** spanning [min, max]
4. **Count** how many non-outlier measurements fall in each bucket

**Bucket Rules**:
- All buckets: **[start, end)** (left-closed, right-open)
- Last bucket: **[start, end]** (both closed)
- Must cover entire [min, max] range
- Must be SortedMap ordered by start value
- Use Range<Double> for keys

**Example**:
```
Non-outlier measurements: 20.0, 22.5, 21.0, 23.5, 19.5, 24.0, 22.0

Min: 19.5
Max: 24.0
Range: 4.5
Bucket width: 4.5 / 20 = 0.225

Buckets:
[19.5, 19.725) → 1 measurement (19.5)
[19.725, 19.95) → 0 measurements
[19.95, 20.175) → 1 measurement (20.0)
...
[23.775, 24.0] → 1 measurement (24.0) (last bucket)
```

**Edge Cases**:
- If 0 non-outlier measurements: Empty histogram
- If all measurements are outliers: Empty histogram
- If min = max (all same value): Create 20 buckets anyway with width approaching 0

---

### Task 3: Update OperationsFactory

**File**: `OperationsFactory.java`

**Method to modify**:
```java
public static SensorOperations getSensorOperations()
```

**What it does**: Return your SensorOperations implementation

---

### R3 Summary Checklist

- [ ] Implement `storeMeasurements()` in DataImportingService
- [ ] Call `checkMeasurement()` after each save
- [ ] Create SensorOperations implementation class
- [ ] Implement all 7 SensorOperations methods
- [ ] Generate complete SensorReport with all 10 fields
- [ ] Calculate statistics correctly (mean, variance, stdDev)
- [ ] Identify outliers using 2-sigma rule
- [ ] Build value histogram using only non-outliers
- [ ] Create exactly 20 equal-width buckets
- [ ] Update OperationsFactory
- [ ] Handle all exceptions
- [ ] Use Timestamped fields
- [ ] Call AlertingService.notifyDeletion() on delete

---

## 9. REQUIREMENT R4: TOPOLOGY INTEGRATION

### What You Build

Connect everything together - this is the INTEGRATION requirement.

### When to Start

**CRITICAL**: R4 starts AFTER R1, R2, and R3 are ALL merged into main branch!

**Why**:
- R4 depends on code from all three previous requirements
- R4 may require refactoring across all domains
- All team members work on R4 together

---

### Task 1: Refactoring

**What is Refactoring?**
Improving code structure without changing functionality.

**What You Can Do**:
- Remove duplicate code across R1, R2, R3
- Centralize common logic
- Improve naming and organization
- Harmonize design decisions
- Make code more readable and maintainable

**What You CANNOT Do**:
- Break public interfaces
- Change method signatures exposed to tests
- Break existing tests

**Example Refactoring**:
```java
// BEFORE (duplicated in R1, R2, R3):
private void validateUser(String username) {
    User user = userRepo.findById(username);
    if (user == null) throw new UnauthorizedException();
    if (user.getType() == UserType.VIEWER) throw new UnauthorizedException();
}

// AFTER (single shared method):
class PermissionValidator {
    public static void requireMaintainer(String username) {
        User user = userRepo.findById(username);
        if (user == null || user.getType() == UserType.VIEWER) {
            throw new UnauthorizedException();
        }
    }
}
```

---

### Task 2: TopologyOperations Implementation

**File**: Create a new class (e.g., `TopologyOperationsImpl.java`)

**Interface to implement**: `TopologyOperations`

**What is Topology?**
The relationships/connections between Networks, Gateways, and Sensors.

**Hierarchy**:
```
Network
  └─ Contains multiple Gateways
      └─ Each Gateway contains multiple Sensors
```

---

#### 2.1 Associate Gateway to Network
```java
void associateGateway(String username, String networkCode, String gatewayCode)
```

**What it does**:
- Validate user permissions
- Find network and gateway
- Add gateway to network's collection
- Save

**Important Notes**:
- This is about RELATIONSHIP, not creation
- Network and Gateway must already exist
- One gateway can belong to only ONE network
- If gateway already associated elsewhere: Handle appropriately

**Throws**:
- `ElementNotFoundException`: Network or gateway doesn't exist
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 2.2 Disassociate Gateway from Network
```java
void disassociateGateway(String username, String networkCode, String gatewayCode)
```

**What it does**:
- Validate user permissions
- Find network and gateway
- Remove gateway from network's collection
- Save

**Throws**:
- `ElementNotFoundException`: Network or gateway doesn't exist, or gateway not in network
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 2.3 Get Gateways of Network
```java
Collection<Gateway> getGatewaysOfNetwork(String networkCode)
```

**What it does**:
- Find network
- Return all gateways associated with it
- If network has no gateways: Return empty collection

**Throws**:
- `ElementNotFoundException`: Network doesn't exist

---

#### 2.4 Associate Sensor to Gateway
```java
void associateSensor(String username, String gatewayCode, String sensorCode)
```

**What it does**:
- Validate user permissions
- Find gateway and sensor
- Add sensor to gateway's collection
- Save

**Important Notes**:
- Sensor can belong to only ONE gateway
- Gateway and Sensor must already exist

**Throws**:
- `ElementNotFoundException`: Gateway or sensor doesn't exist
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 2.5 Disassociate Sensor from Gateway
```java
void disassociateSensor(String username, String gatewayCode, String sensorCode)
```

**What it does**:
- Validate user permissions
- Find gateway and sensor
- Remove sensor from gateway's collection
- Save

**Throws**:
- `ElementNotFoundException`: Gateway or sensor doesn't exist, or sensor not in gateway
- `UnauthorizedException`: User doesn't exist or is VIEWER

---

#### 2.6 Get Sensors of Gateway
```java
Collection<Sensor> getSensorsOfGateway(String gatewayCode)
```

**What it does**:
- Find gateway
- Return all sensors associated with it
- If gateway has no sensors: Return empty collection

**Throws**:
- `ElementNotFoundException`: Gateway doesn't exist

---

### Task 3: Complete Integration Logic

Now that you have topology, some operations need updates:

**Example**: `checkMeasurement()` in DataImportingService

**Full Logic**:
1. Get sensor from measurement
2. Check if sensor has threshold
3. If violated, need to notify operators
4. **NEW**: Operators come from the NETWORK
5. Must navigate: Sensor → Gateway → Network → Operators

**Implementation**:
```
measurement.sensorCode → Sensor
Sensor → Gateway (via topology)
Gateway → Network (via topology)
Network → Operators
Alert those operators
```

---

### Task 4: Update OperationsFactory

**File**: `OperationsFactory.java`

**Method to modify**:
```java
public static TopologyOperations getTopologyOperations()
```

**What it does**: Return your TopologyOperations implementation

---

### R4 Summary Checklist

- [ ] Wait for R1, R2, R3 to be merged into main
- [ ] Create R4 branch from main
- [ ] Perform refactoring (remove duplicates, improve structure)
- [ ] Create TopologyOperations implementation class
- [ ] Implement all 6 TopologyOperations methods
- [ ] Update OperationsFactory
- [ ] Complete integration logic (e.g., checkMeasurement with topology)
- [ ] Ensure all existing tests still pass
- [ ] All team members contribute to R4

---

## 10. GIT WORKFLOW AND CODE REVIEW

### GitFlow Workflow

You MUST follow the GitFlow process as specified in:
https://git-oop.polito.it/labs/docs/-/blob/main/Git/GitFlow_en.md

**Key Points**:
- Develop features in separate branches
- Merge to main through Merge Requests (MR)
- Never commit directly to main
- Use proper branch naming

---

### Branch Naming Rules

**Format**: `X-rN[-optional-description]`

**Where**:
- X = Integer (usually issue number)
- N = Requirement number (1, 2, 3, or 4)
- Optional description after second dash

**Examples**:
```
✓ Valid:
  1-r1
  1-r1-network-management
  5-r2
  5-r2-gateway-features
  3-r3-sensor-implementation
  7-r4-topology-integration

✗ Invalid:
  r1-network
  feature-r1
  network-management
  requirement-1
```

---

### Commit Rules

**R1, R2, R3 branches**:
- ONLY the assigned team member can commit
- Each requirement is one person's responsibility
- No pair programming on these branches

**R4 branch**:
- ALL team members can commit
- Collaborative integration work
- Joint refactoring

---

### Merge Request Process

**Requirements**:
1. Create Merge Request (MR) to merge your branch into main
2. Other team member(s) MUST review
3. Reviewer uses the Code Review Checklist (see below)
4. All checklist items must be satisfied
5. Only then can the MR be approved and merged

**Timeline**:
```
Developer A: Creates branch 1-r1-network
Developer A: Implements NetworkOperations
Developer A: Creates MR: 1-r1-network → main
Developer B: Reviews using checklist
Developer B: Requests changes if needed
Developer A: Makes fixes
Developer B: Approves MR
Developer A: Merges to main
```

---

### Code Review Checklist

Use the provided `checklist.md` file. Here's what reviewers check:

#### 1. Naming and Style
- [ ] Classes use PascalCase (e.g., `NetworkOperationsImpl`)
- [ ] Methods and variables use camelCase (e.g., `createNetwork`)
- [ ] Constants use UPPER_SNAKE_CASE (e.g., `MAX_RETRIES`)
- [ ] Names are meaningful, not abbreviated

#### 2. Class Design & Encapsulation
- [ ] Each class has a single clear responsibility
- [ ] Fields have minimum required visibility (private when possible)
- [ ] Objects initialized via constructors, not separate init methods

#### 3. Methods and Logic
- [ ] Each method does one thing
- [ ] No duplicated code (extract common logic)
- [ ] Method names describe what they do

#### 4. Object-Oriented Usage
- [ ] Static members only for constants or stateless utilities
- [ ] Inheritance used only for "is-a" relationships

#### 5. Collections and Streams
- [ ] Use enhanced for-loop unless index needed
- [ ] Appropriate data structures (List vs Map vs Set)
- [ ] Stream pipelines are side-effect free
- [ ] forEach only for terminal actions

#### 6. Persistence (JPA/Hibernate)
- [ ] Relationships only when necessary
- [ ] No direct SQL, use JPQL

#### 7. Error Handling
- [ ] Resources closed properly (try-with-resources)
- [ ] Exceptions handled meaningfully
- [ ] No silent catch blocks

#### 8. Readability and Clean Code
- [ ] Constants instead of magic numbers
- [ ] No commented-out code
- [ ] No debug print statements

---

## 11. EXCEPTION HANDLING RULES

### Exception Hierarchy

All exceptions extend `WeatherReportException`.

**Important**: Exception order doesn't matter. If multiple conditions are violated, throwing any of them is acceptable.

---

### InvalidInputDataException

**When to throw**:
- Invalid code format (e.g., "NET_1" instead of "NET_01")
- Missing required fields (e.g., email for Operator)
- Invalid data format

**Examples**:
```java
// Bad network code
createNetwork("user1", "NET_1", ...)  // Throws InvalidInputDataException

// Missing operator email
addOperator("user1", "NET_01", new Operator(null, "John", "Doe"))  // Throws

// Missing threshold value
setThreshold("user1", "S_000001", null, ComparisonType.GREATER_THAN)  // Throws
```

**Note**: Optional fields (name, description) should NOT trigger this exception.

---

### IdAlreadyInUseException

**When to throw**:
- Attempting to create an entity with a code/ID that already exists

**Examples**:
```java
// Network NET_01 already exists
createNetwork("user1", "NET_01", ...)  // Throws IdAlreadyInUseException

// Operator with this email already exists
// Actually, this might just update - check spec carefully

// Sensor S_000001 already exists
createSensor("user1", "S_000001", ...)  // Throws IdAlreadyInUseException
```

---

### ElementNotFoundException

**When to throw**:
- Referencing a code that doesn't exist in the system
- Update/delete/retrieve operations on non-existent entities

**Examples**:
```java
// Network doesn't exist
updateNetwork("user1", "NET_99", ...)  // Throws ElementNotFoundException

// Gateway doesn't exist
deleteGateway("user1", "GW_9999")  // Throws ElementNotFoundException

// Sensor doesn't exist
generateSensorReport("S_999999", ...)  // Throws ElementNotFoundException
```

**Note**: `getNetworks()`, `getGateways()`, `getSensors()` should NOT throw this exception - they just ignore non-existent codes.

---

### UnauthorizedException

**When to throw**:
- Username doesn't exist in the system
- User is VIEWER but operation requires MAINTAINER

**Examples**:
```java
// User doesn't exist
createNetwork("nonexistent_user", ...)  // Throws UnauthorizedException

// User is VIEWER, but creating requires MAINTAINER
// Assume "viewer1" has type VIEWER
createNetwork("viewer1", "NET_01", ...)  // Throws UnauthorizedException
```

**Permission Rules**:
- VIEWER: Can only call methods that READ data
- MAINTAINER: Can call ANY method (read and write)

**Methods requiring MAINTAINER**:
- All create operations
- All update operations
- All delete operations
- Setting/removing parameters, thresholds, operators
- Association/disassociation operations

**Methods allowing VIEWER**:
- All get operations
- All report generation operations

---

## 12. STATISTICS AND MATHEMATICAL FORMULAS

### Basic Statistics

All reports need these calculations. Here's the complete reference.

---

### Mean (Average)

**Formula**:
```
mean = (x₁ + x₂ + ... + xₙ) / n
```

**Where**:
- x₁, x₂, ..., xₙ are the measurements
- n is the number of measurements

**Example**:
```
Measurements: 20, 22, 21, 23, 24
mean = (20 + 22 + 21 + 23 + 24) / 5 = 110 / 5 = 22.0
```

**Edge Cases**:
- 0 measurements: return 0
- 1 measurement: return that value

---

### Sample Variance

**Formula**:
```
variance = Σ(xᵢ - mean)² / (n - 1)
```

**Where**:
- xᵢ is each measurement
- mean is the average
- n is the number of measurements
- Divide by (n-1), not n (this is "sample" variance)

**Example**:
```
Measurements: 20, 22, 21, 23, 24
mean = 22.0

Deviations from mean:
20 - 22 = -2  →  (-2)² = 4
22 - 22 =  0  →  (0)² = 0
21 - 22 = -1  →  (-1)² = 1
23 - 22 =  1  →  (1)² = 1
24 - 22 =  2  →  (2)² = 4

Sum of squared deviations = 4 + 0 + 1 + 1 + 4 = 10
variance = 10 / (5 - 1) = 10 / 4 = 2.5
```

**Edge Cases**:
- Fewer than 2 measurements: return 0
- Dividing by (n-1) means you need at least 2 measurements

---

### Standard Deviation

**Formula**:
```
stdDev = √variance
```

**Example**:
```
variance = 2.5
stdDev = √2.5 ≈ 1.58
```

**Edge Cases**:
- Fewer than 2 measurements: return 0
- Standard deviation is always non-negative

---

### Outlier Detection (Measurements)

Used in **Sensor Reports**.

**Formula**:
```
A measurement is an outlier if:
|value - mean| ≥ 2 × stdDev
```

**Example**:
```
Measurements: 20, 22, 21, 23, 50, 24
mean = 26.67
stdDev = 11.98

Check value = 50:
|50 - 26.67| = 23.33
2 × 11.98 = 23.96
23.33 < 23.96 → NOT an outlier (close though!)

Check value = 22:
|22 - 26.67| = 4.67
2 × 11.98 = 23.96
4.67 < 23.96 → NOT an outlier
```

**Edge Cases**:
- Fewer than 2 measurements: no outliers (empty collection)
- stdDev = 0 (all same value): no outliers

---

### Outlier Sensors (Gateway Report)

Different from outlier measurements!

**Formula**:
```
A sensor is an outlier if:
|sensor_mean - EXPECTED_MEAN| ≥ 2 × EXPECTED_STD_DEV
```

**Where**:
- sensor_mean: Average of that sensor's measurements
- EXPECTED_MEAN: Gateway parameter value
- EXPECTED_STD_DEV: Gateway parameter value

**Example**:
```
Gateway parameters:
  EXPECTED_MEAN = 25.0
  EXPECTED_STD_DEV = 3.0

Sensor S_000001 measurements: 30, 31, 32
  sensor_mean = 31.0
  |31.0 - 25.0| = 6.0
  2 × 3.0 = 6.0
  6.0 ≥ 6.0 → OUTLIER!

Sensor S_000002 measurements: 24, 26, 25
  sensor_mean = 25.0
  |25.0 - 25.0| = 0.0
  2 × 3.0 = 6.0
  0.0 < 6.0 → Not an outlier
```

**Edge Cases**:
- If EXPECTED_MEAN or EXPECTED_STD_DEV parameters don't exist: no outlier sensors
- If sensor has 0 measurements in time range: can't calculate mean, skip it

---

### Load Ratio (Percentage)

Used in Network and Gateway reports.

**Formula**:
```
ratio = (entity_measurements / total_measurements) × 100
```

**Example for Gateway Load Ratio**:
```
Network has 3 gateways:
  GW_0001: 50 measurements
  GW_0002: 30 measurements
  GW_0003: 20 measurements
  Total: 100 measurements

Ratios:
  GW_0001: (50/100) × 100 = 50.0%
  GW_0002: (30/100) × 100 = 30.0%
  GW_0003: (20/100) × 100 = 20.0%
```

**Example for Sensor Load Ratio**:
```
Gateway has 3 sensors:
  S_000001: 25 measurements
  S_000002: 50 measurements
  S_000003: 25 measurements
  Total: 100 measurements

Ratios:
  S_000001: 25.0%
  S_000002: 50.0%
  S_000003: 25.0%
```

**Edge Cases**:
- If total = 0: ratios are undefined (shouldn't happen with proper queries)

---

## 13. CRITICAL IMPLEMENTATION DETAILS

### Code Format Validation

Each entity has strict code format rules.

**Network Code**:
- Pattern: `NET_XX`
- Must start with "NET_"
- Must end with exactly 2 digits
- Valid: NET_01, NET_99
- Invalid: NET_1, NET_001, NETWORK_01

**Gateway Code**:
- Pattern: `GW_XXXX`
- Must start with "GW_"
- Must end with exactly 4 digits
- Valid: GW_0001, GW_9999
- Invalid: GW_001, GW_00001, GATEWAY_0001

**Sensor Code**:
- Pattern: `S_XXXXXX`
- Must start with "S_"
- Must end with exactly 6 digits
- Valid: S_000001, S_999999
- Invalid: S_00001, S_0000001, SENSOR_000001

**How to Validate**:
```java
// Example regex patterns
boolean isValidNetworkCode = code.matches("NET_\\d{2}");
boolean isValidGatewayCode = code.matches("GW_\\d{4}");
boolean isValidSensorCode = code.matches("S_\\d{6}");

// If invalid, throw InvalidInputDataException
```

---

### Date Handling

**Date Format**: Defined in `WeatherReport.DATE_FORMAT`

**Important Notes**:
- No timezone consideration (absolute dates)
- Can be null (no bound in that direction)
- Interval is INCLUSIVE of both boundaries

**Examples**:
```
startDate = "2024-01-15T10:00:00"
endDate = "2024-01-15T12:00:00"
→ Include measurements with timestamps 10:00:00 through 12:00:00 (inclusive)

startDate = null
endDate = "2024-01-15T12:00:00"
→ Include all measurements up to and including 12:00:00

startDate = "2024-01-15T10:00:00"
endDate = null
→ Include all measurements from 10:00:00 onwards
```

---

### Histogram Bucket Conventions

**Critical Rule**: All buckets are [start, end) EXCEPT the last bucket which is [start, end]

**Why?**
- Ensures every value falls in exactly one bucket
- The maximum value must be included somewhere

**Example with Numbers**:
```
Values: 10, 15, 20, 25, 30
Range: [10, 30]
3 buckets:

Bucket 1: [10, 16.67) → includes 10, 15 (not 16.67)
Bucket 2: [16.67, 23.33) → includes 20 (not 23.33)
Bucket 3: [23.33, 30] → includes 25, 30 (INCLUDES 30!)
```

**Example with Times**:
```
Times: 10:00, 11:00, 12:00
Range: [10:00, 12:00]
2 buckets:

Bucket 1: [10:00, 11:00) → includes 10:00 (not 11:00)
Bucket 2: [11:00, 12:00] → includes 11:00, 12:00
```

**Implementation Note**:
When checking if a value belongs to a bucket:
```java
// For all buckets except last
if (value >= start && value < end)

// For last bucket
if (value >= start && value <= end)
```

---

### Histogram Granularity (Network Report)

**Time Histogram Rules**:
- **HOURLY** if time range ≤ 24 hours
- **DAILY** if time range > 24 hours

**Hourly Buckets**:
```
[2024-01-15T10:00, 2024-01-15T11:00)
[2024-01-15T11:00, 2024-01-15T12:00)
...
```

**Daily Buckets**:
```
[2024-01-15T00:00, 2024-01-16T00:00)
[2024-01-16T00:00, 2024-01-17T00:00)
...
```

---

### Creating 20 Equal-Width Buckets

Used in Gateway and Sensor reports.

**Algorithm**:
1. Find min and max values
2. Calculate range = max - min
3. Calculate bucket_width = range / 20
4. Create 20 buckets starting from min

**Example**:
```
Values: 10.0, 15.5, 20.0, 25.5, 30.0
Min: 10.0
Max: 30.0
Range: 20.0
Bucket width: 20.0 / 20 = 1.0

Buckets:
[10.0, 11.0), [11.0, 12.0), [12.0, 13.0), ...
..., [29.0, 30.0]  (last one is closed)
```

**Edge Case - All Same Value**:
```
Values: 25.0, 25.0, 25.0
Min: 25.0
Max: 25.0
Range: 0.0
Bucket width: 0.0 / 20 = 0.0

Create 20 buckets with width approaching 0:
[25.0, 25.0), [25.0, 25.0), ..., [25.0, 25.0]
All measurements go in the last bucket (the only one that includes 25.0)
```

---

### Inter-Arrival Time Calculation

**What it is**: Time between consecutive measurements.

**Steps**:
1. Get all measurements in time range, sorted by timestamp
2. For each pair of consecutive measurements:
   - Calculate duration between them
3. Collect all these durations
4. Build histogram of the durations

**Example**:
```
Measurements (sorted):
  2024-01-15T10:00:00
  2024-01-15T10:15:30  → 15m 30s after previous
  2024-01-15T10:28:45  → 13m 15s after previous
  2024-01-15T10:45:00  → 16m 15s after previous

Inter-arrival times:
  Duration 1: 15m 30s (930 seconds)
  Duration 2: 13m 15s (795 seconds)
  Duration 3: 16m 15s (975 seconds)

Now create histogram with these 3 durations:
Min: 795s, Max: 975s, Range: 180s
Bucket width: 180 / 20 = 9 seconds
```

---

### Most/Least Active Entities

In reports, you need to identify:
- Most active gateways (in Network Report)
- Least active gateways (in Network Report)
- Most active sensors (in Gateway Report)
- Least active sensors (in Gateway Report)

**Rule**: Include ALL entities tied for the max/min count.

**Example**:
```
Gateways:
  GW_0001: 100 measurements
  GW_0002: 100 measurements
  GW_0003: 50 measurements
  GW_0004: 50 measurements
  GW_0005: 25 measurements

Most active: GW_0001, GW_0002 (both have 100)
Least active: GW_0005 (has 25)
```

**Another Example**:
```
Sensors:
  S_000001: 80 measurements
  S_000002: 80 measurements
  S_000003: 80 measurements

Most active: S_000001, S_000002, S_000003 (all tied at 80)
Least active: S_000001, S_000002, S_000003 (all tied at 80)
```

---

### Timestamped Metadata

When creating or updating Network, Gateway, or Sensor:

**On Create**:
```java
entity.setCreatedBy(username);
entity.setCreatedAt(LocalDateTime.now());
entity.setLastModifiedBy(username);
entity.setLastModifiedAt(LocalDateTime.now());
```

**On Update**:
```java
// Don't change createdBy or createdAt
entity.setLastModifiedBy(username);
entity.setLastModifiedAt(LocalDateTime.now());
```

---

### Notification Calls

**Delete Notifications**:
```java
// When deleting Network
AlertingService.notifyDeletion(username, networkCode, Network.class);

// When deleting Gateway
AlertingService.notifyDeletion(username, gatewayCode, Gateway.class);

// When deleting Sensor
AlertingService.notifyDeletion(username, sensorCode, Sensor.class);
```

**Threshold Violation**:
```java
// When measurement violates threshold
AlertingService.notifyThresholdViolation(operators, sensorCode);
```

**Where operators come from**:
1. Find the sensor
2. Find the gateway that owns the sensor
3. Find the network that owns the gateway
4. Get operators from that network

---

### Repository Usage

**Basic Operations**:
```java
// Save (create or update)
repository.save(entity);

// Find by ID
Entity entity = repository.findById(id);

// Find all
Collection<Entity> all = repository.findAll();

// Delete
repository.delete(entity);
```

**Important Notes**:
- Use CRUDRepository for Network, Gateway, Sensor, Operator, User, etc.
- Use MeasurementRepository for Measurement
- Never write direct SQL queries - use JPQL if needed

---

### Testing Considerations

**Mocking**:
- Tests may use mocked repositories
- Don't modify the structure of provided code that tests expect
- Particularly important in `checkMeasurement()` method

**Custom Tests**:
- Can add your own tests in `com.weather.report.test.custom` package
- These won't affect grading
- Use for development and debugging

---

### Performance Notes

**Measurements**:
- Can be thousands or millions of records
- Queries should filter by time range FIRST
- Use appropriate indexes (likely already configured)

**Reports**:
- Calculate statistics efficiently
- Don't load all measurements into memory at once if possible
- Use database aggregation when possible

---

## FINAL CHECKLIST - BEFORE SUBMITTING

### R1 - Network
- [ ] storeMeasurements() reads CSV and saves measurements
- [ ] checkMeasurement() validates thresholds and alerts
- [ ] NetworkOperations implementation created
- [ ] All 7 NetworkOperations methods implemented
- [ ] Network code validation (NET_XX)
- [ ] NetworkReport generates all fields correctly
- [ ] Histogram uses correct granularity (hourly/daily)
- [ ] Most/least active gateways include ties
- [ ] OperationsFactory returns NetworkOperations
- [ ] Timestamped fields managed correctly
- [ ] Exceptions thrown appropriately
- [ ] AlertingService calls made

### R2 - Gateway
- [ ] storeMeasurements() implemented
- [ ] checkMeasurement() called (even if empty)
- [ ] GatewayOperations implementation created
- [ ] All 7 GatewayOperations methods implemented
- [ ] Gateway code validation (GW_XXXX)
- [ ] GatewayReport generates all fields correctly
- [ ] Outlier sensors calculated with formula
- [ ] Inter-arrival time histogram correct
- [ ] Special parameters handled (EXPECTED_MEAN, etc.)
- [ ] OperationsFactory returns GatewayOperations
- [ ] Timestamped fields managed
- [ ] Exceptions thrown appropriately
- [ ] AlertingService calls made

### R3 - Sensor
- [ ] storeMeasurements() implemented
- [ ] checkMeasurement() called
- [ ] SensorOperations implementation created
- [ ] All 7 SensorOperations methods implemented
- [ ] Sensor code validation (S_XXXXXX)
- [ ] SensorReport generates all fields correctly
- [ ] Statistics calculated correctly (mean, variance, stdDev)
- [ ] Outliers identified with 2-sigma rule
- [ ] Value histogram uses only non-outliers
- [ ] 20 equal-width buckets created
- [ ] OperationsFactory returns SensorOperations
- [ ] Timestamped fields managed
- [ ] Exceptions thrown appropriately
- [ ] AlertingService calls made

### R4 - Topology
- [ ] Waited for R1, R2, R3 to be merged
- [ ] Created R4 branch from main
- [ ] Refactoring performed (removed duplicates)
- [ ] TopologyOperations implementation created
- [ ] All 6 TopologyOperations methods implemented
- [ ] Association/disassociation logic correct
- [ ] Integration logic completed (checkMeasurement with topology)
- [ ] OperationsFactory returns TopologyOperations
- [ ] All existing tests still pass
- [ ] All team members contributed

### General
- [ ] GitFlow workflow followed
- [ ] Branch naming correct (X-rN)
- [ ] Merge Requests created
- [ ] Code reviewed using checklist
- [ ] All checklist points addressed
- [ ] No direct commits to main
- [ ] Proper commit messages
- [ ] Code well-documented
- [ ] No debug statements left in code
- [ ] Clean code principles followed

---

## SUMMARY

You are building a complete environmental monitoring system with:

1. **R1**: Networks that group equipment and have operators
2. **R2**: Gateways that manage sensors and have parameters
3. **R3**: Sensors that take measurements and have thresholds
4. **R4**: Topology that connects everything together

Each requirement involves:
- Creating/updating/deleting entities
- Managing relationships
- Importing measurement data
- Generating detailed statistical reports
- Alerting operators when problems occur

Use proper:
- Git workflow with feature branches
- Code review with the provided checklist
- Exception handling for all error cases
- JPA/Hibernate for persistence
- Clean code principles

Good luck with your implementation! This is a comprehensive project that will teach you professional software development practices.