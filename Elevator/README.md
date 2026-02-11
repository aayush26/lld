# Elevator System – Low Level Design (LLD)

---

## 1. Requirements

### a. Functional Requirements

| Category | Questions to Ask |
|----------|------------------|
| Elevators | Single elevator or multiple elevators? |
| Floors | How many floors? Basement included? |
| Requests | Inside cabin requests + outside floor requests? |
| Direction | Separate up/down buttons per floor? |
| Scheduling | Simple FCFS or optimized scheduling? |
| Capacity | Max weight / person limit? |
| Emergency | Fire mode / emergency stop handling? |
| Maintenance | Can elevator be taken out of service? |

---

### b. Non-Functional Requirements

| Category | Questions / Constraints |
|----------|------------------------|
| Concurrency | Multiple simultaneous floor requests |
| Performance | Minimize average waiting time |
| Scalability | Support high-rise buildings |
| Reliability | No request loss |
| Extensibility | Add new scheduling strategies |
| Safety | Strict state validation |
| Consistency | Avoid duplicate servicing of same request |

---

## 2. Core Domain Entities

| Entity | Responsibility |
|--------|----------------|
| ElevatorSystem | Manages all elevators |
| Elevator | Represents a single elevator |
| ElevatorController | Controls movement logic |
| Request | Represents floor request |
| Floor | Represents building floor |
| Scheduler | Assigns requests to elevators |
| Button | Inside / outside request trigger |
| Door | Open / close management |

---

## 3. Core System Flows

| Flow | Steps |
|------|-------|
| External Request | Floor button → Create Request → Scheduler assigns elevator |
| Internal Request | Cabin button → Add destination to elevator queue |
| Movement | Elevator → Move toward next destination → Stop → Open door |
| Idle Handling | No requests → Elevator enters IDLE state |
| Emergency | Emergency trigger → Stop elevator → Disable movement |

---

## 4. Design Patterns – Must Have

| Design Pattern | Used For | Example | Why It Is Needed |
|----------------|----------|---------|------------------|
| State | Elevator behavior | IDLE, MOVING_UP, MOVING_DOWN, DOOR_OPEN | Prevent invalid transitions |
| Strategy | Scheduling algorithm | NearestCarStrategy | Pluggable scheduling logic |
| Singleton | System manager | ElevatorSystem | Central coordination |
| Observer | Request handling | Floor button notifies system | Decoupled request generation |

---

## 5. Design Patterns – Good to Have

| Design Pattern | Used For | Example | Benefit |
|----------------|----------|---------|---------|
| Template Method | Movement workflow | Validate → Move → Stop → Open Door | Standardized flow |
| Command | Requests | MoveCommand | Queue-based processing |
| Chain of Responsibility | Elevator selection | Try idle → Try same direction → Try closest | Flexible assignment logic |

---

## 6. Concurrency & Data Consistency

| Area | Strategy |
|------|----------|
| Request queue | Thread-safe priority queue |
| Elevator state | Synchronized state transitions |
| Scheduling | Lock per elevator assignment |
| Duplicate requests | Deduplicate floor requests |
| Movement updates | Single controller thread per elevator |

---

## 7. Edge Cases & Failure Handling

| Scenario | Expected Behavior |
|----------|------------------|
| Over capacity | Reject new passengers |
| Same floor request | Ignore duplicate |
| Power failure | Stop and resume safely |
| Elevator breakdown | Reassign requests |
| Emergency stop | Immediately halt movement |

---

## 8. Explicitly Out of Scope

- Hardware-level motor control
- Real-time OS scheduling
- Distributed building coordination
- UI rendering

---

## 9. One-Line Interview Summary

> “I designed the elevator system as a state-driven model with pluggable scheduling strategies, thread-safe request handling, and controlled state transitions to ensure safety and efficiency.”
