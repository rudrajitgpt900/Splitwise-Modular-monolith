# Messaging Skill

## Purpose

Design reliable asynchronous systems using Kafka or other messaging infrastructure.

---

# STEP 1 — Determine Whether Messaging Is Required

Ask:

- Is asynchronous processing required?
- Is decoupling valuable?
- Is replay valuable?
- Are multiple consumers required?
- Can eventual consistency be tolerated?

If no, prefer synchronous communication.

---

# STEP 2 — Event Definition

Define:

- event name
- producer
- consumers
- schema
- version
- key
- ordering requirement

---

# STEP 3 — Topic Design

Define:

- topic
- partitions
- replication
- retention
- cleanup policy

Do not choose partition count arbitrarily.

---

# STEP 4 — Partition Key

Select a key based on ordering requirements.

Examples:

customerId
orderId
accountId

If events must be ordered for an entity, events for that entity must use the same partition key.

---

# STEP 5 — Consumer Group

Define consumer groups according to independent processing requirements.

---

# STEP 6 — Delivery Semantics

Explicitly determine:

at-most-once
at-least-once
effectively-once

Do not claim exactly-once unless the complete architecture actually provides it.

---

# STEP 7 — Idempotency

Consumers must assume duplicate delivery can happen.

Implement idempotency where required.

Possible approaches:

- processed-event table
- unique event ID
- idempotency key
- state comparison

---

# STEP 8 — Retry

Define:

1. immediate retry
2. delayed retry
3. retry topic
4. dead-letter topic

Do not endlessly retry poison messages.

---

# STEP 9 — Dead Letter

Define:

- DLQ topic
- reason
- original topic
- partition
- offset
- event ID
- timestamp

---

# STEP 10 — Schema Evolution

Prefer backward-compatible changes.

Never casually rename/remove event fields.

Consider:

Schema Registry
Avro
JSON Schema
Protobuf

based on system requirements.

---

# STEP 11 — Transactional Publishing

When DB state and Kafka event must remain consistent, evaluate:

Transactional Outbox

Do not rely on:

DB transaction
+
Kafka publish

as a single atomic operation without an appropriate design.

---

# STEP 12 — Observability

Monitor:

- consumer lag
- throughput
- failures
- retries
- DLQ
- processing latency
- rebalance frequency

---

# STEP 13 — Local Setup

Provide Docker Compose configuration for local Kafka.

Document:

- broker
- ports
- topics
- topic creation
- consumer groups
- application configuration
- health verification

---

# Definition of Done

Messaging design includes:

- topic
- partition strategy
- key
- consumer groups
- delivery semantics
- idempotency
- retry
- DLQ
- schema evolution
- observability
- local setup