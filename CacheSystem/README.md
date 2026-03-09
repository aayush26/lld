# 🗄 Cache System – Low Level Design (LLD)

Design a **Cache System** using Low-Level Design principles.

---

# 1️⃣ Clarifying Requirements

Restating the problem:

> Design an in-memory cache system that supports fast key-value operations, eviction policies, TTL expiry, and concurrent access.

| Question | Why it Matters |
|----------|---------------|
| Who are the actors interacting with the system? | Defines who calls the cache APIs |
| What operations should be supported? | Defines the public API |
| What eviction policies should be supported? | Influences internal design |
| Should the cache support TTL/expiration? | Requires expiry tracking |
| What is the expected scale? | Influences storage structures |
| Is the system single-node or distributed? | Affects architecture complexity |
| What concurrency guarantees are required? | Impacts locking strategy |
| Should cache provide metrics? | Requires observability hooks |

### Assumptions

If not specified by interviewer:

- Cache is **in-memory**
- **Single-node cache**
- Fixed **maximum capacity**
- Supports **LRU / LFU / FIFO eviction**
- Supports **TTL expiration**
- **Thread-safe operations**
- **O(1) get/put**

---

# 2️⃣ Requirements

## Functional Requirements

| Category | Requirement |
|----------|------------|
| Core | `put(key, value)` |
| Core | `get(key)` |
| Core | `delete(key)` |
| Eviction | Evict items when capacity exceeded |
| Eviction | Support configurable eviction policies (LRU, LFU, FIFO) |
| Expiry | Support TTL (time-to-live) |
| Expiry | Auto-expire keys |
| Capacity | Fixed maximum capacity |
| Metrics | Track cache hit rate / miss rate |
| Concurrency | Concurrent get/put operations |

---

## Non-Functional Requirements

| Category | Requirement |
|----------|------------|
| Performance | O(1) get and put |
| Scalability | Handle large number of keys |
| Concurrency | High concurrent reads/writes |
| Reliability | No inconsistent eviction |
| Maintainability | Easy to extend eviction policies |
| Memory Efficiency | Avoid excessive memory overhead |

---

# 3️⃣ Core Entities

| Entity | Responsibility |
|--------|---------------|
| Cache | Main cache interface |
| CacheEntry | Stores key, value, metadata |
| CacheStorage | Key → Entry mapping |
| EvictionPolicy | Interface for eviction logic |
| LRUEvictionPolicy | LRU implementation |
| LFUEvictionPolicy | LFU implementation |
| FIFOEvictionPolicy | FIFO implementation |
| ExpiryManager | Handles TTL expiration |
| DoublyLinkedList | Maintains order for LRU |
| MetricsTracker | Tracks cache statistics |

---

## Key Relationships

### Inheritance

```
EvictionPolicy
 ├── LRUEvictionPolicy
 ├── LFUEvictionPolicy
 └── FIFOEvictionPolicy
```

### Composition

```
Cache
 ├── CacheStorage
 ├── EvictionPolicy
 ├── ExpiryManager
 └── MetricsTracker
```

### Aggregation

```
EvictionPolicy
 └── DoublyLinkedList (used for ordering)
```

---

# 4️⃣ Minimal Working LLD (Start Simple)

First design the **simplest working cache**.

### Simplifying Assumptions

- Only **LRU eviction**
- No TTL
- Single-threaded

---

### Core Structure

```
Cache
 ├── HashMap<K, Node>
 └── DoublyLinkedList
```

---

### Data Structures

**HashMap**

```
key → Node
```

**Doubly Linked List**

```
Head (Most Recently Used)
...
Tail (Least Recently Used)
```

---

### Request Flow

### Put

```
1. If key exists:
      update value
      move node to head
2. Else:
      insert node
      add to head
3. If capacity exceeded:
      evict tail node
```

### Get

```
1. Lookup key in HashMap
2. If found:
      move node to head
      return value
3. Else:
      return null
```

---

# 5️⃣ SOLID Principles Applied

| Principle | Application |
|----------|-------------|
| Single Responsibility | Cache, eviction logic, and expiry handled by separate classes |
| Open/Closed | New eviction policies can be added without modifying cache |
| Liskov Substitution | Any EvictionPolicy can replace another |
| Interface Segregation | Cache depends only on minimal policy interface |
| Dependency Inversion | Cache depends on `EvictionPolicy` abstraction |

---

# 6️⃣ Identify Limitations of the Initial Design

| Limitation | Impact |
|-----------|--------|
| Only LRU supported | Cannot support other eviction policies |
| No TTL support | Cannot auto-expire items |
| Cache tightly coupled to eviction logic | Hard to extend |
| Single-threaded design | Unsafe for concurrent use |
| No metrics | Hard to observe performance |

---

# 7️⃣ Incremental Design Improvements

| Problem | Improvement | Pattern Introduced | Tradeoff |
|--------|-------------|--------------------|----------|
| Hardcoded LRU | Introduce EvictionPolicy interface | Strategy | Extra abstraction |
| Difficult policy creation | Use EvictionPolicyFactory | Factory | Slight indirection |
| No TTL | Introduce ExpiryManager | Composition | Background thread cost |
| Multiple responsibilities | Split components | SRP Refactor | More classes |
| No extensibility hooks | Add EvictionListener | Observer | Event overhead |

---

# 8️⃣ Final Design Overview

### Final Architecture

```
Cache
 ├── CacheStorage (ConcurrentHashMap)
 ├── EvictionPolicy (Strategy)
 │     ├── LRUEvictionPolicy
 │     ├── LFUEvictionPolicy
 │     └── FIFOEvictionPolicy
 ├── ExpiryManager
 ├── MetricsTracker
 └── EvictionListener
```

---

### Interaction Flow

**PUT**

```
Client
  ↓
Cache.put()
  ↓
Check existing entry
  ↓
Insert into storage
  ↓
Update eviction policy
  ↓
If capacity exceeded → Evict
```

**GET**

```
Client
  ↓
Cache.get()
  ↓
Check expiry
  ↓
Lookup storage
  ↓
Update eviction policy
  ↓
Return value
```

---

### Extensibility Points

- Add new eviction policies
- Add persistence layer
- Add distributed cache
- Add compression
- Add read-through caching

---

# 9️⃣ Key Design Decisions & Tradeoffs

| Decision | Reason | Tradeoff |
|---------|--------|----------|
| HashMap + LinkedList | Enables O(1) operations | Additional memory |
| Strategy Pattern | Pluggable eviction policies | More classes |
| Composition over inheritance | Flexible behavior | Requires coordination |
| Background expiry cleaner | Efficient TTL handling | Extra thread |

---

# 🔟 Concurrency Handling

### Possible Race Conditions

- Two threads inserting same key
- Eviction during read
- Expiry during update
- Inconsistent list and map state

---

### Locking Strategy

#### Option 1 – Global Lock

```
synchronized cache
```

Pros:
- Simple

Cons:
- Poor concurrency

---

#### Option 2 – Read Write Lock

```
ReadLock → get()
WriteLock → put() / delete()
```

Pros:
- Multiple concurrent reads

Cons:
- Writes block reads

---

#### Option 3 – Production Grade

Use:

```
ConcurrentHashMap
Segment-level locks
Atomic updates
```

---

### Eviction Critical Section

```
lock eviction
  remove entry
  update structures
unlock
```

---

### Deadlock Prevention

- Maintain consistent lock order
- Avoid nested locks
- Keep lock scope minimal

---

# 1️⃣1️⃣ Common Interview Extensions

Interviewers may ask to:

- Add **distributed cache**
- Add **consistent hashing**
- Add **read-through cache**
- Add **write-through cache**
- Add **replication**
- Add **cache persistence**
- Handle **cache stampede**
- Add **eviction statistics**
- Add **cache warming**

---

# 1️⃣2️⃣ Where Interviewers Push Deeper

Typical follow-up questions:

- How do you ensure **O(1)** operations?
- How to implement **LFU efficiently**?
- What happens under **high concurrency**?
- How to scale cache to **multiple nodes**?
- How to prevent **cache stampede**?
- What tradeoffs exist between **LRU and LFU**?
- How do you handle **hot keys**?
- How do you prevent **memory fragmentation**?
- How do you design **Redis-like distributed cache**?

---

# 1️⃣3️⃣ Optional: Class Skeleton (Conceptual)

```java
interface EvictionPolicy<K> {
    void keyAccessed(K key);
    K evictKey();
}

class LRUEvictionPolicy<K> implements EvictionPolicy<K> {
    public void keyAccessed(K key) {}
    public K evictKey() { return null; }
}

class CacheEntry<K, V> {
    K key;
    V value;
    long expiryTime;
}

class Cache<K, V> {

    private Map<K, CacheEntry<K,V>> storage;
    private EvictionPolicy<K> evictionPolicy;

    public V get(K key) {}

    public void put(K key, V value) {}

    public void delete(K key) {}
}
```

---