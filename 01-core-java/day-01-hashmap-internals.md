# Day 1 – HashMap Internals and Frequency Counting

## Learning outcomes
By the end of this session, be able to:
1. Explain how a `HashMap` stores and retrieves entries.
2. Explain hashing, bucket indexing and collisions.
3. Explain why `equals()` and `hashCode()` must agree.
4. Implement a frequency counter and analyse its complexity.
5. Explain why `HashMap` is not safe for concurrent mutation.

## 1. Why HashMap?
A `HashMap<K,V>` stores key-value pairs and is useful when lookup by key is frequent.

- Expected average `get` / `put`: O(1), assuming a good hash distribution.
- Worst-case behaviour depends on collisions and implementation details.
- Iteration over entries is not a substitute for an ordered collection.
- `HashMap` permits one null key and null values; it does not guarantee iteration order.
- It is not thread-safe for concurrent writes.

Compare:
- `ArrayList`: efficient indexed access, lookup by value is generally O(n).
- `HashMap`: lookup by key is expected O(1) average.
- `TreeMap`: sorted keys, O(log n) basic operations.

## 2. Internal model
A HashMap maintains a bucket array. A key's hash is transformed, then used to choose a bucket. Each bucket can hold:
- no entry,
- one node,
- a linked chain of nodes after collisions,
- or a balanced tree bin in sufficiently large collision cases.

In OpenJDK implementations, the spread operation is conceptually:

```java
int hash = key.hashCode();
int spreadHash = hash ^ (hash >>> 16);
int index = (capacity - 1) & spreadHash;
```

The exact implementation is version-specific. The capacity is kept as a power of two in standard OpenJDK HashMap implementations, which makes the mask operation efficient.

### Collision
Two different keys can map to the same bucket. HashMap must then compare keys using equality to find the matching mapping. A collision is not necessarily a bug; a poor hash distribution can, however, hurt performance.

## 3. Capacity, load factor and tree bins
Common OpenJDK defaults:
- Default initial capacity: 16 (allocated lazily in common implementations).
- Default load factor: 0.75.
- At capacity 16 and load factor 0.75, resize threshold is 12.
- Treeification threshold is commonly 8, with minimum table capacity 64; exact details are implementation-specific.

When resizing, entries are redistributed into the larger table. Resizing is relatively expensive, so repeated growth can create latency spikes.

Do not present these implementation constants as Java language guarantees; verify against the JDK version being discussed.

## 4. equals() and hashCode()
Contract:
- If `a.equals(b)` is true, `a.hashCode()` must equal `b.hashCode()`.
- Equal hash codes do not imply equal objects.
- Fields used in equality/hash code should not be mutated while an object is used as a map key.

If a key's hash-relevant fields change after insertion, a later lookup may search a different bucket and fail to find the existing entry.

## 5. Coding exercise – word frequency

Runnable example: `../02-coding-interviews/java/WordFrequency.java`

```java
Map<String, Integer> frequency = new HashMap<>();
for (String word : words) {
    frequency.merge(word, 1, Integer::sum);
}
```

Let n be the number of words and k the number of distinct words:
- Expected time: O(n) under normal hash distribution.
- Space: O(k).

For production text processing, also consider normalisation, case sensitivity, punctuation, nulls, input size and whether the entire input can fit in memory.

## 6. Production scenario – concurrent counters
Suppose many request threads update a shared frequency map.

A plain `HashMap` is unsafe for concurrent writes. Even replacing it with `ConcurrentHashMap<String, Integer>` does not make this compound operation atomic:

```java
map.put(key, map.getOrDefault(key, 0) + 1); // race condition
```

Use an atomic map operation:

```java
concurrentMap.merge(key, 1, Integer::sum);
```

For high-contention counters, evaluate `ConcurrentHashMap<K, LongAdder>`; choose based on semantics and read consistency needs. `ConcurrentHashMap` does not make a multi-step business transaction atomic.

## 7. Interview questions
1. How does HashMap find a value from a key?
2. What happens when two keys have the same hash?
3. Why must equals() and hashCode() be consistent?
4. What happens if a key is mutated after insertion?
5. Why is HashMap unsafe for concurrent updates? Is ConcurrentHashMap always sufficient?
6. What is the impact of resizing?
7. When would you choose TreeMap or LinkedHashMap instead?

## 8. Strong production answer
**Question:** A service has a shared in-memory counter updated by many request threads. What would you do?

**Answer structure:**
1. Establish whether the counter is local to one JVM or must be shared across instances.
2. For a single JVM, use `ConcurrentHashMap` with `merge` or `LongAdder` where suitable.
3. For multiple service instances, an in-memory map is not globally authoritative; use a datastore or messaging/aggregation design appropriate to durability and consistency requirements.
4. Define overflow, retention, reset and read-consistency behaviour.
5. Load-test contention and monitor latency before choosing a more complex design.

## 9. Day 1 completion checklist
- [ ] Explain bucket lookup and collisions without notes.
- [ ] Run the word-frequency program.
- [ ] Explain O(n) expected time and O(k) space.
- [ ] Explain the concurrent update race.
- [ ] Answer at least four interview questions aloud.

## Reflection
- What was easiest?
- Which part needs another explanation?
- Can you explain the concurrent counter scenario in 60 seconds?
