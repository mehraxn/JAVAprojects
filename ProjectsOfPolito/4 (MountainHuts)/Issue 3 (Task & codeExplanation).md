# R3: Input from CSV - Complete Task Breakdown

## 📋 Original Task Description

**R3 - Input from CSV**

The static factory method `fromFile()` creates an object of class `Region` using the information stored inside a file whose name is passed as an argument. In more details, the method should populate the region with both the municipalities and the huts, described in a CSV file that is structured as follows:

| N | Columns | Municipality | MountainHut |
|---|---------|--------------|-------------|
| 0 | Province | ✓ | |
| 1 | Municipality | ✓ | |
| 2 | MunicipalityAltitude | ✓ | |
| 3 | Name | | ✓ |
| 4 | Altitude | | ✓ |
| 5 | Category | | ✓ |
| 6 | BedsNumber | | ✓ |

**Note:** The file contains a line for each hut, therefore the information about municipalities may be duplicated.

CSV fields are separated by a semicolon (`;`). The altitude of a hut is empty if the information is not available.

All data about mountain huts in Piedmont are available in the file: `data/mountain_huts.csv`.

**Hints:**
- To read from a CSV file you can use the provided method `readData()`, which reads a text file line by line, and returns a list of rows. The first row contains the headers, while the actual data starts from the second row.

---

## 🎯 Task Breakdown

### Task 1: Understand the CSV File Structure
**What we need to know:**
- The CSV file has 7 columns (indices 0-6)
- Fields are separated by semicolons (`;`)
- First row contains headers (skip it)
- Each row represents ONE mountain hut
- Municipality information is repeated for each hut in that municipality
- Column 4 (Altitude of hut) can be empty/null

**Example CSV Row:**
```
TORINO;Balme;1430;Rifugio Città di Ciriè;2100;Rifugio Alpino;48
```

This translates to:
- Province: TORINO
- Municipality: Balme
- Municipality Altitude: 1430
- Hut Name: Rifugio Città di Ciriè
- Hut Altitude: 2100
- Category: Rifugio Alpino
- Beds Number: 48

---

### Task 2: Implement the `fromFile()` Static Factory Method
**What we need to do:**
1. Create a new `Region` object with the given name
2. Read all lines from the CSV file using `readData()`
3. Skip the first line (headers)
4. Parse each remaining line
5. Create municipalities and mountain huts from the data
6. Return the populated Region object

**Method Signature:**
```java
public static Region fromFile(String name, String file)
```

---

### Task 3: Parse Each CSV Line
**What we need to do:**
- Split each line by semicolon (`;`)
- Extract the 7 fields:
  - `fields[0]` → Province
  - `fields[1]` → Municipality name
  - `fields[2]` → Municipality altitude
  - `fields[3]` → Hut name
  - `fields[4]` → Hut altitude (may be empty!)
  - `fields[5]` → Category
  - `fields[6]` → Beds number

---

### Task 4: Handle Empty Altitude Fields
**What we need to do:**
- Check if `fields[4]` (hut altitude) is empty or blank
- If empty: use `null` for altitude
- If not empty: parse it to Integer

**Logic:**
```java
Integer hutAltitude = (fields[4] == null || fields[4].trim().isEmpty()) 
    ? null 
    : Integer.parseInt(fields[4].trim());
```

---

### Task 5: Create or Get Municipalities
**What we need to do:**
- For each row, create/get the municipality using `createOrGetMunicipality()`
- Parameters: name, province, altitude
- The factory method handles duplicates automatically

---

### Task 6: Create or Get Mountain Huts
**What we need to do:**
- For each row, create/get the mountain hut
- Use the appropriate overloaded method:
  - If hut altitude is `null`: use version WITHOUT altitude parameter
  - If hut altitude exists: use version WITH altitude parameter

---

## ✅ Implementation (Changes Made)

### Prerequisites: Complete R2 First!
Before R3 can work, we need R2 to be implemented:

**R2 Requirements:**
1. Add data structures to store municipalities and huts
2. Implement `createOrGetMunicipality()` factory method
3. Implement both versions of `createOrGetMountainHut()` factory methods
4. Implement `getMunicipalities()` and `getMountainHuts()` methods
5. Complete the `MountainHut` class with all fields

### The Implementation

**Step 1: Add Storage in Region Class**
```java
// Store municipalities by name (for duplicate detection)
private final Map<String, Municipality> municipalities = new HashMap<>();

// Store mountain huts by name (for duplicate detection)
private final Map<String, MountainHut> mountainHuts = new HashMap<>();
```

**Step 2: Implement `fromFile()` Method**
```java
public static Region fromFile(String name, String file) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(file);
    
    // Create new region
    Region region = new Region(name);
    
    // Read all lines from CSV
    List<String> lines = readData(file);
    
    // Process each line (skip header at index 0)
    lines.stream()
        .skip(1)  // Skip header row
        .forEach(line -> {
            // Split by semicolon
            String[] fields = line.split(";");
            
            // Extract municipality data
            String province = fields[0];
            String municipalityName = fields[1];
            Integer municipalityAltitude = Integer.parseInt(fields[2]);
            
            // Create or get municipality
            Municipality municipality = region.createOrGetMunicipality(
                municipalityName, province, municipalityAltitude
            );
            
            // Extract hut data
            String hutName = fields[3];
            String altitudeStr = fields[4];
            String category = fields[5];
            Integer bedsNumber = Integer.parseInt(fields[6]);
            
            // Handle empty altitude
            if (altitudeStr == null || altitudeStr.trim().isEmpty()) {
                // Version without altitude
                region.createOrGetMountainHut(
                    hutName, category, bedsNumber, municipality
                );
            } else {
                // Version with altitude
                Integer hutAltitude = Integer.parseInt(altitudeStr.trim());
                region.createOrGetMountainHut(
                    hutName, hutAltitude, category, bedsNumber, municipality
                );
            }
        });
    
    return region;
}
```

**Step 3: Implement `createOrGetMunicipality()` Factory Method**
```java
public Municipality createOrGetMunicipality(String name, String province, Integer altitude) {
    // Check if municipality already exists
    if (municipalities.containsKey(name)) {
        return municipalities.get(name);
    }
    
    // Create new municipality
    Municipality municipality = new Municipality(name, province, altitude);
    municipalities.put(name, municipality);
    return municipality;
}
```

**Step 4: Implement Both `createOrGetMountainHut()` Methods**
```java
// Version WITHOUT altitude
public MountainHut createOrGetMountainHut(String name, String category, 
                                         Integer bedsNumber, Municipality municipality) {
    // Check if hut already exists
    if (mountainHuts.containsKey(name)) {
        return mountainHuts.get(name);
    }
    
    // Create new hut without altitude
    MountainHut hut = new MountainHut(name, null, category, bedsNumber, municipality);
    mountainHuts.put(name, hut);
    return hut;
}

// Version WITH altitude
public MountainHut createOrGetMountainHut(String name, Integer altitude, String category, 
                                         Integer bedsNumber, Municipality municipality) {
    // Check if hut already exists
    if (mountainHuts.containsKey(name)) {
        return mountainHuts.get(name);
    }
    
    // Create new hut with altitude
    MountainHut hut = new MountainHut(name, altitude, category, bedsNumber, municipality);
    mountainHuts.put(name, hut);
    return hut;
}
```

**Step 5: Complete the `MountainHut` Class**
```java
public class MountainHut {
    private final String name;
    private final Integer altitude;  // Can be null
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

    public String getName() {
        return name;
    }

    public Optional<Integer> getAltitude() {
        return Optional.ofNullable(altitude);
    }

    public String getCategory() {
        return category;
    }

    public Integer getBedsNumber() {
        return bedsNumber;
    }

    public Municipality getMunicipality() {
        return municipality;
    }
}
```

**Step 6: Implement Collection Getters**
```java
public Collection<Municipality> getMunicipalities() {
    return Collections.unmodifiableCollection(municipalities.values());
}

public Collection<MountainHut> getMountainHuts() {
    return Collections.unmodifiableCollection(mountainHuts.values());
}
```

---

## 🔑 Key Points to Remember

1. **Use Stream API**: The implementation uses `lines.stream().skip(1).forEach()` to process CSV lines efficiently

2. **Factory Pattern**: Both `createOrGetMunicipality()` and `createOrGetMountainHut()` follow the factory pattern with duplicate detection

3. **Optional for Altitude**: The `MountainHut` uses `Optional<Integer>` for altitude to explicitly handle missing values

4. **Immutable Collections**: `getMunicipalities()` and `getMountainHuts()` return unmodifiable collections to protect internal state

5. **Method Overloading**: Two versions of `createOrGetMountainHut()` handle cases with and without altitude

6. **Null Safety**: Check for empty strings when parsing altitude: `altitudeStr.trim().isEmpty()`

---

## 🎓 What We Learned

- **CSV Parsing**: How to read and parse semicolon-separated files
- **Factory Methods**: Creating objects with duplicate detection
- **Optional Pattern**: Handling nullable values explicitly
- **Stream API**: Processing collections functionally
- **Data Deduplication**: Using Maps with unique keys to prevent duplicates
- **Method Overloading**: Providing multiple signatures for flexibility

---

## 🚀 Testing R3

To verify the implementation works:

```java
// Load region from file
Region piedmont = Region.fromFile("Piedmont", "data/mountain_huts.csv");

// Check loaded data
System.out.println("Municipalities: " + piedmont.getMunicipalities().size());
System.out.println("Mountain Huts: " + piedmont.getMountainHuts().size());

// Verify a specific hut
MountainHut hut = piedmont.getMountainHuts().stream()
    .filter(h -> h.getName().equals("Rifugio Città di Ciriè"))
    .findFirst()
    .orElse(null);
    
if (hut != null) {
    System.out.println("Found: " + hut.getName());
    System.out.println("Altitude: " + hut.getAltitude().orElse(null));
    System.out.println("Municipality: " + hut.getMunicipality().getName());
}
```

---

**Ready for R4 (Queries)!** Once R3 is complete, all data is loaded and ready for the Stream API queries.