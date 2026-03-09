# 🧠 LLD Design Template – Interview Format

Design a **Elevator System** using Low-Level Design principles.

Structure the answer using the following format.

---

# 1️⃣ Clarifying Requirements

**Problem Statement:** Design an elevator system for a multi-floor building that efficiently handles passenger requests from inside and outside the elevators, with support for multiple elevators and optimized scheduling.

| Question | Why it Matters |
|--------|---------------|
| Who are the actors interacting with the system? | Identifies key entities (passengers, maintenance staff) |
| What are the primary operations supported? | Defines APIs (request floor, move elevator, open/close doors) |
| What constraints or limits exist? | Influences design decisions (capacity, floor count) |
| Is the system single-node or distributed? | Impacts concurrency and storage (single building, centralized control) |
| Expected scale (users, requests, data)? | Guides performance and scalability decisions (high-rise building, peak hours) |
| Failure handling expectations? | Impacts reliability strategies (emergency stops, breakdowns) |
| Security or access control requirements? | Influences authorization design (none specified, assume open access) |

**Assumptions:** Building has 10 floors (0-9), 3 elevators, each with capacity of 10 persons. System handles concurrent requests, prioritizes efficiency, and supports emergency modes.

---

# 2️⃣ Requirements

## Functional Requirements

| Category | Requirement |
|----------|------------|
| Core | Handle external floor requests (up/down buttons) |
| Core | Handle internal cabin requests (floor buttons) |
| Core | Move elevators to requested floors efficiently |
| Core | Open/close doors at stops |
| Edge Case | Handle emergency stop and maintenance modes |
| Edge Case | Prevent over-capacity boarding |

---

## Non-Functional Requirements

| Category | Requirement |
|----------|------------|
| Performance | Minimize average wait time (<30 seconds) |
| Scalability | Support up to 20 floors and 5 elevators |
| Concurrency | Handle multiple simultaneous requests |
| Security | No specific security requirements |
| Reliability | No request loss, graceful failure handling |
| Maintainability | Extensible scheduling algorithms |

---

# 3️⃣ Core Entities

Identify the primary domain entities in the system.

| Entity | Responsibility |
|--------|---------------|
| ElevatorSystem | Manages all elevators and coordinates requests |
| Elevator | Represents a single elevator's state and operations |
| ElevatorController | Controls movement and door logic |
| Request | Represents a floor request (internal or external) |
| Floor | Represents a building floor with buttons |
| Scheduler | Assigns requests to elevators |
| Button | Triggers requests (inside/outside) |
| Door | Manages opening and closing |

Also describe key relationships:

- **Composition**: ElevatorSystem composes Elevators and Scheduler
- **Aggregation**: Elevator aggregates Requests and Door
- **Inheritance**: Buttons can be specialized (UpButton, DownButton)

---

# 4️⃣ Minimal Working LLD (Start Simple)

Design the **simplest working system first**.

Focus on:

- Core classes: ElevatorSystem, Elevator, Request
- Basic API: requestFloor(), moveToFloor()
- Simplifying assumptions: Single elevator, no concurrency, FCFS scheduling

Goal: demonstrate a **functional baseline design**, then evolve it.

Example conceptual structure:

    ElevatorSystem
     ├── Elevator
     └── List<Request>

Explain how a typical request flows through the system:

1. Passenger presses floor button → Request created
2. ElevatorSystem assigns to elevator
3. Elevator moves to floor → Doors open → Passenger boards

---

# 5️⃣ SOLID Principles Applied

Explain how the baseline design respects SOLID principles.

| Principle | Application |
|----------|-------------|
| Single Responsibility | Elevator handles movement, Request represents data |
| Open/Closed | New request types can be added without changing core logic |
| Liskov Substitution | Any Elevator can be used in ElevatorSystem |
| Interface Segregation | Separate interfaces for movement and scheduling |
| Dependency Inversion | ElevatorSystem depends on abstractions, not concretions |

This ensures the system can **evolve safely**.

---

# 6️⃣ Identify Limitations of the Initial Design

Analyze weaknesses in the minimal design.

| Limitation | Impact |
|-----------|--------|
| Single elevator | Cannot handle high traffic |
| No concurrency handling | Race conditions in multi-threaded environment |
| Hardcoded FCFS scheduling | Inefficient for complex scenarios |
| No state management | Unsafe transitions (e.g., door open while moving) |
| Tight coupling | Hard to add features like emergency modes |

This step demonstrates **critical design thinking**.

---

# 7️⃣ Incremental Design Improvements

Improve the design step-by-step.

For each improvement:

| Problem | Improvement | Pattern Introduced | Tradeoff |
|--------|-------------|--------------------|----------|
| Single elevator | Add multiple elevators | Singleton for system management | Increased complexity |
| No concurrency | Add thread-safe queues | Observer for request notifications | Slight performance overhead |
| Hardcoded scheduling | Pluggable strategies | Strategy pattern | More classes |
| No state management | State machine for elevator | State pattern | Additional state classes |
| Tight coupling | Interface-based design | Dependency Inversion | Abstraction layers |

Explain how each improvement **addresses a limitation** while introducing **tradeoffs**.

---

# 8️⃣ Final Design Overview

Summarize the final evolved design.

Include:

- Main components: ElevatorSystem (Singleton), Elevators with State, Scheduler with Strategies
- Key abstractions: IElevator, IScheduler, IRequest
- Interaction flow: Request → Scheduler → Assigned Elevator → State Transitions → Movement
- Extensibility points: New scheduling algorithms, additional states, custom controllers

---

# 9️⃣ Key Design Decisions & Tradeoffs

Explain major architectural choices.

| Decision | Reason | Tradeoff |
|---------|--------|----------|
| State pattern for elevator behavior | Ensures safe transitions and prevents invalid states | Adds multiple state classes |
| Strategy for scheduling | Allows pluggable algorithms (FCFS, SCAN, etc.) | Requires strategy interface and implementations |
| Observer for button presses | Decouples request generation from handling | Event-driven complexity |
| Thread-safe queues | Handles concurrent requests reliably | Synchronization overhead |

---

# 🔟 Concurrency Handling

Explain how concurrent access is handled.

Discuss:

- Possible race conditions: Multiple requests modifying elevator queues
- Locking strategy: Per-elevator locks for state changes, concurrent queues for requests
- Deadlock prevention: Acquire locks in consistent order
- Atomic operations: Use of ConcurrentLinkedQueue for request addition

---

# 1️⃣1️⃣ Common Interview Extensions

List realistic features interviewers may ask next.

Examples:

- Add support for priority requests (VIP floors)
- Introduce configuration for different building layouts
- Handle failure scenarios such as elevator breakdown
- Support additional entity types like maintenance schedules
- Improve performance with predictive algorithms

---

# 1️⃣2️⃣ Where Interviewers Push Deeper

List conceptual deep-dive questions.

Examples:

- How would this scale to millions of users in a skyscraper?
- What happens if a component fails mid-operation?
- How would this work in a distributed system across buildings?
- What edge cases could break the design (e.g., all elevators at same floor)?
- What alternative design approaches exist (e.g., event-driven vs. polling)?

---

# 1️⃣3️⃣ Optional: Class Skeleton (Conceptual)

Provide high-level interfaces and classes.

    interface IElevator {
        void moveToFloor(int floor);
        void openDoor();
        void closeDoor();
    }

    interface IScheduler {
        IElevator assignRequest(IRequest request);
    }

    class ElevatorSystem {
        List<IElevator> elevators;
        IScheduler scheduler;
    }

    enum ElevatorState {
        IDLE, MOVING_UP, MOVING_DOWN, DOOR_OPEN
    }

Avoid full implementation — focus on **structure and relationships**.

---

# Core Philosophy of This Template

Design evolves incrementally:

    Clarify
       ↓
    Define Requirements
       ↓
    Build Simple LLD
       ↓
    Identify Limitations
       ↓
    Improve Design
       ↓
    Introduce Patterns
       ↓
    Discuss Tradeoffs
       ↓
    Scale & Extend

This mirrors how **real-world engineers evolve systems** rather than designing everything upfront.
