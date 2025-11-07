package com.assignment.clinic.controller;

import com.assignment.clinic.dto.AppointmentRequest;
import com.assignment.clinic.dto.AppointmentResponse;
import com.assignment.clinic.dto.UpdateAppointmentRequest;
import com.assignment.clinic.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * API 1: Create Appointment (Book Appointment)
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

    /**
     * API 2: Cancel Appointment (Delete)
     * DELETE /api/appointments/{appointmentId}
     * 
     * - Chỉ có thể hủy trước 48h
     * - Giải phóng time slot về AVAILABLE
     */
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long appointmentId) {
        try {
            String message = appointmentService.cancelAppointment(appointmentId);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        }
    }

    /**
     * API 3: Update Appointment (Change symptoms or reschedule)
     * PUT /api/appointments/{appointmentId}
     * 
     * Body:
     * {
     *   "symptoms": "Đau đầu dữ dội, buồn nôn",        // Update bất kỳ lúc nào
     *   "suspectedDisease": "Migraine cấp tính",      // Update bất kỳ lúc nào
     *   "newTimeSlotId": 456                          // Chỉ đổi được trước 48h, tối đa 2 lần
     * }
     */
    @PutMapping("/{appointmentId}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody UpdateAppointmentRequest request) {
        try {
            AppointmentResponse response = appointmentService.updateAppointment(appointmentId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        }
    }

    /**
     * API 4: Get Appointments List
     * GET /api/appointments?patientId={patientId}&status={status}
     * 
     * Query Params:
     * - patientId (required): ID của patient
     * - status (optional): Filter theo PENDING, COMPLETED, CANCELED
     */
    @GetMapping
    public ResponseEntity<?> getAppointments(
            @RequestParam Long patientId,
            @RequestParam(required = false) String status) {
        try {
            List<AppointmentResponse> appointments = appointmentService.getAppointments(patientId, status);
            return ResponseEntity.ok(appointments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        }
    }
}
