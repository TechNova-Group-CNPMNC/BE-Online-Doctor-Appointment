package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDetailDTO {
    private Long id;
    private String fullName;
    private String degree;
    private String bio;
    private BigDecimal averageRating;
    private List<String> specialties;
    
    // Danh sách các ngày có lịch trong 7 ngày tới
    private List<LocalDate> availableDates;
    
    // Map: ngày -> danh sách time slots
    private Map<LocalDate, List<TimeSlotDTO>> timeSlotsByDate;
    
    // Danh sách ratings của bác sĩ (hiển thị tối đa 10 ratings gần nhất)
    private List<DoctorRatingDTO> ratings;
    
    // Tổng số ratings
    private Integer totalRatings;
}
