package com.cloud.ids.service;

import com.cloud.ids.entity.SecurityAlert;
import com.cloud.ids.repository.SecurityAlertRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SecurityAlertService {

    private final SecurityAlertRepository repository;

    public SecurityAlertService(SecurityAlertRepository repository) {
        this.repository = repository;
    }

    /**
     * Create and persist a new security alert.
     * Automatically sets timestamp and status if not provided.
     */
    public SecurityAlert createAlert(SecurityAlert alert) {
        if (alert.getTimestamp() == null) {
            alert.setTimestamp(LocalDateTime.now());
        }
        if (alert.getStatus() == null) {
            alert.setStatus("OPEN");
        }
        return repository.save(alert);
    }

    /**
     * Get all alerts, optionally filtered by status.
     * If status is null, returns all alerts.
     */
    public List<SecurityAlert> getAlerts(String status) {
        return status == null ? repository.findAll() : repository.findByStatus(status);
    }

    /**
     * Get all open alerts.
     */
    public List<SecurityAlert> getOpenAlerts() {
        return repository.findByStatus("OPEN");
    }

    /**
     * Get a specific alert by ID.
     */
    public SecurityAlert getAlertById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + id));
    }

    /**
     * Resolve (acknowledge) an alert by changing status to RESOLVED.
     */
    public SecurityAlert resolve(Long id) {
        SecurityAlert alert = getAlertById(id);
        alert.setStatus("RESOLVED");
        return repository.save(alert);
    }

    /**
     * Update the status of an alert.
     * Supports: NEW, ACKNOWLEDGED, RESOLVED
     */
    public SecurityAlert updateStatus(Long id, String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status + ". Must be NEW, ACKNOWLEDGED, or RESOLVED.");
        }
        SecurityAlert alert = getAlertById(id);
        alert.setStatus(status);
        return repository.save(alert);
    }

    private boolean isValidStatus(String status) {
        return status != null && (status.equals("NEW") || status.equals("ACKNOWLEDGED") || status.equals("RESOLVED") || status.equals("OPEN"));
    }
}