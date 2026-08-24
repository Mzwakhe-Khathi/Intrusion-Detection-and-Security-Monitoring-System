package com.cloud.ids.service;

import com.cloud.ids.entity.SecurityAlert;
import com.cloud.ids.repository.SecurityAlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityAlertService {

    private final SecurityAlertRepository repository;

    public SecurityAlertService(SecurityAlertRepository repository) {
        this.repository = repository;
    }

    public List<SecurityAlert> getAlerts(String status) {
        return status == null ? repository.findAll() : repository.findByStatus(status);
    }

    public SecurityAlert resolve(Long id) {
        SecurityAlert alert = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + id));
        alert.setStatus("RESOLVED");
        return repository.save(alert);
    }
}