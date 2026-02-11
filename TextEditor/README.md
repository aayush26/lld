# 📝 Text Editor – Low Level Design (LLD)

---

# 1️⃣ Requirements

## Functional Requirements

| Category        | Requirement |
|-----------------|------------|
| Core Ops        | Insert text |
| Core Ops        | Delete text |
| Core Ops        | Replace text |
| Core Ops        | Copy / Paste |
| Undo/Redo       | Undo last operation |
| Undo/Redo       | Redo last undone operation |
| Formatting      | Bold / Italic / Underline |
| Formatting      | Font size / Font family |
| File Ops        | Save document |
| File Ops        | Load document |
| Cursor          | Move cursor |
| Cursor          | Select text range |
| Extensibility   | Add plugins (spell check, auto-format) |

---

## Non-Functional Requirements

| Category     | Requirement |
|--------------|------------|
| Performance  | Low latency editing |
| Scalability  | Handle large documents (MBs of text) |
| Concurrency  | Optional collaborative editing |
| Memory       | Efficient memory usage |
| Extensibility| Plugin-friendly architecture |
| Reliability  | Undo/Redo must be consistent |

---

# 2️⃣ Core Entities

| Entity              | Responsibility |
|---------------------|---------------|
| TextEditor          | Main orchestrator |
| Document            | Holds text data |
| Command (interface) | Represents edit operation |
| InsertCommand       | Insert operation |
| DeleteCommand       | Delete operation |
| ReplaceCommand      | Replace operation |
| CommandManager      | Manages undo/redo stacks |
| Memento             | Snapshot of document state |
| Caretaker           | Stores mementos |
| Formatter           | Applies formatting |
| Plugin              | Extension interface |

---

## Key Relationships

### Inheritance
- `Command` → `InsertCommand`
- `Command` → `DeleteCommand`
- `Plugin` → `SpellCheckPlugin`

### Composition
- `TextEditor` contains `Document`
- `TextEditor` contains `CommandManager`
- `CommandManager` contains `Stack<Command>`
- `Document` may create `Memento`

### Aggregation
- `TextEditor` manages `Plugins`

---

# 3️⃣ Design Patterns – Must Have

| Design Pattern | Used For | Example | Benefit |
|----------------|----------|----------|----------|
| Command | Encapsulate edit operations | InsertCommand | Enables undo/redo |
| Memento | Store document snapshots | DocumentMemento | Restore previous state |
| Observer | UI updates | DocumentChangeListener | Decouples UI from logic |
| Flyweight | Character storage optimization | Shared character styles | Memory efficiency |

---

# 4️⃣ Design Patterns – Good to Have

| Design Pattern | Used For | Example | Benefit |
|----------------|----------|----------|----------|
| Decorator | Add formatting behavior | BoldDecorator | Extend formatting dynamically |
| Strategy | Different save formats | PDFStrategy, TXTStrategy | Flexible export options |
| Prototype | Clone document | Document.clone() | Fast duplication |
| Factory | Command creation | CommandFactory | Clean object creation |

---

# 5️⃣ Key Design Decisions

### 1. Command Pattern as Backbone
Each edit operation is a Command:
- execute()
- undo()

This makes undo/redo trivial.

### 2. Memento for State Backup
Two options:
- Store inverse command logic
- Store full document snapshot

Tradeoff:
- Snapshot = simple but memory heavy
- Inverse command = efficient but complex

### 3. Document Representation

Options:
- StringBuilder (simple)
- Gap Buffer (efficient insert/delete near cursor)
- Rope (efficient for very large documents)

For large-scale editors → Rope is preferred.

### 4. Important Invariants
- Undo stack and redo stack must stay consistent
- After new command → clear redo stack
- Document state must match command history

---

# 6️⃣ Concurrency Handling

## What Can Go Wrong
- Concurrent edits corrupt document
- Undo during active edit
- Plugin modifying document concurrently

## Locking Strategy

Single-threaded model (simplest)

OR

Document-level lock:
- Lock during command execution

For collaborative editing:
- Use Operational Transform (OT)
- Or CRDT-based approach

---

## Deadlock Prevention
- Avoid nested locks
- Keep command execution atomic

---

## Atomic Operation Flow

Execute Command:
1. Lock document
2. Execute command
3. Push to undo stack
4. Clear redo stack
5. Unlock

---

# 7️⃣ Common Interview Extensions

- Add collaborative editing
- Add version history
- Add auto-save
- Add syntax highlighting
- Add macro recording
- Add real-time spell check
- Add document diff viewer
- Add plugin marketplace

---

# 8️⃣ Where Interviewers Try to Push

- How do you optimize for very large files?
- Snapshot vs inverse command – tradeoff?
- How to implement redo correctly?
- How to avoid memory explosion?
- How to support collaborative editing?
- How to persist undo history?
- How to design plugin sandbox?
- How to support rich text formatting?
- What data structure would you use internally?
- How to handle 1M operations?

---