package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotRequest {
    
    @NotBlank(message = "Symptoms description is required")
    private String symptoms;
    
    // Optional: Thông tin bổ sung để AI phân tích tốt hơn
    private String age;
    private String gender;
    private String medicalHistory;
}
