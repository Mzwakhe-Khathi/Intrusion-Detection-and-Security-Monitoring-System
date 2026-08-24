package com.cloud.ids.controller;

import com.cloud.ids.entity.SecurityAlert;
import com.cloud.ids.service.SecurityAlertService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class SecurityAlertController {

    private final SecurityAlertService service;

    public SecurityAlertController(SecurityAlertService service) {
        this.service = service;
    }

    @GetMapping
    public List<SecurityAlert> getAlerts(@RequestParam(required = false) String status) {
        return service.getAlerts(status);
    }

    @PatchMapping("/{id}/resolve")
    public SecurityAlert resolve(@PathVariable Long id) {
        return service.resolve(id);
    }
}