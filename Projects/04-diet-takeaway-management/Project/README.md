# Diet and Takeaway Management System

Maven-based Java OOP project for managing diet data and takeaway orders.

The project models raw materials, products, recipes, menus, restaurants, opening hours, customers, and orders. It keeps the original lab API while completing the missing behavior and adding validation, tests, scripts, documentation, and CI.

## Features

- Define raw materials with nutritional values per 100 grams.
- Define packaged products with nutritional values per unit.
- Create recipes from raw materials and ingredient quantities.
- Normalize recipe nutrition per 100 grams.
- Create menus from recipe portions and packaged products.
- Calculate total menu calories, proteins, carbohydrates, and fat.
- Register restaurants and opening-hour intervals.
- Check whether a restaurant is open at a time.
- Register customers.
- Create orders for restaurants.
- Adjust delivery time to the next opening time when needed.
- Add menu quantities to orders.
- Track payment method and order status.
- Query open restaurants.
- Query restaurant orders by status.
- Produce deterministic sorted collections and formatted outputs.

## Tech stack

- Java 21
- Maven
- JUnit 4 / JUnit 5 through the existing test setup
- JaCoCo for optional local coverage reports
- GitHub Actions CI

## Requirements

- Java 21
- Maven wrapper included

Check versions:

```bash
java -version
./mvnw -version
```

Windows:

```powershell
java -version
.\mvnw.cmd -version
```

## Build and test

Linux/macOS/Git Bash:

```bash
./mvnw clean test
```

Windows PowerShell:

```powershell
.\mvnw.cmd clean test
```

Fallback:

```bash
mvn clean test
```

Convenience scripts:

```bash
./scripts/test.sh
```

```powershell
.\scripts\test.ps1
```

## Optional coverage report

```bash
./mvnw clean test jacoco:report
```

Windows:

```powershell
.\mvnw.cmd clean test jacoco:report
```

Open the generated local report:

```text
target/site/jacoco/index.html
```

Do not commit the generated `target/` folder.

## Project structure

```text
.
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn/wrapper/
├── src/diet/
│   ├── Food.java
│   ├── NutritionalElement.java
│   ├── Recipe.java
│   ├── Menu.java
│   ├── Takeaway.java
│   ├── Restaurant.java
│   ├── Customer.java
│   ├── Order.java
│   └── ValidationUtils.java
├── test/
│   ├── example/
│   ├── custom/
│   └── it/polito/oop/test/
├── docs/
├── scripts/
├── TEST_RESULTS.md
└── .github/workflows/java-ci.yml
```

The Maven build intentionally preserves the original `src` and `test` directories through `pom.xml`.

## Nutritional calculation

Raw materials are measured per 100 grams:

```text
raw material value = value per 100g
```

Products are measured per unit:

```text
product value = value for one product/package
```

Recipes are normalized per 100 grams:

```text
ingredient contribution = raw material value * ingredient grams / 100
recipe per 100g = total contribution / total recipe weight * 100
```

Menus are totals for the whole menu:

```text
recipe contribution = recipe value per 100g * portion grams / 100
product contribution = product value per unit
menu total = all recipe contributions + all product contributions
```

## Restaurant and order workflow

1. Create restaurants with `Takeaway.addRestaurant()`.
2. Configure opening hours with `Restaurant.setHours()`.
3. Add menus to restaurants with `Restaurant.addMenu()`.
4. Register customers with `Takeaway.registerCustomer()`.
5. Create orders with `Takeaway.createOrder()`.
6. Add menu quantities with `Order.addMenus()`.
7. Query orders by status with `Restaurant.ordersWithStatus()`.

Opening intervals use `[start, end)`: start included, end excluded. Intervals crossing midnight are supported.

If a requested delivery time is outside opening hours, the order is moved to the next opening time.

## Documentation

- [Architecture](docs/ARCHITECTURE.md)
- [Testing](docs/TESTING.md)
- [Design decisions](docs/DESIGN_DECISIONS.md)
- [Final review](docs/FINAL_REVIEW.md)
- [Test results](TEST_RESULTS.md)

## Known limitations

- Educational local Java project.
- In-memory model only.
- No database.
- No REST API.
- No authentication.
- No frontend.
- No deployment setup.
- No payment integration.

## Resume value

Built and validated a Java diet and takeaway management system with raw materials, products, recipes, menus, restaurant opening-hour logic, customer registration, order workflows, nutritional calculations, automated tests, and clean Maven documentation.
