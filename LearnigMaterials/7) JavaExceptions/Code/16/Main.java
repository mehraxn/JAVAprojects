// Custom Exception
class EmptyStack extends Exception {
    public EmptyStack() {
        super("Stack is empty!");
    }
}

// Stack class
class Stack {
    private int[] elements;
    private int size;
    private int capacity;

    public Stack(int capacity) {
        this.capacity = capacity;
        elements = new int[capacity];
        size = 0;
    }

    public void push(int value) {
        if (size < capacity) {
            elements[size++] = value;
        }
    }

    public int pop() throws EmptyStack {
        if (size == 0) {
            throw new EmptyStack(); // Throw exception if stack is empty
        }
        return elements[--size];
    }
}

// Demo class
public class Main {
    public static void main(String[] args) {
        Stack stack = new Stack(5);

        try {
            System.out.println("Popping: " + stack.pop()); // Should throw exception
        } catch (EmptyStack e) {
            System.out.println("Caught exception: " + e.getMessage());
        }

        stack.push(10);
        stack.push(20);

        try {
            System.out.println("Popping: " + stack.pop()); // 20
            System.out.println("Popping: " + stack.pop()); // 10
            System.out.println("Popping: " + stack.pop()); // Exception
        } catch (EmptyStack e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
    }
}
