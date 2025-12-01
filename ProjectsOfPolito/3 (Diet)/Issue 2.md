# Issue 2 Analysis: R2 - Products

## Issue Text (R2 - Products)

The diet may include also pre-packaged products (e.g., an ice cream or a snack). Products are defined by means of the method `defineProduct()` of class `Food` accepting as arguments the name, the kilo-calories, the quantity of proteins, carbohydrates (carbs) and fat. Such values express the value for the whole product, therefore the method `per100g()` returns false. The name of the product can be considered unique.

To retrieve information about the products, we can use the method `products()` of class `Food` that returns a collection of products sorted by name. To get information about a specific product, method `getProduct()` is available that accepts the name of the product and returns the corresponding object.

Both methods return the products as an object implementing the interface `NutritionalElement` (described in the previous requirement); the values are expressed for the whole product (i.e., the method `per100g()` returns false).

---

## Tasks to Complete

### 1. Create Product Inner Class
- Create a private inner class `Product` inside `Food` class
- Implement the `NutritionalElement` interface
- Store: name, calories, proteins, carbs, fat
- Override `per100g()` to return `false` (values for whole product)

### 2. Add Storage for Products
- Create a data structure to store products sorted by name
- Use `TreeMap` for automatic alphabetical sorting

### 3. Implement `defineProduct()` Method
- Accept parameters: name, calories, proteins, carbs, fat
- Create new `Product` object
- Store it in the products collection

### 4. Implement `products()` Method
- Return all products as a collection of `NutritionalElement`
- Ensure they are sorted by name

### 5. Implement `getProduct()` Method
- Accept product name as parameter
- Return the specific product as `NutritionalElement`

---

## File to Modify

**File Name:** `Food.java`

This is the facade class that manages all dietary elements including raw materials, products, recipes, and menus.

---

## Changes Made to Food.java

### Change 1: Added Product Inner Class (lines ~45-69)
```java
// --- R2: Product Inner Class ---
private class Product implements NutritionalElement {
    private String name;
    private double calories;
    private double proteins;
    private double carbs;
    private double fat;

    public Product(String name, double calories, double proteins, double carbs, double fat) {
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
        return false; // Values are for the whole product
    }
}
```

**Explanation:** This is a simple data holder class that encapsulates all product information. Unlike `RawMaterial`, the `per100g()` method returns `false` because nutritional values represent the entire product unit (like one ice cream bar), not per 100 grams.

### Change 2: Added Products Storage (line ~75)
```java
// --- R2: Storage for Products (Sorted by name) ---
private java.util.Map<String, Product> products = new java.util.TreeMap<>();
```

**Explanation:** Uses `TreeMap` to automatically maintain alphabetical order by product name (the key). This eliminates the need for manual sorting when retrieving products.

### Change 3: Implemented defineProduct() Method (lines ~120-123)
```java
public void defineProduct(String name, double calories, double proteins, double carbs, double fat) {
    Product product = new Product(name, calories, proteins, carbs, fat);
    products.put(name, product);
}
```

**Explanation:** Creates a new `Product` instance with the provided nutritional values and stores it in the `products` map using the name as the key.

### Change 4: Implemented products() Method (lines ~130-137)
```java
public Collection<NutritionalElement> products() {
    // Simple loop instead of streams
    ArrayList<NutritionalElement> list = new ArrayList<>();
    for (Product p : products.values()) {
        list.add(p);
    }
    return list;
}
```

**Explanation:** Returns all products as a collection of `NutritionalElement` interface references. Since `TreeMap` maintains sorted order, the collection is automatically sorted by name. Uses a simple loop for clarity.

### Change 5: Implemented getProduct() Method (lines ~145-147)
```java
public NutritionalElement getProduct(String name) {
    return products.get(name);
}
```

**Explanation:** Retrieves a specific product by name from the `products` map and returns it as a `NutritionalElement` interface reference.

---

## Complete Code Scrutiny of Food.java

### Overall Structure
The `Food` class serves as a **facade** for the entire diet management system. It encapsulates three types of nutritional elements (as of R3):
1. **Raw Materials** (R1) - values per 100g
2. **Products** (R2) - values per whole product
3. **Recipes** (R3) - values per 100g

### Design Pattern: Inner Classes
All three element types are implemented as **private inner classes**:
- **Benefit 1:** Encapsulation - These classes are only needed internally by `Food`
- **Benefit 2:** Access to `Food`'s methods - Recipe class uses `food.getRawMaterial()`
- **Benefit 3:** Clean API - Users only see the `NutritionalElement` interface

### Storage Strategy
All three collections use `TreeMap<String, T>`:
```java
private java.util.Map<String, RawMaterial> rawMaterials = new java.util.TreeMap<>(); // R1
private java.util.Map<String, Product> products = new java.util.TreeMap<>(); // R2
private java.util.Map<String, Recipe> recipes = new java.util.TreeMap<>(); // R3
```

**Why TreeMap?**
- Automatically sorts entries by key (name) alphabetically
- O(log n) retrieval time
- Maintains sorted order without explicit sorting

### Code Consistency
Notice the **parallel structure** across all three element types:
1. Inner class implementing `NutritionalElement`
2. TreeMap storage with name as key
3. `define/create` method to add elements
4. Plural method (e.g., `products()`) returning all elements
5. Singular getter method (e.g., `getProduct()`) for specific element

### R1 Code (RawMaterial)
- **per100g() returns true** - nutritional values per 100g
- Simple storage in TreeMap
- Standard getter methods

### R2 Code (Product) - THE CURRENT ISSUE
- **per100g() returns false** - nutritional values for entire product
- Identical structure to RawMaterial except for per100g() behavior
- This design allows polymorphic treatment via `NutritionalElement` interface

### R3 Code (Recipe)
**More Complex Than Previous:**
- Stores a reference to `Food` object (needed to look up raw materials)
- **Accumulates nutritional values** as ingredients are added
- Tracks total `weight` of all ingredients
- **Calculates per-100g values** by scaling: `(total_value / total_weight) * 100`

**Recipe Calculation Logic:**
```java
// Adding ingredients accumulates totals
calories += (raw.getCalories() * quantity) / 100.0;

// Getters scale back to per-100g
return (calories / weight) * 100.0;
```

This is mathematically correct:
- Raw material has X calories per 100g
- We use Y grams, so we add `(X * Y) / 100` to our total
- Final recipe might be 250g total with 500 calories
- Per 100g = `(500 / 250) * 100 = 200` calories per 100g

### Collection Return Methods
All use the same pattern to avoid exposing internal types:
```java
ArrayList<NutritionalElement> list = new ArrayList<>();
for (Type t : collection.values()) {
    list.add(t);
}
return list;
```

This provides **interface-based abstraction** - callers only see `NutritionalElement`, not concrete types.

### Potential Issues (Code Review)
1. **No null checks** - `getRawMaterial()`, `getProduct()`, `getRecipe()` return null if not found
2. **No duplicate checking** - `put()` will silently overwrite existing entries
3. **Recipe allows division by zero** - Has check: `if (weight == 0) return 0.0;`
4. **No validation** - Negative calories, proteins, etc. are not prevented

### Menu Class (R4) - Not Yet Implemented
Based on skeleton code, it should:
- Store both recipe portions and whole products
- Calculate **total** nutritional values (not per 100g)
- `per100g()` returns false

---

## Question 1: Is Putting Storage Necessary in the Code?

### Answer to Question 1

**Yes, storage is absolutely necessary and essential to the design.**

### What is Storage?

In this context, **storage** refers to the data structure that holds and manages the collection of Product objects created by the application:

```java
private java.util.Map<String, Product> products = new java.util.TreeMap<>();
```

This is a **Map** (specifically a TreeMap) that:
- **Key:** Product name (String) - acts as unique identifier
- **Value:** Product object - contains all nutritional data
- **Structure:** Tree-based, maintains sorted order by keys

### Why We Put Storage Here

#### Reason 1: **State Management**
The `Food` class needs to **remember** all products that have been defined during the application's lifetime. Without storage:
```java
// WITHOUT STORAGE - THIS DOESN'T WORK
public void defineProduct(String name, ...) {
    Product product = new Product(name, ...);
    // Where does it go? It vanishes after method ends!
}

public NutritionalElement getProduct(String name) {
    // How do we find it? We never saved it!
    return null; // Always!
}
```

With storage, products persist:
```java
// WITH STORAGE - WORKS CORRECTLY
public void defineProduct(String name, ...) {
    Product product = new Product(name, ...);
    products.put(name, product); // SAVED FOR LATER
}

public NutritionalElement getProduct(String name) {
    return products.get(name); // RETRIEVED FROM STORAGE
}
```

#### Reason 2: **Requirement Fulfillment**
The specification explicitly requires:
> "To retrieve information about the products, we can use the method `products()` of class `Food` that returns a collection of products"

This is **impossible** without storage. You cannot return a collection of products if you haven't stored them anywhere.

#### Reason 3: **Unique Name Enforcement**
The requirement states:
> "The name of the product can be considered unique"

A `Map<String, Product>` enforces this because:
- Each key (name) can only exist once
- If you define a product with an existing name, it replaces the old one
- The Map structure naturally prevents duplicates

#### Reason 4: **Efficient Lookup**
When users call `getProduct("Ice Cream")`, the application needs to:
1. Search through all defined products
2. Find the one named "Ice Cream"
3. Return it

**Without a Map:** You'd need to loop through an array/list - O(n) time
**With a Map:** Direct hash/tree lookup - O(log n) or O(1) time

#### Reason 5: **Sorted Retrieval**
The specification requires:
> "returns a collection of products sorted by name"

`TreeMap` automatically maintains sorted order, so:
```java
public Collection<NutritionalElement> products() {
    // Already sorted because TreeMap maintains order!
    return new ArrayList<>(products.values());
}
```

No manual sorting needed - the storage structure provides this feature.

#### Reason 6: **Encapsulation**
The storage is `private`, meaning:
- Users cannot directly access or modify the products collection
- All access goes through controlled methods (`defineProduct`, `getProduct`, `products`)
- The internal representation can be changed without affecting users

This is a fundamental **Object-Oriented Design** principle.

### What Would Happen Without Storage?

**Scenario:** Remove the storage line and see what breaks:

```java
// NO STORAGE
public class Food {
    // private java.util.Map<String, Product> products = new java.util.TreeMap<>(); // DELETED
    
    public void defineProduct(String name, ...) {
        Product product = new Product(name, ...);
        // product exists only in this method scope
        // disappears when method returns
    }
    
    public Collection<NutritionalElement> products() {
        // What do we return? We have no products stored!
        return new ArrayList<>(); // Always empty!
    }
    
    public NutritionalElement getProduct(String name) {
        // We can't look up anything!
        return null; // Always null!
    }
}
```

**Result:** The entire feature is non-functional. You can define products, but they immediately disappear. The application has **amnesia** - it forgets everything immediately.

### Analogy

Think of the `Food` class as a **library**:
- **Products** = Books
- **defineProduct()** = Adding a new book to the library
- **products()** = Viewing the entire catalog
- **getProduct()** = Finding a specific book by title

Without storage (bookshelves), you could:
- Create books ✓
- Immediately throw them away (method ends) ✗
- Never find them again ✗
- Never see your collection ✗

The **storage** is the bookshelf system that makes the library functional.

### Conclusion

**Storage is not optional - it's the core mechanism that makes the application work.** It provides:
1. ✓ Persistence of data within application lifetime
2. ✓ Ability to retrieve defined products
3. ✓ Enforcement of unique names
4. ✓ Efficient lookup performance
5. ✓ Automatic sorted order
6. ✓ Controlled access through encapsulation

Without storage, the `Food` class would be useless - it would define products that immediately vanish into the void. The storage is what transforms the class from a "product creator" into a "product manager."