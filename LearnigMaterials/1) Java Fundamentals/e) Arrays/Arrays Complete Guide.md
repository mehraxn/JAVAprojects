# Java Arrays - Complete Guide

## Overview
An array stores multiple values of the same type in one object. Arrays have a fixed size.

---

## 1. Creating arrays

### Declaration and allocation

```java
int[] numbers = new int[5];
```

This creates an array of 5 integers. Default values are `0`.

### Declaration with values

```java
int[] numbers = {10, 20, 30};
String[] names = {"Ali", "Sara", "Tom"};
```

---

## 2. Accessing elements

Arrays use zero-based indexes.

```java
int[] numbers = {10, 20, 30};

System.out.println(numbers[0]); // 10
System.out.println(numbers[1]); // 20
System.out.println(numbers[2]); // 30
```

Wrong:

```java
System.out.println(numbers[3]); // ArrayIndexOutOfBoundsException
```

---

## 3. Array length

Use `.length`:

```java
int[] numbers = {10, 20, 30};
System.out.println(numbers.length); // 3
```

Important: array length is a field, not a method.

Correct:

```java
numbers.length
```

Wrong:

```java
numbers.length()
```

---

## 4. Looping over arrays

### Index loop

```java
int[] numbers = {10, 20, 30};

for (int i = 0; i < numbers.length; i++) {
    System.out.println(numbers[i]);
}
```

Use this when you need the index.

### Enhanced for-loop

```java
for (int number : numbers) {
    System.out.println(number);
}
```

Use this when you only need the values.

---

## 5. Arrays are objects

Arrays are reference types:

```java
int[] a = {1, 2, 3};
int[] b = a;

b[0] = 99;
System.out.println(a[0]); // 99
```

Both variables refer to the same array.

---

## 6. Multidimensional arrays

A two-dimensional array is an array of arrays.

```java
int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6}
};

System.out.println(matrix[0][1]); // 2
```

Loop:

```java
for (int row = 0; row < matrix.length; row++) {
    for (int col = 0; col < matrix[row].length; col++) {
        System.out.print(matrix[row][col] + " ");
    }
    System.out.println();
}
```

---

## 7. Useful `Arrays` methods

Import:

```java
import java.util.Arrays;
```

Examples:

```java
int[] numbers = {3, 1, 2};

Arrays.sort(numbers);
System.out.println(Arrays.toString(numbers)); // [1, 2, 3]
```

Compare arrays:

```java
int[] a = {1, 2};
int[] b = {1, 2};

System.out.println(a == b); // false
System.out.println(Arrays.equals(a, b)); // true
```

---

## Common mistakes

### Mistake 1: off-by-one loop

Wrong:

```java
for (int i = 0; i <= numbers.length; i++)
```

Correct:

```java
for (int i = 0; i < numbers.length; i++)
```

### Mistake 2: comparing arrays with `==`
Use `Arrays.equals` for content.

### Mistake 3: expecting arrays to resize
Arrays have fixed length. Use `ArrayList` when size changes dynamically.

---

## 8. More `Arrays` helper methods

```java
import java.util.Arrays;
```

### `Arrays.toString()` — print an array readably

Printing an array directly shows a useless hash-like text, so always use `Arrays.toString`:

```java
int[] numbers = {3, 1, 2};

System.out.println(numbers);                    // e.g. [I@1b6d3586  (not helpful)
System.out.println(Arrays.toString(numbers));   // [3, 1, 2]
```

### `Arrays.sort()` — sort numbers or Strings

```java
int[] nums = {3, 1, 2};
Arrays.sort(nums);
System.out.println(Arrays.toString(nums)); // [1, 2, 3]

String[] fruits = {"banana", "apple", "cherry"};
Arrays.sort(fruits);
System.out.println(Arrays.toString(fruits)); // [apple, banana, cherry]
```

`Arrays.sort` sorts **in place** — it changes the original array and returns nothing.

### `Arrays.fill()` — set every element to one value

```java
int[] scores = new int[4];
Arrays.fill(scores, 10);
System.out.println(Arrays.toString(scores)); // [10, 10, 10, 10]
```

### `Arrays.copyOf()` — make a bigger or smaller copy

```java
int[] a = {1, 2, 3};

int[] longer = Arrays.copyOf(a, 5);   // pads with default 0
System.out.println(Arrays.toString(longer)); // [1, 2, 3, 0, 0]

int[] shorter = Arrays.copyOf(a, 2);  // keeps only the first 2
System.out.println(Arrays.toString(shorter)); // [1, 2]
```

This is one way to "grow" an array, since arrays cannot resize themselves.

### `Arrays.binarySearch()` — fast search on a SORTED array

```java
int[] sorted = {10, 20, 30, 40};
int index = Arrays.binarySearch(sorted, 30);
System.out.println(index); // 2
```

Important: `binarySearch` only works correctly if the array is already **sorted**. On an
unsorted array the result is undefined.

---

## 9. More on array-index mistakes

The valid indexes of an array are `0` to `length - 1`. Anything outside throws
`ArrayIndexOutOfBoundsException` at runtime.

```java
int[] a = {10, 20, 30}; // valid indexes: 0, 1, 2

a[3];   // ArrayIndexOutOfBoundsException (length is 3, last index is 2)
a[-1];  // ArrayIndexOutOfBoundsException (no negative indexes in Java)
```

Safe access pattern before using an index:

```java
int i = 3;
if (i >= 0 && i < a.length) {
    System.out.println(a[i]);
} else {
    System.out.println("index out of range");
}
```

Empty arrays have length `0`, so **every** index access fails:

```java
int[] empty = new int[0];
System.out.println(empty.length); // 0
// empty[0]; // ArrayIndexOutOfBoundsException
```

---

## Exam Notes

- Indexes run from `0` to `length - 1`; `array.length` is a **field**, not a method.
- Printing an array uses `Arrays.toString(array)`; comparing content uses `Arrays.equals(a, b)`.
- `Arrays.sort` sorts **in place** (numbers ascending, Strings alphabetically).
- `Arrays.binarySearch` requires the array to be **sorted** first.
- Arrays have a **fixed size**; use `Arrays.copyOf` for a resized copy, or `ArrayList` for a
  growable list.
- Negative indexes and index `== length` both throw `ArrayIndexOutOfBoundsException`.

---

## Mini quiz

### Q1. What is the first index of an array?
Answer: `0`.

### Q2. How do you get array length?
Answer: `array.length`.

### Q3. Which class provides `Arrays.toString()`?
Answer: `java.util.Arrays`.

---

## More Practice Questions

1. What does `Arrays.toString(new int[]{3, 1, 2})` print, and why is it better than
   `System.out.println(array)`?

2. After `Arrays.sort(words)` on `{"pear", "apple", "kiwi"}`, what is the array's content?

3. Write code that creates an `int[]` of length 3 and fills every element with `-1`.

4. What are the two valid ways this can go wrong: `a[i]` when `a.length == 3`? Give the two
   out-of-range values of `i`. (Answer: `i = 3` and `i = -1`, i.e. `i >= length` or `i < 0`.)

5. Why must an array be sorted before you call `Arrays.binarySearch` on it?

6. You have `int[] a = {1, 2, 3}` and need a 5-element copy keeping the first three values.
   Which method do you use, and what will the new array contain?

7. Given a 2D array `int[][] m`, write the nested loop bounds needed to visit every element
   safely. (Hint: outer uses `m.length`, inner uses `m[row].length`.)
