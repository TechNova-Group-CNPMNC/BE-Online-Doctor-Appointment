package com.assignment.clinic.controller;

import com.assignment.clinic.dto.PatientProfileResponse;
import com.assignment.clinic.dto.PatientUpdateRequest;
import com.assignment.clinic.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý các API liên quan đến thông tin cá nhân của bệnh nhân
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * API: Xem thông tin cá nhân của bệnh nhân
     * Endpoint: GET /api/patients/{patientId}/profile
     * Authorization: PATIENT role only (chỉ xem thông tin của chính mình)
     */
    @GetMapping("/{patientId}/profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientProfileResponse> getPatientProfile(@PathVariable Long patientId) {
        PatientProfileResponse profile = patientService.getPatientProfile(patientId);
        return ResponseEntity.ok(profile);
    }

    /**
     * API: Cập nhật thông tin cá nhân của bệnh nhân
     * Endpoint: PUT /api/patients/{patientId}/profile
     * Authorization: PATIENT role only (chỉ cập nhật thông tin của chính mình)
     */
    @PutMapping("/{patientId}/profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientProfileResponse> updatePatientProfile(
            @PathVariable Long patientId,
            @Valid @RequestBody PatientUpdateRequest request) {
        PatientProfileResponse updatedProfile = patientService.updatePatientProfile(patientId, request);
        return ResponseEntity.ok(updatedProfile);
    }
}
