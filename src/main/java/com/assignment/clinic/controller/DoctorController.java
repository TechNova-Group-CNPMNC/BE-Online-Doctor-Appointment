package com.assignment.clinic.controller;

import com.assignment.clinic.entity.Doctor;
import com.assignment.clinic.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/register")
    public ResponseEntity<Doctor> registerDoctor(@Valid @RequestBody DoctorRegisterRequest request) {
        Doctor registeredDoctor = doctorService.registerDoctor(
                request.getEmail(),
                request.getPassword(),
                request.getFullName(),
                request.getDegree(),
                request.getBio(),
                request.getSpecialtyNames()
        );
        return new ResponseEntity<>(registeredDoctor, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialty) {
        List<Doctor> doctors = doctorService.searchDoctors(name, specialty);
        return ResponseEntity.ok(doctors);
    }
}
