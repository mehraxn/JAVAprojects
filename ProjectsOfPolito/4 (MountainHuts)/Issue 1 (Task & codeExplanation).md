# R1 - Altitude Ranges: Complete Task Breakdown

## 📋 Original Task Description (R1)

**All interactions are through the class Region. The method `getName()` of Region returns the name of the region as it was specified in the constructor.**

Huts are classified according to their altitude range, and such ranges could be freely defined according to the specific needs. Altitude ranges are defined through the method `setAltitudeRanges()` that gets as a parameter an array of strings. Each string describes an altitude range in the format `"minValue-maxValue"` (e.g., `"0-1000"`). The range `"0-1000"` represents altitudes above 0 up to and including 1,000 meters above sea level (i.e., `0 < altitude ≤ 1000`).

Ranges may be assumed non overlapping.

The method `getAltitudeRange()` gets as a parameter an altitude and returns the string describing the range that contains the altitude among the ranges defined through `setAltitudeRanges()`. If no range includes the altitude, the method should return the default string `"0-INF"`.

---

## 🎯 Tasks to Complete for R1

### **Task 1.1: Store the Region Name**
- The `Region` class must store the name provided in the constructor
- The `getName()` method must return this stored name

### **Task 1.2: Parse and Store Altitude Ranges**
- Implement `setAltitudeRanges(String... ranges)` to accept variable arguments
- Parse each range string in format `"min-max"` (e.g., `"0-1000"`)
- Store these ranges for later retrieval
- Handle the range logic: `min < altitude ≤ max`

### **Task 1.3: Find the Correct Altitude Range**
- Implement `getAltitudeRange(Integer altitude)` to find which range contains a given altitude
- Return the range label as a string (e.g., `"0-1000"`)
- If no range contains the altitude, return `"0-INF"` as default
- Handle `null` altitude values appropriately

---

## 🛠️ Implementation Details

### **Task 1.1: Store Region Name**

#### What We Need to Do:
- Add a private field to store the region name
- Initialize it in the constructor
- Return it in the `getName()` method

#### Implementation:
```java
// ADDED: Private field to store region name
private final String name;

// CONSTRUCTOR: Initialize the name
public Region(String name) {
    this.name = name;
    this.ranges = new ArrayList<>();
}

// GETTER: Return the stored name
public String getName() {
    return name;
}
```

**Explanation:**
- We declare `name` as `final` because it shouldn't change after construction
- The constructor parameter is stored directly in the field
- `getName()` simply returns this field

---

### **Task 1.2: Parse and Store Altitude Ranges**

#### What We Need to Do:
- Create a data structure to store multiple altitude ranges
- Parse strings like `"0-1000"` into min/max values
- Store the original label for later retrieval
- Implement the logic: `min < altitude ≤ max` (exclusive min, inclusive max)

#### Implementation:
```java
// ADDED: Field to store list of altitude ranges
private List<AltitudeRange> ranges;

// CONSTRUCTOR: Initialize empty list
public Region(String name) {
    this.name = name;
    this.ranges = new ArrayList<>();
}

// SETTER: Parse and store altitude ranges
public void setAltitudeRanges(String... ranges) {
    this.ranges = Arrays.stream(ranges)
            .map(AltitudeRange::new)
            .toList();
}

// HELPER CLASS: Internal representation of an altitude range
private static class AltitudeRange {
    private final int min;
    private final int max;
    private final String label;

    public AltitudeRange(String label) {
        this.label = label;
        String[] values = label.split("-");
        this.min = Integer.parseInt(values[0]);
        this.max = Integer.parseInt(values[1]);
    }

    public boolean contains(int altitude) {
        return altitude > min && altitude <= max;
    }

    public String getLabel() {
        return label;
    }
}
```

**Explanation:**
- **List of AltitudeRange**: Stores all defined ranges
- **Stream API Usage**: Converts string array to list of `AltitudeRange` objects using `map()` and `toList()`
- **Helper Class `AltitudeRange`**:
  - **Parsing**: Splits `"0-1000"` into min=0, max=1000 using `split("-")`
  - **Logic**: `altitude > min && altitude <= max` implements the exclusive-inclusive rule
  - **Label Storage**: Keeps original string for return purposes

---

### **Task 1.3: Find the Correct Altitude Range**

#### What We Need to Do:
- Search through all stored ranges to find one that contains the given altitude
- Return the matching range's label
- Handle edge cases:
  - `null` altitude → return `"0-INF"`
  - No matching range → return `"0-INF"`

#### Implementation:
```java
public String getAltitudeRange(Integer altitude) {
    // HANDLE NULL: If altitude is null, return default
    if (altitude == null) return "0-INF"; 
    
    // STREAM SEARCH: Find first range containing the altitude
    return ranges.stream()
            .filter(r -> r.contains(altitude))
            .findFirst()
            .map(AltitudeRange::getLabel)
            .orElse("0-INF");
}
```

**Explanation:**
- **Null Check**: Returns default immediately if altitude is `null`
- **Stream Pipeline**:
  1. `stream()`: Convert list to stream
  2. `filter(r -> r.contains(altitude))`: Keep only ranges that contain the altitude
  3. `findFirst()`: Get the first matching range (returns `Optional<AltitudeRange>`)
  4. `map(AltitudeRange::getLabel)`: Extract the label from the range (returns `Optional<String>`)
  5. `orElse("0-INF")`: If no range found, return default value

---

## 🔍 Complete Example Flow

### Example 1: Normal Case
```java
Region region = new Region("Piedmont");
region.setAltitudeRanges("0-1000", "1000-2000", "2000-3000");

String range1 = region.getAltitudeRange(500);    // Returns "0-1000"
String range2 = region.getAltitudeRange(1500);   // Returns "1000-2000"
String range3 = region.getAltitudeRange(1000);   // Returns "0-1000" (1000 is included in 0-1000)
```

**Why 1000 belongs to "0-1000"?**
- The rule is `min < altitude ≤ max`
- For `"0-1000"`: `0 < 1000 ≤ 1000` → **TRUE**
- For `"1000-2000"`: `1000 < 1000 ≤ 2000` → **FALSE** (1000 is not greater than 1000)

### Example 2: Edge Cases
```java
String range4 = region.getAltitudeRange(5000);   // Returns "0-INF" (no range contains 5000)
String range5 = region.getAltitudeRange(null);   // Returns "0-INF" (null handled)
String range6 = region.getAltitudeRange(0);      // Returns "0-INF" (0 is not > 0)
```

---

## ✅ Summary of Changes

| Task | What Changed | Why |
|------|-------------|-----|
| **1.1** | Added `private final String name` field | Store region name |
| **1.1** | Initialized `name` in constructor | Set name when creating region |
| **1.1** | Implemented `getName()` to return `name` | Provide access to region name |
| **1.2** | Added `private List<AltitudeRange> ranges` field | Store multiple altitude ranges |
| **1.2** | Created `AltitudeRange` helper class | Encapsulate range parsing and logic |
| **1.2** | Implemented `setAltitudeRanges()` using Stream API | Parse and store range definitions |
| **1.3** | Implemented `getAltitudeRange()` using Stream API | Find and return matching range label |
| **1.3** | Added null check for altitude | Handle missing altitude values |

---

## 🎓 Key Java Concepts Used

1. **Stream API**: Used for transforming and filtering collections
2. **Optional**: Used to handle presence/absence of values elegantly
3. **Encapsulation**: Helper class `AltitudeRange` hides parsing details
4. **Immutability**: `final` fields ensure data integrity
5. **Method References**: `AltitudeRange::new` and `AltitudeRange::getLabel`

---

## 💡 Design Decisions

- **Why a helper class?** Separates parsing logic and makes `contains()` logic reusable
- **Why Stream API?** More declarative and readable than loops
- **Why Optional?** Explicit handling of "not found" case without null checks
- **Why store both min/max and label?** Efficient searching while preserving original format