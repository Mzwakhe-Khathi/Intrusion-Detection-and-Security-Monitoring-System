package com.cloud.ids.controller;

import com.cloud.ids.entity.SecurityEvent;
import com.cloud.ids.service.SecurityEventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security-events")
public class SecurityEventController {

    private final SecurityEventService service;

    public SecurityEventController(SecurityEventService service) {
        this.service = service;
    }

    @PostMapping
    public SecurityEvent createEvent(
            @RequestBody SecurityEvent event) {

        return service.createEvent(event);
    }

    @GetMapping
    public List<SecurityEvent> getAllEvents() {
        return service.getAllEvents();
    }
}