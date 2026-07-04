# Simple Java Calculator

This repository contains a simple command-line calculator program written in Java. The program allows users to perform basic arithmetic operations: addition, subtraction, multiplication, and division. It features a menu-driven interface and includes basic error handling for invalid operations and division by zero.

## 📁 `Calculator.java` Code Breakdown

The core logic resides in the `Calculator` class. Here is a line-by-line explanation of the provided source code:

---

### **Package and Class Definition**

| Line(s) | Code | Explanation |
| :--- | :--- | :--- |
| `1` | `package calc;` | Declares the **package** name for this class as `calc`. |
| `3` | `public class Calculator {` | Defines the main public class **`Calculator`**. |

---

### **Constants (Operation Codes)**

| Line(s) | Code | Explanation |
| :--- | :--- | :--- |
| `5-6` | `// CONSTANTS` | A comment indicating the start of constant definitions. |
| `7-11` | `static final int SUM = 1;` <br> `static final int SUB = 2;` <br> `static final int MUL = 3;` <br> `static final int DIV = 4;` <br> `static final int EXIT = 5;` | Defines **static final integer constants** (similar to an enum) to represent the menu choices/operations. |

---

### **Global Variables for Error Handling**

| Line(s) | Code | Explanation |
| :--- | :--- | :--- |
| `13-14` | `// Global variables of the class` | A comment indicating the start of class-level (global) variables. |
| `15` | `static boolean error = false;` | A **static boolean flag** used to indicate if an error occurred during the last operation. It's initialized to `false`. |
| `16` | `static String errorMessage;` | A **static String variable** used to store the specific error message when an error occurs. |

---

### **Main Method (`main`)**

| Line(s) | Code | Explanation |
| :--- | :--- | :--- |
| `18` | `public static void main(String[] args) {` | The **entry point** of the program. |
| `19-23` | `int choice;` <br> `double num1;` <br> `double num2;` <br> `double result;` | Declares **local variables** to store the user's menu choice, the two operands, and the calculation result. |
| `25` | `while (true) {` | Starts an **infinite loop** for the calculator's main operation cycle. The loop will only exit when the user chooses the "Exit" option. |
| `27-31` | `// Display the menu` <br> `IO.println("\nSimple Calculator");` <br> `IO.println(SUM+". Add");` <br> `...` | Prints the **calculator's menu** options using the defined constants (`SUM`, `SUB`, etc.) and an assumed `IO` utility class for output. |
| `32` | `IO.print("Enter your choice (1-5): ");` | Prompts the user to enter their choice. |
| `33` | `String in = IO.readln("Enter your choice (1-5): ");` | Reads the user's menu choice as a `String` (using the assumed `IO` utility). *Note: The prompt is repeated inside `readln` here.* |
| `34` | `choice = Integer.parseInt(in);` | **Converts the input `String` into an `int`** and stores it in the `choice` variable. |
| `36-39` | `// Check for exit condition` <br> `if (choice == EXIT) {` <br> `break;` <br> `}` | Checks if the user chose the `EXIT` option (`5`). If so, the `break` statement exits the `while` loop. |
| `41-44` | `// Get user input for numbers` <br> `in = IO.readln("Enter first number: ");` <br> `num1 = Double.parseDouble(in);` <br> `...` | Prompts the user for the first number, reads the input as a `String`, and **converts it to a `double`** (`num1`). This is repeated for the second number (`num2`). |
| `46` | `// Compute result` | A comment preceding the calculation call. |
| `47` | `result = compute(choice, num1, num2);` | **Calls the static `compute` method** to perform the calculation, passing the operation choice and the two numbers. |
| `49` | `// Display the result` | A comment preceding the result output block. |
| `50-54` | `if( error ){` <br> `IO.println("Error: " + errorMessage);` <br> `}else{` <br> `IO.println("Result: " + String.format("%.2f", result));` <br> `}` | Checks the global `error` flag. If **true**, it prints the error message. If **false** (no error), it prints the calculated result formatted to two decimal places. |
| `55` | `}` | Ends the `while (true)` loop. |
| `57` | `IO.println("Thank you for using the calculator!");` | Prints a final message **after the loop exits** (i.e., when the user chooses "Exit"). |
| `58` | `}` | Ends the `main` method. |

---

### **The `compute` Method**

| Line(s) | Code | Explanation |
| :--- | :--- | :--- |
| `60-68` | `/** ... */` | The **Javadoc comment** describing the purpose, parameters, and return value of the `compute` method. |
| `69` | `public static double compute(int operation, double num1, double num2) {` | Defines the **static method** that takes the operation code and two operands, returning the calculation result as a `double`. |
| `70` | `double result = 0.0;` | Initializes a local variable `result`. |
| `71` | `error = false;` | **Resets the global error flag** at the start of every new computation. |
| `72` | `switch (operation) {` | Starts a **`switch` statement** to branch based on the `operation` code. |
| `73-84` | `case SUM: ... break;` <br> `case SUB: ... break;` <br> `...` | For each valid operation, the **corresponding helper method** (`add`, `subtract`, `multiply`, or `divide`) is called, and the result is stored. The `break` prevents fall-through. |
| `85-88` | `default:` <br> `setError("Invalid operation");` <br> `return Double.NaN;` <br> `}` | The `default` case handles **invalid operation codes**. It calls `setError` to set the flag and message, and returns `Double.NaN` (Not a Number) as a placeholder for an invalid result. |
| `89` | `return result;` | **Returns the calculated result** (or `Double.NaN` if an error occurred outside of `default` for some reason, though `divide` handles its own error). |
| `90` | `}` | Ends the `compute` method. |

---

### **Error Setter Method**

| Line(s) | Code | Explanation |
| :--- | :--- | :--- |
| `92` | `private static void setError(String msg){` | Defines a **private static utility method** to consistently set the global error state. |
| `93` | `errorMessage = msg;` | Sets the global `errorMessage` variable to the given `msg`. |
| `94` | `error = true;` | Sets the global `error` flag to **`true`**. |
| `95` | `}` | Ends the `setError` method. |

---

### **Arithmetic Helper Methods**

| Line(s) | Code | Explanation |
| :--- | :--- | :--- |
| `97-99` | `public static double add(double num1, double num2) { ... }` | Returns the **sum** of `num1` and `num2`. |
| `101-103` | `public static double subtract(double num1, double num2) { ... }` | Returns the **difference** of `num1` and `num2`. |
| `105-107` | `public static double multiply(double num1, double num2) { ... }` | Returns the **product** of `num1` and `num2`. |
| `109` | `public static double divide(double num1, double num2) {` | Defines the **division** method. |
| `110-111` | `if (num2 != 0.0) {` <br> `return num1 / num2;` | Checks if the divisor (`num2`) is **not zero**. If so, it returns the division result. |
| `112-114` | `} else {` <br> `setError("Division by zero!");` <br> `return Double.NaN;` <br> `}` | If the divisor is **zero**, it calls `setError` and returns `Double.NaN` to indicate an invalid result. |
| `115` | `}` | Ends the `divide` method. |
| `116` | `}` | Ends the `Calculator` class definition. |

---

## ⚠️ Assumed Dependency

Note that this code relies on an external, unspecified class named **`IO`** (e.g., `IO.println`, `IO.readln`). For the code to compile and run, this `IO` class must exist and provide methods for reading user input and printing output to the console. It often replaces the standard `System.out.println` and `Scanner` or `BufferedReader` setup for simplified input/output in educational contexts.