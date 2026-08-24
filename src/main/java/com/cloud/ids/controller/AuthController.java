package com.cloud.ids.controller;

import com.cloud.ids.dto.LoginRequest;
import com.cloud.ids.dto.LoginResponse;
import com.cloud.ids.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        return authService.login(request, httpRequest);
    }
}