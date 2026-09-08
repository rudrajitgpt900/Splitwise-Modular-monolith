# ADR-001: Use a Modular Monolith for the MVP

## Status

Accepted

## Context

The first version needs authenticated users, groups, memberships, expenses, split validation, balances, and settlements. Expense creation must atomically persist a parent expense and its shares/payments. Balance queries must read a consistent view of those records.

## Requirements

- Strong transactional consistency for financial writes.
- Straightforward local development and deployment.
- Clear ownership boundaries so the system can evolve.
- A path to scale read-heavy balance and expense-history queries.
- Low operational overhead for an early product.

## Options considered

### Microservices

Separate identity, groups, expenses, and balances into independently deployed services.

Advantages: independent scaling and deployment.

Disadvantages: distributed transactions, duplicated authorization context, more infrastructure, and harder local testing for a workflow that is naturally transactional.

### Modular monolith

One Spring Boot deployment with explicit modules and one MySQL database.

Advantages: local ACID transactions, simple queries, low operational overhead, and easy end-to-end testing. Module boundaries preserve a future extraction path.

Disadvantages: modules share a release cycle and database, and careless dependencies can erode boundaries.

## Decision

We will use a modular monolith with MySQL for the MVP. The expense and balance modules will expose application-level interfaces, and modules will not access each other's repositories directly.

## Reason

The dominant correctness requirement is atomic money accounting, not independent service scaling. A monolith keeps the write path transactional and makes authorization and balance calculations easier to reason about. MySQL indexes and read pagination are sufficient for the expected initial workload.

## Trade-offs

### Benefits

- One deployment and one local development stack.
- Strong consistency without distributed transaction coordination.
- Simple reporting queries over normalized financial records.
- Clear migration path to a balance read model if measurements justify it.

### Costs

- All modules share deployment and runtime capacity.
- Database schema discipline is required to protect module ownership.
- A future service split will require explicit integration contracts.

### Risks

- Direct cross-module repository access can create a tightly coupled codebase.
- Balance queries may become expensive as history grows.

## Consequences

- Every financial command is handled in a database transaction.
- Balance results are derived rather than stored as mutable totals.
- Module-level tests and architecture checks should prevent forbidden dependencies.
- Read replicas or a projection can be added later without changing the client API.

## Reconsideration criteria

Revisit this decision if independent scaling is needed, balance queries consistently breach the latency SLO despite indexing and projection, or team ownership requires independently deployable services.
