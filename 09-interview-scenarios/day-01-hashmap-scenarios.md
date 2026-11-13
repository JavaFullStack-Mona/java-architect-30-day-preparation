# Day 1 – Production Scenarios

## Scenario A: Duplicate count is occasionally wrong
**Likely cause:** concurrent read-modify-write updates on a non-thread-safe map or non-atomic compound operation.

**Investigate:** thread count, shared object scope, race reproduction, lost updates, and whether multiple JVM instances are involved.

**Possible fix:** atomic `ConcurrentHashMap.merge` for a single JVM; a shared durable store or stream aggregation for cross-instance requirements.

## Scenario B: HashMap lookup becomes slow
**Investigate:** key hash distribution, collision patterns, mutable keys, map sizing, allocation/GC, and whether a different data structure fits the access pattern.

**Avoid:** assuming every slow lookup is a HashMap collision issue; profile first.

## Scenario C: Memory usage grows continuously
**Investigate:** unbounded map growth, high-cardinality keys, missing eviction/TTL, references preventing collection, and whether the map is intended as a cache.

**Design response:** define capacity, expiry, ownership and observability. A plain HashMap is not a cache with eviction.
