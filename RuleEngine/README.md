# ⚙️ Rule Engine – Low Level Design (LLD)

---

# 1️⃣ Requirements

## Functional Requirements

| Category        | Requirement |
|-----------------|------------|
| Core Ops        | Add rule |
| Core Ops        | Remove rule |
| Core Ops        | Enable / Disable rule |
| Core Ops        | Evaluate rules against input data |
| Execution       | Support priority-based rule execution |
| Execution       | Stop on first match (optional) |
| Execution       | Execute all matching rules |
| Rule Definition | Support conditional expressions (AND / OR / NOT) |
| Rule Definition | Support comparison operators (=, >, <, IN, etc.) |
| Action          | Trigger action when rule matches |
| Action          | Support multiple action types |
| Extensibility   | Add new operators without modifying core engine |

---

## Non-Functional Requirements

| Category     | Requirement |
|--------------|------------|
| Performance  | Low-latency rule evaluation |
| Scalability  | Support thousands of rules |
| Concurrency  | Multiple concurrent evaluations |
| Extensibility| Add new condition types easily |
| Reliability  | Deterministic execution |
| Maintainability | Clear separation between rule definition & execution |

---

# 2️⃣ Core Entities

| Entity            | Responsibility |
|------------------|---------------|
| RuleEngine        | Orchestrates rule evaluation |
| Rule              | Contains condition + action |
| Condition (interface) | Evaluates boolean logic |
| CompositeCondition | Combines multiple conditions (AND/OR) |
| AtomicCondition   | Single comparison |
| Action (interface)| Executes business logic |
| Context           | Input data for evaluation |
| RuleRepository    | Stores rules |

---

## Key Relationships

### Inheritance
- `Condition` → `AtomicCondition`
- `Condition` → `CompositeCondition`
- `Action` → `ConcreteAction`

### Composition
- `Rule` contains `Condition`
- `Rule` contains `Action`
- `CompositeCondition` contains `List<Condition>`

### Aggregation
- `RuleEngine` contains `List<Rule>`

---

# 3️⃣ Design Patterns – Must Have

| Design Pattern | Used For | Example | Benefit |
|----------------|----------|----------|----------|
| Composite | Nested rule conditions | AND/OR tree | Flexible condition trees |
| Strategy | Different actions | EmailAction, DiscountAction | Pluggable behaviors |
| Interpreter | Rule expression evaluation | Expression tree evaluation | Structured parsing logic |
| Chain of Responsibility | Sequential rule processing | Rule execution pipeline | Flexible execution control |

---

# 4️⃣ Design Patterns – Good to Have

| Design Pattern | Used For | Example | Benefit |
|----------------|----------|----------|----------|
| Factory | Condition creation | ConditionFactory | Decouples parsing from logic |
| Observer | Event notification | RuleTriggeredListener | Decoupled monitoring |
| Command | Action encapsulation | ExecuteActionCommand | Enables logging / undo |
| Decorator | Add logging / metrics | LoggingRuleDecorator | Extend without modifying core |

---

# 5️⃣ Key Design Decisions

### 1. Composite for Conditions
Allows complex nested conditions:
- `(A AND B) OR (C AND (D OR E))`

### 2. Interpreter Pattern
Represents rule expressions as evaluatable trees.

### 3. Strategy for Actions
Actions are decoupled from rule evaluation logic.

### 4. Rule Execution Model
Two modes:
- First match wins
- Execute all matches

Make execution strategy configurable.

### 5. Important Invariants
- Rule evaluation must be deterministic
- Rule priority ordering must be preserved
- Disabled rules are skipped
- Context is immutable during evaluation

---

# 6️⃣ Concurrency Handling

## What Can Go Wrong
- Rules being modified while evaluating
- Concurrent enable/disable operations
- Shared mutable context

## Locking Strategy

### Read-Write Lock on RuleRepository
- Reads (evaluation) are frequent
- Writes (add/remove rules) are rare

### Immutable Rule Objects
- Rules should be immutable once created

### Atomic Rule Update
- Replace entire rule list reference (copy-on-write)

---

## Deadlock Prevention
- Avoid nested locks
- Single lock at repository level
- Keep evaluation stateless

---

# 7️⃣ Common Interview Extensions

- Add rule priority levels
- Add rule grouping
- Add rule versioning
- Add dynamic rule loading (JSON / DSL)
- Add rule audit logs
- Add rule caching
- Add distributed rule engine
- Add rule conflict detection
- Add scheduling (time-based rules)

---

# 8️⃣ Where Interviewers Try to Push

- How would you support 100K rules?
- How to optimize evaluation performance?
- Can you short-circuit evaluation?
- How to handle conflicting rules?
- How to design rule DSL?
- How to scale rule engine horizontally?
- How to persist rules safely?
- What pattern alternative to Interpreter?
- How to add backward compatibility for rules?

---