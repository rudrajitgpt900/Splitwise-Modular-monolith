# SplitWise Domain Class Model

This diagram describes the core user, group, expense, settlement, idempotency, and balance-projection models.

## Class Diagram

```mermaid
classDiagram
    direction TB

    %% ------------------------------------------------------------------
    %% Users and identities
    %% ------------------------------------------------------------------

    class User {
        +UUID id
        +String email
        +String displayName
        +UserStatus status
        +Instant createdAt
        +Instant updatedAt
    }

    class UserIdentity {
        +UUID id
        +String issuer
        +String subject
        +UUID userId
    }

    UserIdentity "0..*" --o "1" User : identity of

    %% ------------------------------------------------------------------
    %% Groups and memberships
    %% ------------------------------------------------------------------

    class Group {
        +UUID id
        +String name
        +CurrencyCode defaultCurrency
        +UUID createdBy
        +Instant createdAt
        +Instant archivedAt
    }

    class Membership {
        +UUID groupId
        +UUID userId
        +Role role
        +MembershipStatus status
        +Instant joinedAt
        +Instant removedAt
    }

    User "1" -- "0..*" Membership : memberships
    Group "1" -- "0..*" Membership : memberships

    %% ------------------------------------------------------------------
    %% Money and expenses
    %% ------------------------------------------------------------------

    class Money {
        +long amountMinor
        +CurrencyCode currency
    }

    class Expense {
        +UUID id
        +UUID groupId
        +String description
        +Money amount
        +LocalDate paidAt
        +UUID createdBy
        +SplitMethod splitMethod
        +Instant createdAt
        +Instant updatedAt
    }

    class ExpensePayment {
        +UUID expenseId
        +UUID userId
        +Money paid
    }

    class ExpenseShare {
        +UUID expenseId
        +UUID userId
        +Money share
        +Integer percentageBps
    }

    Group "1" -- "0..*" Expense : expenses
    Expense "1" -- "1..*" ExpensePayment : payments
    Expense "1" -- "1..*" ExpenseShare : shares
    User "1" -- "0..*" ExpensePayment : payer
    User "1" -- "0..*" ExpenseShare : participant

    %% ------------------------------------------------------------------
    %% Settlements
    %% ------------------------------------------------------------------

    class Settlement {
        +UUID id
        +UUID groupId
        +UUID fromUserId
        +UUID toUserId
        +Money amount
        +Instant settledAt
        +UUID createdBy
    }

    Group "1" -- "0..*" Settlement : settlements
    User "1" -- "0..*" Settlement : from
    User "1" -- "0..*" Settlement : to

    %% ------------------------------------------------------------------
    %% Idempotency
    %% ------------------------------------------------------------------

    class IdempotencyKey {
        +UUID id
        +UUID userId
        +UUID groupId
        +String key
        +String targetType
        +UUID targetId
        +Instant createdAt
    }

    User "1" -- "0..*" IdempotencyKey : request keys
    Group "1" -- "0..*" IdempotencyKey : request keys

    %% ------------------------------------------------------------------
    %% Balance projections
    %% ------------------------------------------------------------------

    class BalanceSummary {
        +UUID groupId
        +List~UserBalanceSummary~ perUser
    }

    class UserBalanceSummary {
        +UUID userId
        +long totalPaidMinor
        +long totalShareMinor
        +long netBalanceMinor
        +List~Debt~ debts
    }

    class Debt {
        +UUID fromUserId
        +UUID toUserId
        +Money amount
    }

    BalanceSummary ..> UserBalanceSummary : contains
    UserBalanceSummary ..> Debt : contains

    %% ------------------------------------------------------------------
    %% Domain services
    %% ------------------------------------------------------------------

    class ExpenseSplitCalculator {
        <<service>>
    }

    class BalanceCalculator {
        <<service>>
    }

    ExpenseSplitCalculator ..> Expense : calculates
    ExpenseSplitCalculator ..> ExpenseShare : produces
    BalanceCalculator ..> Expense : reads
    BalanceCalculator ..> ExpenseShare : reads
    BalanceCalculator ..> ExpensePayment : reads
    BalanceCalculator ..> Settlement : reads
```

## Reading the Model

| Area | Responsibility |
|---|---|
| Users and identities | Separates the application user from external authentication identities. |
| Groups and memberships | Models group ownership and user participation through `Membership`. |
| Expenses | Separates who paid (`ExpensePayment`) from who owes (`ExpenseShare`). |
| Settlements | Records money transferred between group members to reduce debt. |
| Idempotency | Associates retry-safe request keys with a user, group, and created target. |
| Balance projections | Represents calculated summaries; these are read models rather than transaction records. |
| Domain services | Calculates expense shares and derives balances from expenses and settlements. |

## Relationship Notation

- `--` — association
- `o--` — aggregation
- `..>` — dependency
- `1..*` — one or more
- `0..*` — zero or more
