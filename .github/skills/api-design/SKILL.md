# API Design Skill

## Purpose

Design consistent, backward-compatible and consumer-friendly REST APIs.

---

# STEP 1 — Define Resource

Identify:

- resource
- ownership
- lifecycle
- relationships

Use nouns rather than verbs.

Prefer:

GET /orders/{id}

rather than:

GET /getOrder

---

# STEP 2 — HTTP Methods

Use:

GET
POST
PUT
PATCH
DELETE

according to semantics.

---

# STEP 3 — HTTP Status Codes

Use appropriate status codes.

Typical:

200 OK
201 Created
202 Accepted
204 No Content
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
422 Unprocessable Entity
429 Too Many Requests
500 Internal Server Error
503 Service Unavailable

Do not return 200 for every scenario.

---

# STEP 4 — Request DTO

Every non-trivial request must have a dedicated request model.

Use Bean Validation.

Example:

@NotBlank
@Size
@Positive
@Email

---

# STEP 5 — Response DTO

Return explicit response DTOs.

Do not return entities.

---

# STEP 6 — Error Contract

Every API should return a consistent error model.

Example:

{
  "timestamp": "...",
  "status": 400,
  "errorCode": "INVALID_REQUEST",
  "message": "...",
  "path": "...",
  "traceId": "..."
}

---

# STEP 7 — Idempotency

For operations that may be retried, determine whether idempotency is required.

Examples:

- payment
- order creation
- message processing

If required implement an idempotency key strategy.

---

# STEP 8 — Pagination

For large collections never return unbounded results.

Define:

page
size
sort

or cursor-based pagination where scale requires it.

---

# STEP 9 — Versioning

Avoid breaking existing consumers.

Use API versioning only when necessary.

Document breaking changes.

---

# STEP 10 — Swagger

Document all public APIs with OpenAPI.

---

# Definition of Done

API has:

- correct HTTP semantics
- validation
- DTOs
- consistent errors
- pagination where needed
- idempotency where needed
- Swagger
- backward compatibility consideration