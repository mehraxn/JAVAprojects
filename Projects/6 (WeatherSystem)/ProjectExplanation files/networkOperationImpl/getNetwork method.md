# Understanding the getNetworks Method

## Complete Guide to Network Retrieval in Weather Report System

---

## Table of Contents
1. [Overview - What Does This Method Do?](#overview---what-does-this-method-do)
2. [Method Signature Deep Dive](#method-signature-deep-dive)
   - **📦 Deep Dive: Varargs (String... codes)**
3. [Line-by-Line Explanation](#line-by-line-explanation)
   - **🔍 Deep Dive: The Boolean Check Pattern**
   - **📋 Deep Dive: ArrayList**
   - **🔄 Deep Dive: Enhanced For Loop**
   - **🗄️ Deep Dive: Repository Read Operations**
4. [Visual Data Flow](#visual-data-flow)
5. [Complete Example Scenarios](#complete-example-scenarios)
6. [Comparison with Other Methods](#comparison-with-other-methods)
7. [Why Each Part is Necessary](#why-each-part-is-necessary)
8. [Common Questions](#common-questions)

---

## Overview - What Does This Method Do?

### High-Level Purpose

The `getNetworks` method retrieves networks from the database. It's flexible - you can either get ALL networks or get specific networks by their codes.

**In Simple Terms:**
Think of it like searching for contacts in your phone:
- **No search term:** Show me ALL my contacts
- **With search terms:** Show me only "John", "Mary", and "Bob"

**In Our System:**
- **No codes provided:** Return ALL networks in the database
- **Codes provided:** Return only networks matching those codes (ignore invalid ones)

### Method Behavior

| Input | Output |
|-------|--------|
| `getNetworks()` | All networks in database |
| `getNetworks("NET_01")` | Only NET_01 (if exists) |
| `getNetworks("NET_01", "NET_02")` | NET_01 and NET_02 (if they exist) |
| `getNetworks("NET_99")` | Empty collection (if NET_99 doesn't exist) |
| `getNetworks(null)` | All networks (null treated as "no codes") |

### Key Characteristics

✅ **No authorization required** - Anyone can read networks (VIEWER or MAINTAINER)
✅ **No exceptions thrown** - Method signature has no `throws` clause
✅ **Graceful handling** - Invalid codes are silently ignored
✅ **Flexible input** - Accepts zero, one, or many codes

---

## Method Signature Deep Dive

### The Complete Signature

```java
@Override
public Collection<Network> getNetworks(String... codes)
```

### Breaking Down Each Part

#### `@Override`
```java
@Override
```

**What it means:**
- This method implements a method declared in `NetworkOperations` interface
- Compiler verifies the method signature matches the interface
- Prevents accidental typos in method name

#### Return Type: `Collection<Network>`
```java
public Collection<Network>
```

**What is `Collection`?**

`Collection` is a Java interface that represents a group of objects:

```
                    Collection<E>
                         │
        ┌────────────────┼────────────────┐
        │                │                │
      List<E>         Set<E>          Queue<E>
        │                │                │
   ┌────┴────┐     ┌────┴────┐      ┌────┴────┐
ArrayList  LinkedList  HashSet  TreeSet  PriorityQueue
```

**Why return `Collection` instead of `List`?**

```java
// More flexible (good):
public Collection<Network> getNetworks(...)  // Can return any collection type

// Less flexible (limits options):
public List<Network> getNetworks(...)  // Must return a List specifically
```

**Benefits of returning `Collection`:**
- 🔄 Implementation can change (ArrayList today, HashSet tomorrow)
- 📋 Callers use the most general interface they need
- 🏗️ Good OOP practice - "program to interfaces"

#### Method Name: `getNetworks`
```java
getNetworks
```

**Naming convention:**
- `get` prefix indicates a retrieval operation (getter pattern)
- `Networks` (plural) indicates multiple items may be returned
- Follows Java naming conventions (camelCase)

#### Parameters: `String... codes`
```java
String... codes
```

This is **varargs** (variable arguments) - one of Java's most useful features!

---

### 📦 Deep Dive: Varargs (String... codes)

#### What is Varargs?

**Varargs** allows a method to accept **zero or more arguments** of the same type.

**Syntax:**
```java
Type... parameterName
```

**The `...` (three dots) is the key!**

#### How Varargs Works

**Behind the scenes, varargs creates an array:**

```java
// When you call:
getNetworks("NET_01", "NET_02", "NET_03")

// Java converts it to:
getNetworks(new String[]{"NET_01", "NET_02", "NET_03"})
```

**Inside the method, `codes` is just a `String[]` array:**

```java
public Collection<Network> getNetworks(String... codes) {
    // codes is a String[] array!
    System.out.println(codes.length);      // Works! It's an array
    System.out.println(codes[0]);          // Works! Access by index
    for (String code : codes) { }          // Works! Iterate like array
}
```

#### Different Ways to Call a Varargs Method

```java
// 1. No arguments (codes = empty array with length 0)
getNetworks()
// codes = String[] {} (empty array)
// codes.length = 0

// 2. One argument
getNetworks("NET_01")
// codes = String[] {"NET_01"}
// codes.length = 1

// 3. Multiple arguments
getNetworks("NET_01", "NET_02", "NET_03")
// codes = String[] {"NET_01", "NET_02", "NET_03"}
// codes.length = 3

// 4. Passing an array directly
String[] networkCodes = {"NET_01", "NET_02"};
getNetworks(networkCodes)
// codes = String[] {"NET_01", "NET_02"}
// codes.length = 2

// 5. Passing null explicitly
getNetworks(null)
// codes = null (NOT an empty array!)
// codes.length would throw NullPointerException!
```

#### Varargs vs Regular Array Parameter

**With varargs:**
```java
public void method(String... args) { }

// Flexible calling:
method();                          // ✅ OK
method("a");                       // ✅ OK
method("a", "b", "c");             // ✅ OK
method(new String[]{"a", "b"});    // ✅ OK
```

**With regular array:**
```java
public void method(String[] args) { }

// Must always pass an array:
method();                          // ❌ Error! No argument
method("a");                       // ❌ Error! Not an array
method("a", "b", "c");             // ❌ Error! Not an array
method(new String[]{"a", "b"});    // ✅ OK (only way to call)
```

#### Varargs Rules

**Rule 1: Only ONE varargs parameter per method**
```java
// ❌ INVALID - Can't have two varargs
public void method(String... a, String... b) { }

// ✅ VALID - Only one varargs
public void method(String... codes) { }
```

**Rule 2: Varargs must be the LAST parameter**
```java
// ❌ INVALID - Varargs not last
public void method(String... codes, String name) { }

// ✅ VALID - Varargs is last
public void method(String name, String... codes) { }
```

**Rule 3: Varargs can receive null**
```java
getNetworks(null)  // codes will be null, NOT empty array!
// This is why we check: codes == null
```

#### Visual: Varargs Transformation

```
Your Code                          What Java Does Internally
─────────────────────────────────────────────────────────────
getNetworks()                  →   getNetworks(new String[]{})
                                                    ↓
                                              codes = []
                                              codes.length = 0

getNetworks("NET_01")          →   getNetworks(new String[]{"NET_01"})
                                                    ↓
                                              codes = ["NET_01"]
                                              codes.length = 1

getNetworks("A", "B", "C")     →   getNetworks(new String[]{"A", "B", "C"})
                                                    ↓
                                              codes = ["A", "B", "C"]
                                              codes.length = 3

getNetworks(null)              →   getNetworks(null)
                                                    ↓
                                              codes = null
                                              codes.length → NullPointerException!
```

---

## Line-by-Line Explanation

### The Complete Method

```java
@Override
public Collection<Network> getNetworks(String... codes) {
    boolean noCodesProvided = (codes == null || codes.length == 0);
    
    if (noCodesProvided) {
        List<Network> allNetworks = networkRepository.read();
        return allNetworks;
    }
    
    List<Network> foundNetworks = new ArrayList<>();
    
    for (String networkCode : codes) {
        Network network = networkRepository.read(networkCode);
        
        if (network != null) {
            foundNetworks.add(network);
        }
    }
    
    return foundNetworks;
}
```

---

### Section 1: Check if Codes Were Provided (Line 1)

```java
boolean noCodesProvided = (codes == null || codes.length == 0);
```

**What this does:**
Creates a boolean variable that is `true` when NO codes were provided.

---

### 🔍 Deep Dive: The Boolean Check Pattern

#### Breaking Down the Condition

```java
codes == null || codes.length == 0
└─────┬─────┘    └───────┬────────┘
      │                  │
  Check 1            Check 2
```

**Check 1: `codes == null`**
- True when `getNetworks(null)` was called
- Protects against NullPointerException in Check 2

**Check 2: `codes.length == 0`**
- True when `getNetworks()` was called (no arguments)
- Varargs creates empty array: `String[] {}`

#### Why Use a Named Boolean Variable?

**Without named variable (harder to read):**
```java
if (codes == null || codes.length == 0) {
    // What does this condition mean again?
}
```

**With named variable (self-documenting):**
```java
boolean noCodesProvided = (codes == null || codes.length == 0);

if (noCodesProvided) {
    // Clear! No codes were provided
}
```

**Benefits:**
- 📖 Self-documenting code
- 🔍 Easier to debug (can inspect the variable)
- 🧪 Easier to test logic
- ♻️ Can reuse the boolean if needed

#### Short-Circuit Evaluation

```java
codes == null || codes.length == 0
```

**Java uses short-circuit evaluation:**
- If `codes == null` is `true`, Java doesn't evaluate `codes.length == 0`
- This prevents `NullPointerException`!

**Example flow:**

```
Scenario 1: codes = null
  codes == null  →  true
       ↓
  (short-circuit! stop here)
       ↓
  Result: true
  
  codes.length is NEVER accessed! ✅ Safe!

Scenario 2: codes = String[]{}  (empty array)
  codes == null  →  false
       ↓
  (not short-circuited, continue)
       ↓
  codes.length == 0  →  true
       ↓
  Result: true

Scenario 3: codes = String[]{"NET_01"}
  codes == null  →  false
       ↓
  codes.length == 0  →  false (length is 1)
       ↓
  Result: false
```

#### The Parentheses

```java
boolean noCodesProvided = (codes == null || codes.length == 0);
                          ^                                   ^
                          └─────── Optional but recommended ──┘
```

**Why include parentheses?**
- Groups the expression visually
- Makes intent clear
- Prevents operator precedence confusion

---

### Section 2: Return All Networks (Lines 2-5)

```java
if (noCodesProvided) {
    List<Network> allNetworks = networkRepository.read();
    return allNetworks;
}
```

**What this does:**
If no specific codes were requested, return ALL networks from the database.

#### Line 3: Fetch All Networks
```java
List<Network> allNetworks = networkRepository.read();
```

**What `networkRepository.read()` does (no parameter):**
- Retrieves ALL Network entities from the database
- Returns a `List<Network>` containing every network

**Database query equivalent:**
```sql
SELECT * FROM Network;
```

**Example result:**
```java
allNetworks = [
    Network{code="NET_01", name="North Campus"},
    Network{code="NET_02", name="South Campus"},
    Network{code="NET_03", name="East Wing"}
]
```

#### Line 4: Return Early
```java
return allNetworks;
```

**Early return pattern:**
- Method exits immediately
- Code after this `if` block won't execute
- Cleaner than nested if-else

**Why return here?**
- We already have our answer (all networks)
- No need to process the rest of the method
- Simpler logic flow

---

### Section 3: Prepare for Selective Retrieval (Line 6)

```java
List<Network> foundNetworks = new ArrayList<>();
```

**What this does:**
Creates an empty list to collect networks we find.

---

### 📋 Deep Dive: ArrayList

#### What is ArrayList?

`ArrayList` is Java's resizable array implementation of the `List` interface.

```
         List<E> (interface)
              │
              │ implements
              │
         ArrayList<E>
```

#### Why ArrayList?

**Regular array (fixed size):**
```java
Network[] networks = new Network[3];  // Fixed size: 3
networks[0] = net1;
networks[1] = net2;
networks[2] = net3;
networks[3] = net4;  // ❌ ArrayIndexOutOfBoundsException!
```

**ArrayList (dynamic size):**
```java
ArrayList<Network> networks = new ArrayList<>();
networks.add(net1);  // Size: 1
networks.add(net2);  // Size: 2
networks.add(net3);  // Size: 3
networks.add(net4);  // Size: 4  ✅ Grows automatically!
networks.add(net5);  // Size: 5  ✅ Keeps growing!
```

#### ArrayList Visualization

```
Initial: new ArrayList<>()
┌───────────────────────────────┐
│  [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ] │  (internal array, initially empty)
│  size = 0                       │
└───────────────────────────────┘

After add(network1):
┌───────────────────────────────┐
│  [NET_01][ ][ ][ ][ ][ ][ ][ ]  │
│  size = 1                       │
└───────────────────────────────┘

After add(network2):
┌───────────────────────────────┐
│  [NET_01][NET_02][ ][ ][ ][ ]   │
│  size = 2                       │
└───────────────────────────────┘

After add(network3):
┌───────────────────────────────┐
│  [NET_01][NET_02][NET_03][ ]    │
│  size = 3                       │
└───────────────────────────────┘
```

#### Key ArrayList Methods

| Method | Purpose | Example |
|--------|---------|---------|
| `add(element)` | Add to end | `list.add(network)` |
| `get(index)` | Get by position | `list.get(0)` |
| `size()` | Get count | `list.size()` |
| `isEmpty()` | Check if empty | `list.isEmpty()` |
| `contains(element)` | Check existence | `list.contains(network)` |
| `remove(element)` | Remove element | `list.remove(network)` |

#### Why `new ArrayList<>()`?

**The diamond operator `<>`:**
```java
// Full syntax (redundant):
List<Network> list = new ArrayList<Network>();

// Diamond operator (Java 7+):
List<Network> list = new ArrayList<>();
//                              ↑
//                   Compiler infers <Network> from left side
```

#### Why Declare as `List` but Create `ArrayList`?

```java
List<Network> foundNetworks = new ArrayList<>();
↑                             ↑
Interface                     Implementation
```

**Programming to interfaces:**
- Flexibility to change implementation later
- Could swap to `LinkedList` without changing other code
- Better abstraction

```java
// Later, if needed:
List<Network> foundNetworks = new LinkedList<>();  // Just change this line!
// Rest of code works the same because it uses List interface
```

---

### Section 4: Loop Through Requested Codes (Lines 7-14)

```java
for (String networkCode : codes) {
    Network network = networkRepository.read(networkCode);
    
    if (network != null) {
        foundNetworks.add(network);
    }
}
```

**What this does:**
For each code provided, try to find the network and add it to our results if found.

---

### 🔄 Deep Dive: Enhanced For Loop

#### What is Enhanced For Loop?

Also called "for-each loop" - a cleaner way to iterate over collections/arrays.

**Traditional for loop:**
```java
for (int i = 0; i < codes.length; i++) {
    String networkCode = codes[i];
    // Use networkCode
}
```

**Enhanced for loop (cleaner):**
```java
for (String networkCode : codes) {
    // Use networkCode directly
}
```

#### Syntax Breakdown

```java
for (String networkCode : codes) {
     ↑       ↑            ↑
     │       │            └── Collection/array to iterate
     │       └── Variable name for current element
     └── Type of each element
```

**Read as:** "For each String networkCode IN codes..."

#### How It Works Internally

```java
for (String networkCode : codes) {
    System.out.println(networkCode);
}

// Is equivalent to:
for (int i = 0; i < codes.length; i++) {
    String networkCode = codes[i];
    System.out.println(networkCode);
}
```

#### Iteration Visualization

```
codes = ["NET_01", "NET_02", "NET_03"]

Iteration 1:
  networkCode = "NET_01"  ← First element
  │
  ▼
  Process NET_01...

Iteration 2:
  networkCode = "NET_02"  ← Second element
  │
  ▼
  Process NET_02...

Iteration 3:
  networkCode = "NET_03"  ← Third element
  │
  ▼
  Process NET_03...

Loop ends (no more elements)
```

#### When to Use Enhanced For Loop

**Use enhanced for loop when:**
- ✅ You need to process ALL elements
- ✅ You don't need the index
- ✅ You're not modifying the collection size

**Use traditional for loop when:**
- ⚠️ You need the index for calculations
- ⚠️ You need to iterate backwards
- ⚠️ You need to skip elements by index

---

### 🗄️ Deep Dive: Repository Read Operations

#### The Two `read()` Methods

The `CRUDRepository` has TWO read methods:

**Method 1: `read()` - Get ALL**
```java
public List<T> read()
```
- No parameters
- Returns ALL entities of this type
- SQL: `SELECT * FROM Network`

**Method 2: `read(id)` - Get ONE by ID**
```java
public T read(ID id)
```
- Takes an ID parameter
- Returns ONE entity or `null`
- SQL: `SELECT * FROM Network WHERE code = ?`

#### In Our Method

```java
// Line 3: Get ALL networks (no parameter)
List<Network> allNetworks = networkRepository.read();

// Line 8: Get ONE network by code (with parameter)
Network network = networkRepository.read(networkCode);
```

#### What Happens When Network Not Found?

```java
Network network = networkRepository.read("NET_99");
// If NET_99 doesn't exist:
// network = null
```

**The repository returns `null`, NOT an exception!**

This is why we check:
```java
if (network != null) {
    foundNetworks.add(network);
}
```

---

### Section 5: Check if Network Exists (Lines 9-11)

```java
if (network != null) {
    foundNetworks.add(network);
}
```

**What this does:**
Only add to results if the network was actually found.

#### Why This Check?

**Without the check (crashes on null):**
```java
foundNetworks.add(network);  // If network is null, we add null to list!
// Later code might crash when processing null elements
```

**With the check (safe):**
```java
if (network != null) {
    foundNetworks.add(network);  // Only add valid networks
}
// Invalid codes are silently ignored
```

#### Graceful Handling of Invalid Codes

**Example scenario:**
```java
getNetworks("NET_01", "INVALID", "NET_02", "DOES_NOT_EXIST")
```

**Processing:**
```
"NET_01" → networkRepository.read("NET_01") → Network found ✅ → ADD
"INVALID" → networkRepository.read("INVALID") → null ❌ → SKIP
"NET_02" → networkRepository.read("NET_02") → Network found ✅ → ADD
"DOES_NOT_EXIST" → networkRepository.read("...") → null ❌ → SKIP
```

**Result:** `[Network{NET_01}, Network{NET_02}]`

**Invalid codes are silently ignored - no exception!**

---

### Section 6: Return Results (Line 15)

```java
return foundNetworks;
```

**What this does:**
Returns the list of networks we found (may be empty if no codes matched).

#### Possible Return Values

```java
// If we found networks:
return [Network{NET_01}, Network{NET_02}]  // List with elements

// If no codes matched:
return []  // Empty list (NOT null!)
```

**Important:** The method NEVER returns `null`, always a collection (possibly empty).

---

## Visual Data Flow

### Complete Method Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  getNetworks(...)                                                           │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 1: Check if codes provided                                            │
│  noCodesProvided = (codes == null || codes.length == 0)                     │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                          ┌───────────┴───────────┐
                          │                       │
                    noCodesProvided          noCodesProvided
                       = true                   = false
                          │                       │
                          ▼                       ▼
┌─────────────────────────────────┐  ┌────────────────────────────────────────┐
│  STEP 2A: Return ALL networks   │  │  STEP 2B: Process specific codes       │
│                                 │  │                                        │
│  allNetworks = repo.read()      │  │  foundNetworks = new ArrayList<>()     │
│  return allNetworks             │  │                                        │
│                                 │  │  for each code in codes:               │
│  ┌───────────────────────────┐  │  │    network = repo.read(code)           │
│  │ Result: All networks      │  │  │    if (network != null)                │
│  │ [NET_01, NET_02, NET_03]  │  │  │      foundNetworks.add(network)        │
│  └───────────────────────────┘  │  │                                        │
└─────────────────────────────────┘  │  return foundNetworks                  │
                                     │                                        │
                                     │  ┌────────────────────────────────────┐│
                                     │  │ Result: Only requested networks   ││
                                     │  │ (invalid codes ignored)           ││
                                     │  └────────────────────────────────────┘│
                                     └────────────────────────────────────────┘
```

### Detailed Flow for Specific Codes

```
getNetworks("NET_01", "NET_99", "NET_02")
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  codes = ["NET_01", "NET_99", "NET_02"]                                     │
│  codes.length = 3                                                           │
│  noCodesProvided = false                                                    │
└─────────────────────────────────────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  foundNetworks = []  (empty ArrayList)                                      │
└─────────────────────────────────────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  ITERATION 1: networkCode = "NET_01"                                        │
│  ├─ networkRepository.read("NET_01")                                        │
│  ├─ network = Network{code="NET_01", name="North"}                          │
│  ├─ network != null? YES ✅                                                  │
│  └─ foundNetworks.add(network) → foundNetworks = [NET_01]                   │
└─────────────────────────────────────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  ITERATION 2: networkCode = "NET_99"                                        │
│  ├─ networkRepository.read("NET_99")                                        │
│  ├─ network = null  (doesn't exist)                                         │
│  ├─ network != null? NO ❌                                                   │
│  └─ SKIP (don't add to list) → foundNetworks = [NET_01]                     │
└─────────────────────────────────────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  ITERATION 3: networkCode = "NET_02"                                        │
│  ├─ networkRepository.read("NET_02")                                        │
│  ├─ network = Network{code="NET_02", name="South"}                          │
│  ├─ network != null? YES ✅                                                  │
│  └─ foundNetworks.add(network) → foundNetworks = [NET_01, NET_02]           │
└─────────────────────────────────────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  return foundNetworks                                                       │
│  Result: [Network{NET_01}, Network{NET_02}]                                 │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Complete Example Scenarios

### Scenario 1: Get All Networks (No Arguments) ✅

**Call:**
```java
Collection<Network> networks = operations.getNetworks();
```

**Database state:**
```
| code   | name          | description          |
|--------|---------------|----------------------|
| NET_01 | North Campus  | North area monitor   |
| NET_02 | South Campus  | South area monitor   |
| NET_03 | East Wing     | East wing sensors    |
```

**Execution:**
```
1. codes = String[] {}  (empty array from varargs)
2. noCodesProvided = (null? NO) || (length == 0? YES) = true
3. if (true) → Enter block
4. allNetworks = networkRepository.read() → [NET_01, NET_02, NET_03]
5. return allNetworks
```

**Result:**
```java
[
    Network{code="NET_01", name="North Campus"},
    Network{code="NET_02", name="South Campus"},
    Network{code="NET_03", name="East Wing"}
]
```

---

### Scenario 2: Get Specific Network ✅

**Call:**
```java
Collection<Network> networks = operations.getNetworks("NET_02");
```

**Execution:**
```
1. codes = String[] {"NET_02"}
2. noCodesProvided = (null? NO) || (length == 0? NO, length is 1) = false
3. if (false) → Skip block, continue to loop
4. foundNetworks = []
5. Loop iteration 1:
   - networkCode = "NET_02"
   - network = networkRepository.read("NET_02") → Network{NET_02}
   - network != null? YES
   - foundNetworks.add(Network{NET_02}) → [NET_02]
6. return foundNetworks
```

**Result:**
```java
[Network{code="NET_02", name="South Campus"}]
```

---

### Scenario 3: Get Multiple Networks ✅

**Call:**
```java
Collection<Network> networks = operations.getNetworks("NET_01", "NET_03");
```

**Execution:**
```
1. codes = String[] {"NET_01", "NET_03"}
2. noCodesProvided = false
3. foundNetworks = []
4. Loop iteration 1: networkCode = "NET_01"
   - network = Network{NET_01} ✅
   - foundNetworks = [NET_01]
5. Loop iteration 2: networkCode = "NET_03"
   - network = Network{NET_03} ✅
   - foundNetworks = [NET_01, NET_03]
6. return foundNetworks
```

**Result:**
```java
[
    Network{code="NET_01", name="North Campus"},
    Network{code="NET_03", name="East Wing"}
]
```

---

### Scenario 4: Network Doesn't Exist ⚠️

**Call:**
```java
Collection<Network> networks = operations.getNetworks("NET_99");
```

**Database:** NET_99 doesn't exist

**Execution:**
```
1. codes = String[] {"NET_99"}
2. noCodesProvided = false
3. foundNetworks = []
4. Loop iteration 1: networkCode = "NET_99"
   - network = networkRepository.read("NET_99") → null ❌
   - network != null? NO
   - Skip adding to list
5. return foundNetworks
```

**Result:**
```java
[]  // Empty collection (NOT null!)
```

**No exception thrown! Invalid codes are silently ignored.**

---

### Scenario 5: Mix of Valid and Invalid Codes ⚠️

**Call:**
```java
Collection<Network> networks = operations.getNetworks("NET_01", "INVALID", "NET_02", "NET_99");
```

**Execution:**
```
1. codes = ["NET_01", "INVALID", "NET_02", "NET_99"]
2. noCodesProvided = false
3. foundNetworks = []
4. Loop iteration 1: "NET_01"
   - network = Network{NET_01} ✅
   - foundNetworks = [NET_01]
5. Loop iteration 2: "INVALID"
   - network = null ❌ (invalid format, doesn't exist)
   - SKIP
6. Loop iteration 3: "NET_02"
   - network = Network{NET_02} ✅
   - foundNetworks = [NET_01, NET_02]
7. Loop iteration 4: "NET_99"
   - network = null ❌ (valid format but doesn't exist)
   - SKIP
8. return foundNetworks
```

**Result:**
```java
[
    Network{code="NET_01", name="North Campus"},
    Network{code="NET_02", name="South Campus"}
]
// Only the valid, existing networks are returned
```

---

### Scenario 6: Null Argument ⚠️

**Call:**
```java
Collection<Network> networks = operations.getNetworks(null);
```

**Execution:**
```
1. codes = null  (NOT empty array!)
2. noCodesProvided = (codes == null? YES) || ... = true
3. if (true) → Enter block
4. allNetworks = networkRepository.read() → [all networks]
5. return allNetworks
```

**Result:** All networks (same as calling with no arguments)

**Note:** `getNetworks(null)` behaves like `getNetworks()` due to null check!

---

### Scenario 7: Empty Database

**Call:**
```java
Collection<Network> networks = operations.getNetworks();
```

**Database:** Empty (no networks exist)

**Execution:**
```
1. codes = []
2. noCodesProvided = true
3. allNetworks = networkRepository.read() → []  (empty list from empty DB)
4. return allNetworks
```

**Result:**
```java
[]  // Empty collection
```

---

### Scenario 8: Passing Array Directly

**Call:**
```java
String[] codes = {"NET_01", "NET_02"};
Collection<Network> networks = operations.getNetworks(codes);
```

**This works because varargs accepts arrays!**

**Execution:** Same as `getNetworks("NET_01", "NET_02")`

**Result:**
```java
[Network{NET_01}, Network{NET_02}]
```

---

## Comparison with Other Methods

### getNetworks vs createNetwork

| Aspect | getNetworks | createNetwork |
|--------|-------------|---------------|
| **Operation** | READ | CREATE |
| **Authorization** | None required | MAINTAINER required |
| **Exceptions** | None | 3 possible exceptions |
| **Input validation** | Minimal (null check) | Extensive (format, uniqueness) |
| **Database** | SELECT queries | INSERT query |
| **Returns** | Collection (possibly empty) | Single Network |

### Method Signature Comparison

```java
// getNetworks - Simple, no exceptions
public Collection<Network> getNetworks(String... codes)

// createNetwork - Complex, many validations
public Network createNetwork(String code, String name, String description, String username)
        throws IdAlreadyInUseException, InvalidInputDataException, UnauthorizedException
```

### Why No Authorization for getNetworks?

**Based on requirements:**
- VIEWER users can perform read operations
- MAINTAINER users can perform read AND write operations
- Both can read networks → no authorization check needed

```java
// createNetwork (write operation)
validateUserIsMaintainer(username);  // Required!

// getNetworks (read operation)
// No validation needed - anyone can read
```

---

## Why Each Part is Necessary

### Summary Table

| Line | Code | Purpose | What If Skipped? |
|------|------|---------|------------------|
| 1 | `boolean noCodesProvided = ...` | Determine request type | Can't decide which logic path |
| 2 | `codes == null` | Handle null varargs | NullPointerException on `.length` |
| 2 | `codes.length == 0` | Handle empty varargs | Wrong path taken |
| 3-5 | `if (noCodesProvided) { return all }` | Return all networks | Would try to loop over empty array |
| 6 | `new ArrayList<>()` | Container for results | Nowhere to store found networks |
| 7 | `for (String networkCode : codes)` | Process each code | Only first code processed |
| 8 | `networkRepository.read(networkCode)` | Fetch from database | Can't get network data |
| 9-11 | `if (network != null) { add }` | Filter out invalid codes | Null values in result |
| 15 | `return foundNetworks` | Return results | No result returned |

### Design Decisions Explained

**1. Why check `null` first in the condition?**
```java
codes == null || codes.length == 0
       ↑
Short-circuit prevents NullPointerException
```

**2. Why use early return instead of if-else?**
```java
// Current (cleaner):
if (noCodesProvided) {
    return allNetworks;  // Exit early
}
// Continue with specific logic...

// Alternative (more nesting):
if (noCodesProvided) {
    return allNetworks;
} else {
    // Specific logic here (more indentation)
}
```

**3. Why silently ignore invalid codes instead of throwing exception?**
- More user-friendly
- Partial results are useful
- Consistent with method contract ("get what you can")
- No `throws` in signature keeps API simple

**4. Why return empty collection instead of null?**
```java
// Bad (forces null checks everywhere):
Collection<Network> networks = getNetworks("NET_99");
if (networks != null) {  // Must check!
    for (Network n : networks) { }
}

// Good (no null checks needed):
Collection<Network> networks = getNetworks("NET_99");
for (Network n : networks) { }  // Works! Empty collection is fine
```

---

## Common Questions

### Q1: Why no authorization check?

**A:** This is a READ operation. According to the system design:
- VIEWERs can read data
- MAINTAINERs can read and write data
- Both user types can read networks, so no check needed

Only write operations (create, update, delete) require MAINTAINER authorization.

### Q2: What's the difference between `getNetworks()` and `getNetworks(null)`?

**A:** Same result! Both return all networks.

```java
getNetworks()     // codes = String[] {} (empty array)
getNetworks(null) // codes = null

// Both satisfy: codes == null || codes.length == 0
```

### Q3: Why use `Collection` return type instead of `List`?

**A:** Flexibility! `Collection` is more general:
- Caller doesn't need list-specific features (like `get(index)`)
- Implementation could change without breaking callers
- Good OOP practice: program to interfaces

### Q4: What happens if I pass duplicate codes?

**A:** Each code is looked up separately:

```java
getNetworks("NET_01", "NET_01", "NET_01")
```

**Execution:**
```
Iteration 1: Read NET_01 → Add
Iteration 2: Read NET_01 → Add (again!)
Iteration 3: Read NET_01 → Add (again!)
Result: [NET_01, NET_01, NET_01]  // Three copies!
```

**The method doesn't deduplicate!** If you need unique results, the caller should handle it:
```java
Set<Network> unique = new HashSet<>(operations.getNetworks("NET_01", "NET_01"));
```

### Q5: Why doesn't this method throw exceptions?

**A:** Design choice for user-friendly API:
- Invalid codes return empty results, not errors
- No authorization needed for reads
- Simpler to use (no try-catch required)

```java
// Simple usage (no exception handling needed):
Collection<Network> networks = operations.getNetworks("NET_01");
if (networks.isEmpty()) {
    System.out.println("No networks found");
}
```

### Q6: Is this method thread-safe?

**A:** The method itself doesn't maintain state, but:
- Multiple threads can call it simultaneously
- Database reads are typically safe
- The `new ArrayList<>()` is local to each call

However, if another thread deletes a network between the read and your usage, you could have stale data. This is normal for database operations.

### Q7: Why loop instead of batch query?

**A:** Current implementation makes N database calls for N codes.

**More efficient alternative:**
```java
// Single query for all codes (hypothetical):
SELECT * FROM Network WHERE code IN ('NET_01', 'NET_02', 'NET_03');
```

The current approach is simpler but less efficient for many codes. A batch query would be an optimization if performance becomes an issue.

### Q8: What if the varargs array contains null elements?

```java
getNetworks("NET_01", null, "NET_02")
```

**Execution:**
```
Iteration 1: "NET_01" → Read → Found → Add
Iteration 2: null → Read(null) → Returns null → Skip
Iteration 3: "NET_02" → Read → Found → Add
```

**Depends on repository behavior**, but typically:
- `read(null)` returns `null`
- Null element is skipped (treated as invalid code)

---

## Summary

### What This Method Does

```
INPUT:   Zero or more network codes (varargs)
         ↓
PROCESS: If no codes → return ALL networks
         If codes provided → return only matching networks
         Invalid codes are silently ignored
         ↓
OUTPUT:  Collection of Network objects (possibly empty, never null)
```

### Key Concepts

1. **Varargs** - Flexible parameter that accepts 0, 1, or many values
2. **Early Return** - Return all networks immediately if no specific codes requested
3. **Graceful Handling** - Invalid codes ignored, no exceptions
4. **Collection Return** - Always return a collection, never null
5. **No Authorization** - Read operations don't require MAINTAINER status

### The Method in One Sentence

> "Return all networks if no codes specified, or return only the networks matching the provided codes (ignoring invalid ones)."

### Quick Reference

```java
// Get ALL networks
getNetworks()                    // Returns all networks

// Get specific networks
getNetworks("NET_01")            // Returns [NET_01] if exists, [] if not
getNetworks("NET_01", "NET_02")  // Returns both if they exist

// Invalid codes are ignored
getNetworks("INVALID")           // Returns []
getNetworks("NET_01", "INVALID") // Returns [NET_01] only
```

---

**End of Document**