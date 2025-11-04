package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSearchDTO {
    private Long id;
    private String fullName;
    private String degree;
    private String bio;
    private BigDecimal averageRating;
    private List<String> specialties;
}
