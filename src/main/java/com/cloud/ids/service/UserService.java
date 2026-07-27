package com.cloud.ids.service;

import com.cloud.ids.dto.UserRequest;
import com.cloud.ids.dto.UserResponse;
import com.cloud.ids.entity.User;
import com.cloud.ids.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserResponse createUser(UserRequest request) {

         if (repository.findByEmail(request.getEmail()).isPresent()) {
        throw new RuntimeException("Email already exists.");
    }


        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User saved = repository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getEmail()
        );
    }

    public List<UserResponse> getAllUsers() {

        return repository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail()
                ))
                .toList();
    }
}