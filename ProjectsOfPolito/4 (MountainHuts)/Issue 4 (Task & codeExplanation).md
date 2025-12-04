# R4 - Queries: Complete Task Analysis

## 📋 Original Task Description (R4)

**The method countMunicipalitiesPerProvince() shall return a map with the name of the province as key, and the total number of the municipalities of that province as value.**

**The method countMountainHutsPerMunicipalityPerProvince() shall return a map with the name of the province as key and as value a second map with the name of the municipality as key, and the number of mountain huts located inside that municipality as value.**

**The method countMountainHutsPerAltitudeRange() shall return a map with the altitude range returned by getAltitudeRange() as key, and the number of huts in that altitude range as value.**
- When no altitude is specified for the hut, do consider the altitude of the municipality.

**The method totalBedsNumberPerProvince() shall return a map with the name of the province as key, and the total number of beds available in all huts located in that province as value.**

**The method maximumBedsNumberPerAltitudeRange() shall return a map with the altitude range returned by getAltitudeRange() as key, and as value the maximum number of beds available in a single mountain hut in that altitude range.**
- When no altitude is specified for the hut, do consider the altitude of the municipality.

**The method municipalityNamesPerCountOfMountainHuts() shall return a map with the number of available huts as key, and a list of the municipalities including exactly that number of huts as value.**
- The list should be alphabetically sorted.

**Note:** To implement the queries, usage of Stream API is recommended; they allow writing more compact and understandable code, with respect to explicit iterations on collections and maps.

---

## 🎯 Task Breakdown

### Task 1: `countMunicipalitiesPerProvince()`
**Goal:** Count how many municipalities exist in each province.

**Input:** Internal collection of municipalities  
**Output:** `Map<String, Long>`
- **Key:** Province name
- **Value:** Count of municipalities in that province

**Example:**
```
Province "TO" has municipalities: [Torino, Ivrea, Pinerolo]
Result: {"TO" → 3}
```

**Implementation Strategy:**
```java
return mountainHuts.stream()
    .map(MountainHut::getMunicipality)
    .collect(Collectors.groupingBy(
        Municipality::getProvince,
        Collectors.counting()
    ));
```

---

### Task 2: `countMountainHutsPerMunicipalityPerProvince()`
**Goal:** Create a nested map structure showing how many huts exist in each municipality, grouped by province.

**Input:** Internal collection of mountain huts  
**Output:** `Map<String, Map<String, Long>>`
- **Outer Key:** Province name
- **Inner Map Key:** Municipality name
- **Inner Map Value:** Count of mountain huts in that municipality

**Example:**
```
Province "TO":
  - Torino: 5 huts
  - Ivrea: 2 huts
Result: {"TO" → {"Torino" → 5, "Ivrea" → 2}}
```

**Implementation Strategy:**
```java
return mountainHuts.stream()
    .collect(Collectors.groupingBy(
        h -> h.getMunicipality().getProvince(),
        Collectors.groupingBy(
            h -> h.getMunicipality().getName(),
            Collectors.counting()
        )
    ));
```

---

### Task 3: `countMountainHutsPerAltitudeRange()`
**Goal:** Count how many huts fall into each altitude range.

**Input:** Internal collection of mountain huts  
**Output:** `Map<String, Long>`
- **Key:** Altitude range string (e.g., "1000-2000")
- **Value:** Count of huts in that range

**Special Rule:** If the hut doesn't have an altitude, use the municipality's altitude.

**Example:**
```
Range "1000-2000": 10 huts
Range "2000-3000": 5 huts
Result: {"1000-2000" → 10, "2000-3000" → 5}
```

**Implementation Strategy:**
```java
return mountainHuts.stream()
    .collect(Collectors.groupingBy(
        h -> getAltitudeRange(
            h.getAltitude()
             .orElse(h.getMunicipality().getAltitude())
        ),
        Collectors.counting()
    ));
```

---

### Task 4: `totalBedsNumberPerProvince()`
**Goal:** Calculate the total number of beds available in all huts within each province.

**Input:** Internal collection of mountain huts  
**Output:** `Map<String, Integer>`
- **Key:** Province name
- **Value:** Total sum of beds in that province

**Example:**
```
Province "TO":
  - Hut A: 20 beds
  - Hut B: 15 beds
  - Hut C: 30 beds
Result: {"TO" → 65}
```

**Implementation Strategy:**
```java
return mountainHuts.stream()
    .collect(Collectors.groupingBy(
        h -> h.getMunicipality().getProvince(),
        Collectors.summingInt(MountainHut::getBedsNumber)
    ));
```

---

### Task 5: `maximumBedsNumberPerAltitudeRange()`
**Goal:** Find the maximum number of beds in a single hut for each altitude range.

**Input:** Internal collection of mountain huts  
**Output:** `Map<String, Optional<Integer>>`
- **Key:** Altitude range string
- **Value:** Optional containing the maximum beds (empty if no huts in range)

**Special Rule:** If the hut doesn't have an altitude, use the municipality's altitude.

**Example:**
```
Range "1000-2000":
  - Hut A: 20 beds
  - Hut B: 35 beds
  - Hut C: 15 beds
Result: {"1000-2000" → Optional[35]}
```

**Implementation Strategy:**
```java
return mountainHuts.stream()
    .collect(Collectors.groupingBy(
        h -> getAltitudeRange(
            h.getAltitude()
             .orElse(h.getMunicipality().getAltitude())
        ),
        Collectors.mapping(
            MountainHut::getBedsNumber,
            Collectors.maxBy(Integer::compareTo)
        )
    ));
```

---

### Task 6: `municipalityNamesPerCountOfMountainHuts()`
**Goal:** Group municipalities by how many huts they contain, with alphabetically sorted lists.

**Input:** Internal collection of mountain huts  
**Output:** `Map<Long, List<String>>`
- **Key:** Number of huts
- **Value:** Sorted list of municipality names with that many huts

**Example:**
```
Municipalities with 3 huts: [Bardonecchia, Cesana, Oulx]
Municipalities with 5 huts: [Torino]
Result: {3 → ["Bardonecchia", "Cesana", "Oulx"], 5 → ["Torino"]}
```

**Implementation Strategy:**
```java
// Step 1: Count huts per municipality
Map<String, Long> hutsPerMunicipality = mountainHuts.stream()
    .collect(Collectors.groupingBy(
        h -> h.getMunicipality().getName(),
        Collectors.counting()
    ));

// Step 2: Invert the map and sort municipality names
return hutsPerMunicipality.entrySet().stream()
    .collect(Collectors.groupingBy(
        Map.Entry::getValue,
        Collectors.mapping(
            Map.Entry::getKey,
            Collectors.collectingAndThen(
                Collectors.toList(),
                list -> { 
                    Collections.sort(list);
                    return list;
                }
            )
        )
    ));
```

---

## 🔧 What We Need to Implement

Based on the provided files, **R4 methods are NOT yet implemented**. Here's what needs to be added to the `Region` class:

### Required Data Structures
```java
// Add to Region class
private Map<String, Municipality> municipalities = new HashMap<>();
private Map<String, MountainHut> mountainHuts = new HashMap<>();
```

### Required Method Implementations

**All 6 methods need complete implementation using Stream API:**

1. ✅ `countMunicipalitiesPerProvince()` - Group municipalities by province and count
2. ✅ `countMountainHutsPerMunicipalityPerProvince()` - Nested grouping of huts
3. ✅ `countMountainHutsPerAltitudeRange()` - Group huts by altitude range
4. ✅ `totalBedsNumberPerProvince()` - Sum beds by province
5. ✅ `maximumBedsNumberPerAltitudeRange()` - Find max beds per altitude range
6. ✅ `municipalityNamesPerCountOfMountainHuts()` - Invert grouping and sort

---

## 🎓 Key Stream API Concepts Used

| Concept | Purpose | Example Method |
|---------|---------|----------------|
| `groupingBy()` | Group elements by a key | All methods |
| `counting()` | Count elements in each group | Tasks 1, 2, 3 |
| `summingInt()` | Sum integer values | Task 4 |
| `maxBy()` | Find maximum value | Task 5 |
| `mapping()` | Transform elements before collecting | Tasks 5, 6 |
| `collectingAndThen()` | Post-process collection result | Task 6 |
| `orElse()` | Provide default for Optional | Tasks 3, 5 |

---

## 📝 Implementation Checklist

- [ ] Complete R2 implementation (municipalities and huts storage)
- [ ] Complete R3 implementation (CSV file parsing)
- [ ] Implement `countMunicipalitiesPerProvince()`
- [ ] Implement `countMountainHutsPerMunicipalityPerProvince()`
- [ ] Implement `countMountainHutsPerAltitudeRange()`
- [ ] Implement `totalBedsNumberPerProvince()`
- [ ] Implement `maximumBedsNumberPerAltitudeRange()`
- [ ] Implement `municipalityNamesPerCountOfMountainHuts()`
- [ ] Test all methods with real data

---

## 💡 Important Notes

1. **Stream API is mandatory** - The requirements explicitly state to use Stream API for cleaner, more maintainable code.

2. **Altitude fallback** - Two methods (Tasks 3 and 5) must use municipality altitude when hut altitude is not available.

3. **Alphabetical sorting** - Task 6 requires sorted municipality names.

4. **Optional handling** - Task 5 returns `Optional<Integer>` to handle cases where no huts exist in a range.

5. **Nested structures** - Task 2 creates a two-level nested map structure.

---

*This analysis provides a complete roadmap for implementing R4 requirements using modern Java Stream API.*