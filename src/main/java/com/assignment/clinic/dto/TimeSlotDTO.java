package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status; // AVAILABLE, BOOKED
}
