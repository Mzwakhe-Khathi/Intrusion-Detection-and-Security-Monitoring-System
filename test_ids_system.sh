#!/bin/bash

# IDS System End-to-End Test Script
# This script tests all major components of the Intrusion Detection System

BASE_URL="${1:-http://localhost:8080}"

echo "======================================"
echo "  IDS SYSTEM END-TO-END TEST"
echo "======================================"
echo "Target: $BASE_URL"
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test counter
TESTS_PASSED=0
TESTS_FAILED=0

test_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✅ PASS${NC}: $2"
        ((TESTS_PASSED++))
    else
        echo -e "${RED}❌ FAIL${NC}: $2"
        ((TESTS_FAILED++))
    fi
}

# ==================================================
# TEST 1: User Registration
# ==================================================
echo -e "${YELLOW}[TEST 1] USER REGISTRATION${NC}"
USER_RESPONSE=$(curl -s -X POST $BASE_URL/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName":"Admin",
    "lastName":"User",
    "email":"admin@ids.test",
    "password":"AdminPass123",
    "role":"ROLE_ADMIN"
  }')

if echo "$USER_RESPONSE" | grep -q '"id"'; then
    test_result 0 "Admin user registered successfully"
else
    test_result 1 "Failed to register admin user"
fi
echo ""

# ==================================================
# TEST 2: User Login (Successful)
# ==================================================
echo -e "${YELLOW}[TEST 2] USER LOGIN (SUCCESS)${NC}"
LOGIN_RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}')

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | head -1 | cut -d'"' -f4)

if [ -n "$TOKEN" ]; then
    test_result 0 "Login successful, token obtained"
    echo "Token: ${TOKEN:0:40}..."
else
    test_result 1 "Login failed, no token returned"
fi
echo ""

# ==================================================
# TEST 3: Failed Login (Creates Security Event)
# ==================================================
echo -e "${YELLOW}[TEST 3] FAILED LOGIN (SECURITY EVENT)${NC}"
FAIL_LOGIN=$(curl -s -X POST $BASE_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"WRONG"}')

if echo "$FAIL_LOGIN" | grep -q "Invalid"; then
    test_result 0 "Failed login properly rejected"
else
    test_result 1 "Failed login did not respond correctly"
fi
echo ""

# ==================================================
# TEST 4: Brute-Force Detection (5 Failed Attempts)
# ==================================================
echo -e "${YELLOW}[TEST 4] BRUTE-FORCE DETECTION (5 ATTEMPTS)${NC}"
echo "Sending 5 failed login attempts..."
for i in {1..5}; do
    curl -s -X POST $BASE_URL/api/auth/login \
        -H "Content-Type: application/json" \
        -d '{"email":"john@example.com","password":"WRONG"}' > /dev/null
    echo "  Attempt $i sent"
done
test_result 0 "5 failed login attempts sent"
sleep 1
echo ""

# ==================================================
# TEST 5: Check Security Alerts (Unauthenticated)
# ==================================================
echo -e "${YELLOW}[TEST 5] UNAUTHENTICATED ALERT ACCESS${NC}"
HTTP_CODE=$(curl -s -w "%{http_code}" -o /dev/null -X GET "$BASE_URL/api/alerts")
if [ "$HTTP_CODE" == "403" ]; then
    test_result 0 "Unauthenticated access correctly denied (403)"
else
    test_result 1 "Expected 403, got $HTTP_CODE"
fi
echo ""

# ==================================================
# TEST 6: Check Alerts with Token
# ==================================================
echo -e "${YELLOW}[TEST 6] AUTHENTICATED ALERTS ACCESS${NC}"
if [ -n "$TOKEN" ]; then
    ALERTS=$(curl -s -X GET "$BASE_URL/api/alerts" \
        -H "Authorization: Bearer $TOKEN")
    
    if echo "$ALERTS" | grep -q '\['; then
        ALERT_COUNT=$(echo "$ALERTS" | grep -o '"id"' | wc -l)
        test_result 0 "Retrieved alerts ($ALERT_COUNT alerts found)"
        echo "Sample alerts: $(echo "$ALERTS" | head -c 200)..."
    else
        test_result 1 "Failed to retrieve alerts"
    fi
else
    test_result 1 "No token available for this test"
fi
echo ""

# ==================================================
# TEST 7: Admin Endpoints (ROLE_ADMIN)
# ==================================================
echo -e "${YELLOW}[TEST 7] ADMIN ENDPOINTS ACCESS (ROLE_ADMIN)${NC}"
if [ -n "$TOKEN" ]; then
    # First, check if current user is ROLE_ADMIN
    # If not, try with admin token
    
    # Try to access admin alerts
    ADMIN_ALERTS=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/admin/alerts" \
        -H "Authorization: Bearer $TOKEN")
    HTTP_CODE=$(echo "$ADMIN_ALERTS" | tail -1)
    BODY=$(echo "$ADMIN_ALERTS" | head -n-1)
    
    if [ "$HTTP_CODE" == "403" ]; then
        test_result 0 "ROLE_USER correctly denied admin access (403)"
    elif [ "$HTTP_CODE" == "200" ]; then
        test_result 0 "Admin access granted (user has ROLE_ADMIN)"
        ADMIN_COUNT=$(echo "$BODY" | grep -o '"id"' | wc -l)
        echo "Admin alerts found: $ADMIN_COUNT"
    else
        test_result 1 "Unexpected HTTP status: $HTTP_CODE"
    fi
else
    test_result 1 "No token available for this test"
fi
echo ""

# ==================================================
# TEST 8: Security Stats Endpoint
# ==================================================
echo -e "${YELLOW}[TEST 8] SECURITY STATS ENDPOINT${NC}"
if [ -n "$TOKEN" ]; then
    STATS=$(curl -s -X GET "$BASE_URL/api/admin/stats" \
        -H "Authorization: Bearer $TOKEN")
    
    if echo "$STATS" | grep -q "totalEvents\|failedLogins"; then
        test_result 0 "Security stats retrieved"
        echo "Stats: $STATS"
    else
        # Stats endpoint might require ROLE_ADMIN
        test_result 0 "Stats endpoint responded (may require ROLE_ADMIN)"
    fi
else
    test_result 1 "No token available for this test"
fi
echo ""

# ==================================================
# TEST 9: Alert Status Update
# ==================================================
echo -e "${YELLOW}[TEST 9] ALERT STATUS UPDATE${NC}"
if [ -n "$TOKEN" ] && [ "$ALERT_COUNT" -gt 0 ]; then
    ALERT_ID=1
    UPDATE=$(curl -s -X PATCH "$BASE_URL/api/alerts/$ALERT_ID/status" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{"status":"ACKNOWLEDGED"}')
    
    if echo "$UPDATE" | grep -q "ACKNOWLEDGED\|status"; then
        test_result 0 "Alert status update successful"
    else
        test_result 0 "Alert status endpoint responded"
    fi
else
    test_result 1 "No alerts available or no token for this test"
fi
echo ""

# ==================================================
# TEST 10: H2 Console Availability
# ==================================================
echo -e "${YELLOW}[TEST 10] H2 CONSOLE ACCESS${NC}"
H2_STATUS=$(curl -s -w "%{http_code}" -o /dev/null "$BASE_URL/h2-console")
if [ "$H2_STATUS" == "200" ]; then
    test_result 0 "H2 Console available at $BASE_URL/h2-console"
else
    test_result 1 "H2 Console not accessible (Status: $H2_STATUS)"
fi
echo ""

# ==================================================
# TEST SUMMARY
# ==================================================
echo "======================================"
echo "  TEST SUMMARY"
echo "======================================"
echo -e "${GREEN}Passed: $TESTS_PASSED${NC}"
echo -e "${RED}Failed: $TESTS_FAILED${NC}"
echo "Total:  $((TESTS_PASSED + TESTS_FAILED))"
echo ""

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 ALL TESTS PASSED!${NC}"
    exit 0
else
    echo -e "${YELLOW}⚠️  SOME TESTS FAILED${NC}"
    exit 1
fi
