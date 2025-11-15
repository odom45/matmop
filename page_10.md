## API Specifications

### REST API Endpoints

**Authentication Endpoints:**

```
POST /api/v1/auth/register
Body: {
    "email": "user@example.com",
    "password": "secure_password",
    "full_name": "John Doe"
}
Response: {
    "user_id": "uuid",
    "access_token": "jwt_token",
    "refresh_token": "refresh_token"
}

POST /api/v1/auth/login
POST /api/v1/auth/logout
POST /api/v1/auth/refresh
POST /api/v1/auth/reset-password
```

**User Endpoints:**

```
GET /api/v1/users/me
Response: {
    "user_id": "uuid",
    "email": "user@example.com",
    "full_name": "John Doe",
    "certification_level": 2,
    "wallet_balance": 1250.50,
    "lifetime_earnings": 15430.00,
    "client_satisfaction_rating": 4.8,
    "total_jobs_completed": 47
}

PATCH /api/v1/users/me
GET /api/v1/users/me/stats
GET /api/v1/users/me/certifications
```

**Certification Endpoints:**

```
GET /api/v1/certifications/courses/{level}
Response: {
    "level": 2,
    "modules": [
        {
            "module_id": "2.1",
            "title": "Advanced AI Agent Management",
            "duration_hours": 5,
            "content_url": "/courses/level2/module1"
        }
    ]
}

POST /api/v1/certifications/exams/{level}/start
POST /api/v1/certifications/exams/{level}/submit
GET /api/v1/certifications/exams/{level}/results
POST /api/v1/certifications/renew
```

**Job Endpoints:**

```
GET /api/v1/jobs/opportunities
Query params: ?platform=upwork&min_budget=100&max_budget=5000
Response: {
    "opportunities": [
        {
            "external_job_id": "upwork_123",
            "platform": "upwork",
            "title": "Blog post writing",
            "budget": 500,
            "deadline": "2024-01-20T17:00:00Z",
            "cca_analysis": {
                "complexity_score": 3.5,
                "success_probability": 0.92,
                "estimated_hours": 6
            }
        }
    ],
    "total": 10
}

POST /api/v1/jobs/accept
Body: {
    "external_job_id": "upwork_123",
    "platform": "upwork",
    "custom_proposal": "Optional custom text..."
}

GET /api/v1/jobs/{job_id}
GET /api/v1/jobs/{job_id}/tasks
GET /api/v1/jobs/{job_id}/deliverables
POST /api/v1/jobs/{job_id}/approve
POST /api/v1/jobs/{job_id}/request-revision
POST /api/v1/jobs/{job_id}/deliver
```

**Agent Endpoints:**

```
GET /api/v1/agents/available
Response: {
    "agents": [
        {
            "agent_id": "WA-01",
            "name": "Writing Agent",
            "capabilities": ["blog_posts", "articles", "documentation"],
            "average_quality_score": 4.7,
            "available": true
        }
    ]
}

POST /api/v1/agents/task/assign
Body: {
    "job_id": "uuid",
    "agent_id": "WA-01",
    "task_description": "Write blog post",
    "requirements": {...}
}

GET /api/v1/agents/task/{task_id}/status
POST /api/v1/agents/task/{task_id}/retry
```

**Payment Endpoints:**

```
GET /api/v1/payments/wallet
Response: {
    "balance": 1250.50,
    "pending": 340.00,
    "available_for_withdrawal": 910.50
}

POST /api/v1/payments/payout
Body: {
    "amount": 500.00,
    "method": "bank_transfer", // or instant, check
    "bank_account_id": "ba_xxx"
}

GET /api/v1/payments/transactions
GET /api/v1/payments/transactions/{transaction_id}
POST /api/v1/payments/connect-stripe
```

---

### GraphQL API

**Schema:**

```graphql
type User {
    id: ID!
    email: String!
    fullName: String!
    certificationLevel: Int!
    walletBalance: Float!
    lifetimeEarnings: Float!
    satisfactionRating: Float!
    totalJobsCompleted: Int!
    activeJobs: [Job!]!
    certifications: [Certification!]!
}

type Job {
    id: ID!
    title: String!
    platform: Platform!
    budget: Float!
    deadline: DateTime!
    status: JobStatus!
    tasks: [Task!]!
    deliverables: JSON
    progress: Float!
}

type Task {
    id: ID!
    agent: Agent!
    description: String!
    status: TaskStatus!
    output: JSON
    qualityScore: Float
}

type Agent {
    id: ID!
    name: String!
    capabilities: [String!]!
    averageQualityScore: Float!
    available: Boolean!
}

enum Platform {
    UPWORK
    FREELANCER
    FIVERR
}

enum JobStatus {
    PENDING
    ACTIVE
    IN_PROGRESS
    REVIEW
    DELIVERED
    COMPLETED
}

type Query {
    me: User!
    job(id: ID!): Job
    jobOpportunities(platform: Platform, minBudget: Float): [JobOpportunity!]!
    availableAgents: [Agent!]!
}

type Mutation {
    acceptJob(externalJobId: String!, platform: Platform!): Job!
    approveJob(jobId: ID!): Job!
    requestRevision(jobId: ID!, feedback: String!): Job!
    deliverJob(jobId: ID!): Job!
}

type Subscription {
    jobProgress(jobId: ID!): JobProgress!
    taskUpdate(jobId: ID!): TaskUpdate!
}
```

---

### Freelance Platform API Integration

**Upwork API Integration:**

```javascript
// OAuth 2.0 Authentication
const upworkAuth = {
    authorization_url: "https://www.upwork.com/ab/account-security/oauth2/authorize",
    token_url: "https://www.upwork.com/api/v3/oauth2/token",
    scopes: ["jobs:read", "jobs:write", "messages:read", "messages:write"]
}

// Job Search
GET https://www.upwork.com/api/profiles/v2/search/jobs.json
Headers: Authorization: Bearer {access_token}
Params: {
    q: "blog writing",
    budget: "100-1000",
    category2: "Writing"
}

// Submit Proposal
POST https://www.upwork.com/api/hr/v2/jobs/{job_key}/applications
Body: {
    "profile_key": "~user_key",
    "cover_letter": "Generated by CCA",
    "charge_rate": 50,
    "estimated_duration": "1-2 weeks"
}
```

**Fiverr API Integration:**

```javascript
// Similar OAuth flow
// Webhook for new opportunities matching user criteria
POST /webhooks/fiverr/opportunities
Body: {
    "event": "buyer.request.created",
    "data": {
        "request_id": "12345",
        "category": "writing-translation",
        "budget": 500
    }
}
```

**Stripe Connect Integration:**

```javascript
// Create connected account for user
const account = await stripe.accounts.create({
    type: 'express',
    country: 'US',
    email: user.email,
    capabilities: {
        card_payments: {requested: true},
        transfers: {requested: true}
    }
});

// Split payment on job completion
const transfer = await stripe.transfers.create({
    amount: user_payout_cents,
    currency: 'usd',
    destination: user.stripe_account_id,
    transfer_group: job_id
});
```
