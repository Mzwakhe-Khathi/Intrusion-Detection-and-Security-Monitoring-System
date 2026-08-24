package com.cloud.ids.service;

import com.cloud.ids.entity.DetectionRule;
import com.cloud.ids.repository.DetectionRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetectionRuleService {

    private final DetectionRuleRepository repository;

    public DetectionRuleService(DetectionRuleRepository repository) {
        this.repository = repository;
    }

    public DetectionRule createRule(DetectionRule rule) {
        return repository.save(rule);
    }

    public List<DetectionRule> getRules() {
        return repository.findAll();
    }
}