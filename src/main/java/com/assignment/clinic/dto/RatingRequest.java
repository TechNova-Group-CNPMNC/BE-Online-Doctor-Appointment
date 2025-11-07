package com.assignment.clinic.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO cho request tạo rating và feedback
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {

    @NotNull(message = "Stars is required")
    @Min(value = 1, message = "Stars must be at least 1")
    @Max(value = 5, message = "Stars must be at most 5")
    private Integer stars;

    private String feedbackText; // Optional - Có thể null hoặc rỗng
}
