# Database Skill

## Purpose

Select and implement persistence architecture based on workload rather than preference.

---

# STEP 1 — Understand Workload

Determine:

- reads/sec
- writes/sec
- peak traffic
- data size
- growth rate
- transaction requirements
- consistency
- query complexity

---

# STEP 2 — Select Database

Evaluate:

PostgreSQL
MySQL
SQL Server
MongoDB
DynamoDB
other appropriate stores

Do not choose NoSQL merely because scalability is mentioned.

---

# STEP 3 — Schema Design

Define:

- tables/collections
- primary keys
- foreign keys
- indexes
- constraints
- unique constraints

Avoid unnecessary normalization/denormalization.

---

# STEP 4 — Query Design

For every important query inspect:

- execution plan
- indexes
- selectivity
- joins
- pagination
- N+1 behavior

Never solve a slow query by immediately adding cache.

---

# STEP 5 — Connection Pool

Configure pool sizing based on:

- application instances
- DB capacity
- concurrency

Do not blindly increase Hikari pool size.

Remember:

total DB connections =
instances × pool size

---

# STEP 6 — Transactions

Define transaction boundaries.

Consider:

- isolation
- locking
- deadlocks
- retry behavior

---

# STEP 7 — Migration

For schema evolution use:

Flyway
or
Liquibase

Never manually modify production schemas without migration tracking.

---

# STEP 8 — Scaling

Evaluate in this order:

1. Query optimization
2. Indexing
3. Connection/pool tuning
4. Application scaling
5. Read replicas
6. Partitioning
7. Sharding

Do not jump directly to sharding.

---

# STEP 9 — Failure

Define behavior for:

- DB unavailable
- timeout
- deadlock
- connection exhaustion
- slow query
- partial failure

---

# Definition of Done

Database design includes:

- schema
- indexes
- constraints
- query strategy
- transaction boundaries
- migration
- scaling strategy
- failure strategy