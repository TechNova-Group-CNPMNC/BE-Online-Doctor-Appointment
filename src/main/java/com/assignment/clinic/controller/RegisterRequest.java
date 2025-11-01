package com.assignment.clinic.controller;

import com.assignment.clinic.constants.UserRole;
import com.assignment.clinic.constants.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private UserRole role;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String phoneNumber;
}
