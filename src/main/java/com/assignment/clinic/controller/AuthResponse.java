package com.assignment.clinic.controller;

public class AuthResponse {
    private String email;
    private String role;
    private String message;

    public AuthResponse(String email, String role, String message) {
        this.email = email;
        this.role = role;
        this.message = message;
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
}
