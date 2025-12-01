# Issue 3: R3 - Recipes Implementation

## 📋 Issue Description

**Requirement R3 - Recipes**

Raw materials can be combined as ingredients of recipes. To define a recipe, we can use the method `createRecipe()`, from class `Food`, that accepts as argument the name of the recipe. The name of the recipe can be considered unique.

A recipe is represented by an object of class `Recipe` that allows adding new ingredients by means of its method `addIngredient()` accepting as arguments the name of the raw material and the relative amount in grams.

Class `Recipe` implements the interface `NutritionalElement` and the values are expressed per 100 grams.

To retrieve the information about the recipes, we can use the method `recipes()`, of class `Food`, that returns a collection of recipes sorted by name. To get information regarding a specific recipe, we can use the method `getRecipe()` that accepts as argument the name of the recipe and return the corresponding recipe. Both methods return recipes as `NutritionalElement`.

**⚠️ Warning:** While the sum of the quantities of ingredients (in grams) of a recipe is not necessarily equal to 100 g, the nutritional values of the recipe must refer to an ideal portion of 100 grams.

---

## 🎯 Tasks Breakdown

### Task 1: Modify `Food.java`
- ✅ Add storage for recipes (sorted collection)
- ✅ Implement `createRecipe(String name)` method
- ✅ Implement `recipes()` method to return all recipes sorted by name
- ✅ Implement `getRecipe(String name)` method

### Task 2: Complete `Recipe.java` Implementation
- ✅ Add private fields for storing recipe data
- ✅ Implement constructor
- ✅ Implement `addIngredient(String material, double quantity)` method
- ✅ Implement all `NutritionalElement` interface methods:
  - `getName()`
  - `getCalories()`
  - `getProteins()`
  - `getCarbs()`
  - `getFat()`
  - `per100g()` - must return `true`
- ✅ Ensure nutritional values are calculated per 100g

---

## 📁 Files Modified

### 1. `Food.java` (diet package)
**Location:** `1764577441100_Food.java`

### 2. `Recipe.java` (diet package)  
**Location:** `1764577441101_Recipe.java`

---

## 🔧 Detailed Implementation Changes

### **File 1: Food.java**

#### **Change 1: Added Recipe Storage**
```java
// --- R3: Storage for Recipes (Sorted by name) ---
private java.util.Map<String, Recipe> recipes = new java.util.TreeMap<>();
```

**Explanation:**
- Uses `TreeMap` to automatically maintain alphabetical sorting by recipe name
- Key: Recipe name (String)
- Value: Recipe object
- TreeMap ensures the `recipes()` method returns recipes sorted alphabetically

---

#### **Change 2: Implemented createRecipe() Method**
```java
public Recipe createRecipe(String name) {
    Recipe recipe = new Recipe(name, this);
    recipes.put(name, recipe);
    return recipe;
}
```

**Explanation:**
- Creates a new `Recipe` object with the given name
- Passes `this` (the Food instance) to the Recipe constructor so the recipe can access raw materials
- Stores the recipe in the TreeMap with the name as the key
- Returns the newly created Recipe object for method chaining

---

#### **Change 3: Implemented recipes() Method**
```java
public Collection<NutritionalElement> recipes() {
    // Simple loop instead of streams
    ArrayList<NutritionalElement> list = new ArrayList<>();
    for (Recipe r : recipes.values()) {
        list.add(r);
    }
    return list;
}
```

**Explanation:**
- Returns all recipes as a collection of `NutritionalElement` interface objects
- Uses simple loop instead of streams for clarity
- TreeMap automatically provides values in sorted order (alphabetically by name)
- Creates a new ArrayList to avoid exposing internal data structure

---

#### **Change 4: Implemented getRecipe() Method**
```java
public NutritionalElement getRecipe(String name) {
    return recipes.get(name);
}
```

**Explanation:**
- Retrieves a specific recipe by name from the TreeMap
- Returns the recipe as a `NutritionalElement` interface object
- Returns `null` if recipe with given name doesn't exist

---

### **File 2: Recipe.java**

#### **Change 1: Added Private Fields**
```java
private String name;
private Food food;
private double calories = 0.0;
private double proteins = 0.0;
private double carbs = 0.0;
private double fat = 0.0;
private double weight = 0.0;
```

**Explanation:**
- `name`: Stores the unique name of the recipe
- `food`: Reference to the Food facade to access raw materials
- `calories`, `proteins`, `carbs`, `fat`: Accumulate total nutritional values
- `weight`: Tracks total weight of all ingredients in grams
- All nutritional fields initialized to 0.0

---

#### **Change 2: Implemented Constructor**
```java
public Recipe(String name, Food food) {
    this.name = name;
    this.food = food;
}
```

**Explanation:**
- Package-visible constructor (no access modifier = package-private)
- Accepts recipe name and Food reference
- Stores both for later use in ingredient calculations

---

#### **Change 3: Implemented addIngredient() Method**
```java
public Recipe addIngredient(String material, double quantity) {
    NutritionalElement raw = food.getRawMaterial(material);
    if (raw != null) {
        weight += quantity;
        
        // Simple Math: Add the values for this ingredient to the total
        calories += (raw.getCalories() * quantity) / 100.0;
        proteins += (raw.getProteins() * quantity) / 100.0;
        carbs += (raw.getCarbs() * quantity) / 100.0;
        fat += (raw.getFat() * quantity) / 100.0;
    }
    return this;
}
```

**Explanation:**

**Step 1:** Retrieve raw material
- Uses `food.getRawMaterial(material)` to get the raw material object
- Returns `NutritionalElement` interface

**Step 2:** Null check
- Only processes if raw material exists (safety check)

**Step 3:** Update total weight
- Adds the ingredient quantity (in grams) to total recipe weight

**Step 4:** Calculate and accumulate nutritional values
- **Key Formula:** `(raw.getCalories() * quantity) / 100.0`
- Raw materials store values **per 100g**
- To get values for `quantity` grams: multiply by quantity and divide by 100
- Example: If Sugar has 400 kcal per 100g, and we add 50g:
  - Calories added = (400 * 50) / 100 = 200 kcal

**Step 5:** Return this for method chaining
- Allows fluent interface: `recipe.addIngredient("A", 50).addIngredient("B", 30)`

---

#### **Change 4: Implemented getName() Method**
```java
@Override
public String getName() {
    return name;
}
```

**Explanation:**
- Simple getter returning the recipe name
- Required by `NutritionalElement` interface

---

#### **Change 5: Implemented Nutritional Getter Methods**

##### getCalories()
```java
@Override
public double getCalories() {
    if (weight == 0) return 0.0;
    return (calories / weight) * 100.0;
}
```

##### getProteins()
```java
@Override
public double getProteins() {
    if (weight == 0) return 0.0;
    return (proteins / weight) * 100.0;
}
```

##### getCarbs()
```java
@Override
public double getCarbs() {
    if (weight == 0) return 0.0;
    return (carbs / weight) * 100.0;
}
```

##### getFat()
```java
@Override
public double getFat() {
    if (weight == 0) return 0.0;
    return (fat / weight) * 100.0;
}
```

**Explanation - The Critical Per 100g Conversion:**

**Problem to Solve:**
- We accumulate total nutritional values for ALL ingredients
- But we must return values **per 100g** (as required by R3)
- Total ingredient weight is NOT necessarily 100g

**Mathematical Formula:**
```
Value per 100g = (Total Value / Total Weight) * 100
```

**Example:**
- Recipe with 3 ingredients:
  - 50g Sugar (400 kcal/100g) → adds 200 kcal
  - 100g Flour (350 kcal/100g) → adds 350 kcal
  - 50g Butter (720 kcal/100g) → adds 360 kcal
- Total: 200g of ingredients, 910 kcal total
- **Per 100g:** (910 / 200) * 100 = **455 kcal per 100g**

**Safety Check:**
- Returns 0.0 if weight is 0 (recipe with no ingredients)
- Prevents division by zero

---

#### **Change 6: Implemented per100g() Method**
```java
@Override
public boolean per100g() {
    return true;
}
```

**Explanation:**
- Always returns `true` for Recipe class
- Indicates that nutritional values are expressed per 100 grams
- This is a **requirement** stated in R3
- Differentiates Recipe from Product (which returns false)

---

## 🔍 Complete Code Scrutiny

### **Food.java - Full Analysis**

#### **R1 Implementation (Raw Materials)**

**Inner Class: RawMaterial**
```java
private class RawMaterial implements NutritionalElement {
    private String name;
    private double calories;
    private double proteins;
    private double carbs;
    private double fat;
    
    // Constructor and getters...
    @Override public boolean per100g() { return true; }
}
```

**Analysis:**
- ✅ Private inner class - encapsulation
- ✅ Implements NutritionalElement interface correctly
- ✅ All fields are immutable after construction
- ✅ `per100g()` returns true (values are per 100g)
- ✅ Simple, clean implementation

**Storage:**
```java
private java.util.Map<String, RawMaterial> rawMaterials = new java.util.TreeMap<>();
```
- ✅ TreeMap ensures alphabetical sorting
- ✅ Private access - good encapsulation

**Methods:**
```java
public void defineRawMaterial(String name, double calories, double proteins, double carbs, double fat) {
    RawMaterial material = new RawMaterial(name, calories, proteins, carbs, fat);
    rawMaterials.put(name, material);
}
```
- ✅ Clear parameter names
- ✅ Creates and stores in one operation
- ⚠️ **Potential Issue:** No duplicate name checking (though requirement says names are unique)

```java
public Collection<NutritionalElement> rawMaterials() {
    ArrayList<NutritionalElement> list = new ArrayList<>();
    for (RawMaterial m : rawMaterials.values()) {
        list.add(m);
    }
    return list;
}
```
- ✅ Returns new collection (defensive copy)
- ✅ Uses interface type in return
- ✅ Simple, readable code
- ✅ Automatically sorted (TreeMap)

```java
public NutritionalElement getRawMaterial(String name) {
    return rawMaterials.get(name);
}
```
- ✅ Simple retrieval
- ✅ Returns null if not found (standard Map behavior)

---

#### **R2 Implementation (Products)**

**Inner Class: Product**
```java
private class Product implements NutritionalElement {
    private String name;
    private double calories;
    private double proteins;
    private double carbs;
    private double fat;
    
    // Constructor and getters...
    @Override public boolean per100g() { return false; }
}
```

**Analysis:**
- ✅ Identical structure to RawMaterial
- ✅ **Key difference:** `per100g()` returns **false**
- ✅ Values represent whole product, not per 100g
- ✅ Clean separation of concerns

**Storage and Methods:**
- Same pattern as RawMaterial
- ✅ Consistent design across different element types
- ✅ Code reuse through similar structure

---

#### **R3 Implementation (Recipes)**

**Storage:**
```java
private java.util.Map<String, Recipe> recipes = new java.util.TreeMap<>();
```
- ✅ Consistent with RawMaterial and Product storage
- ✅ TreeMap for automatic sorting

**createRecipe() Method:**
```java
public Recipe createRecipe(String name) {
    Recipe recipe = new Recipe(name, this);
    recipes.put(name, recipe);
    return recipe;
}
```
- ✅ **Important:** Passes `this` to Recipe constructor
- ✅ Enables Recipe to access Food's raw materials
- ✅ Returns Recipe (not NutritionalElement) for method chaining with `addIngredient()`
- ✅ Stores immediately

**recipes() Method:**
```java
public Collection<NutritionalElement> recipes() {
    ArrayList<NutritionalElement> list = new ArrayList<>();
    for (Recipe r : recipes.values()) {
        list.add(r);
    }
    return list;
}
```
- ✅ Consistent with `rawMaterials()` and `products()`
- ✅ Returns as NutritionalElement interface
- ✅ Defensive copy

**getRecipe() Method:**
```java
public NutritionalElement getRecipe(String name) {
    return recipes.get(name);
}
```
- ✅ Simple and consistent with other getters

---

### **Recipe.java - Full Analysis**

#### **Field Design**
```java
private String name;
private Food food;
private double calories = 0.0;
private double proteins = 0.0;
private double carbs = 0.0;
private double fat = 0.0;
private double weight = 0.0;
```

**Analysis:**
- ✅ `name` and `food` are set once in constructor (effectively final)
- ✅ Nutritional fields initialized to 0.0
- ✅ `weight` tracks total ingredient weight - **critical for per100g calculation**
- ✅ All fields private - good encapsulation

---

#### **Constructor**
```java
public Recipe(String name, Food food) {
    this.name = name;
    this.food = food;
}
```

**Analysis:**
- ✅ Package-private visibility (called only by Food class)
- ✅ Stores Food reference for accessing raw materials
- ✅ Simple initialization

---

#### **addIngredient() Method**

```java
public Recipe addIngredient(String material, double quantity) {
    NutritionalElement raw = food.getRawMaterial(material);
    if (raw != null) {
        weight += quantity;
        calories += (raw.getCalories() * quantity) / 100.0;
        proteins += (raw.getProteins() * quantity) / 100.0;
        carbs += (raw.getCarbs() * quantity) / 100.0;
        fat += (raw.getFat() * quantity) / 100.0;
    }
    return this;
}
```

**Deep Analysis:**

**Line 1:** Method signature
- Returns `Recipe` for method chaining
- Accepts material name (string) and quantity in grams

**Line 2:** Retrieve raw material
- Uses Food reference to get raw material
- ✅ Demonstrates good use of facade pattern

**Line 3:** Null safety
- Only processes if material exists
- ⚠️ **Silent failure** - might want to log or throw exception
- ✅ Prevents NullPointerException

**Line 4:** Weight accumulation
- ✅ Crucial for per100g calculation later
- Example: Adding 50g, 100g, 50g → total weight = 200g

**Lines 5-8:** Nutritional value accumulation
- **Formula breakdown:**
  - `raw.getCalories()` returns kcal **per 100g**
  - `quantity` is in grams
  - `(raw.getCalories() * quantity) / 100.0` converts to actual calories for this quantity
  
**Example Calculation:**
```
Raw material: Sugar
- 400 kcal per 100g
- Adding 50g

Calories to add = (400 * 50) / 100 = 200 kcal
```

**Line 9:** Return this
- ✅ Enables fluent interface
- ✅ Allows: `recipe.addIngredient("A", 50).addIngredient("B", 100)`

---

#### **Nutritional Getters - Mathematical Correctness**

**The Challenge:**
Recipe accumulates nutritional values for ALL ingredients (total weight may be 200g, 300g, etc.), but must return values **per 100g**.

**The Solution:**
```java
public double getCalories() {
    if (weight == 0) return 0.0;
    return (calories / weight) * 100.0;
}
```

**Mathematical Proof:**

Let's say we have a recipe with:
- Ingredient A: 50g with 200 kcal (from calculation)
- Ingredient B: 100g with 350 kcal (from calculation)
- Ingredient C: 50g with 360 kcal (from calculation)

**Accumulated values:**
- Total weight = 200g
- Total calories = 910 kcal

**Per 100g calculation:**
```
Calories per 100g = (910 / 200) * 100 = 455 kcal
```

**Verification:**
- If we scale the recipe to 100g:
  - Ingredient A: (50/200)*100 = 25g → 100 kcal
  - Ingredient B: (100/200)*100 = 50g → 175 kcal
  - Ingredient C: (50/200)*100 = 25g → 180 kcal
  - Total: 100g → 455 kcal ✅

**Safety:**
- `if (weight == 0) return 0.0` prevents division by zero
- ✅ Handles edge case of recipe with no ingredients

---

#### **per100g() Method**
```java
@Override
public boolean per100g() {
    return true;
}
```

**Analysis:**
- ✅ **Hardcoded to true** as per requirement R3
- ✅ Distinguishes Recipe from Product
- ✅ Indicates all nutritional getters return per-100g values

---

## 📊 Design Pattern Analysis

### **Facade Pattern**
- `Food` class acts as a facade
- ✅ Provides simple interface to complex subsystem
- ✅ Manages RawMaterial, Product, and Recipe creation

### **Builder Pattern (Partial)**
- `Recipe.addIngredient()` with method chaining
- ✅ Allows fluent construction of recipes
- ✅ Returns `this` for chaining

### **Strategy Pattern (Polymorphism)**
- `NutritionalElement` interface
- ✅ Different implementations: RawMaterial, Product, Recipe
- ✅ Each has different `per100g()` behavior

---

## ⚠️ Potential Issues & Edge Cases

### **Issue 1: No Duplicate Name Validation**
```java
public Recipe createRecipe(String name) {
    Recipe recipe = new Recipe(name, this);
    recipes.put(name, recipe);  // Overwrites if exists
    return recipe;
}
```
- ⚠️ Creating recipe with existing name silently overwrites
- **Mitigation:** Requirement states names are unique
- **Improvement:** Could add check: `if (recipes.containsKey(name)) throw exception`

### **Issue 2: Silent Failure in addIngredient()**
```java
if (raw != null) {
    // process
}
return this; // Returns even if raw was null
```
- ⚠️ No indication when invalid ingredient is added
- **Impact:** Could lead to confusing bugs
- **Improvement:** Log warning or throw exception

### **Issue 3: Division by Zero Protection**
```java
if (weight == 0) return 0.0;
```
- ✅ Protected against division by zero
- ✅ Handles recipe with no ingredients gracefully

### **Issue 4: Floating Point Precision**
```java
return (calories / weight) * 100.0;
```
- ⚠️ Potential floating-point precision issues with many ingredients
- **Impact:** Minimal for food calculations
- **Note:** Double precision is sufficient for this domain

---

## ✅ Testing Scenarios

### **Test 1: Basic Recipe Creation**
```java
Food food = new Food();
food.defineRawMaterial("Sugar", 400, 0, 100, 0);
food.defineRawMaterial("Flour", 350, 10, 70, 1);

Recipe recipe = food.createRecipe("Cookies");
recipe.addIngredient("Sugar", 50).addIngredient("Flour", 100);

// Expected per 100g:
// Weight = 150g total
// Calories = (400*50/100 + 350*100/100) / 150 * 100
//          = (200 + 350) / 150 * 100 = 366.67 kcal/100g
```

### **Test 2: Alphabetical Sorting**
```java
food.createRecipe("Zebra Cake");
food.createRecipe("Apple Pie");
food.createRecipe("Banana Bread");

Collection<NutritionalElement> recipes = food.recipes();
// Expected order: Apple Pie, Banana Bread, Zebra Cake
```

### **Test 3: Empty Recipe**
```java
Recipe recipe = food.createRecipe("Empty");
// Should return 0.0 for all nutritional values
// per100g() should still return true
```

### **Test 4: Method Chaining**
```java
Recipe recipe = food.createRecipe("Complex")
    .addIngredient("Sugar", 50)
    .addIngredient("Flour", 100)
    .addIngredient("Butter", 50);
// Should work smoothly with chaining
```

---

## 🚀 GitFlow Process

### **Branch Creation**
```bash
# After R2 is merged to main
git checkout main
git pull origin main
git checkout -b feature/R3-recipes
```

### **Implementation Steps**
1. ✅ Modify Food.java - add recipe storage and methods
2. ✅ Complete Recipe.java implementation
3. ✅ Test locally
4. Commit changes
5. Push to feature branch
6. Create Merge Request

### **Commit Message**
```
feat(R3): Implement Recipe functionality

- Add recipe storage in Food class using TreeMap
- Implement createRecipe(), recipes(), getRecipe() methods
- Complete Recipe class with ingredient management
- Add per-100g nutritional value calculation
- Support method chaining in addIngredient()

Closes #3
```

### **Merge Request Checklist**
- [ ] All methods implemented as per requirement
- [ ] Code follows existing patterns (RawMaterial, Product)
- [ ] Nutritional values correctly calculated per 100g
- [ ] Method chaining works in addIngredient()
- [ ] Collection returned sorted alphabetically
- [ ] Pipeline passes
- [ ] Code reviewed using checklist
- [ ] All threads resolved

---

## 📝 Summary

**What Was Implemented:**
1. Recipe storage system in Food class
2. Recipe creation and retrieval methods
3. Complete Recipe class with ingredient management
4. Per-100g nutritional value calculations
5. Method chaining support

**Key Technical Achievements:**
- ✅ Correct mathematical conversion to per-100g values
- ✅ Proper use of Food reference for accessing raw materials
- ✅ Consistent design patterns with R1 and R2
- ✅ Alphabetical sorting maintained
- ✅ Clean separation of concerns

**Files Modified:**
- `Food.java` - Added recipe management
- `Recipe.java` - Complete implementation

**Dependencies:**
- Requires R1 (Raw Materials) to be implemented
- Raw materials must be defined before adding to recipes
- Food facade provides access to raw materials

---

## 🎓 Learning Points

1. **Per-100g Normalization:** Critical concept in nutritional calculations
2. **Method Chaining:** Improves API usability with fluent interface
3. **Facade Pattern:** Food class simplifies complex subsystem interactions
4. **Interface Polymorphism:** NutritionalElement enables uniform handling
5. **Defensive Programming:** Null checks prevent runtime errors

---

**Status:** ✅ COMPLETE - Ready for Merge Request
**Next Requirement:** R4 - Menu Implementation