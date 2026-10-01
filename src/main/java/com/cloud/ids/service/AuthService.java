package com.cloud.ids.service;

import com.cloud.ids.dto.LoginRequest;
import com.cloud.ids.dto.LoginResponse;
import com.cloud.ids.entity.SecurityEvent;
import com.cloud.ids.entity.User;
import com.cloud.ids.repository.UserRepository;
import com.cloud.ids.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecurityEventService securityEventService;
        private final BruteForceLockoutService lockoutService;

    public AuthService(
            UserRepository repository,
            BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService,
            SecurityEventService securityEventService,
            BruteForceLockoutService lockoutService) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.securityEventService = securityEventService;
        this.lockoutService = lockoutService;
    }

    public LoginResponse login(
            LoginRequest request,
            HttpServletRequest httpRequest) {

        String clientIp = httpRequest.getRemoteAddr();
        LocalDateTime now = LocalDateTime.now();

                if (lockoutService.isLocked(clientIp)) {
                        throw new RuntimeException("Too many failed login attempts. This IP is locked for 2 minutes.");
                }

        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> {

                    SecurityEvent event = new SecurityEvent(
                            "LOGIN_FAILURE",
                            clientIp,
                            request.getEmail(),
                            "Login attempt with unknown email",
                            "HIGH",
                            now,
                            "NEW"
                    );

                    securityEventService.createEvent(event);

                    return new RuntimeException("Invalid email or password");
                });

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            SecurityEvent event = new SecurityEvent(
                    "LOGIN_FAILURE",
                    clientIp,
                    request.getEmail(),
                    "Failed login attempt",
                    "HIGH",
                    now,
                    "NEW"
            );

            securityEventService.createEvent(event);

            throw new RuntimeException("Invalid email or password");
        }

        SecurityEvent event = new SecurityEvent(
                "LOGIN_SUCCESS",
                clientIp,
                user.getEmail(),
                "Successful login",
                "LOW",
                now,
                "NEW"
        );

        securityEventService.createEvent(event);

        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        return new LoginResponse(
                token,
                "Login successful"
        );
    }
}