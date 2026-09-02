package com.cloud.ids.controller;

import com.cloud.ids.entity.SecurityAlert;
import com.cloud.ids.service.SecurityAlertService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class SecurityAlertController {

    private final SecurityAlertService service;

    public SecurityAlertController(SecurityAlertService service) {
        this.service = service;
    }

    /**
     * Get all alerts, optionally filtered by status.
     * Query: GET /api/alerts
     * Query: GET /api/alerts?status=OPEN
     */
    @GetMapping
    public List<SecurityAlert> getAlerts(@RequestParam(required = false) String status) {
        return service.getAlerts(status);
    }

    /**
     * Get all open alerts.
     * Query: GET /api/alerts/open
     */
    @GetMapping("/open")
    public List<SecurityAlert> getOpenAlerts() {
        return service.getOpenAlerts();
    }

    /**
     * Get a specific alert by ID.
     * Query: GET /api/alerts/{id}
     */
    @GetMapping("/{id}")
    public SecurityAlert getAlert(@PathVariable Long id) {
        return service.getAlertById(id);
    }

    /**
     * Resolve (acknowledge) an alert.
     * Changes status from OPEN to RESOLVED.
     * Query: PUT /api/alerts/{id}/resolve
     */
    @PutMapping("/{id}/resolve")
    public SecurityAlert resolve(@PathVariable Long id) {
        return service.resolve(id);
    }
}