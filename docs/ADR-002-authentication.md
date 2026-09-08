# ADR-002: Authentication Approach

## Status

Proposed

## Context

The application requires authenticated access for all API endpoints except health and documentation. The domain authorizes actions based on group membership. We need a secure, simple approach for local development and a production-ready path for real deployments.

## Decision

- Use Spring Security.
- For local development, enable HTTP Basic with an in-memory user. This keeps the feedback loop fast and avoids managing an IdP locally.
- For non-local environments, operate as an OAuth2 Resource Server validating JWTs issued by an external IdP (e.g., Azure AD, Auth0, Okta). We map the JWT subject (`sub`) to our `app_user.id`.
- Never accept user identity from request payloads; resolve from the security context via an adapter (`CurrentUser`).
- Permit unauthenticated access only for `/actuator/health`, `/actuator/info`, and OpenAPI endpoints.

## Consequences

- Local development is simple and secure enough (credentials via env vars, not committed).
- Switching to JWT requires only environment configuration (issuer/jwk URIs) and no code changes.
- Downstream modules depend only on the `CurrentUser` adapter and are decoupled from the auth mechanism.

## Alternatives Considered

- Implementing a custom username/password store: rejected due to security risk and operational overhead.
- API keys: rejected because we need end-user identity and group-scoped authorization.

## Rollout

- Default to Basic locally with `SECURITY_BASIC_ENABLED=true` and `SECURITY_JWT_ENABLED=false`.
- In staging/prod set `SECURITY_BASIC_ENABLED=false`, `SECURITY_JWT_ENABLED=true`, and configure `SECURITY_JWT_ISSUER_URI` or `SECURITY_JWT_JWK_SET_URI`.

