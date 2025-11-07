package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;

/**
 * DTO cho rating của bác sĩ (hiển thị trong doctor detail)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRatingDTO {
    private Long ratingId;
    private String patientName;
    private Integer stars;
    private String feedbackText;
    private Instant createdAt;
}
