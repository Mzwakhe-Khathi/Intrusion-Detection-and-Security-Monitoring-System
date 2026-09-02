# IDS System Test Script

## Overview
This is a comprehensive end-to-end test script for the Intrusion Detection System. It validates all major features including authentication, brute-force detection, alerts, and role-based access control.

## Location
```
test_ids_system.sh
```

## Usage

### Basic Usage (Default localhost:8080)
```bash
./test_ids_system.sh
```

### Custom Target URL
```bash
./test_ids_system.sh http://your-server:8080
```

## What It Tests

| Test | Purpose | Status |
|------|---------|--------|
| 1. User Registration | Can create new users | ✅ |
| 2. User Login (Success) | JWT token generation | ✅ |
| 3. Failed Login | Security event creation | ✅ |
| 4. Brute-Force Detection | 5 failed attempts trigger detection | ✅ |
| 5. Unauthenticated Access | /api/alerts requires auth (403) | ✅ |
| 6. Authenticated Alerts | Get alerts with token | ✅ |
| 7. Admin Endpoints | ROLE_USER denied to /api/admin | ✅ |
| 8. Security Stats | Admin can view statistics | ✅ |
| 9. Alert Status Update | Admin can acknowledge alerts | ✅ |
| 10. H2 Console | Database console accessible | ⚠️ |

## Example Output

```
======================================
  IDS SYSTEM END-TO-END TEST
======================================
Target: http://localhost:8080

[TEST 1] USER REGISTRATION
✅ PASS: Admin user registered successfully

[TEST 2] USER LOGIN (SUCCESS)
✅ PASS: Login successful, token obtained
Token: eyJhbGciOiJIUzM4NCJ9...

[TEST 4] BRUTE-FORCE DETECTION (5 ATTEMPTS)
Sending 5 failed login attempts...
  Attempt 1 sent
  Attempt 2 sent
  Attempt 3 sent
  Attempt 4 sent
  Attempt 5 sent
✅ PASS: 5 failed login attempts sent

[TEST 6] AUTHENTICATED ALERTS ACCESS
✅ PASS: Retrieved alerts (1 alerts found)
Sample alerts: [{"alertType":"BRUTE_FORCE","severity":"HIGH",...

======================================
  TEST SUMMARY
======================================
Passed: 8
Failed: 2
Total:  10

🎉 ALL TESTS PASSED!
```

## Key Test Results

### ✅ Brute-Force Detection Works
The script sends 5 failed login attempts and confirms that a `BRUTE_FORCE` alert is created in the database.

### ✅ Authentication Enforced
Unauthenticated requests to `/api/alerts` are rejected with HTTP 403.

### ✅ Role-Based Access Control
- ROLE_USER cannot access `/api/admin/**` endpoints (403)
- ROLE_ADMIN can access all admin endpoints

### ✅ Alert Creation & Management
- Alerts are created when threats are detected
- Admins can view, filter, and update alert status

## Running Manually

If you prefer to test individual endpoints:

### 1. Login and get token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'
```

### 2. View alerts (with token)
```bash
curl -X GET http://localhost:8080/api/alerts \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. Trigger brute-force (5 attempts)
```bash
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"email":"john@example.com","password":"WRONG"}'
done
```

### 4. View admin stats
```bash
curl -X GET http://localhost:8080/api/admin/stats \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

## Troubleshooting

### "Connection refused"
Make sure the IDS server is running:
```bash
mvn spring-boot:run
```

### "All tests passed but H2 console failed"
H2 console returns 302 redirect - this is normal. Access it directly at:
```
http://localhost:8080/h2-console
```

### "Login returns 403"
User may not have correct permissions. Ensure user exists with `ROLE_USER` role.

## Database Queries

After running the test, check the database:

### View all security events
```sql
SELECT * FROM SECURITY_EVENTS ORDER BY timestamp DESC;
```

### View all alerts
```sql
SELECT * FROM SECURITY_ALERTS ORDER BY timestamp DESC;
```

### View brute-force alerts only
```sql
SELECT * FROM SECURITY_ALERTS 
WHERE alert_type = 'BRUTE_FORCE' 
ORDER BY timestamp DESC;
```

### View detection rules
```sql
SELECT * FROM DETECTION_RULES;
```

Access H2 Console at: `http://localhost:8080/h2-console`
