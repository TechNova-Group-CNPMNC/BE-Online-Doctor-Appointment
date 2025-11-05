package com.assignment.clinic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

public class AuthResponse {
    private String email;
    private String role;
    private String message;
    private String token;
    private Long doctorId;  // ID của doctor nếu user là DOCTOR
    private Long patientId; // ID của patient nếu user là PATIENT

    public AuthResponse(String email, String role, String message, String token) {
        this.email = email;
        this.role = role;
        this.message = message;
        this.token = token;
    }

    public AuthResponse(String email, String role, String message, String token, Long doctorId, Long patientId) {
        this.email = email;
        this.role = role;
        this.message = message;
        this.token = token;
        this.doctorId = doctorId;
        this.patientId = patientId;
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

    public Long getDoctorId() {
        return doctorId;
    }

    public Long getPatientId() {
        return patientId;
    }

    @Data
    public static class DoctorRegisterRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        private String password;

        @NotBlank(message = "Full name is required")
        private String fullName;

        private String degree;

        private String bio;

        private Set<String> specialtyNames; // To receive specialty names
    }
}
