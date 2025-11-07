package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;

/**
 * DTO cho response của rating
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {

    private Long ratingId;
    private Long appointmentId;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Integer stars;
    private String feedbackText;
    private Instant createdAt;
}
