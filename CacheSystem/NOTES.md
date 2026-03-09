## Learnings

1. What improvements would you add to a basic LRU cache?
> Make the cache thread-safe using segmented locks, support size-based eviction instead of entry count limits, and scale it using segmented caches for concurrency. At larger scale, the cache could be distributed across multiple nodes using consistent hashing for key distribution.

## Go Deeper

1. W-TinyLFU policy used in caffeine cache