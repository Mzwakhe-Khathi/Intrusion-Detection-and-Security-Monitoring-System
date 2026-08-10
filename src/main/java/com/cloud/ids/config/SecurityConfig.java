package com.cloud.ids.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // Disable CSRF for our REST API and H2 console
                .csrf(csrf -> csrf.disable())

                // Control which endpoints require authentication
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/api/auth/login",
                                "/api/users",
                                "/h2-console/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // Allow H2 console to load inside the browser
                .headers(headers ->
                        headers.frameOptions(frame -> frame.disable())
                )

                // We are using JWT rather than HTTP Basic/Form Login
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }
}