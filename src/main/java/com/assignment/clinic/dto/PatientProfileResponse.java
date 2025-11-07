package com.assignment.clinic.dto;

import com.assignment.clinic.constants.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Response DTO cho thông tin cá nhân của bệnh nhân
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientProfileResponse {
    
    private Long patientId;
    private String email;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String phoneNumber;
    private String address;
    private String medicalHistory;
}
