# Infrastructure Skill

## Purpose

Provide reproducible local infrastructure and production-ready configuration boundaries.

---

# STEP 1 — Identify Dependencies

For every service identify:

- database
- Kafka
- Redis
- Elasticsearch/OpenSearch
- external APIs
- object storage
- other infrastructure

---

# STEP 2 — Local Development

If infrastructure is required locally, provide:

docker-compose.yml

unless there is a strong reason not to.

---

# STEP 3 — Docker Compose

Every dependency should have:

- image
- port
- environment
- volume where required
- healthcheck
- network

Example conceptual structure:

services:

  postgres:
    ...

  redis:
    ...

  kafka:
    ...

Do not use unnecessary infrastructure.

---

# STEP 4 — Startup Dependencies

Define health-based startup ordering where possible.

Application should not blindly assume infrastructure is ready merely because the container started.

---

# STEP 5 — Persistence

For stateful local dependencies define volumes when developers need persistent state.

Document how to reset state.

---

# STEP 6 — Environment Variables

Application configuration should reference environment variables.

Example:

DB_HOST=${DB_HOST:localhost}

Never hardcode credentials.

---

# STEP 7 — Local Configuration

application-local.yml should provide sensible developer defaults.

Local should maximize visibility and ease of debugging.

---

# STEP 8 — QA

QA configuration should use placeholders or externally supplied configuration.

Do not embed QA credentials in source control.

---

# STEP 9 — Production

Production YAML must contain configuration structure but use placeholders/environment references.

Example:

database:
  host: ${DB_HOST}
  username: ${DB_USERNAME}
  password: ${DB_PASSWORD}

---

# STEP 10 — Health Checks

Infrastructure dependencies should expose health checks where supported.

---

# STEP 11 — Verification

Documentation must provide commands to verify:

- DB connectivity
- Redis connectivity
- Kafka connectivity
- application health

---

# STEP 12 — Troubleshooting

Document common issues:

- port conflict
- container not ready
- authentication failure
- DNS/network issue
- stale volume
- Kafka topic missing
- connection pool exhaustion

---

# Definition of Done

A developer cloning the project should be able to:

1. start dependencies
2. start application
3. verify health
4. call API
5. inspect logs
6. inspect metrics
7. stop/reset infrastructure