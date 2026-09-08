# Observability Skill

## Purpose

Make every service diagnosable in local, QA and production environments.

---

# STEP 1 — Logging

Use:

ERROR
WARN
INFO
DEBUG
TRACE

Production default:

INFO

DEBUG must be disabled by default in production.

---

# STEP 2 — Correlation

Every request should have:

traceId

and where distributed tracing exists:

spanId

Propagate correlation identifiers between services.

---

# STEP 3 — Request Logging

Capture useful metadata:

- traceId
- HTTP method
- endpoint
- status
- duration
- service name

Do not log sensitive payloads blindly.

---

# STEP 4 — Business Logging

Log meaningful business transitions.

Example:

Order creation started
Order persisted
Payment requested
Order completed

Avoid logging every trivial method call.

---

# STEP 5 — Sensitive Data

Never log:

- passwords
- JWTs
- access tokens
- API keys
- secrets
- full card numbers
- sensitive personal information

Mask sensitive fields.

---

# STEP 6 — Metrics

Provide metrics for:

- request count
- error count
- latency
- DB latency
- external dependency latency
- Kafka consumer lag
- cache hit ratio

---

# STEP 7 — Health

Implement health checks for important dependencies.

Distinguish:

liveness
readiness

Do not make liveness depend on every external dependency.

---

# STEP 8 — Local Logging

Local logs should prioritize readability.

Use color coding where appropriate.

Example:

TRACE
DEBUG
INFO
WARN
ERROR

However, color must never be required for machine parsing.

---

# STEP 9 — Production Logging

Production logs should preferably be structured JSON.

Include:

timestamp
level
service
traceId
spanId
logger
message

---

# STEP 10 — Debugging Workflow

Logs should allow:

Client request
→ Service A
→ Service B
→ Kafka
→ Service C
→ Database

to be correlated.

---

# Definition of Done

Service provides:

- useful logs
- traceId
- metrics
- health
- latency visibility
- dependency visibility
- sensitive-data protection