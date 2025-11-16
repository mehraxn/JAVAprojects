# Can Anonymous Classes Extend Classes?

This README explains whether anonymous classes in Java can extend abstract or concrete classes, with multiple examples.

## Key Answer

**Anonymous classes can extend both concrete and abstract classes.**

### 1. Extending a Concrete Class

Anonymous classes can extend concrete classes and optionally override their methods.

```java
class Concrete {
    void sayHi() {
        System.out.println("Hi from Concrete");
    }
}

public class TestConcrete {
    public static void main(String[] args) {
        Concrete obj1 = new Concrete() {
            void sayHi() {
                System.out.println("Hi from anonymous class overriding Concrete");
            }
        };
        obj1.sayHi();

        Concrete obj2 = new Concrete() { }; // no method override
        obj2.sayHi();
    }
}
```

### 2. Extending an Abstract Class

If the class is abstract, the anonymous class **must implement all abstract methods**.

```java
abstract class AbstractClass {
    abstract void doSomething();
}

public class TestAbstract {
    public static void main(String[] args) {
        AbstractClass obj = new AbstractClass() {
            void doSomething() {
                System.out.println("Doing something in anonymous class");
            }
        };
        obj.doSomething();
    }
}
```

### 3. Multiple Anonymous Classes for Same Class

```java
class Base {
    void greet() { System.out.println("Hello from Base"); }
}

public class TestMultiple {
    public static void main(String[] args) {
        Base obj1 = new Base() { void greet() { System.out.println("Hi from anonymous 1"); } };
        Base obj2 = new Base() { void greet() { System.out.println("Hi from anonymous 2"); } };

        obj1.greet();
        obj2.greet();
    }
}
```

### 4. Anonymous Class Extending Abstract Class and Adding New Methods

```java
abstract class Animal {
    abstract void sound();
}

public class TestAnimal {
    public static void main(String[] args) {
        Animal dog = new Animal() {
            void sound() {
                System.out.println("Woof Woof");
            }
            void play() {
                System.out.println("Dog is playing");
            }
        };
        dog.sound();
        // dog.play(); // cannot call directly, only accessible inside anonymous class scope
    }
}
```

### Summary Table

| Class type     | Anonymous class allowed? | Notes                               |
| -------------- | ------------------------ | ----------------------------------- |
| Concrete class | Yes                      | Can optionally override methods     |
| Abstract class | Yes                      | Must implement all abstract methods |

## Important Point

The statement that "anonymous classes can only extend abstract classes and cannot extend concrete classes" is **false**. They can extend **both**.
