package com.assignment.clinic.controller;

import com.assignment.clinic.dto.DoctorDTO;
import com.assignment.clinic.dto.DoctorRegistrationRequest;
import com.assignment.clinic.entity.Doctor;
import com.assignment.clinic.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/doctors") // Base URL cho Doctor API
public class DoctorController {

    private final DoctorService doctorService;

    // Dependency Injection qua Constructor
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        // Gọi Service và nhận về List<DoctorDTO>
        List<DoctorDTO> doctors = doctorService.getAllDoctors();

        return ResponseEntity.ok(doctors);
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
}