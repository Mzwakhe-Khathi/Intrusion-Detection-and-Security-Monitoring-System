package com.cloud.ids.controller;

import com.cloud.ids.dto.SecurityStatsResponse;
import com.cloud.ids.entity.SecurityAlert;
import com.cloud.ids.service.SecurityAlertService;
import com.cloud.ids.service.SecurityStatsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Admin-only endpoints for security monitoring and alert management.
 * All endpoints require ROLE_ADMIN and are protected at /api/admin/**
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final SecurityAlertService alertService;
    private final SecurityStatsService statsService;

    public AdminController(
            SecurityAlertService alertService,
            SecurityStatsService statsService) {
        this.alertService = alertService;
        this.statsService = statsService;
    }

    /**
     * Get all security alerts.
     * Admin endpoint: GET /api/admin/alerts
     */
    @GetMapping("/alerts")
    public List<SecurityAlert> getAllAlerts(@RequestParam(required = false) String status) {
        return alertService.getAlerts(status);
    }

    /**
     * Get a specific alert by ID.
     * Admin endpoint: GET /api/admin/alerts/{id}
     */
    @GetMapping("/alerts/{id}")
    public SecurityAlert getAlert(@PathVariable Long id) {
        return alertService.getAlertById(id);
    }

    /**
     * Update the status of an alert.
     * Admin endpoint: PATCH /api/admin/alerts/{id}/status
     * Body: { "status": "ACKNOWLEDGED" } or { "status": "RESOLVED" }
     */
    @PatchMapping("/alerts/{id}/status")
    public SecurityAlert updateAlertStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String status = request.get("status");
        return alertService.updateStatus(id, status);
    }

    /**
     * Get all open/active alerts.
     * Admin endpoint: GET /api/admin/alerts/open
     */
    @GetMapping("/alerts/open")
    public List<SecurityAlert> getOpenAlerts() {
        return alertService.getOpenAlerts();
    }

    /**
     * Get security monitoring statistics.
     * Admin endpoint: GET /api/admin/stats
     * Returns: {
     *   "totalEvents": 25,
     *   "failedLogins": 12,
     *   "successfulLogins": 10,
     *   "activeAlerts": 3,
     *   "resolvedAlerts": 2,
     *   "criticalAlerts": 1
     * }
     */
    @GetMapping("/stats")
    public SecurityStatsResponse getSecurityStats() {
        return statsService.getSecurityStats();
    }
}