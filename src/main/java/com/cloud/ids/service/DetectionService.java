package com.cloud.ids.service;

import com.cloud.ids.entity.DetectionRule;
import com.cloud.ids.entity.SecurityAlert;
import com.cloud.ids.entity.SecurityEvent;
import com.cloud.ids.repository.DetectionRuleRepository;
import com.cloud.ids.repository.SecurityAlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetectionService {

    private final DetectionRuleRepository ruleRepository;
    private final SecurityAlertRepository alertRepository;

    public DetectionService(
            DetectionRuleRepository ruleRepository,
            SecurityAlertRepository alertRepository) {
        this.ruleRepository = ruleRepository;
        this.alertRepository = alertRepository;
    }

    public List<SecurityAlert> evaluate(SecurityEvent event) {
        return ruleRepository.findByEnabledTrue().stream()
                .filter(rule -> matches(rule, event))
                .map(rule -> createAlert(rule, event))
                .map(alertRepository::save)
                .toList();
    }

    private boolean matches(DetectionRule rule, SecurityEvent event) {
        return rule.getRuleType().equalsIgnoreCase(event.getEventType())
                || event.getDescription().toLowerCase()
                .contains(rule.getRuleType().toLowerCase());
    }

    private SecurityAlert createAlert(DetectionRule rule, SecurityEvent event) {
        SecurityAlert alert = new SecurityAlert();
        alert.setAlertType(rule.getRuleType());
        alert.setSeverity(event.getSeverity());
        alert.setSourceIp(event.getSourceIp());
        alert.setUsername(event.getUsername());
        alert.setSource("DetectionEngine");
        alert.setTimestamp(event.getTimestamp());
        alert.setDescription(rule.getDescription() + ": " + event.getDescription());
        return alert;
    }
}