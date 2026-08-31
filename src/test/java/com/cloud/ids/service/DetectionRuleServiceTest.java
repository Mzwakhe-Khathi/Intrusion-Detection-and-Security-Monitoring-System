package com.cloud.ids.service;

import com.cloud.ids.entity.DetectionRule;
import com.cloud.ids.repository.DetectionRuleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DetectionRuleServiceTest {

    @Autowired
    private DetectionRuleRepository repository;

    @Autowired
    private DetectionRuleService service;

    @Test
    void ensureDefaultBruteForceRule_createsRuleWhenMissing() {
        repository.deleteAll();

        service.ensureDefaultBruteForceRule();

        DetectionRule rule = repository.findByRuleTypeIgnoreCaseAndEnabledTrue("BRUTE_FORCE")
                .orElseThrow();

        assertThat(rule.getName()).isEqualTo("Brute Force Detector");
        assertThat(rule.getThreshold()).isEqualTo(5);
        assertThat(rule.getTimeWindowMinutes()).isEqualTo(5);
        assertThat(rule.isEnabled()).isTrue();
    }
}
