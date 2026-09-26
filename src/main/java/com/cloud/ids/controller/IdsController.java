package com.cloud.ids.controller;

import com.cloud.ids.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IdsController {

    @GetMapping("/health")
    public ApiResponse health() {

        return new ApiResponse(
                true,
                "Server is healthy",
                null
        );
    }
}