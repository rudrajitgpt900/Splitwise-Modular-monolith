# Code Review Skill

## Purpose

Review code at Principal/Senior Staff engineering standards.

---

# STEP 1 — Functional Correctness

Verify:

- requirements
- edge cases
- null handling
- exception handling
- concurrency behavior

---

# STEP 2 — Architecture

Check:

- responsibility boundaries
- coupling
- cohesion
- dependency direction
- abstraction quality
- package structure

---

# STEP 3 — Performance

Check:

- algorithmic complexity
- database queries
- N+1 queries
- unnecessary network calls
- serialization
- caching
- connection pools
- thread pools

---

# STEP 4 — Reliability

Check:

- timeout
- retry
- idempotency
- circuit breaker
- fallback
- partial failure
- transaction behavior

---

# STEP 5 — Security

Check:

- authentication
- authorization
- validation
- secret exposure
- sensitive logging
- dependency issues

---

# STEP 6 — Observability

Check:

- traceId
- logs
- metrics
- errors
- health checks

---

# STEP 7 — Testing

Check:

- meaningful tests
- edge cases
- integration tests
- failure scenarios

Do not judge quality purely by coverage percentage.

---

# STEP 8 — Maintainability

Check:

- naming
- complexity
- duplication
- abstractions
- documentation
- extensibility

---

# STEP 9 — Review Severity

Classify findings:

CRITICAL
HIGH
MEDIUM
LOW
NIT

Do not call style preferences critical issues.

---

# STEP 10 — Provide Fix

For each important finding provide:

Problem
Why it matters
Recommended fix
Example implementation

---

# Final Review

Conclude with:

Architecture
Performance
Reliability
Security
Observability
Testing
Maintainability

and an overall assessment.