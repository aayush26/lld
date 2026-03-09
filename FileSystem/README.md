# 🗂 File System – Low Level Design (LLD)

---

# 1️⃣ Clarifying Requirements

Start by restating the problem and asking clarifying questions.

| Question | Why it Matters |
|--------|---------------|
| Who are the actors interacting with the system? | Identifies key entities |
| What are the primary operations supported? | Defines APIs |
| What constraints or limits exist? | Influences design decisions |
| Is the system single-node or distributed? | Impacts concurrency and storage |
| Expected scale (users, requests, data)? | Guides performance and scalability decisions |
| Failure handling expectations? | Impacts reliability strategies |
| Security or access control requirements? | Influences authorization design |

If answers are not provided, **state reasonable assumptions before continuing**.

**Assumptions:**
- Actors: Users and applications interacting via API.
- Primary operations: File/directory CRUD, navigation, search.
- Constraints: Single-node system, in-memory for simplicity.
- Scale: Millions of files, concurrent users.
- Failure: Atomic operations, no distributed concerns.
- Security: Basic permission model (read/write/execute).

---

# 2️⃣ Requirements

## Functional Requirements

| Category | Requirement |
|----------|------------|
| Core | Create file |
| Core | Create directory |
| Core | Delete file |
| Core | Delete directory |
| Core | Move file / directory |
| Core | Rename file / directory |
| Navigation | List directory contents |
| Navigation | Get full path |
| Navigation | Get parent directory |
| File Ops | Read file |
| File Ops | Write to file (overwrite) |
| File Ops | Append to file |
| Metadata | Get file size |
| Metadata | Get creation timestamp |
| Metadata | Get last modified timestamp |
| Permissions | Read / Write / Execute access control |
| Search | Find file by name (within directory) |
| Search | Recursive search |

---

## Non-Functional Requirements

| Category | Requirement |
|----------|------------|
| Performance | O(1) or O(log n) lookup inside a directory |
| Scalability | Support millions of files |
| Concurrency | Multiple users modifying structure safely |
| Security | Enforce permission validation before operations |
| Reliability | Atomic move/delete operations |
| Extensibility | Easy addition of symbolic links, versioning, etc. |

---

# 3️⃣ Core Entities

Identify the primary domain entities in the system.

| Entity | Responsibility |
|--------|---------------|
| FileSystem | Entry point, manages root directory |
| Node (abstract) | Common abstraction for File & Directory |
| File | Stores content and file-specific behavior |
| Directory | Contains child nodes |
| Metadata | Stores size, timestamps, owner |
| Permission | Encapsulates access rights |
| SearchService | Handles recursive search logic |

Also describe key relationships:

- **Inheritance**
  - `Node` → `File`
  - `Node` → `Directory`

- **Composition**
  - `Directory` contains `Map<String, Node>` (children)
  - `Node` contains `Metadata`
  - `Node` contains `Permission`

- **Aggregation**
  - `FileSystem` contains root `Directory`

---

# 4️⃣ Minimal Working LLD (Start Simple)

Design the **simplest working system first**.

Focus on:

- Core classes
- Basic API interactions
- Simplifying assumptions

Goal: demonstrate a **functional baseline design**, then evolve it.

Example conceptual structure:

    FileSystem
     ├── Directory (root)
     ├── File
     └── Directory (child)

Explain how a typical request flows through the system.

A typical create file request: FileSystem receives request, validates path, creates File node, adds to parent Directory's children map.

Simplifying assumptions: No permissions, no concurrency, in-memory storage, no metadata beyond name.

---

# 5️⃣ SOLID Principles Applied

Explain how the baseline design respects SOLID principles.

| Principle | Application |
|----------|-------------|
| Single Responsibility | FileSystem manages root; Directory manages children; File manages content |
| Open/Closed | Node abstraction allows adding new node types without changing existing code |
| Liskov Substitution | File and Directory can be treated as Node uniformly |
| Interface Segregation | No interfaces yet, but Node defines common methods |
| Dependency Inversion | High-level modules (FileSystem) don't depend on low-level (File/Directory), but on abstraction (Node) |

This ensures the system can **evolve safely**.

---

# 6️⃣ Identify Limitations of the Initial Design

Analyze weaknesses in the minimal design.

| Limitation | Impact |
|-----------|--------|
| No permissions | Security issues |
| No concurrency control | Race conditions in multi-user scenarios |
| No metadata | Cannot track size, timestamps |
| Hardcoded in-memory | No persistence, limited scalability |
| No search beyond basic | Inefficient for large directories |
| Tight coupling | Difficult to extend with new features |

This step demonstrates **critical design thinking**.

---

# 7️⃣ Incremental Design Improvements

Improve the design step-by-step.

For each improvement:

| Problem | Improvement | Pattern Introduced | Tradeoff |
|--------|-------------|--------------------|----------|
| No permissions | Add Permission class and validation | Strategy (for permission checks) | Slight complexity increase |
| No concurrency | Add locking mechanism | Resource-level locking | Performance overhead |
| No metadata | Introduce Metadata class | Composition | Additional objects |
| Hardcoded storage | Abstract storage layer | Factory (for storage types) | Indirection |
| Basic search | Add SearchService with recursive logic | Iterator (for traversal) | More components |
| Tight coupling | Use Composite pattern fully | Composite | Requires abstraction |

Explain how each improvement **addresses a limitation** while introducing **tradeoffs**.

---

# 8️⃣ Final Design Overview

Summarize the final evolved design.

Include:

- Main components: FileSystem, Node (File/Directory), Metadata, Permission, SearchService
- Key abstractions: Node for uniform treatment
- Interaction flow: Requests go through FileSystem, validated, operations on Nodes
- Extensibility points: Add new Node types, storage backends, permission strategies

---

# 9️⃣ Key Design Decisions & Tradeoffs

Explain major architectural choices.

| Decision | Reason | Tradeoff |
|---------|--------|----------|
| Composite pattern for Node | Uniform file/directory handling | Slight abstraction overhead |
| Map for directory children | O(1) lookup, prevent duplicates | Memory usage for large dirs |
| Separate Metadata/Permission | Clean separation of concerns | More objects |
| Atomic move with locking | Consistency | Concurrency performance |
| Strategy for permissions | Flexible policies | Additional classes |

---

# 🔟 Concurrency Handling

Explain how concurrent access is handled.

Discuss:

- Possible race conditions: Concurrent creates, moves, deletes
- Locking strategy: Directory-level locks for operations
- Deadlock prevention: Consistent lock order (by path)
- Atomic operations: Lock source and dest for move

---

# 1️⃣1️⃣ Common Interview Extensions

List realistic features interviewers may ask next.

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

# 1️⃣2️⃣ Where Interviewers Push Deeper

List conceptual deep-dive questions.

- How do you prevent cycles in moves?
- How to handle millions of files efficiently?
- What if directory has 1M children?
- How to scale horizontally?
- How to design distributed file system?
- How to handle permission inheritance?
- What alternatives to Composite pattern?
- Memory optimization techniques?

---

# 1️⃣3️⃣ Optional: Class Skeleton (Conceptual)

Provide high-level interfaces and classes.

    interface Node {
        String getName();
        String getPath();
        Metadata getMetadata();
        Permission getPermission();
    }

    class File implements Node {
        private String content;
        private Metadata metadata;
        private Permission permission;

        public void write(String data) { /* append or overwrite */ }
        public String read() { return content; }
    }

    class Directory implements Node {
        private Map<String, Node> children = new HashMap<>();
        private Metadata metadata;
        private Permission permission;

        public void addChild(String name, Node node) { children.put(name, node); }
        public Node getChild(String name) { return children.get(name); }
        public List<Node> listChildren() { return new ArrayList<>(children.values()); }
    }

    class FileSystem {
        private Directory root;

        public FileSystem() {
            this.root = new Directory();
            root.addChild("/", root); // self-reference for root
        }

        public void createFile(String path) {
            // Parse path, find parent directory, create File, add to parent
        }

        public void createDirectory(String path) {
            // Similar to createFile but for Directory
        }
    }

Avoid full implementation — focus on **structure and relationships**.
