package com.cloud.ids.controller;

import com.cloud.ids.dto.UserRequest;
import com.cloud.ids.dto.UserResponse;
import com.cloud.ids.service.UserService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;




@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

   @PostMapping
public UserResponse createUser(@Valid @RequestBody UserRequest request) {
    return service.createUser(request);
}

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return service.getAllUsers();
    }
}