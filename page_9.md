## Database Schema & Data Models

### PostgreSQL Schema (Primary Database)

**Users Table:**
```sql
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    certification_level INTEGER DEFAULT 1, -- 1, 2, or 3
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
    status VARCHAR(50) DEFAULT 'active', -- active, suspended, deleted
    preferences JSONB, -- user settings and preferences
    CONSTRAINT valid_cert_level CHECK (certification_level IN (1, 2, 3)),
    CONSTRAINT valid_rating CHECK (client_satisfaction_rating >= 0 AND client_satisfaction_rating <= 5)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_cert_level ON users(certification_level);
CREATE INDEX idx_users_status ON users(status);
```

**Certifications Table:**
```sql
CREATE TABLE certifications (
    certification_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    level INTEGER NOT NULL,
    exam_score DECIMAL(5,2),
    practical_score DECIMAL(5,2),
    industry_tracks TEXT[], -- Array of completed tracks
    certification_date TIMESTAMP DEFAULT NOW(),
    expiry_date TIMESTAMP,
    renewal_count INTEGER DEFAULT 0,
    continuing_education_hours INTEGER DEFAULT 0,
    status VARCHAR(50) DEFAULT 'active', -- active, expired, revoked
    CONSTRAINT valid_level CHECK (level IN (1, 2, 3))
);

CREATE INDEX idx_cert_user ON certifications(user_id);
CREATE INDEX idx_cert_level ON certifications(level);
CREATE INDEX idx_cert_status ON certifications(status);
```

**Jobs Table:**
```sql
CREATE TABLE jobs (
    job_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(user_id) ON DELETE SET NULL,
    external_job_id VARCHAR(255), -- ID from Upwork/Freelancer/Fiverr
    platform VARCHAR(50) NOT NULL, -- upwork, freelancer, fiverr
    client_id VARCHAR(255),
    client_name VARCHAR(255),
    title VARCHAR(500) NOT NULL,
    description TEXT,
    job_type VARCHAR(100), -- writing, design, development, etc.
    budget DECIMAL(10,2),
    deadline TIMESTAMP,
    status VARCHAR(50) DEFAULT 'pending', -- pending, active, in_progress, review, delivered, completed, disputed, cancelled
    requirements JSONB, -- Structured requirements from CCA
    work_breakdown JSONB, -- Task tree from CCA
    deliverables JSONB, -- Links to completed work
    automated_qa_score DECIMAL(5,2),
    user_approval_timestamp TIMESTAMP,
    client_approval_timestamp TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    completed_at TIMESTAMP,
    revision_count INTEGER DEFAULT 0
);

CREATE INDEX idx_jobs_user ON jobs(user_id);
CREATE INDEX idx_jobs_status ON jobs(status);
CREATE INDEX idx_jobs_platform ON jobs(platform);
CREATE INDEX idx_jobs_deadline ON jobs(deadline);
```

**Tasks Table:**
```sql
CREATE TABLE tasks (
    task_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id UUID REFERENCES jobs(job_id) ON DELETE CASCADE,
    agent_id VARCHAR(50) NOT NULL, -- e.g., WA-01, GD-01
    task_type VARCHAR(100),
    description TEXT,
    requirements JSONB,
    dependencies UUID[], -- Array of task_ids that must complete first
    status VARCHAR(50) DEFAULT 'pending', -- pending, assigned, in_progress, completed, failed
    priority INTEGER DEFAULT 5, -- 1-10, higher = more urgent
    estimated_duration_minutes INTEGER,
    actual_duration_minutes INTEGER,
    output JSONB, -- Results from agent
    quality_score DECIMAL(5,2),
    retry_count INTEGER DEFAULT 0,
    assigned_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_tasks_job ON tasks(job_id);
CREATE INDEX idx_tasks_agent ON tasks(agent_id);
CREATE INDEX idx_tasks_status ON tasks(status);
```

**Transactions Table:**
```sql
CREATE TABLE transactions (
    transaction_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(user_id) ON DELETE SET NULL,
    job_id UUID REFERENCES jobs(job_id) ON DELETE SET NULL,
    transaction_type VARCHAR(50) NOT NULL, -- job_payment, certification_fee, payout, refund
    gross_amount DECIMAL(10,2) NOT NULL,
    platform_fee DECIMAL(10,2) DEFAULT 0.00, -- Upwork/Fiverr fee
    creator_fee DECIMAL(10,2) DEFAULT 0.00, -- MATMOP fee (10% or 8%)
    net_amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    stripe_transaction_id VARCHAR(255),
    status VARCHAR(50) DEFAULT 'pending', -- pending, completed, failed, refunded
    created_at TIMESTAMP DEFAULT NOW(),
    completed_at TIMESTAMP
);

CREATE INDEX idx_trans_user ON transactions(user_id);
CREATE INDEX idx_trans_job ON transactions(job_id);
CREATE INDEX idx_trans_type ON transactions(transaction_type);
CREATE INDEX idx_trans_status ON transactions(status);
```

**Agent Performance Table:**
```sql
CREATE TABLE agent_performance (
    performance_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id VARCHAR(50) NOT NULL,
    task_id UUID REFERENCES tasks(task_id) ON DELETE CASCADE,
    execution_time_seconds INTEGER,
    quality_score DECIMAL(5,2),
    user_feedback_score DECIMAL(5,2),
    retry_required BOOLEAN DEFAULT FALSE,
    error_message TEXT,
    tokens_used INTEGER,
    cost DECIMAL(8,4),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_perf_agent ON agent_performance(agent_id);
CREATE INDEX idx_perf_created ON agent_performance(created_at);
```

---

### MongoDB Collections (Document Store)

**Job Metadata Collection:**
```javascript
{
    _id: ObjectId("..."),
    job_id: "uuid-from-postgres",
    raw_job_description: "Full text from platform",
    client_communication: [
        {
            timestamp: ISODate("2024-01-15T10:00:00Z"),
            from: "client",
            message: "Can you add more details?"
        }
    ],
    agent_outputs: {
        "WA-01": {
            "task_001": {
                content: "Generated blog post text...",
                metadata: {word_count: 1015, tone: "professional"},
                raw_llm_response: {...}
            }
        }
    },
    revision_history: [
        {
            revision_number: 1,
            timestamp: ISODate("..."),
            changes_requested: "More data-driven content",
            agent_responses: {...}
        }
    ]
}
```

**Agent Knowledge Base Collection:**
```javascript
{
    _id: ObjectId("..."),
    agent_id: "WA-01",
    knowledge_type: "client_preferences",
    client_id: "client_123",
    learned_preferences: {
        tone: "professional",
        avoid_words: ["utilize", "leverage"],
        preferred_structure: "intro-body-conclusion",
        avg_revision_requests: 0.2
    },
    confidence_score: 0.87,
    last_updated: ISODate("...")
}
```
