package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityBlockDTO {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
