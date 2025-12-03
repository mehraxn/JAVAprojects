# R2 - Municipalities and Mountain Huts - Complete Explanation

## 📋 R2 Task Description (from Requirements)

**Original Text:**
> Municipalities are defined using the factory method `createOrGetMunicipality()` that gets as parameters the unique name of the municipality, the province, and its altitude. The method returns an object of class `Municipality`.
> 
> If a municipality with the same name already exists, the method shall return it, ignoring the remaining parameters.
> 
> Mountain huts are created using the factory method `createOrGetMountainHut()` which has two versions:
> - One that gets as parameters the unique name of the hut, its category, number of beds, and the municipality where it is located.
> - Another version that also accepts the altitude of the hut as a parameter (placed after the name).
> 
> The method returns an object of the class `MountainHut`.
> 
> If a hut with the same name already exists, the method shall return it, ignoring the remaining parameters.
> 
> The class `Municipality` and the class `MountainHut` shall implement getters for all properties.
> 
> The method `getAltitude()` in the class `MountainHut` returns an `Optional` that is empty if the altitude of the hut was not specified in `createOrGetMountainHut()`.
> 
> The collections containing the names of municipalities and the names huts are available through the methods `getMunicipalities()` and `getMountainHuts()`, respectively.

---

## 🎯 Tasks Breakdown for R2

### **Task 1: Implement the Municipality Class**
Create a complete `Municipality` class with:
- Three private final fields: `name`, `province`, `altitude`
- A constructor accepting all three parameters
- Three getter methods for all properties

### **Task 2: Implement the MountainHut Class**
Create a complete `MountainHut` class with:
- Five private final fields: `name`, `altitude`, `category`, `bedsNumber`, `municipality`
- A constructor accepting all five parameters
- Five getter methods, with special handling for `getAltitude()` returning an `Optional<Integer>`

### **Task 3: Add Storage in Region Class**
Add data structures to store municipalities and mountain huts:
- A `Map<String, Municipality>` to store municipalities by name
- A `Map<String, MountainHut>` to store mountain huts by name
- Initialize these maps in the constructor

### **Task 4: Implement Factory Method for Municipalities**
Implement `createOrGetMunicipality()`:
- Check if a municipality with the given name already exists
- If yes, return the existing one (ignoring new parameters)
- If no, create a new one and store it in the map

### **Task 5: Implement Factory Methods for Mountain Huts**
Implement two overloaded versions of `createOrGetMountainHut()`:
- Version 1: Without altitude parameter (4 parameters)
- Version 2: With altitude parameter (5 parameters)
- Both should check for duplicates and return existing huts if found

### **Task 6: Implement Collection Getter Methods**
Implement `getMunicipalities()` and `getMountainHuts()`:
- Return unmodifiable collections of the stored objects
- Use `Collections.unmodifiableCollection()`

---

## 💡 Detailed Task Explanations

### **Why Factory Methods?**
Factory methods provide controlled object creation:
- They prevent duplicate objects with the same name
- They centralize object creation logic
- They allow reuse of existing objects instead of creating duplicates

### **Why Optional for Altitude?**
The `Optional<Integer>` type explicitly indicates that altitude might not be available:
- `Optional.ofNullable(altitude)` creates an Optional that:
  - Contains the altitude if it's not null
  - Is empty if altitude is null
- This is clearer than returning null and forces callers to handle the missing value case

### **Why Unmodifiable Collections?**
Returning unmodifiable collections protects the internal state:
- External code cannot add/remove municipalities or huts directly
- All modifications must go through the factory methods
- This maintains data integrity

---

## 🔧 Implementation Details

### **Task 1: Municipality Class**

**What we did:**
```java
public class Municipality {
    private final String name;
    private final String province;
    private final Integer altitude;

    public Municipality(String name, String province, Integer altitude) {
        this.name = name;
        this.province = province;
        this.altitude = altitude;
    }

    public String getName() { return name; }
    public String getProvince() { return province; }
    public Integer getAltitude() { return altitude; }
}
```

**Explanation:**
- **Final fields**: Once set, these cannot be changed (immutable object)
- **Constructor**: Initializes all three fields
- **Getters**: Simple return statements for each property

---

### **Task 2: MountainHut Class**

**What we did:**
```java
public class MountainHut {
    private final String name;
    private final Integer altitude;  // Can be null!
    private final String category;
    private final Integer bedsNumber;
    private final Municipality municipality;

    public MountainHut(String name, Integer altitude, String category, 
                       Integer bedsNumber, Municipality municipality) {
        this.name = name;
        this.altitude = altitude;
        this.category = category;
        this.bedsNumber = bedsNumber;
        this.municipality = municipality;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public Integer getBedsNumber() { return bedsNumber; }
    public Municipality getMunicipality() { return municipality; }
    
    public Optional<Integer> getAltitude() {
        return Optional.ofNullable(altitude);
    }
}
```

**Explanation:**
- **Altitude field**: Can be null (not all huts have altitude data)
- **Special getter**: `getAltitude()` wraps the possibly-null value in an Optional
- **Optional.ofNullable()**: 
  - If altitude is 500 → returns `Optional[500]`
  - If altitude is null → returns `Optional.empty`

---

### **Task 3: Storage in Region Class**

**What we did:**
```java
public class Region {
    private final String name;
    private List<AltitudeRange> ranges;
    private final Map<String, Municipality> municipalities;  // NEW
    private final Map<String, MountainHut> mountainHuts;     // NEW

    public Region(String name) {
        this.name = name;
        this.ranges = new ArrayList<>();
        this.municipalities = new HashMap<>();  // NEW
        this.mountainHuts = new HashMap<>();    // NEW
    }
}
```

**Explanation:**
- **HashMap choice**: Fast O(1) lookup by name (key)
- **Key = name**: We use the name as the unique identifier
- **Value = object**: The actual Municipality or MountainHut object

---

### **Task 4: Factory Method for Municipalities**

**What we did:**
```java
public Municipality createOrGetMunicipality(String name, String province, Integer altitude) {
    return municipalities.computeIfAbsent(name, 
        k -> new Municipality(name, province, altitude));
}
```

**Explanation:**
- **computeIfAbsent()**: This is a powerful Map method that:
  1. Checks if the key (`name`) exists in the map
  2. If **exists**: Returns the existing value (ignores new parameters!)
  3. If **not exists**: Computes new value using the lambda function
  4. Stores and returns the new value
- **Lambda `k -> new Municipality(...)`**: Creates a new Municipality only if needed
- **k parameter**: The key (name) passed to the lambda (we don't use it here)

**Example Flow:**
```
First call:  createOrGetMunicipality("Torino", "TO", 239)
→ "Torino" not in map
→ Creates new Municipality("Torino", "TO", 239)
→ Stores it and returns it

Second call: createOrGetMunicipality("Torino", "MI", 500)
→ "Torino" already in map
→ Returns existing Municipality (still has TO and 239!)
→ Ignores the new province "MI" and altitude 500
```

---

### **Task 5: Factory Methods for Mountain Huts**

**What we did:**
```java
// Version 1: Without altitude (4 parameters)
public MountainHut createOrGetMountainHut(String name, String category, 
                                          Integer bedsNumber, Municipality municipality) {
    return createOrGetMountainHut(name, null, category, bedsNumber, municipality);
}

// Version 2: With altitude (5 parameters)
public MountainHut createOrGetMountainHut(String name, Integer altitude, String category, 
                                          Integer bedsNumber, Municipality municipality) {
    return mountainHuts.computeIfAbsent(name, 
        k -> new MountainHut(name, altitude, category, bedsNumber, municipality));
}
```

**Explanation:**
- **Method overloading**: Two methods with same name, different parameters
- **Version 1 delegates to Version 2**: Passes `null` for altitude
- **Version 2 does the work**: Uses `computeIfAbsent()` just like municipalities
- **Why pass null?**: When altitude is unknown, we store null, which will return `Optional.empty` later

**Example Flow:**
```
Call: createOrGetMountainHut("Rifugio Italia", "Bivacco", 20, municTorino)
→ Calls 5-parameter version with altitude = null
→ Creates MountainHut with altitude = null
→ Later, getAltitude() returns Optional.empty

Call: createOrGetMountainHut("Rifugio Torino", 3375, "Rifugio", 150, municTorino)
→ Creates MountainHut with altitude = 3375
→ Later, getAltitude() returns Optional[3375]
```

---

### **Task 6: Collection Getter Methods**

**What we did:**
```java
public Collection<Municipality> getMunicipalities() {
    return Collections.unmodifiableCollection(municipalities.values());
}

public Collection<MountainHut> getMountainHuts() {
    return Collections.unmodifiableCollection(mountainHuts.values());
}
```

**Explanation:**
- **municipalities.values()**: Gets all Municipality objects from the map (ignores keys)
- **unmodifiableCollection()**: Wraps the collection to prevent modifications
- **Why unmodifiable?**: 
  - Caller cannot do: `region.getMunicipalities().clear()`
  - Protects internal data
  - Forces use of factory methods to add/remove

**What callers can do:**
```java
Collection<Municipality> munis = region.getMunicipalities();
for (Municipality m : munis) {
    System.out.println(m.getName());  // ✓ READ operations OK
}

munis.add(new Municipality(...));  // ✗ THROWS UnsupportedOperationException
```

---

## 🎓 Key Concepts Used

### **1. Factory Pattern**
- Centralized object creation
- Prevents duplicates
- Encapsulates creation logic

### **2. Optional Type**
- Explicit handling of "no value" case
- Better than null (forces caller to check)
- Part of Java 8+ functional programming style

### **3. Immutability**
- `final` fields cannot be changed
- Objects are created once and never modified
- Thread-safe and easier to reason about

### **4. Encapsulation**
- Private fields
- Public methods control access
- Unmodifiable collections protect internal state

### **5. Map.computeIfAbsent()**
- Atomic "get or create" operation
- Cleaner than if-else logic
- Thread-safe when using ConcurrentHashMap

---

## ✅ Summary

**What R2 achieves:**
1. ✓ Municipality and MountainHut classes fully implemented with all properties
2. ✓ Factory methods prevent duplicate objects with same names
3. ✓ Optional used to handle missing altitude data explicitly
4. ✓ Collections are exposed safely (unmodifiable)
5. ✓ Clean, maintainable code using modern Java features

**Why this design is good:**
- **Type safety**: Optional prevents NullPointerException
- **Data integrity**: Factory methods prevent duplicates
- **Encapsulation**: Internal maps are protected
- **Flexibility**: Two versions of createOrGetMountainHut for different use cases
- **Clear contracts**: Method signatures make requirements obvious

---

## 🔍 Testing R2

To verify R2 works correctly:

```java
Region region = new Region("Piedmont");

// Create municipalities
Municipality m1 = region.createOrGetMunicipality("Torino", "TO", 239);
Municipality m2 = region.createOrGetMunicipality("Torino", "MI", 500);  // Should return m1!

assert m1 == m2;  // Same object reference
assert m1.getProvince().equals("TO");  // Still has original province

// Create huts without altitude
MountainHut h1 = region.createOrGetMountainHut("Rifugio A", "Bivacco", 10, m1);
assert h1.getAltitude().isEmpty();  // No altitude

// Create huts with altitude
MountainHut h2 = region.createOrGetMountainHut("Rifugio B", 2500, "Rifugio", 50, m1);
assert h2.getAltitude().isPresent();  // Has altitude
assert h2.getAltitude().get() == 2500;

// Verify collections
assert region.getMunicipalities().size() == 1;  // Only Torino
assert region.getMountainHuts().size() == 2;    // Two huts
```

---

**End of R2 Explanation** 🎉