# 🗄 Cache System – Low Level Design (LLD)

---

# 1️⃣ Requirements

## Functional Requirements

| Category        | Requirement |
|-----------------|------------|
| Core Ops        | Put (key, value) |
| Core Ops        | Get (key) |
| Core Ops        | Delete (key) |
| Eviction        | Support eviction when capacity exceeded |
| Eviction        | Configurable eviction policy (LRU / LFU / FIFO) |
| Expiry          | Support TTL (time-to-live) |
| Expiry          | Auto-expire keys |
| Capacity        | Fixed maximum capacity |
| Metrics         | Track hit rate / miss rate |
| Thread Safety   | Concurrent get/put support |

---

## Non-Functional Requirements

| Category     | Requirement |
|--------------|------------|
| Performance  | O(1) get and put |
| Scalability  | Support large number of keys |
| Concurrency  | High concurrent reads/writes |
| Reliability  | No inconsistent eviction |
| Extensibility| Easy addition of new eviction policies |
| Memory       | Memory-efficient storage |

---

# 2️⃣ Core Entities

| Entity              | Responsibility |
|---------------------|---------------|
| Cache               | Main interface |
| CacheEntry          | Stores key, value, metadata |
| EvictionPolicy      | Interface for eviction logic |
| LRUEvictionPolicy   | LRU implementation |
| LFUEvictionPolicy   | LFU implementation |
| ExpiryManager       | Handles TTL expiration |
| DoublyLinkedList    | Maintains access order (for LRU) |
| CacheStorage        | Key → Entry map |

---

## Key Relationships

### Composition
- `Cache` contains `CacheStorage`
- `Cache` contains `EvictionPolicy`
- `Cache` contains `ExpiryManager`
- `EvictionPolicy` may use `DoublyLinkedList`

### Inheritance
- `EvictionPolicy` → `LRUEvictionPolicy`
- `EvictionPolicy` → `LFUEvictionPolicy`

---

# 3️⃣ Design Patterns – Must Have

| Design Pattern | Used For | Example | Benefit |
|----------------|----------|----------|----------|
| Strategy | Eviction policy selection | LRU / LFU | Plug-and-play eviction logic |
| Factory | Policy creation | EvictionPolicyFactory | Clean instantiation |
| Singleton | Cache instance | Global cache | Single shared cache |
| Template Method | Cache workflow | get() / put() flow | Standardized steps |

---

# 4️⃣ Design Patterns – Good to Have

| Design Pattern | Used For | Example | Benefit |
|----------------|----------|----------|----------|
| Observer | Cache eviction notifications | EvictionListener | Monitoring hooks |
| Decorator | Add logging / metrics | LoggingCacheDecorator | Extend behavior cleanly |
| Proxy | Lazy loading | LoadingCacheProxy | Auto-fetch on miss |
| Flyweight | Shared metadata objects | Shared config | Memory optimization |

---

# 5️⃣ Key Design Decisions

### 1. O(1) Operations
Use:
- HashMap for key lookup
- DoublyLinkedList for order tracking (LRU)

### 2. Eviction Strategy via Strategy Pattern
Cache should not know eviction details.

### 3. TTL Handling
Options:
- Lazy expiration (check during get)
- Background cleaner thread
- Min-heap ordered by expiry

### 4. Important Invariants
- Size ≤ capacity
- Eviction must remove exactly one entry
- Expired entries must not be returned
- HashMap & linked list must stay consistent

### 5. Memory Tradeoff
- DoublyLinkedList gives O(1) but increases memory usage.
- LFU needs frequency map → more memory.

---

# 6️⃣ Concurrency Handling

## What Can Go Wrong
- Two threads evicting simultaneously
- Concurrent put on same key
- Removing entry while being accessed
- Inconsistent list-map state

## Locking Strategy

### Option 1: Global Lock
- Simple but low concurrency

### Option 2: Fine-Grained Locking
- Lock per key
- Lock list operations

### Option 3: Read-Write Lock
- Multiple reads allowed
- Writes exclusive

### Best Approach (Production-Grade)
- Use ConcurrentHashMap
- Use segment-level locking
- Lock only eviction-critical sections

---

## Deadlock Prevention
- Avoid nested locks
- Maintain lock acquisition order
- Keep lock scope minimal

---

## Atomic Operations

Put:
1. Lock
2. If exists → update
3. Else insert
4. If capacity exceeded → evict
5. Unlock

---

# 7️⃣ Common Interview Extensions

- Add distributed cache
- Add consistent hashing
- Add write-through / write-back
- Add read-through cache
- Add replication
- Add persistence
- Add cache warming
- Add eviction statistics
- Add compression

---

# 8️⃣ Where Interviewers Try to Push

- How to ensure O(1) operations?
- How to implement LFU efficiently?
- What happens under heavy concurrency?
- How to scale cache horizontally?
- How to handle cache stampede?
- How to design distributed cache?
- What tradeoffs between LRU and LFU?
- How to handle memory fragmentation?
- How to handle hot keys?
- How to ensure thread safety without hurting performance?

---