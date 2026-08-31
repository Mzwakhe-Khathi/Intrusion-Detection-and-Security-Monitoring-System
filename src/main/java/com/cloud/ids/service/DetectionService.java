package com.cloud.ids.service;

import com.cloud.ids.entity.DetectionRule;
import com.cloud.ids.entity.SecurityAlert;
import com.cloud.ids.entity.SecurityEvent;
import com.cloud.ids.repository.DetectionRuleRepository;
import com.cloud.ids.repository.SecurityAlertRepository;
import com.cloud.ids.repository.SecurityEventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DetectionService {

    private final DetectionRuleRepository ruleRepository;
    private final SecurityAlertRepository alertRepository;
    private final SecurityEventRepository eventRepository;

    public DetectionService(
            DetectionRuleRepository ruleRepository,
            SecurityAlertRepository alertRepository,
            SecurityEventRepository eventRepository) {
        this.ruleRepository = ruleRepository;
        this.alertRepository = alertRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Evaluate an incoming security event against all enabled detection rules.
     * Returns a list of alerts that were generated.
     */
    public List<SecurityAlert> evaluate(SecurityEvent event) {
        return ruleRepository.findByEnabledTrue().stream()
                .filter(rule -> shouldCheckRule(rule, event))
                .flatMap(rule -> checkRule(rule, event).stream())
                .toList();
    }

    /**
     * Determine if a rule should be checked for this event type.
     */
    private boolean shouldCheckRule(DetectionRule rule, SecurityEvent event) {
        String ruleType = rule.getRuleType();
        
        // Check for brute-force detection on login failures
        if ("BRUTE_FORCE".equalsIgnoreCase(ruleType)) {
            return "LOGIN_FAILURE".equalsIgnoreCase(event.getEventType());
        }
        
        // Default matching
        return rule.getRuleType().equalsIgnoreCase(event.getEventType())
                || event.getDescription().toLowerCase()
                .contains(rule.getRuleType().toLowerCase());
    }

    /**
     * Check if a rule is triggered by this event.
     * Returns a list of alerts (empty if rule not triggered, or if alert already exists).
     */
    private List<SecurityAlert> checkRule(DetectionRule rule, SecurityEvent event) {
        if ("BRUTE_FORCE".equalsIgnoreCase(rule.getRuleType())) {
            return checkBruteForceRule(rule, event);
        }
        
        // Default behavior: create alert if rule matches
        return List.of(createAlert(rule, event));
    }

    /**
     * Check if a brute-force rule is triggered.
     * Rule is triggered when there are >= threshold failed logins from the same IP
     * within the specified time window.
     */
    private List<SecurityAlert> checkBruteForceRule(DetectionRule rule, SecurityEvent event) {
        LocalDateTime until = LocalDateTime.now();
        LocalDateTime since = until.minusMinutes(rule.getTimeWindowMinutes());
        
        // Count failed login attempts from this IP in the time window
        long failureCount = eventRepository.countFailedLoginsByIpAndTime(
                event.getSourceIp(),
                since,
                until
        );
        
        // If threshold is met
        if (failureCount >= rule.getThreshold()) {
            // Check if an open alert already exists to prevent duplicates
            var existingAlert = alertRepository.findOpenAlertByTypeAndIp(
                    "BRUTE_FORCE",
                    event.getSourceIp()
            );
            
            if (existingAlert.isEmpty()) {
                // Create a new alert
                SecurityAlert alert = new SecurityAlert();
                alert.setAlertType("BRUTE_FORCE");
                alert.setSeverity("HIGH");
                alert.setSourceIp(event.getSourceIp());
                alert.setUsername(event.getUsername());
                alert.setSource("DetectionEngine");
                alert.setTimestamp(LocalDateTime.now());
                alert.setDescription(String.format(
                        "Brute-force attack detected: %d failed login attempts from IP %s within %d minutes",
                        failureCount,
                        event.getSourceIp(),
                        rule.getTimeWindowMinutes()
                ));
                
                SecurityAlert saved = alertRepository.save(alert);
                return List.of(saved);
            }
        }
        
        return List.of();
    }

    /**
     * Create a standard security alert from a rule and event.
     */
    private SecurityAlert createAlert(DetectionRule rule, SecurityEvent event) {
        SecurityAlert alert = new SecurityAlert();
        alert.setAlertType(rule.getRuleType());
        alert.setSeverity(event.getSeverity());
        alert.setSourceIp(event.getSourceIp());
        alert.setUsername(event.getUsername());
        alert.setSource("DetectionEngine");
        alert.setTimestamp(event.getTimestamp());
        alert.setDescription(rule.getDescription() + ": " + event.getDescription());
        return alertRepository.save(alert);
    }
}