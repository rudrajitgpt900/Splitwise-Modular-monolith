# Implementation Plan

This document turns the architecture into small, reviewable delivery phases. Each phase has a clear
reason for existing, a human-readable implementation target, and a validation checkpoint.

## Phase 1: Project foundation

Purpose: establish a reproducible Spring Boot service and local MySQL dependency.

Implemented:

- Maven dependency management in `pom.xml`.
- Spring Boot application bootstrap.
- Environment-based database configuration.
- Flyway migration ownership.
- Actuator and OpenAPI dependencies.
- Docker Compose MySQL service.
- Test and Testcontainers dependencies.

Validation: `mvn clean test` compiles the application and runs the domain tests.

## Phase 2: Shared contracts and domain rules

Purpose: define rules that must remain correct regardless of whether a request comes from a web client,
batch job, or future integration.

Implemented:

- Minor-unit money representation.
- Stable API validation error shape.
- Equal, exact, and percentage split methods.
- Deterministic remainder allocation.
- Unit tests for valid and invalid splits.

Validation: split totals always equal the recorded expense amount.

Add in this phase (concrete tasks):
- Global exception handling with `@RestControllerAdvice` returning a stable problem-details shape and error codes (module: `shared/error`).
- Money/currency validation utilities and JSON serializers for minor units (module: `shared/money`).
- Common pagination types and request validation helpers (module: `shared/paging`).
- Structured error responses verified by controller-slice tests.

## Phase 3: Users, groups, and membership (authorization backbone)

Purpose: make every group operation attributable to an authenticated user and enforce membership rules.

Note: The auth foundation already exists; do not add new auth features here. Focus on user, group, and membership.

Next implementation:

- Package layout per module (applies to all modules below):
  - `api/` (controllers only)
  - `dto/` (request/response records)
  - `application/` (use-cases, policies, transactions)
  - `domain/` (pure business logic, value objects)
  - `persistence/` (JPA entities, repositories, projections)
  - `config/` only if strictly necessary

- User module:
  - Persistence: `AppUserEntity` mapped to `app_user`; `UserRepository`.
  - Application: `UserService` (lookup by id), adapter for current user id is provided by `auth/CurrentUser`.
  - API: `GET /api/v1/users/me` returning a DTO with id, email, displayName, createdAt.
  - Tests: repository constraint (unique email), controller test for `/users/me`.

- Group & Membership module:
  - Domain: `Role {OWNER, MEMBER}`, `Status {ACTIVE, REMOVED}`; policies: only owner manages members; cannot remove last owner.
  - Persistence: `GroupEntity` -> `expense_group`, `MembershipEntity` -> `membership` (indexes/constraints align with Flyway).
  - Application:
    - `GroupService`: create, patch name, archive.
    - `MembershipService`: add/remove members, change role, `assertActiveMember(groupId, userId)`, `assertOwner(groupId, userId)`.
  - API:
    - `POST /api/v1/groups`
    - `GET /api/v1/groups`
    - `GET /api/v1/groups/{groupId}`
    - `PATCH /api/v1/groups/{groupId}`
    - `POST /api/v1/groups/{groupId}/members`
    - `DELETE /api/v1/groups/{groupId}/members/{userId}`
  - Tests: integration tests with Testcontainers covering membership constraints and authorization (active, removed, non-member).

Validation: a non-owner cannot modify membership; membership checks are enforced across group-scoped APIs.

## Phase 4: Expense commands and history

Purpose: persist a complete expense as one atomic financial operation.

Next implementation:

- Domain:
  - Use existing split strategies; add validators: sums match amount, participants and payers are ACTIVE members, currency == group default, paidAt present.
- Persistence:
  - `ExpenseEntity` -> `expense`
  - `ExpensePaymentEntity` -> `expense_payment`
  - `ExpenseShareEntity` -> `expense_share`
  - Repositories for each; optional `IdempotencyKeyEntity` if implementing idempotency here.
- Application:
  - `ExpenseService.create(groupId, command, currentUser)` runs in a transaction; invokes membership assertions; persists parent and children; guarantees invariants.
  - Queries: list by group with pagination and optional date filter.
- API:
  - `POST /api/v1/groups/{groupId}/expenses`
  - `GET /api/v1/groups/{groupId}/expenses?from=&to=&page=&size=`
  - `GET /api/v1/groups/{groupId}/expenses/{expenseId}`
- Tests:
  - Unit tests for validators and deterministic remainder allocation.
  - Transactional integration test: one call writes expense + payments + shares; DB constraints hold; duplicate request handling if idempotency enabled.

Validation: creating an expense enforces invariants and is atomic; listing honors membership and pagination.

## Phase 5: Balance views and settlements

Purpose: show users exactly what they paid, owe, and are owed, and allow immutable settlement records.

Next implementation:

- 5a) Balances (derived, read-focused):
  - Persistence: SQL queries/projections deriving `totalPaidMinor`, `totalShareMinor`, `settlementsPaid/Received`, `netBalance`, and bilateral debts (A→B) using existing indexes.
  - Application: `BalanceQueryService` providing group summary and current-user summary.
  - API:
    - `GET /api/v1/groups/{groupId}/balances`
    - `GET /api/v1/groups/{groupId}/balances/me`
  - Tests: integration tests verifying formulas over seeded expenses/shares/payments/settlements.

- 5b) Settlements (immutable financial records):
  - Persistence: `SettlementEntity` -> `settlement`; repository.
  - Application: `SettlementService.create(...)` validates ACTIVE members, `from != to`, `amount > 0`, currency matches group, and optional idempotency.
  - API:
    - `POST /api/v1/groups/{groupId}/settlements`
    - `GET  /api/v1/groups/{groupId}/settlements`
  - Tests: integration tests ensuring immutability and that balances reflect settlements.

Validation: balance endpoints reflect expenses and settlements consistently; settlements are append-only.

## Phase 6: Production hardening

Purpose: make financial history operable and safe under real usage.

Next implementation:

- Audit history and reversal semantics for corrections.
- Structured logging with correlation/trace id, userId, groupId, expenseId where present.
- Metrics: expense creation failures, balance latency, authorization failures.
- Rate limits and request-size limits.
- Backup/restore verification.
- Performance tests for balance queries; add a read projection only if latency SLOs justify it.

---

## Cross-cutting conventions

- Controllers are thin; all rules live in application/domain services; never expose JPA entities.
- Dependency direction: `api -> application -> domain`; `persistence` is used from application only; `domain` has no Spring/JPA deps.
- Cross-module calls go through application services (e.g., expense uses group membership checks), never other modules’ repositories.
- Observability: structured logs, metrics, and tracing around expense create, balance queries, settlements.
- Security: resolve `CurrentUser` from security context; enforce membership on every group-scoped call; return 403/404 per architecture doc.

## Immediate next steps (practical)

1) Finish shared: exception handler + error shape; Money validation utilities; tests.
2) Implement user module minimal read path (`/users/me`) and repository.
3) Implement group + membership (entities, repos, services, controllers) with authorization checks and integration tests.
4) Implement expense creation + listing using the existing split calculator; make it transactional; add idempotency if time allows.
5) Implement balance queries; then add settlements.
6) Add metrics/logging around these paths and finalize controller-slice/integration tests.
