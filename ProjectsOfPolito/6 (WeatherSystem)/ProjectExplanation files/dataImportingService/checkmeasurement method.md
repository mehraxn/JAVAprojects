# Understanding the checkMeasurement Method

## Complete Guide to Threshold Validation & Alerting in the Weather Report System

---

## Table of Contents

1. [Overview – What Does This Method Do?](#overview--what-does-this-method-do)
2. [Inputs and Outputs](#inputs-and-outputs)
3. [What Data the Method Uses (Sensor, Threshold, Network, Operators)](#what-data-the-method-uses-sensor-threshold-network-operators)
4. [Line-by-Line Explanation](#line-by-line-explanation)

   * [🔎 Deep Dive: Why it reads Sensors from the DB (Mocked in tests)](#-deep-dive-why-it-reads-sensors-from-the-db-mocked-in-tests)
   * [📏 Deep Dive: EPSILON and floating-point equality](#-deep-dive-epsilon-and-floating-point-equality)
   * [🚦 Deep Dive: What “violation” means for each ThresholdType](#-deep-dive-what-violation-means-for-each-thresholdtype)
5. [Visual Data Flow](#visual-data-flow)
6. [Complete Example with Real Data](#complete-example-with-real-data)
7. [Why Each Part is Necessary](#why-each-part-is-necessary)
8. [Common Questions](#common-questions)

---

## Overview – What Does This Method Do?

### High-Level Purpose

`checkMeasurement(Measurement measurement)` validates a newly saved measurement against the **threshold** configured for the corresponding **sensor**.

If the measurement **violates** the threshold:

1. It finds the related **network**
2. It gets the network’s **operators**
3. It triggers a notification via **AlertingService**

**In Simple Terms:**
It’s like a “safety alarm check” after each measurement:

* Find the sensor
* See if it has an alarm rule (threshold)
* If the rule is broken, notify the responsible people

---

## Inputs and Outputs

### Method Signature

```java
private static void checkMeasurement(Measurement measurement)
```

### Input

* `measurement`: the measurement that has just been imported and saved

### Output

* No return value (`void`), but it may trigger a side-effect:

  * **calls `AlertingService.notifyThresholdViolation(...)`**

---

## What Data the Method Uses (Sensor, Threshold, Network, Operators)

The method depends on these objects:

* **Measurement**

  * `sensorCode`, `networkCode`, `value`

* **Sensor**

  * identified by `code`
  * may have a `Threshold`

* **Threshold**

  * has a `type` (e.g., LESS_THAN)
  * has a `value`

* **Network**

  * identified by `networkCode`
  * holds a collection of `Operator`s

* **Operator**

  * people who must be notified on threshold violations

---

## Line-by-Line Explanation

Here is the method (same as your file):

```java
private static void checkMeasurement(Measurement measurement) {
  CRUDRepository<Sensor, String> sensorRepository = new CRUDRepository<>(Sensor.class);
  Sensor currentSensor = sensorRepository.read().stream()
      .filter(s -> measurement.getSensorCode().equals(s.getCode()))
      .findFirst()
      .orElse(null);

  boolean sensorExists = currentSensor != null;
  if (sensorExists) {
    Threshold sensorThreshold = currentSensor.getThreshold();
    boolean sensorHasThreshold = sensorThreshold != null;

    if (sensorHasThreshold) {
      double measurementValue = measurement.getValue();
      double thresholdValue = sensorThreshold.getValue();
      ThresholdType thresholdType = sensorThreshold.getType();

      boolean violationDetected = false;

      if (thresholdType == ThresholdType.LESS_THAN) {
        violationDetected = measurementValue < thresholdValue;
      } else if (thresholdType == ThresholdType.GREATER_THAN) {
        violationDetected = measurementValue > thresholdValue;
      } else if (thresholdType == ThresholdType.LESS_OR_EQUAL) {
        violationDetected = measurementValue <= thresholdValue;
      } else if (thresholdType == ThresholdType.GREATER_OR_EQUAL) {
        violationDetected = measurementValue >= thresholdValue;
      } else if (thresholdType == ThresholdType.EQUAL) {
        violationDetected = Math.abs(measurementValue - thresholdValue) < EPSILON;
      } else if (thresholdType == ThresholdType.NOT_EQUAL) {
        violationDetected = Math.abs(measurementValue - thresholdValue) >= EPSILON;
      }

      if (violationDetected) {
        CRUDRepository<Network, String> networkRepository = new CRUDRepository<>(Network.class);
        Network network = networkRepository.read(measurement.getNetworkCode());

        if (network != null) {
          Collection<Operator> operators = network.getOperators();
          if (operators != null && !operators.isEmpty()) {
            AlertingService.notifyThresholdViolation(operators, currentSensor.getCode());
          }
        }
      }
    }
  }
}
```

---

### Step 1: Find the sensor for this measurement

```java
CRUDRepository<Sensor, String> sensorRepository = new CRUDRepository<>(Sensor.class);
Sensor currentSensor = sensorRepository.read().stream()
    .filter(s -> measurement.getSensorCode().equals(s.getCode()))
    .findFirst()
    .orElse(null);
```

**What happens:**

* It loads the sensors from the database.
* It searches for the sensor whose `code` matches the measurement’s `sensorCode`.

If not found, `currentSensor` becomes `null`.

---

### 🔎 Deep Dive: Why it reads Sensors from the DB (Mocked in tests)

Your file has a big comment:

* “Do not change these lines”
* Tests mock this interaction

So even if it’s not the most efficient approach, you **should not change it** because automated tests expect this exact DB read.

---

### Step 2: If the sensor exists, check if it has a threshold

```java
boolean sensorExists = currentSensor != null;
if (sensorExists) {
  Threshold sensorThreshold = currentSensor.getThreshold();
  boolean sensorHasThreshold = sensorThreshold != null;

  if (sensorHasThreshold) {
    ...
  }
}
```

**What happens:**

* If there is no sensor → stop (do nothing)
* If there is a sensor but no threshold → stop (do nothing)

This prevents null-pointer errors.

---

### Step 3: Extract values needed for comparison

```java
double measurementValue = measurement.getValue();
double thresholdValue = sensorThreshold.getValue();
ThresholdType thresholdType = sensorThreshold.getType();
```

Now the method has:

* the measured value
* the threshold numeric value
* the threshold rule (type)

---

### Step 4: Decide if there is a violation

```java
boolean violationDetected = false;

if (thresholdType == ThresholdType.LESS_THAN) {
  violationDetected = measurementValue < thresholdValue;
} else if (thresholdType == ThresholdType.GREATER_THAN) {
  violationDetected = measurementValue > thresholdValue;
} ...
```

The method compares `measurementValue` with `thresholdValue` differently depending on the threshold type.

---

### 🚦 Deep Dive: What “violation” means for each ThresholdType

| ThresholdType    | Rule checked            | Violation happens when…   | Example threshold=10 | measurement=8 | measurement=12 |
| ---------------- | ----------------------- | ------------------------- | -------------------: | ------------: | -------------: |
| LESS_THAN        | `value < thr`           | value is smaller          |          ✅ violation |             ❌ |              ✅ |
| GREATER_THAN     | `value > thr`           | value is larger           |                    ❌ |             ❌ |              ✅ |
| LESS_OR_EQUAL    | `value <= thr`          | value is smaller or equal |                    ✅ |             ✅ |              ❌ |
| GREATER_OR_EQUAL | `value >= thr`          | value is larger or equal  |                    ❌ |     ✅ (if 10) |              ✅ |
| EQUAL            | `abs(value-thr) < eps`  | value is “almost equal”   |                    ❌ |         (8) ❌ |         (10) ✅ |
| NOT_EQUAL        | `abs(value-thr) >= eps` | value is not equal        |                    ✅ |             ✅ |  ✅ (unless 10) |

**Important:**
This table describes what your code currently does. Whether it matches the project definition depends on how thresholds are described in your README.

---

### 📏 Deep Dive: EPSILON and floating-point equality

For doubles, equality is tricky:

* because numbers like 0.1 cannot be represented exactly in binary.

So instead of checking:

```java
measurementValue == thresholdValue
```

It checks:

```java
Math.abs(measurementValue - thresholdValue) < EPSILON
```

Meaning: *“the two values are close enough.”*

---

### Step 5: If violation detected, load the network and notify operators

```java
if (violationDetected) {
  CRUDRepository<Network, String> networkRepository = new CRUDRepository<>(Network.class);
  Network network = networkRepository.read(measurement.getNetworkCode());

  if (network != null) {
    Collection<Operator> operators = network.getOperators();
    if (operators != null && !operators.isEmpty()) {
      AlertingService.notifyThresholdViolation(operators, currentSensor.getCode());
    }
  }
}
```

**What happens:**

* It finds the network using the measurement’s `networkCode`
* It fetches the operators list
* If there are operators, it sends a notification:

  * **operators** = who gets notified
  * **sensor code** = what caused the alert

---

## Visual Data Flow

```
Measurement imported
   ↓
checkMeasurement(measurement)
   ↓
Find Sensor by sensorCode
   ↓
Sensor has Threshold?
   ↓ yes
Compare measurement value vs threshold rule
   ↓
Violation?
   ↓ yes
Find Network by networkCode
   ↓
Get Operators
   ↓
Notify operators (AlertingService)
```

---

## Complete Example with Real Data

### Scenario

* Sensor: `S_000001`
* Threshold: `GREATER_THAN 35.0`
* Network: `NET_01`
* Operators: Alice, Bob

### Incoming measurement

* sensorCode: `S_000001`
* networkCode: `NET_01`
* value: `37.2`

### Evaluation

* Type: GREATER_THAN
* Check: `37.2 > 35.0` → **true**
* Violation detected ✅

### Result

The method calls:

```java
AlertingService.notifyThresholdViolation([Alice,Bob], "S_000001");
```

Operators receive an alert.

---

## Why Each Part is Necessary

| Block                      | Purpose                                   | What if removed?                      |
| -------------------------- | ----------------------------------------- | ------------------------------------- |
| Find sensor by code        | Links measurement to configured threshold | No way to know which threshold to use |
| Null checks                | Prevent crashes when data is missing      | `NullPointerException` risk           |
| ThresholdType branching    | Applies the correct comparison rule       | Wrong alerts / missed alerts          |
| EPSILON for equality       | Avoids floating-point equality bugs       | False negatives/positives for EQUAL   |
| Load network and operators | Finds people responsible for alerts       | Alerts cannot be delivered            |
| Notify service call        | Triggers the alert side-effect            | No alert is ever sent                 |

---

## Common Questions

### Q1: Why doesn’t it notify if network has no operators?

Because of this check:

```java
if (operators != null && !operators.isEmpty())
```

No operators = nobody to notify.

---

### Q2: What if the measurement contains a networkCode that is wrong?

Then the method may:

* load the wrong network
* or load null

A stricter design could derive the network from topology (sensor → gateway → network), but your current method trusts the CSV.

---

### Q3: Why is the method private?

Because it’s a helper used internally by `storeMeasurements()`.
External code should only call `storeMeasurements()`.

---

### Q4: Is reading all sensors inefficient?

Yes, but you **must not change** that part because tests mock it.

---

## Summary

> `checkMeasurement()` finds the sensor for the imported measurement, checks whether the measurement violates the sensor’s threshold rule, and if so it notifies the network operators.

It is the “alarm logic” of the data importing pipeline.
