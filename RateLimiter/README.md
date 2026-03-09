# 🧠 LLD Design – Rate Limiter

Design a **Rate Limiter** that restricts how many requests a client can make within a given time window.

Example rule:

```
100 requests per minute per user
```

If the limit is exceeded → the request is rejected.

---

# 1️⃣ Clarifying Requirements

Start by restating the problem and asking clarifying questions.

| Question | Why it Matters |
|--------|---------------|
| Who are the actors interacting with the system? | Clients (users, API keys, services, IP addresses) |
| What are the primary operations supported? | allowRequest(clientId) |
| What constraints or limits exist? | Requests per time window |
| Is the system single-node or distributed? | Impacts concurrency and storage |
| Expected scale (users, requests, data)? | Guides algorithm and performance decisions |
| Failure handling expectations? | Determines fail-open vs fail-closed |
| Security or access control requirements? | Prevent spoofing client identity |

### Assumptions

- Default limit: **100 requests per minute per client**
- Client is identified via **API key / user ID**
- Initial design assumes **single-node deployment**
- Must handle **concurrent requests**
- Decision latency should be **< 1ms**

---

# 2️⃣ Requirements

## Functional Requirements

| Category | Requirement |
|----------|------------|
| Core | Allow request if under rate limit |
| Core | Reject request if limit exceeded |
| Core | Track request usage per client |
| Core | Support configurable rate limits |
| Edge Case | Handle new clients with no history |
| Edge Case | Reset usage when window expires |

## Non-Functional Requirements

| Category | Requirement |
|----------|------------|
| Performance | Rate check should be extremely fast |
| Scalability | Support millions of clients |
| Concurrency | Handle simultaneous requests safely |
| Security | Prevent spoofed client identifiers |
| Reliability | Limiter failure should not crash application |
| Maintainability | Support multiple rate limiting algorithms |

---

# 3️⃣ Core Entities

| Entity | Responsibility |
|--------|---------------|
| RateLimiter | Entry point to evaluate requests |
| RateLimitRule | Defines limit configuration |
| RateLimitConfigProvider | Retrieves rate limit rules |
| RateLimitStrategy | Defines rate limiting algorithm |
| Storage | Stores counters or tokens |
| RequestCounter | Tracks request count and window |

### Relationships

**Composition**

```
RateLimiter
├── RateLimitConfigProvider
├── RateLimitStrategyFactory
└── Storage
```

**Strategy Pattern**

```
RateLimitStrategy
├── FixedWindowStrategy
├── SlidingWindowStrategy
└── TokenBucketStrategy
```

---

# 4️⃣ Minimal Working LLD (Start Simple)

Start with the **Fixed Window Counter** algorithm.

Example rule:

```
limit = 100
window = 60 seconds
```

Basic storage:

```
Map<ClientId, RequestCounter>
```

Where:

```
RequestCounter
├── count
└── windowStartTime
```

### Request Flow

```
Client Request
↓
RateLimiter.allowRequest(clientId)
↓
Fetch RequestCounter
↓
Check count vs limit
↓
Allow / Reject
```

Minimal structure:

```
RateLimiter
└── Map<ClientId, RequestCounter>
```

This provides a **working baseline design**.

---

# 5️⃣ SOLID Principles Applied

| Principle | Application |
|----------|-------------|
| Single Responsibility | Each class manages one responsibility |
| Open/Closed | New algorithms can be added without modifying RateLimiter |
| Liskov Substitution | Any RateLimitStrategy can replace another |
| Interface Segregation | Clients depend only on necessary interfaces |
| Dependency Inversion | High-level modules depend on abstractions |

---

# 6️⃣ Identify Limitations of the Initial Design

| Limitation | Impact |
|-----------|--------|
| Hardcoded algorithm | Difficult to support other algorithms |
| In-memory counters | Cannot scale across instances |
| No dynamic configuration | Limits must be hardcoded |
| No concurrency protection | Race conditions possible |

Example burst problem:

```
100 requests at 00:59
100 requests at 01:00
```

Result:

```
200 requests within ~1 second
```

This breaks the expected rate limiting behavior.

---

# 7️⃣ Incremental Design Improvements

## Improvement 1 – Introduce Strategy Pattern

| Problem | Improvement | Pattern Introduced | Tradeoff |
|--------|-------------|--------------------|----------|
| Hardcoded algorithm | Strategy abstraction | Strategy | More classes |

Interface:

```
interface RateLimitStrategy {
    boolean allowRequest(clientId, rule)
}
```

Implementations:

```
FixedWindowStrategy
SlidingWindowStrategy
TokenBucketStrategy
```

---

## Improvement 2 – Introduce Configuration Layer

| Problem | Improvement | Pattern | Tradeoff |
|--------|-------------|--------|----------|
| Hardcoded limits | Config provider abstraction | Dependency Inversion | Extra lookup |

Interface:

```
RateLimitConfigProvider
getRule(clientId)
```

Example rule:

```
RateLimitRule
├── limit
├── windowSize
└── algorithm
```

Example configuration:

| Client | Limit | Window | Algorithm |
|------|------|------|-----------|
| user123 | 100 | 60s | TOKEN_BUCKET |
| premiumUser | 500 | 60s | TOKEN_BUCKET |
| anonymousIP | 50 | 60s | FIXED_WINDOW |

---

## Improvement 3 – Strategy Factory

| Problem | Improvement | Pattern | Tradeoff |
|--------|-------------|--------|----------|
| Algorithm selection | Strategy factory | Factory | Slight indirection |

Factory:

```
RateLimitStrategyFactory
create(rule)
```

Mapping example:

```
TOKEN_BUCKET → TokenBucketStrategy
FIXED_WINDOW → FixedWindowStrategy
SLIDING_WINDOW → SlidingWindowStrategy
```

---

## Improvement 4 – Storage Abstraction

| Problem | Improvement | Pattern | Tradeoff |
|--------|-------------|--------|----------|
| In-memory counters | Storage abstraction | Repository | Extra abstraction |

Interface:

```
Storage
get(clientId)
update(clientId)
```

Implementations:

```
InMemoryStorage
RedisStorage
```

---

# 8️⃣ Final Design Overview

Final architecture:

```
RateLimiter
├── RateLimitConfigProvider
├── RateLimitStrategyFactory
├── RateLimitStrategy
└── Storage
```

### Request Flow

```
Client Request
↓
RateLimiter
↓
Fetch RateLimitRule
↓
Create Strategy
↓
Strategy.allowRequest()
↓
Update Storage
↓
Allow / Reject
```

---

# 9️⃣ Key Design Decisions & Tradeoffs

| Decision | Reason | Tradeoff |
|---------|--------|----------|
| Strategy pattern | Support multiple algorithms | Additional abstraction |
| Factory pattern | Dynamic strategy creation | More components |
| Storage abstraction | Enable distributed deployment | Slight complexity |
| Config provider | Dynamic configuration | Requires caching |

---

# 🔟 Concurrency Handling

Potential race condition:

```
Two requests from same client arrive simultaneously
```

Problem:

```
Both read count = 99
Both increment
Limit exceeded incorrectly
```

Solutions:

**Atomic counters**

```
AtomicInteger.incrementAndGet()
```

**Client-level locking**

```
ConcurrentHashMap<ClientId, Lock>
```

**Distributed atomic operations (Redis)**

```
INCR key
EXPIRE key
```

---

# 1️⃣1️⃣ Common Interview Extensions

Interviewers may ask to add:

- Sliding window algorithm
- Token bucket algorithm
- Distributed rate limiting
- IP-based rate limiting
- Endpoint-level limits
- Dynamic rule updates

Example:

```
Standard user → 100 req/min
Premium user → 500 req/min
```

---

# 1️⃣2️⃣ Where Interviewers Push Deeper

Possible deep-dive questions:

- How would this scale to **millions of users**?
- How would this work in a **distributed environment**?
- How do you handle **clock drift**?
- What happens if **storage fails**?
- How to implement **global rate limiting across regions**?

---

# 1️⃣3️⃣ Optional: Class Skeleton (Conceptual)

RateLimiter:

```
class RateLimiter {

    RateLimitConfigProvider configProvider
    RateLimitStrategyFactory strategyFactory
    Storage storage

    boolean allowRequest(clientId)

}
```

RateLimitStrategy:

```
interface RateLimitStrategy {
    boolean allowRequest(clientId, rule)
}
```

RateLimitConfigProvider:

```
interface RateLimitConfigProvider {
    RateLimitRule getRule(clientId)
}
```

Storage:

```
interface Storage {
    RequestCounter get(clientId)
    void update(clientId)
}
```

RateLimitRule:

```
class RateLimitRule {

    int limit
    Duration window
    AlgorithmType algorithm

}
```

---

# Core Philosophy of This Template

```
Clarify requirements
↓
Build simple fixed-window limiter
↓
Identify limitations
↓
Introduce strategy pattern
↓
Add configuration layer
↓
Separate storage
↓
Handle concurrency
↓
Enable extensibility
```
