package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDate;

/**
 * Request để lấy danh sách bác sĩ phù hợp
 * Dùng sau khi user đã nhận gợi ý chuyên khoa từ AI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSuggestionRequest {
    
    /**
     * ID của chuyên khoa (từ AI response)
     */
    @NotNull(message = "Specialty ID is required")
    private Long specialtyId;
    
    /**
     * Ngày muốn khám (optional, mặc định là hôm nay)
     * Format: yyyy-MM-dd
     */
    @FutureOrPresent(message = "Appointment date must be today or in the future")
    private LocalDate appointmentDate;
    
    /**
     * Số ngày tìm kiếm về sau (optional, mặc định 7 ngày)
     */
    private Integer searchDays = 7;
}
