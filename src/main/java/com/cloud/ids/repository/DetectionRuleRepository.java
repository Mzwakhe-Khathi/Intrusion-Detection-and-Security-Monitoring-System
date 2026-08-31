package com.cloud.ids.repository;

import com.cloud.ids.entity.DetectionRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetectionRuleRepository extends JpaRepository<DetectionRule, Long> {
    List<DetectionRule> findByEnabledTrue();
    Optional<DetectionRule> findByRuleTypeIgnoreCaseAndEnabledTrue(String ruleType);
}