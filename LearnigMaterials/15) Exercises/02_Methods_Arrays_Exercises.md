# 02 — Methods and Arrays Exercises

Topics: methods, parameters, return values, pass-by-value, arrays (1D and 2D),
`Arrays` helpers. Solutions are under each **Solution** heading. Outputs checked by static review.

---

## Exercise 1 — Write a method

Write a static method `int max(int a, int b)` that returns the larger of two numbers, and
call it from `main`.

**Expected output** (for `max(4, 9)`):
```
9
```

### Solution
```java
public class MaxDemo {
    static int max(int a, int b) {
        return (a > b) ? a : b;
    }
    public static void main(String[] args) {
        System.out.println(max(4, 9)); // 9
    }
}
```

---

## Exercise 2 — Sum an array

Write `int sum(int[] values)` that returns the total of all elements.

**Expected output** (for `{10, 20, 30}`):
```
60
```

### Solution
```java
static int sum(int[] values) {
    int total = 0;
    for (int v : values) {
        total += v;
    }
    return total;
}
// sum(new int[]{10, 20, 30}) -> 60
```

---

## Exercise 3 — Find the largest in an array

Write `int largest(int[] a)` assuming the array has at least one element.

**Expected output** (for `{3, 9, 2, 7}`):
```
9
```

### Solution
```java
static int largest(int[] a) {
    int best = a[0];
    for (int i = 1; i < a.length; i++) {
        if (a[i] > best) best = a[i];
    }
    return best;
}
```

---

## Exercise 4 — 2D array sum of a row

Given a 2D array, write code that prints the sum of row index 1.
```java
int[][] m = {
    {1, 2, 3},
    {4, 5, 6}
};
```

**Expected output:**
```
15
```

### Solution
```java
int rowSum = 0;
for (int col = 0; col < m[1].length; col++) {
    rowSum += m[1][col];
}
System.out.println(rowSum); // 4 + 5 + 6 = 15
```

---

## Exercise 5 — Sort and print

Sort `int[] nums = {5, 1, 4, 2};` ascending and print it readably.

**Expected output:**
```
[1, 2, 4, 5]
```

### Solution
```java
import java.util.Arrays;

int[] nums = {5, 1, 4, 2};
Arrays.sort(nums);
System.out.println(Arrays.toString(nums)); // [1, 2, 4, 5]
```

---

## Exercise 6 — Predict the output (pass-by-value)

```java
static void tryChangeInt(int x) { x = 99; }
static void tryChangeArray(int[] a) { a[0] = 99; }

public static void main(String[] args) {
    int n = 5;
    tryChangeInt(n);
    System.out.println(n);

    int[] arr = {5};
    tryChangeArray(arr);
    System.out.println(arr[0]);
}
```

### Solution
```
5
99
```
Java is **pass-by-value**. For `int`, the method gets a copy, so `n` stays 5. For an array,
the copied value is the **reference**, so the method changes the same array — `arr[0]` becomes 99.

---

## Exercise 7 — Fix the bug (`.length` vs `.length()`)

This does not compile. Fix it.
```java
int[] a = {1, 2, 3};
System.out.println(a.length());
```

### Solution
Arrays use the field `length`, not a method:
```java
System.out.println(a.length); // 3
```
*(Note: `String` uses the method `length()`. Arrays use the field `length`.)*

---

## Exercise 8 — Fix the bug (off-by-one / index range)

This throws `ArrayIndexOutOfBoundsException`. Fix it to print all elements.
```java
int[] a = {10, 20, 30};
for (int i = 0; i <= a.length; i++) {
    System.out.println(a[i]);
}
```

### Solution
Valid indexes are `0` to `length - 1`. Use `<`, not `<=`:
```java
for (int i = 0; i < a.length; i++) {
    System.out.println(a[i]);
}
```

---

## Challenge — Reverse an array in place

Write `void reverse(int[] a)` that reverses the array without creating a new one.

**Expected output** (for `{1, 2, 3, 4}` then printed):
```
[4, 3, 2, 1]
```

### Solution
```java
static void reverse(int[] a) {
    int i = 0, j = a.length - 1;
    while (i < j) {
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
        i++;
        j--;
    }
}
```
