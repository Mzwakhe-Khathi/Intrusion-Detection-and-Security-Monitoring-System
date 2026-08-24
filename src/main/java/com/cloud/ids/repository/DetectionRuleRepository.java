package com.cloud.ids.repository;

import com.cloud.ids.entity.DetectionRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetectionRuleRepository extends JpaRepository<DetectionRule, Long> {
    List<DetectionRule> findByEnabledTrue();
}