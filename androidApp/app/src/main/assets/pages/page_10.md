## API Specifications

### REST API Endpoints

Authentication Endpoints:

POST /api/v1/auth/register

POST /api/v1/auth/login
POST /api/v1/auth/logout
POST /api/v1/auth/refresh
POST /api/v1/auth/reset-password

User Endpoints:

GET /api/v1/users/me
PATCH /api/v1/users/me
GET /api/v1/users/me/stats
GET /api/v1/users/me/certifications

Job Endpoints:

GET /api/v1/jobs/opportunities
POST /api/v1/jobs/accept
GET /api/v1/jobs/{job_id}
POST /api/v1/jobs/{job_id}/approve
POST /api/v1/jobs/{job_id}/request-revision
POST /api/v1/jobs/{job_id}/deliver

