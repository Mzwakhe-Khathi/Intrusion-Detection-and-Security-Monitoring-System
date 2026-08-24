package com.cloud.ids.service;

import com.cloud.ids.entity.SecurityEvent;
import com.cloud.ids.repository.SecurityEventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SecurityEventService {

    private final SecurityEventRepository repository;
    private final DetectionService detectionService;

    public SecurityEventService(
            SecurityEventRepository repository,
            DetectionService detectionService) {
        this.repository = repository;
        this.detectionService = detectionService;
    }

    public SecurityEvent createEvent(SecurityEvent event) {

        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }

        if (event.getStatus() == null) {
            event.setStatus("NEW");
        }

        SecurityEvent savedEvent = repository.save(event);
        detectionService.evaluate(savedEvent);
        return savedEvent;
    }

    public List<SecurityEvent> getAllEvents() {
        return repository.findAll();
    }
}