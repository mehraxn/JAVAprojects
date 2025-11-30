# R1 - Raw Materials: Complete Analysis

## Issue 1 Text (R1 - Raw Materials)

**The system works through the facade class Food.**

To define a raw material, we can use the method `defineRawMaterial()` that accepts as arguments:
- name
- kilo-calories
- quantity of proteins
- carbohydrates (carbs)
- fat

All values refer to **100 grams** of raw material. The name of the raw material can be considered unique.

To retrieve information about raw materials:
- `rawMaterials()`: returns a list of raw materials, sorted by name in alphabetic order
- `getRawMaterial(name)`: accepts the name and returns the corresponding raw material

The raw materials returned are objects implementing the `NutritionalElement` interface, which provides:
- `getName()`, `getCalories()`, `getProteins()`, `getCarbs()`, `getFat()`
- Calories: expressed in KCal
- Proteins, carbs, and fat: expressed in grams

The interface includes `per100g()` that indicates whether values refer to 100 grams or absolute value. **For raw materials, values are always per 100 grams, so the method returns `true`.**

---

## Tasks to Complete for R1

### File: `Food.java`

**Changes Required:**
1. ✅ Create an inner class `RawMaterial` that implements `NutritionalElement`
2. ✅ Add storage for raw materials (sorted by name)
3. ✅ Implement `defineRawMaterial()` method
4. ✅ Implement `rawMaterials()` method (returns sorted collection)
5. ✅ Implement `getRawMaterial()` method

---

## Changes Made to Food.java

### 1. Inner Class RawMaterial
```java
private class RawMaterial implements NutritionalElement {
    private String name;
    private double calories;
    private double proteins;
    private double carbs;
    private double fat;

    public RawMaterial(String name, double calories, double proteins, 
                       double carbs, double fat) {
        this.name = name;
        this.calories = calories;
        this.proteins = proteins;
        this.carbs = carbs;
        this.fat = fat;
    }

    @Override public String getName() { return name; }
    @Override public double getCalories() { return calories; }
    @Override public double getProteins() { return proteins; }
    @Override public double getCarbs() { return carbs; }
    @Override public double getFat() { return fat; }
    
    @Override 
    public boolean per100g() {
        return true; // Values are always per 100g
    }
}
```

### 2. Storage Structure
```java
private java.util.Map<String, RawMaterial> rawMaterials = 
    new java.util.TreeMap<>();
```

### 3. Define Raw Material Method
```java
public void defineRawMaterial(String name, double calories, 
                              double proteins, double carbs, double fat) {
    RawMaterial material = new RawMaterial(name, calories, proteins, 
                                          carbs, fat);
    rawMaterials.put(name, material);
}
```

### 4. Retrieve All Raw Materials
```java
public Collection<NutritionalElement> rawMaterials() {
    ArrayList<NutritionalElement> list = new ArrayList<>();
    for (RawMaterial m : rawMaterials.values()) {
        list.add(m);
    }
    return list;
}
```

### 5. Retrieve Specific Raw Material
```java
public NutritionalElement getRawMaterial(String name) {
    return rawMaterials.get(name);
}
```

---

## Complete Food.java Code Scrutiny

The `Food.java` file implements a **Facade Pattern** for managing nutritional elements.

### Architecture Overview

**Inner Classes (Encapsulation):**
- `RawMaterial` (R1): Represents basic ingredients
- `Product` (R2): Represents packaged products
- Both implement `NutritionalElement` interface

**Storage Maps:**
- `Map<String, RawMaterial> rawMaterials`: TreeMap for sorted storage
- `Map<String, Product> products`: TreeMap for sorted storage
- `Map<String, Recipe> recipes`: TreeMap for sorted storage

### Key Design Decisions

**1. Private Inner Classes**
- Encapsulation: Implementation details hidden from external code
- Only accessible through `NutritionalElement` interface
- Prevents direct instantiation outside `Food` class

**2. TreeMap Usage**
- **Automatic sorting** by key (name)
- Guarantees alphabetical order
- O(log n) insertion and retrieval

**3. Interface Return Types**
- Methods return `NutritionalElement` not concrete classes
- Promotes loose coupling
- Allows polymorphic behavior

### R1 Implementation Details

**RawMaterial Characteristics:**
- Values always per 100g (`per100g()` returns `true`)
- Immutable after creation (no setters)
- Unique names enforced by Map

**Methods Flow:**
1. `defineRawMaterial()` → creates and stores
2. `rawMaterials()` → returns sorted collection
3. `getRawMaterial()` → retrieves by name

---

## Questions and Answers

### Question 1: Why define RawMaterial as a private inner class that implements NutritionalElement?

**Answer:**

**Why it's a class:**
- Represents a distinct entity (raw material) with specific attributes
- Encapsulates data (name, calories, proteins, carbs, fat) and behavior
- Provides a concrete implementation of the `NutritionalElement` interface

**Why it's private:**
- **Encapsulation principle**: Hides implementation details from external code
- **Controlled access**: Users can only interact through the `Food` facade and the `NutritionalElement` interface
- **Flexibility**: The internal implementation can change without affecting external code
- **Security**: Prevents direct instantiation outside the `Food` class

**Why it implements NutritionalElement:**
- **Polymorphism**: Allows raw materials, products, recipes, and menus to be treated uniformly
- **Contract enforcement**: Guarantees all nutritional elements provide the same methods
- **Return type consistency**: Methods like `rawMaterials()` can return `Collection<NutritionalElement>` regardless of the concrete type

**Why the return type is `Collection<NutritionalElement>`:**
- **Interface programming**: Clients work with the interface, not the implementation
- **Flexibility**: Different types (RawMaterial, Product, Recipe) can be returned from similar methods
- **Abstraction**: Users don't need to know about `RawMaterial` class - they only see `NutritionalElement`

---

### Question 2: Why is there the @ symbol before all the methods (getters)?

**Answer:**

The `@Override` annotation serves several purposes:

**1. Compiler Verification:**
- Tells the compiler "this method is supposed to override a method from the parent interface/class"
- If you misspell the method name or get the signature wrong, the compiler will generate an error
- Example: If you wrote `getName1()` instead of `getName()`, the compiler would catch this mistake

**2. Code Documentation:**
- Makes it immediately clear to developers that this method implements an interface requirement
- Improves code readability and maintenance
- Shows the method is not arbitrarily defined but fulfills a contract

**3. Prevent Errors:**
- Without `@Override`, if you make a typo, you'd create a NEW method instead of overriding
- The interface method would remain unimplemented, causing runtime errors

**Example:**
```java
// WITHOUT @Override - DANGEROUS
public String getname() { return name; } // Typo! Creates new method
// Interface method getName() is NOT implemented!

// WITH @Override - SAFE
@Override
public String getname() { return name; } // COMPILER ERROR!
// Compiler says: "No method 'getname()' to override"
```

**Note:** These are **getter methods** (they retrieve values), not setters (which would set values).

---

### Question 3: What is per100g() and why do we use it? Is it related to Issue 1?

**Answer:**

**What it is:**
```java
@Override 
public boolean per100g() {
    return true;
}
```

This method is a **flag/indicator** that tells how nutritional values should be interpreted.

**Purpose:**
- Returns `true`: Values represent nutrients per 100 grams
- Returns `false`: Values represent nutrients for the whole item/portion

**Why we need it:**

Different nutritional elements have different reference bases:

| Element      | per100g() | Meaning                                    |
|--------------|-----------|-------------------------------------------|
| RawMaterial  | `true`    | 200 calories means "per 100g of flour"   |
| Product      | `false`   | 200 calories means "for one ice cream"   |
| Recipe       | `true`    | 200 calories means "per 100g of recipe"  |
| Menu         | `false`   | 200 calories means "for entire menu"     |

**Yes, it IS related to Issue 1:**
- The requirement explicitly states: "For raw materials the nutritional values are always expressed per 100 grams, so the method returns true."
- This is a mandatory part of the R1 specification
- It distinguishes raw materials from products (which return `false`)

**Practical Use:**
```java
NutritionalElement element = food.getRawMaterial("Flour");
double cal = element.getCalories(); // 364 calories

if (element.per100g()) {
    // 364 calories per 100g
    // To calculate for 250g: (364 * 250) / 100 = 910 calories
} else {
    // 364 calories for the whole item
}
```

---

### Question 4: Explain completely what is `private java.util.Map<String, RawMaterial> rawMaterials = new java.util.TreeMap<>();` and why we use TreeMap?

**Answer:**

**Complete Breakdown:**

**1. `private`:**
- Access modifier
- Makes the field accessible only within the `Food` class
- Encapsulation: external code cannot directly access or modify the map

**2. `java.util.Map<String, RawMaterial>`:**
- **Map**: Interface representing key-value pairs
- **String**: The key type (raw material name)
- **RawMaterial**: The value type (the raw material object)
- This is the declared type (interface, not implementation)

**3. `rawMaterials`:**
- Variable name
- Stores the collection of all raw materials

**4. `new java.util.TreeMap<>()`:**
- **TreeMap**: Concrete implementation of the Map interface
- **Creates the actual object** that stores the data
- The `<>` (diamond operator) infers the types from the left side

**Why TreeMap and not other data types?**

| Data Type | Advantages | Disadvantages | Why NOT used? |
|-----------|-----------|---------------|---------------|
| **TreeMap** ✅ | • Automatic sorting by key<br>• Alphabetical order guaranteed<br>• O(log n) access<br>• Implements NavigableMap | • Slightly slower than HashMap<br>• More memory overhead | **CHOSEN** - Requirement: "sorted by name in alphabetic order" |
| ArrayList | • Simple<br>• Fast iteration | • No key-based lookup<br>• Must maintain sorting manually<br>• Slow search O(n) | No efficient name-based retrieval |
| HashMap | • Fastest access O(1)<br>• Less memory | • **NO guaranteed order**<br>• Random iteration order | Requirement demands sorted output |
| LinkedHashMap | • Maintains insertion order<br>• Fast access | • **Insertion order** ≠ alphabetical<br>• Must sort manually | Not alphabetically sorted |
| Array | • Compact | • Fixed size<br>• No key-based lookup | Not flexible, no name lookup |

**The Critical Requirement:**
> "returns a list of raw materials, **sorted by name in alphabetic order**"

**TreeMap automatically maintains alphabetical sorting:**
```java
// Adding materials
defineRawMaterial("Tomato", 18, 0.9, 3.9, 0.2);
defineRawMaterial("Apple", 52, 0.3, 14, 0.2);
defineRawMaterial("Banana", 89, 1.1, 23, 0.3);

// Iteration is AUTOMATICALLY sorted:
for (RawMaterial m : rawMaterials.values()) {
    System.out.println(m.getName());
}
// Output:
// Apple    (A comes first)
// Banana   (B comes next)
// Tomato   (T comes last)
```

**How it works internally:**
- TreeMap uses a **Red-Black Tree** (self-balancing binary search tree)
- Keys are sorted using natural ordering (alphabetical for Strings)
- Every insertion maintains sorted order automatically
- No need for manual sorting when retrieving data

**Summary:**
TreeMap is chosen because it perfectly satisfies the requirement of returning raw materials in alphabetical order while providing efficient name-based lookup. It's the optimal data structure for this specific use case where sorted retrieval is mandatory.

---

## Conclusion

R1 implementation demonstrates solid object-oriented design:
- **Encapsulation**: Private inner class
- **Abstraction**: Interface-based returns
- **Efficiency**: TreeMap for automatic sorting
- **Completeness**: All requirements satisfied

The design is extensible (R2-R8 follow similar patterns) and maintainable.