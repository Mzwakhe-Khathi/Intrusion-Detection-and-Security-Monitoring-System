package com.cloud.ids.repository;

import com.cloud.ids.entity.SecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityEventRepository
        extends JpaRepository<SecurityEvent, Long> {
}