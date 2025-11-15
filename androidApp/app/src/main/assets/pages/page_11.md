## Security & Compliance

### Authentication & Authorization

**Multi-Factor Authentication (MFA):**
- Required for all Level 2 and Level 3 certified users
- Optional but recommended for Level 1 users
- Supported methods: SMS, authenticator app (TOTP), email
- Backup codes provided for account recovery

**Role-Based Access Control (RBAC):**

const permissions = {
    level_1: [
        "jobs:view:basic",
        "jobs:accept:max_500",
        "agents:use:limited",
        "wallet:view",
        "wallet:withdraw"
    ]
}

