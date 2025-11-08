package com.assignment.clinic.controller;

import com.assignment.clinic.dto.AppointmentResponse;
import com.assignment.clinic.dto.DoctorDTO;
import com.assignment.clinic.dto.DoctorDetailDTO;
import com.assignment.clinic.dto.DoctorRegistrationRequest;
import com.assignment.clinic.dto.DoctorSearchDTO;
import com.assignment.clinic.entity.Doctor;
import com.assignment.clinic.service.AppointmentService;
import com.assignment.clinic.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors") // Base URL cho Doctor API
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    // Dependency Injection qua Constructor
    public DoctorController(DoctorService doctorService, AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    /**
     * API 1: Get all doctors
     * GET /api/doctors
     */
    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        // Gọi Service và nhận về List<DoctorDTO>
        List<DoctorDTO> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    /**
     * API 2: Search doctors with availability
     * GET /api/doctors/search?specialty={specialtyId}&name={name}&date={date}
     * 
     * Params:
     * - specialty (optional): ID chuyên khoa
     * - name (optional): Tên bác sĩ
     * - date (required): Ngày cần tìm (format: yyyy-MM-dd)
     */
    @GetMapping("/search")
    public ResponseEntity<List<DoctorSearchDTO>> searchDoctorsWithAvailability(
            @RequestParam(required = false) Long specialty,
            @RequestParam(required = false) String name,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<DoctorSearchDTO> doctors = doctorService.searchDoctorsWithAvailability(specialty, name, date);
        return ResponseEntity.ok(doctors);
    }

    /**
     * API 3: Get doctor detail for appointment booking
     * GET /api/doctors/{id}/detail
     * 
     * Trả về:
     * - Thông tin bác sĩ
     * - Các ngày có lịch trong 7 ngày tới
     * - Time slots theo từng ngày
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<DoctorDetailDTO> getDoctorDetailForAppointment(@PathVariable Long id) {
        DoctorDetailDTO detail = doctorService.getDoctorDetailForAppointment(id);
        return ResponseEntity.ok(detail);
    }

    @PostMapping
    public ResponseEntity<?> registerDoctor(@Valid @RequestBody DoctorRegistrationRequest request) {
        try {
            Doctor newDoctor = doctorService.registerNewDoctor(request);
            // Trả về HTTP 201 Created cùng với đối tượng Doctor đã tạo
            return new ResponseEntity<>(newDoctor, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            // Xử lý trường hợp email đã tồn tại (HTTP 409 Conflict)
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Xử lý trường hợp không tìm thấy Specialty (HTTP 400 Bad Request)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
    
    /**
     * API: Get doctor appointments by date (DOCTOR ROLE ONLY)
     * GET /api/doctors/{doctorId}/appointments?date={date}
     * 
     * Authorization: Doctor chỉ xem được lịch hẹn của chính mình
     * 
     * @param doctorId ID của doctor
     * @param date Ngày cần xem lịch hẹn (format: yyyy-MM-dd)
     * @return Danh sách appointments trong ngày đó
     */
    @GetMapping("/{doctorId}/appointments")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointmentsByDate(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<AppointmentResponse> appointments = appointmentService.getDoctorAppointmentsByDate(doctorId, date);
        return ResponseEntity.ok(appointments);
    }
}