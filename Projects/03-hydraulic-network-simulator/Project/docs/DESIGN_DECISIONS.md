# Design Decisions

## Java 21 and Maven

Java 21 is an LTS release with broad tooling support. Maven preserves the course project's existing build and legacy `src/` and `test/` source layout. The wrapper makes the selected Maven version reproducible without requiring a global installation.

## Object model and observer

Inheritance keeps connection, identity, and maximum-flow behavior in `Element`, while subclasses own their hydraulic rule. `SimulationObserver` separates flow computation from display or test recording and allows status and safety events to be consumed independently.

## Branch behavior

`Split` always calculates two equal flows, even when one output is disconnected. `Multisplit` validates that finite, non-negative proportions match its output count and sum to one within `1e-9`. Input and returned arrays are copied so callers cannot mutate network state indirectly.

## Deletion contract

A target with zero or one connected output may be removed. All incoming references are rewired to its sole non-null downstream element, or disconnected if none exists. Removing a target with multiple connected outputs returns `false` because choosing one branch would lose meaning. A missing name also returns `false`.

## Simulation scope

The course model assumes a single entry source. If several sources are stored, simulation starts at the first source in insertion order. Disconnected outputs and an empty/no-source system are safe. Cycle detection is included in `HSystem.toString()`; recursive flow simulation still assumes an acyclic network.

## Builder compatibility

The builder uses an unbounded `Deque` of branch frames instead of a fixed-size stack. The original misspelled `withPropotions()` method remains part of the public API for professor-test and client compatibility. `withProportions()` is the preferred correctly spelled alias.

## Trade-offs

The system is deliberately in-memory and educational. It does not model pressure, pipe capacity over time, persistence, concurrency, or deployment concerns. Those features would obscure the OOP and graph-simulation focus.
