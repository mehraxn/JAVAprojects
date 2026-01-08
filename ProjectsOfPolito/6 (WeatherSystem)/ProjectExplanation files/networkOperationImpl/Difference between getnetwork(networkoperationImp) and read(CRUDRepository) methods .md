# Difference between `getNetworks()` (Operations) and `read()` (CRUDRepository)

This document explains the difference between:

* `NetworkOperationsImpl.getNetworks(String... codes)`
* `CRUDRepository<Network, String>.read(String id)` and `CRUDRepository.read()`

Although both are involved in “getting networks”, they live at **different layers** and have **different responsibilities**.

---

## 1) The main idea (one sentence)

> **`CRUDRepository.read(...)` is a low-level database access method, while `getNetworks(...)` is a higher-level business/use-case method that defines how the system should return networks to the user.**

---

## 2) Layer responsibility

### `CRUDRepository.read(...)` → Persistence Layer (DB)

* Goal: **fetch entities from the database**
* Generic: works for **any entity type** (`Network`, `Gateway`, `Sensor`, …)
* Contains **no business rules**

### `getNetworks(...)` → Operations Layer (Use-cases)

* Goal: provide a **feature of the system** (“get networks”) with business behavior
* Specific to the `Network` domain
* Defines **policy decisions** such as:

  * return all networks if no codes are given
  * ignore unknown codes

---

## 3) What each method returns

### `CRUDRepository.read(ID id)`

* Returns **one entity** (or null)

```java
Network n = networkRepository.read("NET_01");
// n is either the Network or null
```

### `NetworkOperations.getNetworks(String... codes)`

* Returns a **collection** of networks

```java
Collection<Network> list = networkOps.getNetworks("NET_01", "NET_02");
```

Also supports this:

```java
Collection<Network> all = networkOps.getNetworks();
// returns all networks
```

---

## 4) Behavior when data is missing

### `read("NET_99")`

* If not found → returns `null`

### `getNetworks("NET_01", "NET_99")`

* Reads each code
* Adds only the existing ones
* Missing ones are **silently skipped**

So the result would contain only `NET_01`.

This skipping behavior is a **business decision** made by the operations layer.

---

## 5) Flexibility and reuse

### CRUDRepository

* Reusable for every entity
* Same logic for networks, sensors, operators, etc.

### NetworkOperations

* Designed around the use-cases of the system
* Can add validation, permissions, logging, notifications, or aggregation

---

## 6) Example side-by-side

### A) Repository call (low-level)

```java
Network net = networkRepository.read("NET_01");
if (net == null) {
  // caller decides what to do
}
```

### B) Operations call (high-level)

```java
Collection<Network> nets = networkOps.getNetworks("NET_01", "NET_99");
// returns only the networks that exist
// caller does not deal with EntityManager, queries, etc.
```

---

## 7) Why the project has both

The design follows a common architecture principle:

* **Repository** = “how to talk to the database”
* **Operations** = “what the system does”

This separation gives:

* cleaner code
* easier testing
* flexibility to change database logic without changing business API

---

## 8) Summary table

| Feature                 | `CRUDRepository.read(id)` | `NetworkOperations.getNetworks(...)` |
| ----------------------- | ------------------------- | ------------------------------------ |
| Layer                   | Persistence               | Operations / Use-case                |
| Purpose                 | Fetch from DB             | Provide a system feature             |
| Returns                 | Single entity or null     | Collection of networks               |
| When codes not provided | Not applicable            | Returns all networks                 |
| Missing code behavior   | Returns null              | Skips missing networks               |
| Business logic          | No                        | Yes (policy behavior)                |
| Generic                 | Yes (all entities)        | No (network-specific)                |

---

## Final takeaway

> Use **`CRUDRepository.read()`** when you want a basic DB fetch.
> Use **`getNetworks()`** when you want the system’s official way of retrieving networks (including the project’s chosen behavior for “all networks” and “missing codes”).
