package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotResponse {
    
    // AI analysis của triệu chứng
    private String analysis;
    
    // Gợi ý chuyên khoa (có thể nhiều)
    private List<SpecialtySuggestion> suggestedSpecialties;
    
    // Gợi ý bác sĩ phù hợp
    private List<DoctorSuggestion> suggestedDoctors;
    
    // Cảnh báo nếu cần cấp cứu
    private boolean emergencyWarning;
    private String emergencyMessage;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SpecialtySuggestion {
        private Long specialtyId;
        private String specialtyName;
        private String reason;
        private Integer confidenceScore; // 0-100
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DoctorSuggestion {
        private Long doctorId;
        private String doctorName;
        private String specialty;
        private Double rating;
        private Boolean hasAvailableSlots;
    }
}
