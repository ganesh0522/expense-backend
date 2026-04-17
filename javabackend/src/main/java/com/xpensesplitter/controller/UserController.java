package com.xpensesplitter.controller;

import com.xpensesplitter.dto.response.UserResponse;
import com.xpensesplitter.entity.User;
import com.xpensesplitter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // ✅ GET ALL USERS (SAFE VERSION)
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<User> users = userRepository.findAll();

        List<UserResponse> response = users.stream()
                .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail()))
                .toList();

        return ResponseEntity.ok(response);
    }
}