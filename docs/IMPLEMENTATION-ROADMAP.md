# SplitWise App Implementation Roadmap

## Deployment Decision

The MVP is one Spring Boot modular monolith, not a collection of microservices. This keeps expense writes, split validation, balance calculation, and settlement records inside one MySQL transaction boundary.

The modules are independently owned in code and can become services later. We should not split them operationally until traffic, team ownership, or availability requirements justify the cost of distributed transactions and service-to-service security.

## Application Modules

| Module | Responsibility | Primary data | MVP status |
|---|---|---|---|
| Identity and users | User profiles, current-user identity, authentication integration | `app_user` | Foundation pending |
| Groups and memberships | Group lifecycle, membership roles, access policies | `expense_group`, `membership` | Pending |
| Expenses and splits | Expense commands, payer validation, deterministic split calculation | `expense`, `expense_payment`, `expense_share` | Split calculator implemented; API pending |
| Balances and settlements | Derived balances, bilateral debts, settlement commands | `settlement` plus expense records | Pending |
| Shared platform | Money types, validation errors, pagination, audit metadata, idempotency | Cross-cutting | Partial |

## Remaining Implementation Order

1. **Identity boundary**: define the current-user contract and implement a local development identity provider without weakening production authentication assumptions.
2. **Groups and memberships**: add DTOs, application services, repositories, authorization checks, and integration tests.
3. **Expense command flow**: persist expenses, payments, and shares atomically; add idempotency keys and API validation.
4. **Balance queries**: derive member balances and bilateral debts from indexed SQL queries.
5. **Settlements**: validate member relationships and record immutable settlement transactions.
6. **Production hardening**: replace local identity with OIDC/JWT, add audit logging, rate limits, metrics, tracing, and Testcontainers integration coverage.

## Future Extraction Candidates

These are potential services, not current deployments:

- **Identity Service**: only when authentication has independent ownership or scaling requirements. It should own user identity and tokens, not expense data.
- **Expense Service**: owns expense, payment, and share records. It remains the source of truth for financial commands.
- **Balance Service**: extract only when balance reads require independent scaling. It should consume immutable expense and settlement events and expose a read model; it must not write expense data.
- **Notification Service**: asynchronous email or push delivery. This is the first likely standalone service because delivery can be retried independently.

Groups and memberships should remain with the expense boundary initially because authorization and membership checks are synchronous prerequisites for financial writes.

## Service Boundaries if Extraction Becomes Necessary

| Future service | Owns | Communicates through | Must not do |
|---|---|---|---|
| Identity | User identity and authentication metadata | OIDC/JWT, user events | Modify expense or balance records |
| Expense | Expenses, payments, shares | REST commands, durable events | Compute mutable balance totals |
| Balance | Read model of balances and debts | Expense/settlement events | Become a second financial source of truth |
| Notification | Delivery attempts and templates | Events from domain services | Participate in the expense transaction |

Any extraction requires a separate database per service, explicit contracts, idempotent event handling, replay strategy, and distributed tracing. Until then, package boundaries and architecture tests are sufficient.
