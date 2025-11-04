// File: com/assignment/clinic/dto/DoctorRegistrationRequest.java

package com.assignment.clinic.dto;

import java.util.Set;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DoctorRegistrationRequest {

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password must not be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Full name must not be empty")
    private String fullName;

    private String degree;
    private String bio;

    // Set<Long> để nhận ID của các chuyên khoa
    private Set<Long> specialtyIds;
}