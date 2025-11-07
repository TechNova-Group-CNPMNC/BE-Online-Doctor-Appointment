package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Response chứa danh sách bác sĩ phù hợp với chuyên khoa và ngày khám
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorSuggestionResponse {
    
    /**
     * Thông tin chuyên khoa được chọn
     */
    private Long specialtyId;
    private String specialtyName;
    
    /**
     * Danh sách bác sĩ có lịch trống
     */
    private List<DoctorWithAvailability> doctors;
    
    /**
     * Thông báo nếu không tìm thấy bác sĩ
     */
    private String message;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DoctorWithAvailability {
        // Thông tin bác sĩ
        private Long doctorId;
        private String doctorName;
        private String bio;
        private Double rating;
        
        // Danh sách lịch trống
        private List<AvailableSlot> availableSlots;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class AvailableSlot {
            private Long availabilityBlockId;
            private LocalDate date;
            private LocalTime startTime;
            private LocalTime endTime;
            private Integer availableSlots; // Số slot còn trống
        }
    }
}
