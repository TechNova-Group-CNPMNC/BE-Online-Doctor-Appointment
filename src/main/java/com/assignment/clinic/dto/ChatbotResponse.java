package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * Response từ AI phân tích triệu chứng
 * CHỈ chứa dự đoán bệnh + gợi ý chuyên khoa (KHÔNG có danh sách bác sĩ)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotResponse {
    
    /**
     * Dự đoán bệnh từ AI
     * Ví dụ: "Có thể bạn đang bị Migraine (đau nửa đầu) hoặc Tension headache (đau đầu do căng thẳng)"
     */
    private String diseasePrediction;
    
    /**
     * Phân tích chi tiết triệu chứng từ AI
     */
    private String analysis;
    
    /**
     * Gợi ý chuyên khoa phù hợp (có thể nhiều)
     */
    private List<SpecialtySuggestion> suggestedSpecialties;
    
    /**
     * Cảnh báo cấp cứu
     */
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
}
