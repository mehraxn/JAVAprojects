# README — Element.java (line-by-line explanation)

This document explains **`Element.java`** (package `hydraulic`) line by line — what each field, method, and section does and why it is present.

---

## File header and purpose

* `package hydraulic;`

  * Places the class inside the `hydraulic` package so other classes in the project can import it.

* Javadoc block

  * Explains at a high level that `Element` is an abstract base class representing any component of the hydraulic system. It describes the intent and mentions the important `connect(Element)` method.

---

## Class declaration and fields

* `public abstract class Element {`

  * Declares an abstract class named `Element`. `abstract` means the class cannot be instantiated directly and may contain abstract methods that subclasses must implement.

* `private String name;`

  * Stores the element's name. It is `private` because direct access should be controlled via methods (encapsulation).

* `protected Element output;`

  * Holds the downstream element for simple elements (the next element connected after this one). It is `protected` so subclasses can access it directly when needed.

* `protected double maxFlow = Double.POSITIVE_INFINITY; // R7: Default is unlimited`

  * Stores the maximum input flow allowed for this element (used for alarm checks). Default is `+infinity` to mean "no limit" unless set otherwise.

---

## Constructor

* `public Element(String name) { this.name = name; }`

  * Saves the provided `name` in the private `name` field. Every concrete element calls this constructor to set its identifier.

---

## Basic getters and connection methods

* `public String getName() { return this.name; }`

  * Returns the element's name.

* `public void connect(Element elem) { this.output = elem; }`

  * Connects this element to a downstream `elem`. For simple elements with a single output, this stores the downstream element in the `output` field.

* `public void connect(Element elem, int index) { ... }`

  * Overload intended for elements that have multiple outputs (splits, multisplits). The default implementation redirects index `0` to the single-output `connect`. Subclasses with multiple outputs override this method to connect specific outputs.

* `public Element getOutput() { return this.output; }`

  * Returns the single downstream element; useful when working with simple elements.

* `public Element[] getOutputs() { ... }`

  * Returns an array of downstream elements. Default behavior returns either an empty array (if no downstream) or an array of length 1 containing the single `output`. Multi-output subclasses override this to return all outputs.

---

## Flow limit setter

* `public void setMaxFlow(double maxFlow) { this.maxFlow = maxFlow; }`

  * Allows configuring the maximum acceptable input flow for the element (used in R7 to generate alarms). `Source` may ignore max flow in simulation logic if required by specification; the field still exists here for uniformity.

---

## Simulation-related abstract methods

* `public abstract void simulate(double inputFlow, SimulationObserver observer, boolean enableMaxFlowCheck);`

  * **Abstract**: each concrete subclass must implement it.
  * Intended behavior: compute the element's input and output flow(s), notify the provided `observer` when flows are available, and, if `enableMaxFlowCheck` is `true`, trigger an observer error notification if the input flow exceeds `maxFlow`.
  * The parameters are:

    * `inputFlow` — the input flow value for this element (for a `Source` this may be unused or marked as missing).
    * `observer` — receives notifications using `notifyFlow(...)` and `notifyFlowError(...)`.
    * `enableMaxFlowCheck` — when `true`, the implementation should check `inputFlow` against `maxFlow` and call the observer's error notification when appropriate.

* `public abstract void layout(SimulationObserver observer, boolean enableMaxFlowCheck);`

  * **Abstract**: each concrete subclass must implement it.
  * Purpose: propagate the simulation to downstream elements. Implementations usually call `simulate(...)` on the current element and then invoke `layout(...)` or `simulate(...)` on downstream elements as appropriate to continue the traversal.

These abstract methods centralize the simulation logic but leave the element-specific behavior to subclasses.

---

## Utility methods for string rendering

* `protected static String pad(String current, String down) { ... }`

  * Helper used by `toString()` to format multiline tree-like diagrams representing the network downstream of this element. It aligns subsequent lines under the current prefix.

* `@Override public String toString() { ... }`

  * Produces a textual representation of the element and its downstream graph. It starts with `[%s] `.formatted(getName()), collects `getOutputs()`, and if outputs are present, recursively appends their `toString()` results with formatting and indentation using `pad`.

---

## How subclasses use `Element`

* **`Source`** implements `simulate` to report its output flow and to start the propagation to its downstream element.
* **`Tap`** implements `simulate` to use its open/closed state to produce either the same input as output or zero output; it will notify using the observer and then push the result downstream.
* **`Sink`** implements `simulate` to report only the input flow (no output) and stop.
* **`Split`** implements `simulate` and `layout` to divide input into two equal outputs and propagate them to both downstream outputs.
* **`Multisplit`** overrides connect, `getOutputs`, and provides `setProportions` / `getProportions` and its `simulate` divides the input according to proportions then propagates.

---

## Design rationale (brief)

* Use of an abstract base class allows polymorphic handling of different elements during simulation: `HSystem` can call `simulate` on any `Element` reference, and the concrete subclass performs the correct behavior.
* `protected` fields (`output`, `maxFlow`) make light-weight subclass implementations simpler while maintaining encapsulation from outside packages.
* Default implementations for single-output behavior make simple elements trivial to implement and allow multi-output elements to override only the methods they need.

---

## Developer notes / tips

* When you add methods to subclasses that `HSystem` uses directly (e.g. `getFlow()` on `Source`, `isOpen()` on `Tap`, `getProportions()` on `Multisplit`), ensure those methods are present and compiled before compiling classes that reference them.
* Keep `simulate(...)` and `layout(...)` behavior consistent: notify the observer as soon as the element's in/out flows are known and before simulating downstream elements.

---

If you want, I can also prepare a matching `README` for each concrete subclass (`Source`, `Tap`, `Split`, `Multisplit`, `Sink`) explaining exactly how their `simulate` and `layout` implementations should behave and showing minimal sample implementations.
