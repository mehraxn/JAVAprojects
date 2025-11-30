# Diet Management System - Requirements Guide

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

### Files to Modify
1. **Food.java**
   - Implement `defineRawMaterial(String name, double calories, double proteins, double carbs, double fat)`
   - Implement `rawMaterials()` - returns collection sorted by name
   - Implement `getRawMaterial(String name)` - returns specific raw material

2. **Create new class: RawMaterial.java**
   - Implement `NutritionalElement` interface
   - Store name and nutritional values (calories, proteins, carbs, fat)
   - Implement all interface methods
   - `per100g()` must return `true`

### Key Points
- All nutritional values are per 100 grams
- Raw material names are unique
- Collections must be sorted alphabetically

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

### Files to Modify
1. **Food.java**
   - Implement `defineProduct(String name, double calories, double proteins, double carbs, double fat)`
   - Implement `products()` - returns collection sorted by name
   - Implement `getProduct(String name)` - returns specific product

2. **Create new class: Product.java**
   - Implement `NutritionalElement` interface
   - Store name and nutritional values for entire product
   - Implement all interface methods
   - `per100g()` must return `false`

### Key Points
- Nutritional values are for the entire product (not per 100g)
- Product names are unique
- Collections must be sorted alphabetically

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

### Files to Modify
1. **Food.java**
   - Implement `createRecipe(String name)` - creates and returns new Recipe
   - Implement `recipes()` - returns collection sorted by name
   - Implement `getRecipe(String name)` - returns specific recipe

2. **Recipe.java**
   - Implement `addIngredient(String material, double quantity)` - adds raw material with quantity
   - Implement all `NutritionalElement` interface methods
   - Calculate nutritional values per 100g based on ingredients
   - `per100g()` must return `true`
   - Support method chaining for `addIngredient()`

### Key Points
- Recipe nutritional values are ALWAYS per 100g
- Must scale ingredient nutritional values proportionally
- If recipe has 200g of ingredients, scale values to represent 100g
- Recipe names are unique

### Calculation Example
If a recipe has 200g total ingredients:
- Sum all ingredient nutritional values
- Divide by total weight (200g)
- Multiply by 100 to get per-100g values

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
   - Implement `addRecipe(String recipe, double quantity)` - adds recipe portion
   - Implement `addProduct(String product)` - adds one product unit
   - Implement all `NutritionalElement` interface methods
   - Calculate total nutritional values for entire menu
   - `per100g()` must return `false`
   - Support method chaining

### Key Points
- Menu values are for the ENTIRE menu (not per 100g)
- Recipes: scale per-100g values by quantity/100
- Products: add full product values
- Support method chaining

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
   - Implement `getName()` - returns restaurant name
   - Implement `setHours(String ... hm)` - sets opening/closing times
   - Implement `isOpenAt(String time)` - checks if open at given time
   - Implement `addMenu(Menu menu)` - adds menu to offerings
   - Implement `getMenu(String name)` - returns specific menu

### Key Points
- Time format: "HH:MM"
- Hours array: [open1, close1, open2, close2, ...]
- Must handle multiple time ranges (e.g., lunch and dinner)
- Example: "08:15", "14:00", "19:00", "00:00"
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
   - Implement `registerCustomer(String firstName, String lastName, String email, String phoneNumber)`
   - Implement `customers()` - returns collection sorted by last name, first name

2. **Customer.java**
   - Implement constructor to initialize fields
   - Implement `getFirstName()`, `getLastName()`, `getEmail()`, `getPhone()`
   - Implement `SetEmail(String email)` and `setPhone(String phone)` (note: SetEmail has capital S)
   - Override `toString()` - returns "FirstName LastName"

### Key Points
- Customers sorted by last name, then first name
- Only email and phone can be modified after creation
- String format: "FirstName LastName"

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
   - Implement `createOrder(Customer customer, String restaurantName, String time)`
   - Adjust delivery time to next opening if outside hours

2. **Order.java**
   - Implement `setPaymentMethod(PaymentMethod pm)` and `getPaymentMethod()`
   - Default: `PaymentMethod.CASH`
   - Implement `setStatus(OrderStatus os)` and `getStatus()`
   - Default: `OrderStatus.ORDERED`
   - Implement `addMenus(String menu, int quantity)` - adds/updates menu quantity
   - Override `toString()` - format order as specified

### Key Points
- Default status: ORDERED
- Default payment: CASH
- If delivery time is outside hours, set to next opening time
- Example: order at 15:30 for restaurant open 19:00-00:00 → delivery set to 19:00
- `addMenus()` overwrites previous quantity if menu already added

### Order Format
```
RESTAURANT_NAME, USER_FIRST_NAME USER_LAST_NAME : (DELIVERY_HH:MM):
	MENU_NAME_1->MENU_QUANTITY_1
	MENU_NAME_2->MENU_QUANTITY_2
```
- Menus sorted alphabetically
- Each menu on new line with tab prefix
- Format: `\t` before each menu line

---

## R8 - Information

### Task Description
Implement query methods to retrieve restaurants and orders based on specific criteria.

### What to Implement
- **Find open restaurants** at a given time
- **Retrieve orders by status** for a restaurant with formatted output

### Files to Modify
1. **Takeaway.java**
   - Implement `openRestaurants(String time)` - returns restaurants open at given time
   - Sort results alphabetically by restaurant name

2. **Restaurant.java**
   - Implement `ordersWithStatus(OrderStatus status)` - returns formatted string of matching orders
   - Sort by: restaurant name, customer name (last, first), delivery time

### Key Points
- Restaurant is open if time is in range [open, close)
- Results sorted alphabetically by restaurant name
- Orders sorted by: restaurant name → customer last name → customer first name → delivery time

### Order Status Output Format
```
Napoli, Judi Dench : (19:00):
	M6->1
Napoli, Ralph Fiennes : (19:00):
	M1->2
	M6->1
```

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

### Important Notes
- All classes in `diet` package
- Main branch is protected (no direct commits)
- Pipeline must pass before merging
- All review threads must be resolved
- Self-review using checklist

---

## Summary of File Changes

| Requirement | New Classes | Modified Classes |
|-------------|-------------|------------------|
| R1 | RawMaterial.java | Food.java |
| R2 | Product.java | Food.java |
| R3 | - | Food.java, Recipe.java |
| R4 | - | Food.java, Menu.java |
| R5 | - | Takeaway.java, Restaurant.java |
| R6 | - | Takeaway.java, Customer.java |
| R7 | - | Takeaway.java, Order.java |
| R8 | - | Takeaway.java, Restaurant.java |

---

## Interface Implementation Guide

### NutritionalElement Interface
Must be implemented by: RawMaterial, Product, Recipe, Menu

**Methods to implement:**
- `String getName()` - return element name
- `double getCalories()` - return calories value
- `double getProteins()` - return proteins value
- `double getCarbs()` - return carbs value
- `double getFat()` - return fat value
- `boolean per100g()` - return true/false based on type

**per100g() Return Values:**
- `true`: RawMaterial, Recipe (values per 100g)
- `false`: Product, Menu (values for whole item)