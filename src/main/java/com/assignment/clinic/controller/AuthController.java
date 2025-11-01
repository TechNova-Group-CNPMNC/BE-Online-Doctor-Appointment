package com.assignment.clinic.controller;

import com.assignment.clinic.entity.User;
import com.assignment.clinic.constants.UserRole;
import com.assignment.clinic.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.registerUser(request.getEmail(), request.getPassword(), UserRole.PATIENT,
                    request.getFullName(), request.getDateOfBirth(), request.getGender(), request.getPhoneNumber());
            return ResponseEntity.ok(new AuthResponse(user.getEmail(), user.getRole().name(), "Registration successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponse(null, null, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Optional<User> userOptional = authService.authenticateUser(request.getEmail(), request.getPassword());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return ResponseEntity.ok(new AuthResponse(user.getEmail(), user.getRole().name(), "Login successful"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, null, "Invalid credentials"));
        }
    }
}
