package com.cloud.ids.controller;

import com.cloud.ids.entity.DetectionRule;
import com.cloud.ids.service.DetectionRuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detection-rules")
public class DetectionRuleController {

    private final DetectionRuleService service;

    public DetectionRuleController(DetectionRuleService service) {
        this.service = service;
    }

    @PostMapping
    public DetectionRule createRule(@RequestBody DetectionRule rule) {
        return service.createRule(rule);
    }

    @GetMapping
    public List<DetectionRule> getRules() {
        return service.getRules();
    }
}