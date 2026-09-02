package com.cloud.ids.service;

import com.cloud.ids.dto.SecurityStatsResponse;
import com.cloud.ids.repository.SecurityAlertRepository;
import com.cloud.ids.repository.SecurityEventRepository;
import org.springframework.stereotype.Service;

@Service
public class SecurityStatsService {

    private final SecurityEventRepository eventRepository;
    private final SecurityAlertRepository alertRepository;

    public SecurityStatsService(
            SecurityEventRepository eventRepository,
            SecurityAlertRepository alertRepository) {
        this.eventRepository = eventRepository;
        this.alertRepository = alertRepository;
    }

    /**
     * Generate security statistics dashboard data.
     */
    public SecurityStatsResponse getSecurityStats() {
        long totalEvents = eventRepository.count();
        long failedLogins = countEventsByType("LOGIN_FAILURE");
        long successfulLogins = countEventsByType("LOGIN_SUCCESS");
        long activeAlerts = countAlertsByStatus("OPEN");
        long resolvedAlerts = countAlertsByStatus("RESOLVED");
        long criticalAlerts = countCriticalAlerts();

        return new SecurityStatsResponse(
                totalEvents,
                failedLogins,
                successfulLogins,
                activeAlerts,
                resolvedAlerts,
                criticalAlerts
        );
    }

    private long countEventsByType(String eventType) {
        // This would ideally use a repository query
        return eventRepository.findAll().stream()
                .filter(e -> e.getEventType().equalsIgnoreCase(eventType))
                .count();
    }

    private long countAlertsByStatus(String status) {
        return alertRepository.findByStatus(status).size();
    }

    private long countCriticalAlerts() {
        return alertRepository.findAll().stream()
                .filter(a -> "HIGH".equalsIgnoreCase(a.getSeverity()) && "OPEN".equalsIgnoreCase(a.getStatus()))
                .count();
    }
}
