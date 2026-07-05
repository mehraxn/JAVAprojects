# Complete Explanation of TopologyOperationsImpl Methods

## Overview

TopologyOperationsImpl has **6 methods** (not 4):
1. **getNetworkGateways** - Find all gateways connected to a network
2. **connectGateway** - Connect a gateway to a network
3. **disconnectGateway** - Disconnect a gateway from a network
4. **getGatewaySensors** - Find all sensors connected to a gateway
5. **connectSensor** - Connect a sensor to a gateway
6. **disconnectSensor** - Disconnect a sensor from a gateway

---

## Method 1: getNetworkGateways()

### Purpose
Find and return all gateways that are connected to a specific network.

### Method Signature
```java
public Collection<Gateway> getNetworkGateways(String networkCode)
    throws InvalidInputDataException, ElementNotFoundException
```

### What It Does - Step by Step

**Step 1: Validate Network Code Format**
```java
if (!isValidNetworkCode(networkCode)) {
    throw new InvalidInputDataException("Network code must be NET_ followed by 2 digits");
}
```
- Checks if the network code follows the format: "NET_##" (e.g., "NET_01")
- If format is wrong, throws `InvalidInputDataException`

**Step 2: Check if Network Exists**
```java
Network network = networkRepo.read(networkCode);
if (network == null) {
    throw new ElementNotFoundException("Network not found");
}
```
- Tries to find the network in the database using the code
- If network doesn't exist, throws `ElementNotFoundException`

**Step 3: Query Database for Gateways**
```java
EntityManager entityManager = PersistenceManager.getEntityManager();
try {
    TypedQuery<Gateway> q = entityManager.createQuery(
        "SELECT g FROM Gateway g WHERE g.network.code = :code", Gateway.class);
    q.setParameter("code", networkCode);
    return q.getResultList();
} finally {
    entityManager.close();
}
```
- Creates a database query using JPQL (Java Persistence Query Language)
- The query says: "Find all gateways where the network code equals the given code"
- Returns a list of all matching gateways
- Always closes the EntityManager in the `finally` block (important for resource management)

### Example Usage
```java
Input:  networkCode = "NET_01"
Output: [Gateway(GW_0001), Gateway(GW_0002), Gateway(GW_0003)]
```

### What Can Go Wrong
- ❌ `InvalidInputDataException` - If network code format is invalid (e.g., "NET_1", "NETWORK_01")
- ❌ `ElementNotFoundException` - If network doesn't exist in database

---

## Method 2: connectGateway()

### Purpose
Create a connection between a gateway and a network. After this, the gateway belongs to the network.

### Method Signature
```java
public Network connectGateway(String networkCode, String gatewayCode, String username)
    throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException
```

### What It Does - Step by Step

**Step 1: Validate Code Formats**
```java
if (!isValidNetworkCode(networkCode) || !isValidGatewayCode(gatewayCode)) {
    throw new InvalidInputDataException("Invalid network or gateway code format");
}
```
- Checks network code is "NET_##"
- Checks gateway code is "GW_####"
- If either format is wrong, throws exception

**Step 2: Check User Authorization**
```java
User user = userRepo.read(username);
if (user == null || user.getType() != UserType.MAINTAINER) {
    throw new UnauthorizedException("User not authorized or not a maintainer");
}
```
- Finds the user in the database
- Checks if user exists AND is a MAINTAINER (not just a VIEWER)
- Only MAINTAINER users can modify connections
- If not authorized, throws `UnauthorizedException`

**Step 3: Check Gateway Exists**
```java
Gateway gateway = gatewayRepo.read(gatewayCode);
if (gateway == null) throw new ElementNotFoundException("Gateway not found");
```
- Tries to find the gateway in database
- If doesn't exist, throws exception

**Step 4: Check Network Exists**
```java
Network network = networkRepo.read(networkCode);
if (network == null) throw new ElementNotFoundException("Network not found");
```
- Tries to find the network in database
- If doesn't exist, throws exception

**Step 5: Create the Connection**
```java
gateway.setNetwork(network);
gatewayRepo.update(gateway);
return network;
```
- Sets the network for this gateway (creates the relationship)
- Updates the gateway in the database to save the connection
- Returns the network object

### Example Usage
```java
Input:  networkCode = "NET_01"
        gatewayCode = "GW_0001"
        username = "john_maintainer"

Before: GW_0001 → not connected to any network
After:  GW_0001 → connected to NET_01
```

### What Can Go Wrong
- ❌ `InvalidInputDataException` - Invalid network or gateway code format
- ❌ `UnauthorizedException` - User doesn't exist or is not a MAINTAINER
- ❌ `ElementNotFoundException` - Gateway or network doesn't exist

---

## Method 3: disconnectGateway()

### Purpose
Remove the connection between a gateway and a network. After this, the gateway is no longer part of the network.

### Method Signature
```java
public Network disconnectGateway(String networkCode, String gatewayCode, String username)
    throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException
```

### What It Does - Step by Step

**Step 1: Validate Code Formats**
```java
if (!isValidNetworkCode(networkCode) || !isValidGatewayCode(gatewayCode)) {
    throw new InvalidInputDataException("Invalid format for codes");
}
```
- Same as connectGateway - validates both code formats

**Step 2: Check User Authorization**
```java
User user = userRepo.read(username);
if (user == null || user.getType() != UserType.MAINTAINER) {
    throw new UnauthorizedException("User not authorized");
}
```
- Same as connectGateway - checks user is MAINTAINER

**Step 3: Verify Gateway and Connection Exist**
```java
Gateway gateway = gatewayRepo.read(gatewayCode);
if (gateway == null || gateway.getNetwork() == null ||
    !networkCode.equals(gateway.getNetwork().getCode())) {
    throw new ElementNotFoundException("Gateway or connection not found");
}
```
This is a **compound check** that verifies THREE things:
1. Gateway exists (`gateway == null`)
2. Gateway is connected to SOME network (`gateway.getNetwork() == null`)
3. Gateway is connected to THIS specific network (`!networkCode.equals(...)`)

If any of these fails, throws exception because you can't disconnect something that isn't connected!

**Step 4: Remove the Connection**
```java
gateway.setNetwork(null);
gatewayRepo.update(gateway);
return networkRepo.read(networkCode);
```
- Sets the gateway's network to `null` (breaks the relationship)
- Updates the gateway in database
- Returns the network object

### Example Usage
```java
Input:  networkCode = "NET_01"
        gatewayCode = "GW_0001"
        username = "john_maintainer"

Before: GW_0001 → connected to NET_01
After:  GW_0001 → not connected to any network
```

### What Can Go Wrong
- ❌ `InvalidInputDataException` - Invalid code format
- ❌ `UnauthorizedException` - User not authorized
- ❌ `ElementNotFoundException` - Gateway doesn't exist, OR gateway isn't connected to any network, OR gateway is connected to a different network

---

## Method 4: getGatewaySensors()

### Purpose
Find and return all sensors that are connected to a specific gateway.

### Method Signature
```java
public Collection<Sensor> getGatewaySensors(String gatewayCode)
    throws InvalidInputDataException, ElementNotFoundException
```

### What It Does - Step by Step

**Step 1: Validate Gateway Code Format**
```java
if (!isValidGatewayCode(gatewayCode)) {
    throw new InvalidInputDataException("Invalid gateway code format");
}
```
- Checks if gateway code is "GW_####" format
- If not, throws exception

**Step 2: Check Gateway Exists**
```java
Gateway gateway = gatewayRepo.read(gatewayCode);
if (gateway == null) throw new ElementNotFoundException("Gateway not found");
```
- Looks up gateway in database
- If doesn't exist, throws exception

**Step 3: Query Database for Sensors**
```java
EntityManager entityManager = PersistenceManager.getEntityManager();
try {
    TypedQuery<Sensor> q = entityManager.createQuery(
        "SELECT s FROM Sensor s WHERE s.gateway.code = :code", Sensor.class);
    q.setParameter("code", gatewayCode);
    return q.getResultList();
} finally {
    entityManager.close();
}
```
- Creates JPQL query: "Find all sensors where gateway code equals the given code"
- Executes query and returns list of sensors
- Always closes EntityManager

### Example Usage
```java
Input:  gatewayCode = "GW_0001"
Output: [Sensor(S_000001), Sensor(S_000002), Sensor(S_000003)]
```

### What Can Go Wrong
- ❌ `InvalidInputDataException` - Gateway code format invalid
- ❌ `ElementNotFoundException` - Gateway doesn't exist

---

## Method 5: connectSensor()

### Purpose
Create a connection between a sensor and a gateway. After this, the sensor belongs to the gateway.

### Method Signature
```java
public Gateway connectSensor(String sensorCode, String gatewayCode, String username)
    throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException
```

### What It Does - Step by Step

**Step 1: Validate Code Formats**
```java
if (!isValidSensorCode(sensorCode) || !isValidGatewayCode(gatewayCode)) {
    throw new InvalidInputDataException("Invalid sensor or gateway code format");
}
```
- Checks sensor code is "S_######"
- Checks gateway code is "GW_####"

**Step 2: Check User Authorization**
```java
User user = userRepo.read(username);
if (user == null || user.getType() != UserType.MAINTAINER) {
    throw new UnauthorizedException("User not authorized");
}
```
- Verifies user exists and is MAINTAINER

**Step 3: Check Sensor Exists**
```java
Sensor sensor = sensorRepo.read(sensorCode);
if (sensor == null) throw new ElementNotFoundException("Sensor not found");
```
- Finds sensor in database

**Step 4: Check Gateway Exists**
```java
Gateway gateway = gatewayRepo.read(gatewayCode);
if (gateway == null) throw new ElementNotFoundException("Gateway not found");
```
- Finds gateway in database

**Step 5: Create the Connection**
```java
sensor.setGateway(gateway);
sensorRepo.update(sensor);
return gateway;
```
- Connects sensor to gateway
- Saves the change to database
- Returns the gateway object

### Example Usage
```java
Input:  sensorCode = "S_000001"
        gatewayCode = "GW_0001"
        username = "john_maintainer"

Before: S_000001 → not connected to any gateway
After:  S_000001 → connected to GW_0001
```

### What Can Go Wrong
- ❌ `InvalidInputDataException` - Invalid sensor or gateway code format
- ❌ `UnauthorizedException` - User not authorized
- ❌ `ElementNotFoundException` - Sensor or gateway doesn't exist

---

## Method 6: disconnectSensor()

### Purpose
Remove the connection between a sensor and a gateway. After this, the sensor is no longer part of the gateway.

### Method Signature
```java
public Gateway disconnectSensor(String sensorCode, String gatewayCode, String username)
    throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException
```

### What It Does - Step by Step

**Step 1: Validate Code Formats**
```java
if (!isValidSensorCode(sensorCode) || !isValidGatewayCode(gatewayCode)) {
    throw new InvalidInputDataException("Invalid format for codes");
}
```
- Validates both sensor and gateway code formats

**Step 2: Check User Authorization**
```java
User user = userRepo.read(username);
if (user == null || user.getType() != UserType.MAINTAINER) {
    throw new UnauthorizedException("User not authorized");
}
```
- Checks user is MAINTAINER

**Step 3: Verify Sensor and Connection Exist**
```java
Sensor sensor = sensorRepo.read(sensorCode);
if (sensor == null || sensor.getGateway() == null ||
    !sensor.getGateway().getCode().equals(gatewayCode)) {
    throw new ElementNotFoundException("Sensor not connected to this gateway");
}
```
This **compound check** verifies THREE things:
1. Sensor exists
2. Sensor is connected to SOME gateway
3. Sensor is connected to THIS specific gateway

**Step 4: Remove the Connection**
```java
sensor.setGateway(null);
sensorRepo.update(sensor);
return gatewayRepo.read(gatewayCode);
```
- Breaks the connection (sets to null)
- Saves the change
- Returns the gateway object

### Example Usage
```java
Input:  sensorCode = "S_000001"
        gatewayCode = "GW_0001"
        username = "john_maintainer"

Before: S_000001 → connected to GW_0001
After:  S_000001 → not connected to any gateway
```

### What Can Go Wrong
- ❌ `InvalidInputDataException` - Invalid code format
- ❌ `UnauthorizedException` - User not authorized
- ❌ `ElementNotFoundException` - Sensor doesn't exist, OR sensor isn't connected, OR sensor is connected to different gateway

---

## Summary Table

| Method | Purpose | Requires Auth | Validates Format | Database Query |
|--------|---------|---------------|------------------|----------------|
| getNetworkGateways | Find gateways in network | ❌ No | ✅ Network | ✅ JPQL |
| connectGateway | Link gateway to network | ✅ MAINTAINER | ✅ Both | ❌ Simple update |
| disconnectGateway | Unlink gateway from network | ✅ MAINTAINER | ✅ Both | ❌ Simple update |
| getGatewaySensors | Find sensors in gateway | ❌ No | ✅ Gateway | ✅ JPQL |
| connectSensor | Link sensor to gateway | ✅ MAINTAINER | ✅ Both | ❌ Simple update |
| disconnectSensor | Unlink sensor from gateway | ✅ MAINTAINER | ✅ Both | ❌ Simple update |

---

## Common Patterns

### Pattern 1: Query Methods (get...)
- Validate code format
- Check element exists
- Use JPQL to find related elements
- Return collection

### Pattern 2: Connect Methods
- Validate code formats
- Check user authorization (MAINTAINER only)
- Check both elements exist
- Create relationship
- Update database

### Pattern 3: Disconnect Methods
- Validate code formats
- Check user authorization (MAINTAINER only)
- Check element exists AND is currently connected to the specified element
- Break relationship (set to null)
- Update database

---

## Visual Example

```
Initial State:
NET_01: (empty)
NET_02: (empty)
GW_0001: not connected
GW_0002: not connected
S_000001: not connected
S_000002: not connected

After: connectGateway("NET_01", "GW_0001", "john")
NET_01: [GW_0001]
NET_02: (empty)
GW_0001: → NET_01
GW_0002: not connected
S_000001: not connected
S_000002: not connected

After: connectSensor("S_000001", "GW_0001", "john")
NET_01: [GW_0001]
GW_0001: → NET_01, [S_000001]
S_000001: → GW_0001

After: connectSensor("S_000002", "GW_0001", "john")
NET_01: [GW_0001]
GW_0001: → NET_01, [S_000001, S_000002]
S_000001: → GW_0001
S_000002: → GW_0001

Full Hierarchy:
NET_01
  └── GW_0001
        ├── S_000001
        └── S_000002
```

---

## End of Method Explanations