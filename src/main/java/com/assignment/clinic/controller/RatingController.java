package com.assignment.clinic.controller;

import com.assignment.clinic.dto.RatingRequest;
import com.assignment.clinic.dto.RatingResponse;
import com.assignment.clinic.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller cho Rating & Comment API
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    /**
     * Tạo rating và feedback cho appointment
     * Endpoint: POST /api/appointments/{appointmentId}/rating
     * Authorization: PATIENT role only
     */
    @PostMapping("/{appointmentId}/rating")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<RatingResponse> createRating(
            @PathVariable Long appointmentId,
            @Valid @RequestBody RatingRequest request) {

        RatingResponse response = ratingService.createRating(appointmentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
