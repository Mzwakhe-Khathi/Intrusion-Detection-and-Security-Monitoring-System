package com.cloud.ids.dto;

public class SecurityStatsResponse {
    private long totalEvents;
    private long failedLogins;
    private long successfulLogins;
    private long activeAlerts;
    private long resolvedAlerts;
    private long criticalAlerts;

    public SecurityStatsResponse(
            long totalEvents,
            long failedLogins,
            long successfulLogins,
            long activeAlerts,
            long resolvedAlerts,
            long criticalAlerts) {
        this.totalEvents = totalEvents;
        this.failedLogins = failedLogins;
        this.successfulLogins = successfulLogins;
        this.activeAlerts = activeAlerts;
        this.resolvedAlerts = resolvedAlerts;
        this.criticalAlerts = criticalAlerts;
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    public long getFailedLogins() {
        return failedLogins;
    }

    public long getSuccessfulLogins() {
        return successfulLogins;
    }

    public long getActiveAlerts() {
        return activeAlerts;
    }

    public long getResolvedAlerts() {
        return resolvedAlerts;
    }

    public long getCriticalAlerts() {
        return criticalAlerts;
    }
}
