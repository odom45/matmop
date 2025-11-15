## Database Schema & Data Models

### PostgreSQL Schema (Primary Database)

**Users Table:**

CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    certification_level INTEGER DEFAULT 1,
    certification_expiry TIMESTAMP,
    stripe_account_id VARCHAR(255),
    stripe_customer_id VARCHAR(255),
    wallet_balance DECIMAL(10,2) DEFAULT 0.00,
    lifetime_earnings DECIMAL(12,2) DEFAULT 0.00,
    client_satisfaction_rating DECIMAL(3,2) DEFAULT 0.00,
    total_jobs_completed INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    last_login TIMESTAMP,
    status VARCHAR(50) DEFAULT 'active',
    preferences JSONB,
    CONSTRAINT valid_cert_level CHECK (certification_level IN (1, 2, 3)),
    CONSTRAINT valid_rating CHECK (client_satisfaction_rating >= 0 AND client_satisfaction_rating <= 5)
);

