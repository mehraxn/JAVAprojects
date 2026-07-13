# CustomLineReader Documentation

## Overview
This Java program demonstrates how to build a custom line-reading method that processes text character by character, handling different line termination conventions (`\n`, `\r\n`).

---

## Complete Code Breakdown

### Package and Imports
```java
import java.io.*;
```
Imports all classes from the `java.io` package, providing access to input/output operations like `Reader`, `StringReader`, and `IOException`.

---

### Class Declaration
```java
public class CustomLineReader {
```
Declares a public class named `CustomLineReader` that contains the main logic for reading lines from a stream.

---

### Main Method

#### Method Signature
```java
public static void main(String[] args) throws IOException {
```
- **`public static`**: Entry point accessible without creating an instance
- **`String[] args`**: Command-line arguments (unused here)
- **`throws IOException`**: Declares that this method may throw IO exceptions without catching them

#### Creating Test Data
```java
String data = "First Line\nSecond Line\r\n Third Line";
```
Creates a string with three lines separated by different line terminators:
- `\n` - Unix/Linux line ending (Line Feed)
- `\r\n` - Windows line ending (Carriage Return + Line Feed)

#### Creating a Reader
```java
Reader reader = new StringReader(data);
```
Wraps the string data in a `StringReader` object, which implements the `Reader` interface and allows character-by-character reading from the string.

#### Reading and Printing Lines
```java
System.out.println(readLine(reader));
System.out.println(readLine(reader));
System.out.println(readLine(reader));
```
Calls the custom `readLine()` method three times to read each line sequentially and prints them to the console.

---

### The readLine Method

#### Method Signature
```java
public static String readLine(Reader r) throws IOException {
```
- **`public static`**: Can be called without creating a class instance
- **`String`**: Returns the line as a String (or `null` if end of stream)
- **`Reader r`**: Accepts any Reader implementation as input
- **`throws IOException`**: May throw IO exceptions during reading

#### Initialize String Builder
```java
StringBuffer res = new StringBuffer();
```
Creates a `StringBuffer` to efficiently build the result string character by character. `StringBuffer` is mutable, unlike `String`.

#### Read First Character
```java
int ch = r.read();
```
Reads the first character from the reader. Returns:
- An integer (0-65535) representing the character's Unicode value
- `-1` if the end of the stream is reached

#### Check for End of File
```java
if (ch == -1) return null;
```
If the first read returns `-1`, the stream is empty or exhausted, so return `null` to indicate no more lines are available.

#### Main Reading Loop
```java
while (ch != -1) {
```
Continues reading characters until the end of the stream is reached.

#### Convert to Character
```java
char unicode = (char) ch;
```
Casts the integer value to a `char` to work with the actual character representation.

#### Check for Newline
```java
if (unicode == '\n') break;
```
If a newline character (`\n`) is encountered, exit the loop immediately. This marks the end of the current line.

#### Filter Carriage Return
```java
if (unicode != '\r') res.append(unicode);
```
Appends the character to the result **only if** it's not a carriage return (`\r`). This effectively strips out `\r` characters, normalizing line endings.

#### Read Next Character
```java
ch = r.read();
```
Reads the next character from the stream and updates the `ch` variable for the next iteration.

#### Return Complete Line
```java
return res.toString();
```
Converts the `StringBuffer` to a `String` and returns the complete line (without line terminators).

---

## How It Works

### Line Terminator Handling
The method handles multiple line terminator conventions:

| System | Line Ending | How It's Handled |
|--------|-------------|------------------|
| Unix/Linux/Mac | `\n` | Breaks the loop, doesn't append |
| Windows | `\r\n` | Ignores `\r`, breaks on `\n` |
| Old Mac | `\r` | Ignored but doesn't break (edge case) |

### Example Execution

**Input:** `"First Line\nSecond Line\r\nThird Line"`

1. **First call:** Reads until `\n`, returns `"First Line"`
2. **Second call:** Skips `\r`, reads until `\n`, returns `"Second Line"`
3. **Third call:** Reads remaining characters, returns `"Third Line"`
4. **Fourth call:** Would return `null` (end of stream)

---

## Key Concepts

### Why Use Reader?
`Reader` is an abstract class for reading character streams. It's more flexible than reading entire files at once and supports different character encodings.

### StringBuffer vs String
`StringBuffer` is used because:
- Strings are immutable in Java
- Concatenating with `+=` creates new String objects (inefficient)
- `StringBuffer` modifies the same object, improving performance

### Character Code -1
The value `-1` is used as a sentinel value to indicate EOF (End of File) because valid Unicode characters are 0-65535.

---

## Potential Improvements

1. **Use StringBuilder**: For single-threaded code, `StringBuilder` is faster than `StringBuffer`
2. **Handle Solo `\r`**: The current code doesn't treat standalone `\r` as a line terminator
3. **Buffer Size**: For large files, consider using `BufferedReader` for better performance
4. **Resource Management**: In production code, use try-with-resources to ensure proper closing of streams

---

## Output
When executed, the program prints:
```
First Line
Second Line
Third Line
```

Each line is cleanly separated without any carriage return or newline characters.