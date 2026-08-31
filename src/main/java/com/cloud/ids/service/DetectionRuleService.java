package com.cloud.ids.service;

import com.cloud.ids.entity.DetectionRule;
import com.cloud.ids.repository.DetectionRuleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetectionRuleService {

    private final DetectionRuleRepository repository;

    public DetectionRuleService(DetectionRuleRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void initializeDefaultRules() {
        ensureDefaultBruteForceRule();
    }

    public DetectionRule createRule(DetectionRule rule) {
        return repository.save(rule);
    }

    public List<DetectionRule> getRules() {
        return repository.findAll();
    }

    public DetectionRule ensureDefaultBruteForceRule() {
        return repository.findByRuleTypeIgnoreCaseAndEnabledTrue("BRUTE_FORCE")
                .orElseGet(() -> {
                    DetectionRule rule = new DetectionRule();
                    rule.setName("Brute Force Detector");
                    rule.setDescription("Detects multiple failed login attempts from the same IP within 5 minutes");
                    rule.setRuleType("BRUTE_FORCE");
                    rule.setEnabled(true);
                    rule.setThreshold(5);
                    rule.setTimeWindowMinutes(5);
                    return repository.save(rule);
                });
    }
}