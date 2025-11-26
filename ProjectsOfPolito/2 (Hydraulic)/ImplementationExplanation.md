# Hydraulic System Project - Complete Task Guide

## Project Overview

This project simulates a hydraulic system composed of various elements (sources, taps, splits, sinks) connected together. Water flows from a source through the system, and you need to implement the functionality to manage, connect, and simulate these elements.

---

## Task R1: System Management and Element Storage

### What This Task Is About
You need to create a container system (`HSystem`) that can store and manage hydraulic elements. The system must keep track of all elements added to it.

### What You Must Implement

1. **Storage Mechanism**
   - Create an internal data structure to store elements
   - Maximum capacity: 100 elements (you can use an array)
   - Keep track of how many elements are currently stored

2. **Method: `addElement(Element elem)`**
   - Add a new element to the system
   - Store the element in your internal data structure
   - Increment the count of stored elements

3. **Method: `getElements()`**
   - Return an array containing all elements currently in the system
   - The returned array length must equal the number of elements added (not the maximum capacity)
   - Return only the elements that have been added, no empty slots

4. **Method: `size()`**
   - Return the current number of elements in the system
   - This should reflect how many elements have been added via `addElement()`

---

## Task R2: Simple Elements (Source, Tap, Sink)

### What This Task Is About
Implement the basic behavior of simple hydraulic elements that form the building blocks of the system.

### What You Must Implement

1. **Source Element**
   - Represents the starting point of water flow
   - Has a settable flow rate (in cubic meters per hour)
   - Method `setFlow(double flow)`: Define the output flow
   - A source has NO input flow (it generates water)
   - A source CAN be connected to one downstream element

2. **Tap Element**
   - Can be open or closed to control water flow
   - Method `setOpen(boolean open)`: Set the tap state
   - When OPEN: output flow = input flow
   - When CLOSED: output flow = 0
   - Has both input and output connections

3. **Sink Element**
   - Represents the end point where water exits the system
   - Special behavior: calling `connect()` on a Sink does NOTHING (it cannot connect to other elements)
   - Method `getOutput()` must return `null`
   - Method `getOutputs()` must return an empty array or null
   - A sink only receives input, produces no output

---

## Task R3: Split Element with Multiple Outputs

### What This Task Is About
Create a T-shaped split element that can divide water flow into two separate paths.

### What You Must Implement

1. **Split Element Basics**
   - Has exactly 2 outputs (output 0 and output 1)
   - Divides input flow equally: each output gets 50% of input

2. **Connection Methods**
   - `connect(Element elem)`: Connect to the first output (index 0)
   - `connect(Element elem, int index)`: Connect to a specific output (0 or 1)
   - Both outputs can be connected to different elements

3. **Method: `getOutputs()`**
   - Return an array containing both connected elements
   - The array should have 2 positions
   - If an output is not connected, that position may be null

---

## Task R4: Flow Simulation

### What This Task Is About
Implement the core simulation logic that computes and tracks water flow through the entire system.

### What You Must Implement

1. **Method: `HSystem.simulate(SimulationObserver observer)`**
   - Start the simulation from the Source element
   - Compute flows for all elements in the system
   - Notify the observer about each element's flow status

2. **Flow Computation Rules**
   - **Source**: Output flow = the flow set via `setFlow()`
   - **Tap**: If open, output = input; if closed, output = 0
   - **Split**: Each output = input / 2
   - **Sink**: Terminal element, no output flow

3. **Observer Notifications**
   - For each element, call `observer.notifyFlow(type, name, inFlow, outFlow...)`
   - `type`: Class name (e.g., "Source", "Tap", "Split", "Sink")
   - `name`: Element's name
   - `inFlow`: Input flow to the element (use `SimulationObserver.NO_FLOW` for Source)
   - `outFlow`: Output flow(s) from the element (use `SimulationObserver.NO_FLOW` for Sink)
   - For Split: provide both output flows as separate parameters

4. **Simulation Flow**
   - Start from the Source
   - Recursively process each connected element
   - Follow the chain of connections until reaching Sink(s)

---

## Task R5: Multisplit Element

### What This Task Is About
Create an advanced split element that can divide flow among multiple outputs (more than 2) with customizable proportions.

### What You Must Implement

1. **Constructor: `Multisplit(String name, int numOutput)`**
   - Accept the number of outputs as a parameter
   - Create internal storage for the specified number of outputs
   - Initialize default proportions (equal distribution)

2. **Method: `setProportions(double... proportions)`**
   - Accept a variable number of proportion values
   - Store these proportions for use during simulation
   - Assumption: number of proportions = number of outputs
   - Assumption: sum of proportions = 1.0

3. **Connection Method**
   - Override `connect(Element elem, int index)`
   - Allow connecting elements to any output index (0 to numOutput-1)
   - Store the connection in the appropriate position

4. **Method: `getOutputs()`**
   - Return an array containing all connected output elements
   - Array size = number of outputs specified in constructor

5. **Flow Simulation**
   - For each output i: output_flow[i] = input_flow × proportion[i]
   - Notify observer with all output flows
   - Continue simulation for all connected downstream elements

---

## Task R6: Element Deletion

### What This Task Is About
Implement functionality to remove elements from the system while maintaining correct connections.

### What You Must Implement

1. **Method: `HSystem.deleteElement(String name)`**
   - Find and remove the element with the given name
   - Return `true` if deletion was successful, `false` otherwise

2. **Deletion Rules**
   - **Cannot delete** Split or Multisplit if it has MORE than 1 connected output
   - Can delete if 0 or 1 outputs are connected
   - Can delete any other type of element

3. **Connection Rewiring**
   - Find the element upstream (connected TO the element being deleted)
   - Find the element downstream (connected FROM the element being deleted)
   - Connect upstream directly to downstream, bypassing the deleted element
   - Maintain the correct output index if the upstream element is a Split/Multisplit

4. **System Array Update**
   - Remove the element from the internal storage array
   - Shift remaining elements to fill the gap
   - Update the element count

---

## Task R7: Maximum Flow Check

### What This Task Is About
Add safety checks to ensure elements don't receive more flow than they can handle.

### What You Must Implement

1. **Method: `Element.setMaxFlow(double maxFlow)`**
   - Store the maximum allowable input flow for an element
   - Default value: no limit (infinite)
   - **Exception**: Source elements should IGNORE this method (no effect)

2. **Method: `HSystem.simulate(SimulationObserver observer, boolean enableMaxFlowCheck)`**
   - Enhanced version of simulate
   - If `enableMaxFlowCheck` is `false`: behaves like normal simulation
   - If `enableMaxFlowCheck` is `true`: perform flow validation

3. **Flow Validation Logic**
   - For each element (except Source), before processing:
   - Check if input_flow > maxFlow
   - If exceeded: call `observer.notifyFlowError(type, name, inFlow, maxFlow)`
   - Continue simulation even after detecting errors

4. **Implementation Requirements**
   - Add the check in each element's `simulate()` method
   - Pass the `enableMaxFlowCheck` flag through the entire simulation chain
   - Perform checks for: Tap, Split, Multisplit, Sink
   - Do NOT check for Source (sources generate flow, don't receive it)

---

## Task R8: Fluent API Builder

### What This Task Is About
Create a convenient builder pattern that allows constructing hydraulic systems using method chaining.

### What You Must Implement

1. **Static Method: `HSystem.build()`**
   - Return a new `HBuilder` instance
   - This is the entry point for the fluent API

2. **HBuilder Class - Element Creation Methods**
   - `addSource(String name)`: Create and add a source
   - `linkToTap(String name)`: Create tap and link to previous element
   - `linkToSink(String name)`: Create sink and link to previous element
   - `linkToSplit(String name)`: Create split and link to previous element
   - `linkToMultisplit(String name, int numOutput)`: Create multisplit and link to previous element
   - All methods must return `this` (the builder) for chaining

3. **HBuilder Class - Configuration Methods**
   - `withFlow(double flow)`: Set flow for the last added source
   - `open()`: Set the last added tap to open
   - `closed()`: Set the last added tap to closed
   - `withProportions(double... props)`: Set proportions for last added multisplit (note the typo in method name)
   - `maxFlow(double max)`: Set max flow for the last added element
   - All methods must return `this` for chaining

4. **HBuilder Class - Branch Management Methods**
   - `withOutputs()`: Begin defining outputs for the last split/multisplit
   - `then()`: Move to the next output of the current split/multisplit
   - `done()`: Finish defining outputs, return to the element before the split/multisplit
   - All methods must return `this` for chaining

5. **Method: `complete()`**
   - Return the fully constructed `HSystem` object

6. **Implementation Requirements**
   - Track the last element added for automatic connection
   - Maintain a stack structure to handle nested splits (without using java.util.Stack)
   - Handle output indexing for splits/multisplits
   - Automatically connect elements as they are added
   - Connect split/multisplit outputs based on current output index

7. **Branching Behavior**
   - After `withOutputs()`: next elements connect to first output
   - After `then()`: next elements connect to next output
   - After `done()`: continue from element before split/multisplit
   - Example structure management for nested splits

8. **NO IMPORTS ALLOWED**
   - Cannot use any Java utility classes (Stack, ArrayList, etc.)
   - Must implement your own stack using arrays
   - Maximum nesting depth assumption: reasonable limit (e.g., 10 levels)

---

## Usage Example

Here's how the completed system should work:

```java
// Traditional approach (R1-R7)
HSystem s = new HSystem();
Source src = new Source("Src");
s.addElement(src);
Tap r = new Tap("R");
s.addElement(r);
Sink sink = new Sink("Sink");
s.addElement(sink);

src.connect(r);
r.connect(sink);

src.setFlow(20);
r.setOpen(true);

s.simulate(new PrintObserver());

// Fluent API approach (R8)
HSystem system = HSystem.build()
    .addSource("Src").withFlow(20)
    .linkToTap("R").open()
    .linkToSink("Sink")
    .complete();
    
system.simulate(new PrintObserver());
```

---

## Important Notes

- The `Element` class is abstract - you don't instantiate it directly
- Each concrete element class extends `Element`
- The `SimulationObserver` is an interface - you'll receive implementations for testing
- Pay attention to special cases (Sink cannot connect, Source ignores maxFlow)
- Maintain proper flow computation according to element type
- Ensure all builder methods return `this` for chaining
- Handle edge cases (null connections, array bounds, stack overflow)

---

## Grading Criteria

Each requirement (R1-R8) will be tested independently. Make sure:
- Methods have correct signatures
- Return types match specifications
- Behavior matches requirements exactly
- Observer notifications use correct parameters
- Connections work properly for all element types
- Deletion handles all cases correctly
- Builder creates equivalent systems to manual construction
- No imports are used in the builder class