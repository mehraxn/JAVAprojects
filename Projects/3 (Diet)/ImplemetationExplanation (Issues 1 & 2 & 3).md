# Diet Management System - Requirements Guide with Implementation Details

## Project Overview
This project implements a diet management application that computes nutritional values for raw materials, recipes, products, and menus. It also manages a takeaway restaurant chain with customers and orders.

**Package:** `diet`  
**Development Process:** GitFlow (feature branches → merge requests → main branch)

---

## R1 - Raw Materials

### Task Description
Implement the system to define and manage raw materials with their nutritional values per 100 grams.

### What to Implement
- **Define raw materials** with name, calories, proteins, carbs, and fat (all values per 100g)
- **Retrieve all raw materials** sorted alphabetically by name
- **Retrieve specific raw material** by name
- Raw materials must implement the `NutritionalElement` interface
- The `per100g()` method must return `true` for raw materials

### Implementation Details

#### File: Food.java

**Changes Made:**

1. **Added Inner Class `RawMaterial`:**
   ```java
   private class RawMaterial implements NutritionalElement {
       private String name;
       private double calories;
       private double proteins;
       private double carbs;
       private double fat;
       
       public RawMaterial(String name, double calories, double proteins, double carbs, double fat) {
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
       @Override public boolean per100g() { return true; }
   }
   ```
   - Created as inner class to encapsulate raw material data
   - Implements `NutritionalElement` interface with all required methods
   - `per100g()` returns `true` because values are per 100g
   - Simple constructor to initialize all fields

2. **Added Storage Map:**
   ```java
   private java.util.Map<String, RawMaterial> rawMaterials = new java.util.TreeMap<>();
   ```
   - Uses `TreeMap` for automatic alphabetical sorting by name
   - Key is the raw material name (unique identifier)
   - Value is the RawMaterial object

3. **Implemented `defineRawMaterial()` Method:**
   ```java
   public void defineRawMaterial(String name, double calories, double proteins, double carbs, double fat) {
       RawMaterial material = new RawMaterial(name, calories, proteins, carbs, fat);
       rawMaterials.put(name, material);
   }
   ```
   - Creates new `RawMaterial` instance with provided values
   - Stores it in the TreeMap with name as key
   - TreeMap ensures automatic sorting

4. **Implemented `rawMaterials()` Method:**
   ```java
   public Collection<NutritionalElement> rawMaterials() {
       ArrayList<NutritionalElement> list = new ArrayList<>();
       for (RawMaterial m : rawMaterials.values()) {
           list.add(m);
       }
       return list;
   }
   ```
   - Returns all raw materials as `NutritionalElement` collection
   - Uses simple loop instead of streams for clarity
   - Already sorted due to TreeMap

5. **Implemented `getRawMaterial()` Method:**
   ```java
   public NutritionalElement getRawMaterial(String name) {
       return rawMaterials.get(name);
   }
   ```
   - Retrieves specific raw material by name
   - Returns null if not found

**Why These Changes Work:**
- TreeMap automatically maintains alphabetical order
- Inner class keeps implementation encapsulated
- All interface methods properly implemented
- `per100g()` correctly returns `true` for raw materials

---

## R2 - Products

### Task Description
Implement pre-packaged products (e.g., ice cream, snacks) with absolute nutritional values for the entire product.

### What to Implement
- **Define products** with name and nutritional values for the whole product
- **Retrieve all products** sorted alphabetically by name
- **Retrieve specific product** by name
- Products must implement the `NutritionalElement` interface
- The `per100g()` method must return `false` for products

### Implementation Details

#### File: Food.java

**Changes Made:**

1. **Added Inner Class `Product`:**
   ```java
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
       @Override public boolean per100g() { return false; }
   }
   ```
   - Similar structure to RawMaterial but returns `false` for `per100g()`
   - Values represent entire product, not per 100g
   - Implements all `NutritionalElement` interface methods

2. **Added Storage Map:**
   ```java
   private java.util.Map<String, Product> products = new java.util.TreeMap<>();
   ```
   - TreeMap for automatic alphabetical sorting
   - Separate storage from raw materials

3. **Implemented `defineProduct()` Method:**
   ```java
   public void defineProduct(String name, double calories, double proteins, double carbs, double fat) {
       Product product = new Product(name, calories, proteins, carbs, fat);
       products.put(name, product);
   }
   ```
   - Creates new Product instance
   - Stores in TreeMap with automatic sorting

4. **Implemented `products()` Method:**
   ```java
   public Collection<NutritionalElement> products() {
       ArrayList<NutritionalElement> list = new ArrayList<>();
       for (Product p : products.values()) {
           list.add(p);
       }
       return list;
   }
   ```
   - Returns all products as NutritionalElement collection
   - Simple loop for clarity
   - Sorted by TreeMap

5. **Implemented `getProduct()` Method:**
   ```java
   public NutritionalElement getProduct(String name) {
       return products.get(name);
   }
   ```
   - Retrieves specific product by name

**Key Difference from R1:**
- `per100g()` returns `false` because values are for the entire product, not per 100g

---

## R3 - Recipes

### Task Description
Implement recipes that combine raw materials as ingredients, with nutritional values calculated per 100 grams.

### What to Implement
- **Create recipes** by name
- **Add ingredients** (raw materials) with quantities in grams
- **Calculate nutritional values** per 100g (even if total ingredients ≠ 100g)
- **Retrieve all recipes** sorted alphabetically
- **Retrieve specific recipe** by name

### Implementation Details

#### File: Recipe.java

**Changes Made:**

1. **Added Fields:**
   ```java
   private String name;
   private Food food;
   private double calories = 0.0;
   private double proteins = 0.0;
   private double carbs = 0.0;
   private double fat = 0.0;
   private double weight = 0.0;
   ```
   - `name`: Recipe identifier
   - `food`: Reference to Food object to access raw materials
   - `calories, proteins, carbs, fat`: Accumulated nutritional values
   - `weight`: Total weight of all ingredients

2. **Added Constructor:**
   ```java
   public Recipe(String name, Food food) {
       this.name = name;
       this.food = food;
   }
   ```
   - Initializes recipe with name and Food reference
   - Food reference needed to lookup raw materials

3. **Implemented `addIngredient()` Method:**
   ```java
   public Recipe addIngredient(String material, double quantity) {
       NutritionalElement raw = food.getRawMaterial(material);
       if (raw != null) {
           weight += quantity;
           
           // Add the values for this ingredient to the total
           calories += (raw.getCalories() * quantity) / 100.0;
           proteins += (raw.getProteins() * quantity) / 100.0;
           carbs += (raw.getCarbs() * quantity) / 100.0;
           fat += (raw.getFat() * quantity) / 100.0;
       }
       return this;
   }
   ```
   - Retrieves raw material from Food object
   - Adds ingredient quantity to total weight
   - Calculates nutritional contribution: `(value_per_100g * quantity) / 100.0`
   - Accumulates all nutritional values
   - Returns `this` for method chaining

4. **Implemented Getter Methods:**
   ```java
   @Override
   public String getName() {
       return name;
   }

   @Override
   public double getCalories() {
       if (weight == 0) return 0.0;
       return (calories / weight) * 100.0;
   }

   @Override
   public double getProteins() {
       if (weight == 0) return 0.0;
       return (proteins / weight) * 100.0;
   }

   @Override
   public double getCarbs() {
       if (weight == 0) return 0.0;
       return (carbs / weight) * 100.0;
   }

   @Override
   public double getFat() {
       if (weight == 0) return 0.0;
       return (fat / weight) * 100.0;
   }
   ```
   - **Critical Formula:** `(accumulated_value / total_weight) * 100.0`
   - Converts total accumulated values to per-100g values
   - Example: If 200g total ingredients with 500 total calories:
     - `(500 / 200) * 100 = 250` calories per 100g
   - Zero-weight check prevents division by zero

5. **Implemented `per100g()` Method:**
   ```java
   @Override
   public boolean per100g() {
       return true;
   }
   ```
   - Always returns `true` for recipes
   - Indicates values are per 100g

#### File: Food.java

**Changes Made:**

1. **Added Storage Map:**
   ```java
   private java.util.Map<String, Recipe> recipes = new java.util.TreeMap<>();
   ```
   - TreeMap for automatic sorting

2. **Implemented `createRecipe()` Method:**
   ```java
   public Recipe createRecipe(String name) {
       Recipe recipe = new Recipe(name, this);
       recipes.put(name, recipe);
       return recipe;
   }
   ```
   - Creates new Recipe with `this` (Food reference)
   - Stores in TreeMap
   - Returns Recipe object for method chaining

3. **Implemented `recipes()` Method:**
   ```java
   public Collection<NutritionalElement> recipes() {
       ArrayList<NutritionalElement> list = new ArrayList<>();
       for (Recipe r : recipes.values()) {
           list.add(r);
       }
       return list;
   }
   ```
   - Returns all recipes as NutritionalElement collection

4. **Implemented `getRecipe()` Method:**
   ```java
   public NutritionalElement getRecipe(String name) {
       return recipes.get(name);
   }
   ```
   - Retrieves specific recipe by name

**Why This Implementation Works:**
- **Accumulation Phase:** As ingredients are added, accumulate their nutritional contributions
- **Conversion Phase:** When values are requested, convert accumulated totals to per-100g
- Formula ensures correct per-100g values regardless of total recipe weight
- Method chaining allows fluid recipe building: `recipe.addIngredient("A", 50).addIngredient("B", 100)`

**Example Calculation:**
```
Recipe with 200g total ingredients:
- Ingredient A: 100g, 300 cal/100g → contributes 300 cal
- Ingredient B: 100g, 200 cal/100g → contributes 200 cal
Total: 500 cal in 200g
Per 100g: (500 / 200) * 100 = 250 cal/100g
```

---

## R4 - Menu

### Task Description
Implement menus that consist of recipe portions and/or packaged products, with total nutritional values.

### What to Implement
- **Create menus** by name
- **Add recipe portions** with specific quantities in grams
- **Add products** (one unit per addition)
- **Calculate total nutritional values** for the entire menu

### Files to Modify
1. **Food.java**
   - Implement `createMenu(String name)` - creates and returns new Menu

2. **Menu.java**
   - Implement constructor and all methods
   - Support adding recipes and products
   - Calculate total nutritional values

### Implementation Status
⚠️ **NOT YET IMPLEMENTED** - Files show only skeleton code with `return null` statements

### Required Implementation

#### What Needs to be Added to Menu.java:

1. **Add Fields:**
   ```java
   private String name;
   private Food food;
   private Map<String, Double> recipePortions;  // recipe name -> quantity in grams
   private List<String> products;               // list of product names
   ```

2. **Add Constructor:**
   ```java
   public Menu(String name, Food food) {
       this.name = name;
       this.food = food;
       this.recipePortions = new HashMap<>();
       this.products = new ArrayList<>();
   }
   ```

3. **Implement `addRecipe()`:**
   - Store recipe name and quantity in map
   - Return `this` for method chaining

4. **Implement `addProduct()`:**
   - Add product name to list
   - Return `this` for method chaining

5. **Implement Nutritional Getters:**
   - For recipes: `(recipe_per_100g_value * quantity) / 100.0`
   - For products: Add full product value
   - Sum all contributions

#### What Needs to be Added to Food.java:

```java
public Menu createMenu(String name) {
    return new Menu(name, this);
}
```

### Key Points
- Menu values are for the ENTIRE menu (not per 100g)
- Recipes: scale per-100g values by quantity/100
- Products: add full product values
- Support method chaining
- `per100g()` returns `false`

---

## R5 - Restaurant

### Task Description
Implement restaurant management with opening hours and menu offerings.

### What to Implement
- **Create restaurants** by name
- **Set working hours** with multiple time ranges
- **Check if open** at a specific time
- **Add menus** to restaurant offerings
- **Retrieve menus** by name

### Files to Modify
1. **Takeaway.java**
   - Implement `addRestaurant(String restaurantName)` - creates and returns Restaurant
   - Implement `restaurants()` - returns collection of restaurant names

2. **Restaurant.java**
   - Implement all methods for managing hours and menus

### Implementation Status
⚠️ **NOT YET IMPLEMENTED** - Files show only skeleton code with `return null` statements

### Required Implementation

#### What Needs to be Added to Restaurant.java:

1. **Add Fields:**
   ```java
   private String name;
   private List<String> hours;  // Opening/closing times
   private Map<String, Menu> menus;
   ```

2. **Add Constructor:**
   ```java
   public Restaurant(String name) {
       this.name = name;
       this.hours = new ArrayList<>();
       this.menus = new HashMap<>();
   }
   ```

3. **Implement `getName()`:**
   ```java
   public String getName() {
       return name;
   }
   ```

4. **Implement `setHours()`:**
   ```java
   public void setHours(String ... hm) {
       hours.clear();
       for (String h : hm) {
           hours.add(h);
       }
   }
   ```

5. **Implement `isOpenAt()`:**
   - Parse time to minutes (HH * 60 + MM)
   - Check if time falls in any [open, close) range
   - Handle pairs of opening/closing times

6. **Implement `addMenu()` and `getMenu()`:**
   - Store menus in map
   - Retrieve by name

#### What Needs to be Added to Takeaway.java:

1. **Add Field:**
   ```java
   private Map<String, Restaurant> restaurants = new TreeMap<>();
   ```

2. **Implement Methods:**
   ```java
   public Restaurant addRestaurant(String restaurantName) {
       Restaurant r = new Restaurant(restaurantName);
       restaurants.put(restaurantName, r);
       return r;
   }

   public Collection<String> restaurants() {
       return restaurants.keySet();
   }
   ```

### Key Points
- Time format: "HH:MM"
- Hours array: [open1, close1, open2, close2, ...]
- Must handle multiple time ranges
- A restaurant is open if time is in range [open, close)

---

## R6 - Customers

### Task Description
Implement customer registration and management with personal information.

### What to Implement
- **Register customers** with first name, last name, email, phone
- **Retrieve all customers** sorted by last name, then first name
- **Get/set customer information** (getters for all, setters for email/phone only)
- **String representation**: "FirstName LastName"

### Files to Modify
1. **Takeaway.java**
   - Implement `registerCustomer()` and `customers()`

2. **Customer.java**
   - Implement all fields and methods

### Implementation Status
⚠️ **NOT YET IMPLEMENTED** - Files show only skeleton code with `return null` statements

### Required Implementation

#### What Needs to be Added to Customer.java:

1. **Add Fields:**
   ```java
   private String firstName;
   private String lastName;
   private String email;
   private String phone;
   ```

2. **Add Constructor:**
   ```java
   public Customer(String firstName, String lastName, String email, String phone) {
       this.firstName = firstName;
       this.lastName = lastName;
       this.email = email;
       this.phone = phone;
   }
   ```

3. **Implement Getters:**
   ```java
   public String getFirstName() { return firstName; }
   public String getLastName() { return lastName; }
   public String getEmail() { return email; }
   public String getPhone() { return phone; }
   ```

4. **Implement Setters:**
   ```java
   public void SetEmail(String email) { this.email = email; }  // Note: Capital S
   public void setPhone(String phone) { this.phone = phone; }
   ```

5. **Override `toString()`:**
   ```java
   @Override
   public String toString() {
       return firstName + " " + lastName;
   }
   ```

#### What Needs to be Added to Takeaway.java:

1. **Add Field:**
   ```java
   private List<Customer> customers = new ArrayList<>();
   ```

2. **Implement `registerCustomer()`:**
   ```java
   public Customer registerCustomer(String firstName, String lastName, String email, String phoneNumber) {
       Customer c = new Customer(firstName, lastName, email, phoneNumber);
       customers.add(c);
       return c;
   }
   ```

3. **Implement `customers()`:**
   ```java
   public Collection<Customer> customers() {
       List<Customer> sorted = new ArrayList<>(customers);
       sorted.sort((c1, c2) -> {
           int lastNameComp = c1.getLastName().compareTo(c2.getLastName());
           if (lastNameComp != 0) return lastNameComp;
           return c1.getFirstName().compareTo(c2.getFirstName());
       });
       return sorted;
   }
   ```

### Key Points
- Customers sorted by last name, then first name
- Only email and phone can be modified after creation
- Note the capital 'S' in `SetEmail()` - this appears to be intentional in the skeleton

---

## R7 - Orders

### Task Description
Implement order management with menus, quantities, status, and payment methods.

### What to Implement
- **Create orders** for customers at restaurants with delivery time
- **Adjust delivery time** if outside restaurant hours
- **Add menus** to orders with quantities
- **Manage order status**: ORDERED, READY, DELIVERED
- **Manage payment method**: PAID, CASH, CARD
- **Format orders** as strings

### Files to Modify
1. **Takeaway.java**
   - Implement `createOrder()`

2. **Order.java**
   - Implement all methods and `toString()`

### Implementation Status
⚠️ **NOT YET IMPLEMENTED** - Files show only skeleton code with `return null` statements

### Required Implementation

#### What Needs to be Added to Order.java:

1. **Add Fields:**
   ```java
   private Customer customer;
   private Restaurant restaurant;
   private String deliveryTime;
   private OrderStatus status = OrderStatus.ORDERED;
   private PaymentMethod paymentMethod = PaymentMethod.CASH;
   private Map<String, Integer> menus = new TreeMap<>();  // TreeMap for sorting
   ```

2. **Add Constructor:**
   ```java
   public Order(Customer customer, Restaurant restaurant, String deliveryTime) {
       this.customer = customer;
       this.restaurant = restaurant;
       this.deliveryTime = deliveryTime;
   }
   ```

3. **Implement Status Methods:**
   ```java
   public void setStatus(OrderStatus os) { this.status = os; }
   public OrderStatus getStatus() { return status; }
   ```

4. **Implement Payment Methods:**
   ```java
   public void setPaymentMethod(PaymentMethod pm) { this.paymentMethod = pm; }
   public PaymentMethod getPaymentMethod() { return paymentMethod; }
   ```

5. **Implement `addMenus()`:**
   ```java
   public Order addMenus(String menu, int quantity) {
       menus.put(menu, quantity);  // Overwrites if exists
       return this;
   }
   ```

6. **Override `toString()`:**
   ```java
   @Override
   public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append(restaurant.getName()).append(", ");
       sb.append(customer.getFirstName()).append(" ").append(customer.getLastName());
       sb.append(" : (").append(deliveryTime).append("):\n");
       
       for (Map.Entry<String, Integer> entry : menus.entrySet()) {
           sb.append("\t").append(entry.getKey()).append("->").append(entry.getValue()).append("\n");
       }
       
       return sb.toString();
   }
   ```

#### What Needs to be Added to Takeaway.java:

1. **Implement `createOrder()`:**
   ```java
   public Order createOrder(Customer customer, String restaurantName, String time) {
       Restaurant restaurant = restaurants.get(restaurantName);
       String deliveryTime = time;
       
       // Adjust delivery time if outside hours
       if (!restaurant.isOpenAt(time)) {
           deliveryTime = findNextOpenTime(restaurant, time);
       }
       
       Order order = new Order(customer, restaurant, deliveryTime);
       // Store order somewhere if needed for R8
       return order;
   }
   ```

2. **Helper Method for Time Adjustment:**
   - Parse time to minutes
   - Find next opening time from restaurant hours
   - Handle wrap-around if needed

### Key Points
- Default status: ORDERED
- Default payment: CASH
- If delivery time is outside hours, set to next opening time
- `addMenus()` overwrites previous quantity
- TreeMap ensures menus are sorted alphabetically in output

### Order Format Example
```
Napoli, Judi Dench : (19:00):
	M6->1
```

---

## R8 - Information

### Task Description
Implement query methods to retrieve restaurants and orders based on specific criteria.

### What to Implement
- **Find open restaurants** at a given time
- **Retrieve orders by status** for a restaurant with formatted output

### Files to Modify
1. **Takeaway.java**
   - Implement `openRestaurants(String time)`

2. **Restaurant.java**
   - Implement `ordersWithStatus(OrderStatus status)`

### Implementation Status
⚠️ **NOT YET IMPLEMENTED** - Files show only skeleton code with `return null` statements

### Required Implementation

#### What Needs to be Added to Takeaway.java:

1. **Implement `openRestaurants()`:**
   ```java
   public Collection<Restaurant> openRestaurants(String time) {
       List<Restaurant> open = new ArrayList<>();
       for (Restaurant r : restaurants.values()) {
           if (r.isOpenAt(time)) {
               open.add(r);
           }
       }
       // Already sorted by TreeMap
       return open;
   }
   ```

#### What Needs to be Added to Restaurant.java:

1. **Add Field to Store Orders:**
   ```java
   private List<Order> orders = new ArrayList<>();
   ```

2. **Add Method to Add Order:**
   ```java
   public void addOrder(Order order) {
       orders.add(order);
   }
   ```

3. **Implement `ordersWithStatus()`:**
   ```java
   public String ordersWithStatus(OrderStatus status) {
       List<Order> matching = new ArrayList<>();
       for (Order o : orders) {
           if (o.getStatus() == status) {
               matching.add(o);
           }
       }
       
       // Sort by restaurant name, customer name, delivery time
       matching.sort((o1, o2) -> {
           // Compare restaurant names
           int restaurantComp = o1.getRestaurant().getName().compareTo(o2.getRestaurant().getName());
           if (restaurantComp != 0) return restaurantComp;
           
           // Compare customer last names
           int lastNameComp = o1.getCustomer().getLastName().compareTo(o2.getCustomer().getLastName());
           if (lastNameComp != 0) return lastNameComp;
           
           // Compare customer first names
           int firstNameComp = o1.getCustomer().getFirstName().compareTo(o2.getCustomer().getFirstName());
           if (firstNameComp != 0) return firstNameComp;
           
           // Compare delivery times
           return o1.getDeliveryTime().compareTo(o2.getDeliveryTime());
       });
       
       StringBuilder sb = new StringBuilder();
       for (Order o : matching) {
           sb.append(o.toString());
       }
       return sb.toString();
   }
   ```

### Key Points
- Restaurant is open if time is in range [open, close)
- Results sorted alphabetically by restaurant name
- Orders sorted by: restaurant name → customer last name → customer first name → delivery time
- Need to add getter methods to Order for customer, restaurant, and delivery time

---

## Development Guidelines

### GitFlow Process
1. Create feature branch from main: `feature/R1-raw-materials`
2. Implement requirement completely
3. Test thoroughly
4. Create merge request
5. Review using checklist
6. Ensure pipeline passes
7. Resolve all threads
8. Merge to main
9. Move to next requirement

### Implementation Order
- **Must be sequential**: R1 → R2 → R3 → R4 → R5 → R6 → R7 → R8
- Only create next MR after previous is merged
- Each requirement builds on previous ones

### Current Status
- ✅ **R1 - Raw Materials**: COMPLETED
- ✅ **R2 - Products**: COMPLETED
- ✅ **R3 - Recipes**: COMPLETED
- ⚠️ **R4 - Menu**: NOT IMPLEMENTED
- ⚠️ **R5 - Restaurant**: NOT IMPLEMENTED
- ⚠️ **R6 - Customers**: NOT IMPLEMENTED
- ⚠️ **R7 - Orders**: NOT IMPLEMENTED
- ⚠️ **R8 - Information**: NOT IMPLEMENTED

### Important Notes
- All classes in `diet` package
- Main branch is protected (no direct commits)
- Pipeline must pass before merging
- All review threads must be resolved
- Self-review using checklist

---

## Summary of Completed vs Pending Changes

| Requirement | Files Modified | Status |
|-------------|----------------|--------|
| R1 | Food.java (RawMaterial inner class) | ✅ COMPLETED |
| R2 | Food.java (Product inner class) | ✅ COMPLETED |
| R3 | Food.java, Recipe.java | ✅ COMPLETED |
| R4 | Food.java, Menu.java | ⚠️ PENDING |
| R5 | Takeaway.java, Restaurant.java | ⚠️ PENDING |
| R6 | Takeaway.java, Customer.java | ⚠️ PENDING |
| R7 | Takeaway.java, Order.java | ⚠️ PENDING |
| R8 | Takeaway.java, Restaurant.java | ⚠️ PENDING |

---

## Key Implementation Patterns

### Pattern 1: Inner Classes (R1, R2)
- Keep implementation encapsulated within Food class
- Implement NutritionalElement interface
- Use private fields with public getters

### Pattern 2: TreeMap for Sorting (R1, R2, R3)
- Automatic alphabetical sorting
- Key = name (unique identifier)
- Value = object instance

### Pattern 3: Method Chaining (R3, R4, R7)
- Return `this` from modifier methods
- Allows fluid interface: `recipe.add(...).add(...)`

### Pattern 4: Nutritional Calculations
- **Raw Materials**: Direct values (per 100g)
- **Products**: Direct values (per unit)
- **Recipes**: `(accumulated / weight) * 100` (per 100g)
- **Menus**: Sum of `(recipe_per100g * quantity/100) + product_values` (total)

### Pattern 5: Time Handling
- Parse "HH:MM" to minutes: `HH * 60 + MM`
- Compare as integers
- Check if time in [open, close) range