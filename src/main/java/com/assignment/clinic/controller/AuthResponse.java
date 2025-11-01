package com.assignment.clinic.controller;

public class AuthResponse {
    private String email;
    private String role;
    private String message;
    private String token;

    public AuthResponse(String email, String role, String message, String token) {
        this.email = email;
        this.role = role;
        this.message = message;
        this.token = token;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
