// Generic Interface
interface Printer<T> {
    void print(T item);
}

// Implementation of the generic interface
class StringPrinter implements Printer<String> {
    @Override
    public void print(String item) {
        System.out.println("String: " + item);
    }
}

class IntegerPrinter implements Printer<Integer> {
    @Override
    public void print(Integer item) {
        System.out.println("Integer: " + item);
    }
}

// Test the generic interface
public class Main {
    public static void main(String[] args) {
        Printer<String> sp = new StringPrinter();
        sp.print("Hello!");

        Printer<Integer> ip = new IntegerPrinter();
        ip.print(123);
    }
}
