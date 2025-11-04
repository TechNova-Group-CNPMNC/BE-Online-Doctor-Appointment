package com.assignment.clinic.controller;

import com.assignment.clinic.dto.AppointmentRequest;
import com.assignment.clinic.dto.AppointmentResponse;
import com.assignment.clinic.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * API: Make an appointment
     * POST /api/appointments
     * 
     * Body:
     * {
     *   "patientId": 1,
     *   "doctorId": 2,
     *   "timeSlotId": 123,
     *   "symptoms": "Đau đầu, chóng mặt",
     *   "suspectedDisease": "Migraine"
     * }
     */
    @PostMapping
    public ResponseEntity<?> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        try {
            AppointmentResponse response = appointmentService.createAppointment(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
