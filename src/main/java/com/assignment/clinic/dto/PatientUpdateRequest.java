package com.assignment.clinic.dto;

import com.assignment.clinic.constants.Gender;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO để cập nhật thông tin cá nhân của bệnh nhân
 * TẤT CẢ FIELDS ĐỀU OPTIONAL - Chỉ update những fields được gửi lên
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientUpdateRequest {
    
    // ⭕ Optional - Chỉ update nếu có giá trị
    private String fullName;
    
    // ⭕ Optional - Chỉ update nếu có giá trị
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    // ⭕ Optional - Chỉ update nếu có giá trị
    private Gender gender;
    
    // ⭕ Optional - Chỉ update nếu có giá trị
    private String phoneNumber;
    
    // ⭕ Optional - Chỉ update nếu có giá trị
    private String address;
    
    // ⭕ Optional - Chỉ update nếu có giá trị
    private String medicalHistory;
}
