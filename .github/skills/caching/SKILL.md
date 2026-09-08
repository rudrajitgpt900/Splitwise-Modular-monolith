# Caching Skill

## Purpose

Introduce caching only when measurement or workload characteristics justify it.

---

# STEP 1 — Identify Cache Candidate

Determine:

- expensive operation
- frequency
- data volatility
- cacheability
- acceptable staleness

---

# STEP 2 — Choose Cache

Evaluate:

### Local cache

Use when:

- data is local
- consistency requirements are low
- extremely low latency is needed

### Redis

Use when:

- cache must be shared across instances
- distributed invalidation is needed
- TTL management is required

### CDN

Use for:

- static content
- globally distributed read-heavy content

---

# STEP 3 — Define TTL

Every cache must have an explicit TTL unless there is a strong reason otherwise.

---

# STEP 4 — Define Invalidation

Determine:

- write-through
- cache-aside
- write-behind
- explicit invalidation

Default recommendation:

Cache-aside unless requirements dictate otherwise.

---

# STEP 5 — Failure Handling

Cache failure must not automatically bring down the application unless cache is explicitly part of the source of truth.

Example:

Application
   ↓
Redis unavailable
   ↓
Database fallback

where appropriate.

---

# STEP 6 — Prevent Cache Stampede

For high-value keys consider:

- locking
- request coalescing
- jittered TTL
- background refresh

---

# STEP 7 — Monitor

Measure:

- hit ratio
- miss ratio
- latency
- memory
- evictions
- errors

---

# STEP 8 — Cost

Compare:

local memory
vs
Redis
vs
database optimization

Caching must have measurable justification.

---

# Definition of Done

Caching has:

- reason
- strategy
- TTL
- invalidation
- fallback
- failure behavior
- monitoring
- cost consideration