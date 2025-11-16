# Java Anonymous Classes - Complete Guide

## Table of Contents
1. [Introduction to Anonymous Classes](#introduction)
2. [Anonymous Classes Extending a Class](#extending-a-class)
3. [Anonymous Classes Implementing an Interface](#implementing-an-interface)
4. [Advanced Examples](#advanced-examples)
5. [Best Practices and Limitations](#best-practices)

---

## Introduction to Anonymous Classes

An **anonymous class** in Java is a local class without a name that is declared and instantiated in a single expression. Anonymous classes enable you to make your code more concise by declaring and instantiating a class at the same time, without the need to create a separate named class.

### Key Characteristics:
- **No name**: The class has no explicit name
- **Single use**: Typically used only once in the code
- **Inline declaration**: Declared at the point of instantiation
- **Can extend a class OR implement an interface**: Not both simultaneously
- **Can access final or effectively final local variables** from the enclosing scope
- **Cannot have constructors**: Since they have no name, they cannot declare constructors
- **Can have instance initializers**: Used instead of constructors

### Syntax:
```java
new SuperClassOrInterface() {
    // Class body: fields, methods, initializers
}
```

### When to Use Anonymous Classes:
- When you need a one-time implementation of a class or interface
- For event listeners and callbacks
- For simple implementations that don't require reuse
- Before Java 8, for functional interfaces (now replaced by lambda expressions)

---

## 1. Anonymous Classes Extending a Class

When an anonymous class extends a parent class, it inherits all the properties and methods of that class and can override them as needed.

### Basic Example

```java
public class AnonymousExtendingClass {
    public static void main(String[] args) {
        // Parent class
        class Animal {
            void makeSound() {
                System.out.println("Some generic animal sound");
            }
            
            void sleep() {
                System.out.println("Animal is sleeping");
            }
        }
        
        // Anonymous class extending Animal
        Animal dog = new Animal() {
            @Override
            void makeSound() {
                System.out.println("Woof! Woof!");
            }
            
            // Additional method specific to this anonymous class
            void wagTail() {
                System.out.println("Dog is wagging tail");
            }
        };
        
        dog.makeSound();  // Output: Woof! Woof!
        dog.sleep();      // Output: Animal is sleeping
        // dog.wagTail(); // Compilation error: wagTail() is not visible outside
    }
}
```

### Example: Extending Abstract Class

```java
abstract class Vehicle {
    protected String brand;
    protected int speed;
    
    public Vehicle(String brand) {
        this.brand = brand;
        this.speed = 0;
    }
    
    abstract void accelerate();
    
    void displayInfo() {
        System.out.println("Brand: " + brand + ", Speed: " + speed + " km/h");
    }
}

public class AbstractClassExample {
    public static void main(String[] args) {
        // Anonymous class extending abstract class Vehicle
        Vehicle car = new Vehicle("Toyota") {
            @Override
            void accelerate() {
                speed += 20;
                System.out.println("Car accelerating...");
            }
            
            // Additional method
            void honk() {
                System.out.println("Beep! Beep!");
            }
        };
        
        car.displayInfo();    // Output: Brand: Toyota, Speed: 0 km/h
        car.accelerate();     // Output: Car accelerating...
        car.displayInfo();    // Output: Brand: Toyota, Speed: 20 km/h
        
        // Creating another anonymous class with different implementation
        Vehicle bike = new Vehicle("Harley") {
            @Override
            void accelerate() {
                speed += 15;
                System.out.println("Bike speeding up!");
            }
        };
        
        bike.accelerate();    // Output: Bike speeding up!
        bike.displayInfo();   // Output: Brand: Harley, Speed: 15 km/h
    }
}
```

### Example: Accessing Enclosing Scope Variables

```java
public class ScopeAccessExample {
    private String instanceVar = "Instance Variable";
    
    public void demonstrateScope() {
        final String finalVar = "Final Local Variable";
        String effectivelyFinalVar = "Effectively Final Variable";
        
        // Anonymous class extending Object
        Object obj = new Object() {
            private String innerVar = "Inner Variable";
            
            @Override
            public String toString() {
                return "Anonymous class can access:\n" +
                       "1. " + instanceVar + "\n" +
                       "2. " + finalVar + "\n" +
                       "3. " + effectivelyFinalVar + "\n" +
                       "4. " + innerVar;
            }
        };
        
        System.out.println(obj.toString());
    }
    
    public static void main(String[] args) {
        ScopeAccessExample example = new ScopeAccessExample();
        example.demonstrateScope();
    }
}
```

### Example: Using Instance Initializer

```java
class Calculator {
    protected int result;
    
    void calculate() {
        System.out.println("Performing generic calculation");
    }
}

public class InstanceInitializerExample {
    public static void main(String[] args) {
        Calculator advancedCalc = new Calculator() {
            // Instance initializer block (runs when object is created)
            {
                result = 100;
                System.out.println("Initializing calculator with result: " + result);
            }
            
            @Override
            void calculate() {
                result = result * 2 + 50;
                System.out.println("Advanced calculation result: " + result);
            }
        };
        
        // Output: Initializing calculator with result: 100
        advancedCalc.calculate();  // Output: Advanced calculation result: 250
    }
}
```

### Example: Real-World Scenario - Thread Creation

```java
public class ThreadExample {
    public static void main(String[] args) {
        // Traditional way with anonymous class extending Thread
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                for (int i = 1; i <= 5; i++) {
                    System.out.println("Thread 1 - Count: " + i);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        
        thread1.start();
        
        // Another thread with different implementation
        Thread thread2 = new Thread() {
            private int sum = 0;
            
            @Override
            public void run() {
                for (int i = 1; i <= 10; i++) {
                    sum += i;
                }
                System.out.println("Thread 2 - Sum of 1 to 10: " + sum);
            }
        };
        
        thread2.start();
    }
}
```

---

## 2. Anonymous Classes Implementing an Interface

Anonymous classes can implement interfaces, providing concrete implementations for all abstract methods declared in the interface.

### Basic Example

```java
interface Greeting {
    void greet(String name);
}

public class InterfaceImplementationExample {
    public static void main(String[] args) {
        // Anonymous class implementing Greeting interface
        Greeting greeting = new Greeting() {
            @Override
            public void greet(String name) {
                System.out.println("Hello, " + name + "! Welcome!");
            }
        };
        
        greeting.greet("Alice");  // Output: Hello, Alice! Welcome!
        
        // Another implementation
        Greeting formalGreeting = new Greeting() {
            @Override
            public void greet(String name) {
                System.out.println("Good day, Mr./Ms. " + name);
            }
        };
        
        formalGreeting.greet("Smith");  // Output: Good day, Mr./Ms. Smith
    }
}
```

### Example: Implementing Interface with Multiple Methods

```java
interface MathOperation {
    int operate(int a, int b);
    String getOperationName();
}

public class MultiMethodInterfaceExample {
    public static void main(String[] args) {
        // Anonymous class for addition
        MathOperation addition = new MathOperation() {
            @Override
            public int operate(int a, int b) {
                return a + b;
            }
            
            @Override
            public String getOperationName() {
                return "Addition";
            }
        };
        
        System.out.println(addition.getOperationName() + ": " + 
                          addition.operate(10, 5));  // Output: Addition: 15
        
        // Anonymous class for multiplication
        MathOperation multiplication = new MathOperation() {
            @Override
            public int operate(int a, int b) {
                return a * b;
            }
            
            @Override
            public String getOperationName() {
                return "Multiplication";
            }
        };
        
        System.out.println(multiplication.getOperationName() + ": " + 
                          multiplication.operate(10, 5));  // Output: Multiplication: 50
    }
}
```

### Example: Event Listener Pattern

```java
interface ClickListener {
    void onClick();
}

class Button {
    private String label;
    private ClickListener listener;
    
    public Button(String label) {
        this.label = label;
    }
    
    public void setOnClickListener(ClickListener listener) {
        this.listener = listener;
    }
    
    public void click() {
        System.out.println("Button '" + label + "' clicked!");
        if (listener != null) {
            listener.onClick();
        }
    }
}

public class EventListenerExample {
    public static void main(String[] args) {
        Button submitButton = new Button("Submit");
        Button cancelButton = new Button("Cancel");
        
        // Anonymous class implementing ClickListener for submit button
        submitButton.setOnClickListener(new ClickListener() {
            @Override
            public void onClick() {
                System.out.println("Processing form submission...");
                System.out.println("Data saved successfully!");
            }
        });
        
        // Anonymous class implementing ClickListener for cancel button
        cancelButton.setOnClickListener(new ClickListener() {
            @Override
            public void onClick() {
                System.out.println("Operation cancelled by user.");
            }
        });
        
        submitButton.click();
        System.out.println();
        cancelButton.click();
    }
}
```

### Example: Comparator Interface

```java
import java.util.*;

class Student {
    String name;
    int age;
    double gpa;
    
    public Student(String name, int age, double gpa) {
        this.name = name;
        this.age = age;
        this.gpa = gpa;
    }
    
    @Override
    public String toString() {
        return String.format("%s (Age: %d, GPA: %.2f)", name, age, gpa);
    }
}

public class ComparatorExample {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Alice", 20, 3.8));
        students.add(new Student("Bob", 19, 3.5));
        students.add(new Student("Charlie", 21, 3.9));
        students.add(new Student("David", 20, 3.7));
        
        System.out.println("Original list:");
        students.forEach(System.out::println);
        
        // Sort by name using anonymous class
        Collections.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return s1.name.compareTo(s2.name);
            }
        });
        
        System.out.println("\nSorted by name:");
        students.forEach(System.out::println);
        
        // Sort by GPA (descending) using anonymous class
        Collections.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return Double.compare(s2.gpa, s1.gpa);
            }
        });
        
        System.out.println("\nSorted by GPA (descending):");
        students.forEach(System.out::println);
        
        // Sort by age, then by name using anonymous class
        Collections.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                int ageComparison = Integer.compare(s1.age, s2.age);
                if (ageComparison != 0) {
                    return ageComparison;
                }
                return s1.name.compareTo(s2.name);
            }
        });
        
        System.out.println("\nSorted by age, then by name:");
        students.forEach(System.out::println);
    }
}
```

### Example: Runnable Interface

```java
public class RunnableExample {
    public static void main(String[] args) {
        // Anonymous class implementing Runnable
        Runnable task1 = new Runnable() {
            @Override
            public void run() {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + " started");
                
                for (int i = 1; i <= 5; i++) {
                    System.out.println(threadName + " - Processing step " + i);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                
                System.out.println(threadName + " completed");
            }
        };
        
        // Another task with different implementation
        Runnable task2 = new Runnable() {
            @Override
            public void run() {
                String threadName = Thread.currentThread().getName();
                int factorial = 1;
                for (int i = 1; i <= 5; i++) {
                    factorial *= i;
                }
                System.out.println(threadName + " calculated factorial: " + factorial);
            }
        };
        
        Thread thread1 = new Thread(task1, "Worker-1");
        Thread thread2 = new Thread(task2, "Calculator");
        
        thread1.start();
        thread2.start();
    }
}
```

### Example: Custom Interface with State

```java
interface DataProcessor {
    void process(String data);
    int getProcessedCount();
}

public class StatefulInterfaceExample {
    public static void main(String[] args) {
        // Anonymous class with internal state
        DataProcessor processor = new DataProcessor() {
            private int count = 0;
            private StringBuilder log = new StringBuilder();
            
            @Override
            public void process(String data) {
                count++;
                String processed = data.toUpperCase();
                log.append(count).append(". ").append(processed).append("\n");
                System.out.println("Processed: " + processed);
            }
            
            @Override
            public int getProcessedCount() {
                return count;
            }
            
            // This method won't be accessible from outside
            private void printLog() {
                System.out.println("Processing Log:\n" + log.toString());
            }
        };
        
        processor.process("hello");
        processor.process("world");
        processor.process("java");
        
        System.out.println("\nTotal processed: " + processor.getProcessedCount());
    }
}
```

---

## Advanced Examples

### Example: Nested Anonymous Classes

```java
interface Outer {
    void outerMethod();
}

public class NestedAnonymousExample {
    public static void main(String[] args) {
        Outer outer = new Outer() {
            private String outerData = "Outer Level";
            
            @Override
            public void outerMethod() {
                System.out.println("Outer method called: " + outerData);
                
                // Nested anonymous class
                Runnable inner = new Runnable() {
                    private String innerData = "Inner Level";
                    
                    @Override
                    public void run() {
                        System.out.println("Inner method called: " + innerData);
                        System.out.println("Can access outer: " + outerData);
                    }
                };
                
                inner.run();
            }
        };
        
        outer.outerMethod();
    }
}
```

### Example: Passing Anonymous Classes as Arguments

```java
interface Validator {
    boolean validate(String input);
}

class Form {
    public void submitForm(String input, Validator validator) {
        System.out.println("Validating input: " + input);
        
        if (validator.validate(input)) {
            System.out.println("✓ Validation passed. Form submitted successfully!");
        } else {
            System.out.println("✗ Validation failed. Please check your input.");
        }
        System.out.println();
    }
}

public class AnonymousAsArgumentExample {
    public static void main(String[] args) {
        Form form = new Form();
        
        // Email validator
        form.submitForm("user@example.com", new Validator() {
            @Override
            public boolean validate(String input) {
                return input != null && input.contains("@") && input.contains(".");
            }
        });
        
        // Password validator
        form.submitForm("pass123", new Validator() {
            @Override
            public boolean validate(String input) {
                return input != null && input.length() >= 8;
            }
        });
        
        // Phone number validator
        form.submitForm("123-456-7890", new Validator() {
            @Override
            public boolean validate(String input) {
                return input != null && input.matches("\\d{3}-\\d{3}-\\d{4}");
            }
        });
    }
}
```

### Example: Factory Pattern with Anonymous Classes

```java
interface Animal {
    void makeSound();
    String getType();
}

class AnimalFactory {
    public static Animal createAnimal(String type) {
        switch (type.toLowerCase()) {
            case "dog":
                return new Animal() {
                    @Override
                    public void makeSound() {
                        System.out.println("Woof!");
                    }
                    
                    @Override
                    public String getType() {
                        return "Dog";
                    }
                };
                
            case "cat":
                return new Animal() {
                    @Override
                    public void makeSound() {
                        System.out.println("Meow!");
                    }
                    
                    @Override
                    public String getType() {
                        return "Cat";
                    }
                };
                
            case "bird":
                return new Animal() {
                    @Override
                    public void makeSound() {
                        System.out.println("Chirp!");
                    }
                    
                    @Override
                    public String getType() {
                        return "Bird";
                    }
                };
                
            default:
                return new Animal() {
                    @Override
                    public void makeSound() {
                        System.out.println("Unknown sound");
                    }
                    
                    @Override
                    public String getType() {
                        return "Unknown";
                    }
                };
        }
    }
}

public class FactoryPatternExample {
    public static void main(String[] args) {
        Animal[] animals = {
            AnimalFactory.createAnimal("dog"),
            AnimalFactory.createAnimal("cat"),
            AnimalFactory.createAnimal("bird")
        };
        
        for (Animal animal : animals) {
            System.out.print(animal.getType() + " says: ");
            animal.makeSound();
        }
    }
}
```

---

## Best Practices and Limitations

### Best Practices

1. **Keep It Simple**: Anonymous classes should be short and focused. If the implementation is complex, consider using a named class instead.

2. **Use for Single-Use Implementations**: Perfect for one-off implementations that won't be reused.

3. **Consider Lambda Expressions**: For functional interfaces (interfaces with a single abstract method), lambda expressions are more concise than anonymous classes (Java 8+).

```java
// Anonymous class
Runnable r1 = new Runnable() {
    @Override
    public void run() {
        System.out.println("Running");
    }
};

// Lambda expression (preferred for functional interfaces)
Runnable r2 = () -> System.out.println("Running");
```

4. **Access Local Variables**: Only final or effectively final variables can be accessed from anonymous classes.

5. **Use Instance Initializers**: Since anonymous classes can't have constructors, use instance initializer blocks for initialization logic.

### Limitations

1. **Cannot declare constructors**: Anonymous classes have no name, so they cannot declare constructors.

2. **Cannot extend a class AND implement interfaces**: You can do one or the other, not both.

3. **Cannot be static**: Anonymous classes are always inner classes associated with an instance.

4. **Limited reusability**: By definition, anonymous classes are meant for single use.

5. **Debugging difficulty**: Stack traces show anonymous classes as `OuterClass$1`, `OuterClass$2`, etc., making debugging harder.

6. **Cannot declare static members** (except static final constants).

7. **Accessing members**: Additional methods defined in anonymous classes cannot be accessed through the reference variable of the supertype.

```java
interface Test {
    void method1();
}

public class LimitationExample {
    public static void main(String[] args) {
        Test test = new Test() {
            @Override
            public void method1() {
                System.out.println("Method 1");
            }
            
            public void method2() {
                System.out.println("Method 2");
            }
        };
        
        test.method1();  // Works
        // test.method2();  // Compilation error: method2() not defined in Test
    }
}
```

### When NOT to Use Anonymous Classes

- When the class implementation is long (more than 20-30 lines)
- When you need to reuse the implementation in multiple places
- When you need constructors with parameters
- When the logic is complex and needs to be tested separately
- When working with functional interfaces in Java 8+ (use lambdas instead)

---

## Summary

Anonymous classes are a powerful feature in Java that allow for concise, inline implementations of classes and interfaces. They are particularly useful for:

- **Event handling and callbacks**
- **One-time implementations of interfaces**
- **Overriding specific methods on the fly**
- **Creating simple worker threads**

While lambda expressions have replaced many use cases for anonymous classes in modern Java (especially for functional interfaces), anonymous classes remain important for:

- Implementing interfaces with multiple methods
- Extending classes
- Scenarios requiring state or multiple methods

Understanding anonymous classes is essential for working with legacy code and for situations where lambda expressions are not sufficient.