package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Long timeSlotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String symptoms;
    private String suspectedDisease;
    private String status;
    
    // Patient medical history
    private String medicalHistory; // Tiền sử bệnh của bệnh nhân
    
    // Rating information (chỉ có khi appointment đã được rating)
    private Integer rating; // Số sao (1-5)
    private String feedback; // Nhận xét của bệnh nhân
}
