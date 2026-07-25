package com.cloud.ids.controller;

import com.cloud.ids.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class IdsController {

    @GetMapping("/")
    public ApiResponse home() {

        return new ApiResponse(
                true,
                "IDS Server is running",
                Map.of(
                        "application", "Cloud IDS",
                        "version", "1.0",
                        "time", LocalDateTime.now()
                )
        );
    }

    @GetMapping("/health")
    public ApiResponse health() {

        return new ApiResponse(
                true,
                "Server is healthy",
                null
        );
    }
}