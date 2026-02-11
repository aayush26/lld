# 🗂 File System – Low Level Design (LLD)

---

# 1️⃣ Requirements

## Functional Requirements

| Category     | Requirement |
|--------------|------------|
| Core Ops     | Create file |
| Core Ops     | Create directory |
| Core Ops     | Delete file |
| Core Ops     | Delete directory |
| Core Ops     | Move file / directory |
| Core Ops     | Rename file / directory |
| Navigation   | List directory contents |
| Navigation   | Get full path |
| Navigation   | Get parent directory |
| File Ops     | Read file |
| File Ops     | Write to file (overwrite) |
| File Ops     | Append to file |
| Metadata     | Get file size |
| Metadata     | Get creation timestamp |
| Metadata     | Get last modified timestamp |
| Permissions  | Read / Write / Execute access control |
| Search       | Find file by name (within directory) |
| Search       | Recursive search |

---

## Non-Functional Requirements

| Category     | Requirement |
|--------------|------------|
| Performance  | O(1) or O(log n) lookup inside a directory |
| Scalability  | Support millions of files |
| Concurrency  | Multiple users modifying structure safely |
| Security     | Enforce permission validation before operations |
| Reliability  | Atomic move/delete operations |
| Extensibility| Easy addition of symbolic links, versioning, etc. |

---

# 2️⃣ Core Entities

| Entity            | Responsibility |
|------------------|---------------|
| FileSystem        | Entry point, manages root directory |
| Node (abstract)   | Common abstraction for File & Directory |
| File              | Stores content and file-specific behavior |
| Directory         | Contains child nodes |
| Metadata          | Stores size, timestamps, owner |
| Permission        | Encapsulates access rights |
| SearchService     | Handles recursive search logic |

---

## Key Relationships

### Inheritance
- `Node` → `File`
- `Node` → `Directory`

### Composition
- `Directory` contains `Map<String, Node>` (children)
- `Node` contains `Metadata`
- `Node` contains `Permission`

### Aggregation
- `FileSystem` contains root `Directory`

---

# 3️⃣ Design Patterns – Must Have

| Design Pattern | Used For | Example | Benefit |
|----------------|----------|----------|----------|
| Composite | Uniform treatment of File & Directory | `Node` abstraction | Treat leaf & composite uniformly |
| Iterator | Directory traversal | `DirectoryIterator` | Clean traversal logic |
| Factory | Node creation | `NodeFactory.create(type)` | Encapsulated object creation |
| Strategy | Permission validation | `RoleBasedPermissionStrategy` | Flexible access policies |

---

# 4️⃣ Design Patterns – Good to Have

| Design Pattern | Used For | Example | Benefit |
|----------------|----------|----------|----------|
| Observer | File change notification | Watch service | Decoupled event handling |
| Command | File operations | `MoveCommand`, `DeleteCommand` | Enables undo/redo |
| Decorator | Add compression / encryption | `EncryptedFile` | Extend behavior dynamically |
| Flyweight | Shared metadata objects | Shared permission instances | Memory optimization |

---

# 5️⃣ Key Design Decisions

### 1. Composite Pattern
Allows treating files and directories uniformly via `Node`.

### 2. Directory Children as Map
Using `Map<String, Node>`:
- O(1) lookup
- Prevent duplicate names

### 3. Metadata Separation
Encapsulates:
- Size
- Timestamps
- Owner

Keeps `Node` clean and extensible.

### 4. Atomic Move Design
Move must:
- Validate no cycles
- Check permissions
- Lock source & destination
- Update parent references atomically

### 5. Important Invariants
- Root has no parent
- No directory cycles allowed
- Unique name per directory
- Parent-child consistency always maintained

---

# 6️⃣ Concurrency Handling

## What Can Go Wrong
- Two threads creating same file
- Move + delete conflict
- Concurrent rename
- Partial move leading to inconsistent tree

## Locking Strategy
- Resource-level locking (Directory level)
- Lock parent directory for create/delete
- Lock both source & destination for move

## Deadlock Prevention
- Lock directories in consistent order (top-down path order)
- Or use ordered lock acquisition by unique node ID

## Atomic Operations
Move:
1. Lock source parent
2. Lock destination parent
3. Validate
4. Remove from source
5. Add to destination
6. Release locks

---

# 7️⃣ Common Interview Extensions

- Add symbolic links
- Add soft delete (trash system)
- Add versioning
- Add distributed file system
- Add caching layer
- Add quota per user
- Add indexing for fast search
- Add snapshot support
- Add journaling for crash recovery

---

# 8️⃣ Where Interviewers Try to Push

- How do you prevent cycles?
- How to move atomically?
- What if directory has 1M children?
- How to support millions of files?
- How would you scale this horizontally?
- How to design distributed file system?
- How to handle permission inheritance?
- How to handle concurrent writes?
- What pattern alternative to Composite?
- What are memory optimization techniques?

---
