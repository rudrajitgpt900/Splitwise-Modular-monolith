# Spring Boot Skill

## Purpose

Implement production-grade Java Spring Boot services using clean boundaries, explicit configuration, testability and maintainability.

Default stack:

- Java 17+ unless project specifies otherwise
- Spring Boot 3.x+
- Maven
- Spring Web
- Spring Validation
- Spring Actuator
- Spring Data where applicable
- OpenAPI
- JUnit 5
- Mockito

Do not introduce dependencies without justification.

---

# STEP 1 — Create Maven Structure

For a small service:

src/main/java/com/company/service

├── config
├── controller
├── dto
│   ├── request
│   └── response
├── exception
├── mapper
├── service
├── repository
├── entity
└── client

For larger services prefer:

api/
application/
domain/
infrastructure/
config/

---

# STEP 2 — Configuration

Create:

application.yml
application-local.yml
application-qa.yml
application-prod.yml

Use:

spring.config.activate.on-profile

or standard Spring profile configuration.

Never hardcode:

- password
- API key
- token
- database credentials
- Kafka credentials
- Redis credentials

Use environment variables or secret providers.

---

# STEP 3 — Controller

Controllers must:

- expose HTTP endpoints
- validate input
- convert HTTP request into application request
- call application/service layer
- return HTTP response

Controllers must NOT:

- contain business logic
- call repositories directly
- contain SQL
- implement complex transformations

---

# STEP 4 — DTO

Create separate:

Request DTO
Response DTO

Use Java records for immutable DTOs when appropriate.

Example:

CreateOrderRequest
CreateOrderResponse

Never expose JPA entities directly.

---

# STEP 5 — Mapper

Create mapper classes between:

DTO ↔ Domain
Domain ↔ Entity

Use a reusable mapper abstraction only where multiple implementations benefit from it.

Do not create an abstraction for a single trivial mapping.

---

# STEP 6 — Service/Application Layer

Service/application layer owns:

- business workflow
- orchestration
- validation beyond simple request validation
- transaction boundary where applicable
- calls to domain objects
- repository interactions

Avoid massive service classes.

If a service exceeds reasonable responsibility boundaries, split use cases.

---

# STEP 7 — Domain

Business rules should live as close to the domain as practical.

Avoid putting business rules inside:

- controllers
- repositories
- DTOs

Prefer explicit domain methods over procedural manipulation scattered across services.

---

# STEP 8 — Repository

Repository layer owns persistence access.

Repository must NOT:

- decide business rules
- perform HTTP calls
- publish Kafka events unless specifically designed as an infrastructure adapter

---

# STEP 9 — Transactions

Define transaction boundaries deliberately.

Use:

@Transactional

around business operations requiring atomicity.

Do not place @Transactional blindly on every service method.

Explicitly consider:

- isolation
- propagation
- rollback behavior
- long-running transactions

Never hold a DB transaction open while waiting unnecessarily for remote services.

---

# STEP 10 — Global Exception Handling

Create:

GlobalExceptionHandler

using:

@RestControllerAdvice

Create a consistent:

ErrorResponse

containing where appropriate:

- timestamp
- status
- errorCode
- message
- path
- traceId

Never expose stack traces or internal exception details.

---

# STEP 11 — Actuator

Include Spring Boot Actuator.

Local:

Expose useful diagnostic endpoints.

QA:

Expose controlled operational endpoints.

Prod:

Expose only required endpoints.

Default production:

health
info
metrics

Do not expose:

env
beans
configprops
mappings

in production unless explicitly justified and secured.

---

# STEP 12 — Swagger

Configure OpenAPI.

Document:

- endpoint
- request
- response
- validation
- status codes
- authentication
- important headers

---

# STEP 13 — Configuration Validation

Fail fast when mandatory configuration is missing.

Use:

@ConfigurationProperties

for grouped configuration.

Prefer typed configuration over scattered:

@Value

properties.

---

# STEP 14 — Dependency Injection

Always prefer constructor injection.

Avoid field injection.

---

# STEP 15 — Final Implementation Review

Check:

- package boundaries
- DTO separation
- transaction boundaries
- exception handling
- configuration
- validation
- actuator
- swagger
- tests
- logging
- security