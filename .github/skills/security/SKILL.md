# Security Skill

## Purpose

Apply security based on threat model and system requirements.

---

# STEP 1 — Identify Assets

Determine what must be protected:

- user data
- credentials
- tokens
- APIs
- financial information
- infrastructure
- internal services

---

# STEP 2 — Authentication

Determine whether the service requires:

- JWT
- OAuth2
- OIDC
- API key
- mTLS
- service identity

Do not implement custom authentication if an established standard is appropriate.

---

# STEP 3 — Authorization

Determine:

RBAC
ABAC
resource-based authorization

Authentication answers:

"Who are you?"

Authorization answers:

"What are you allowed to do?"

---

# STEP 4 — Input Validation

Validate:

- body
- query parameters
- path variables
- headers

Never trust external input.

---

# STEP 5 — Secrets

Never commit:

- passwords
- API keys
- private keys
- JWT signing secrets
- cloud credentials

Use:

environment variables
secret manager
Vault
cloud secret store

---

# STEP 6 — API Security

Evaluate:

- authentication
- authorization
- rate limiting
- CORS
- CSRF where applicable
- security headers
- TLS

---

# STEP 7 — Dependency Security

Consider:

- dependency vulnerabilities
- SAST
- DAST
- OWASP
- dependency scanning

---

# STEP 8 — Error Security

Never expose:

- stack trace
- SQL
- internal class names
- credentials
- infrastructure topology

---

# STEP 9 — Logging Security

Ensure tokens and secrets are never logged.

---

# STEP 10 — Production Review

Before production verify:

- authentication
- authorization
- secrets
- TLS
- validation
- dependency vulnerabilities
- actuator exposure
- Swagger exposure