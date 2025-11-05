package com.assignment.clinic.controller;

import com.assignment.clinic.dto.AuthResponse;
import com.assignment.clinic.dto.LoginRequest;
import com.assignment.clinic.dto.RegisterRequest;
import com.assignment.clinic.entity.User;
import com.assignment.clinic.entity.Doctor;
import com.assignment.clinic.entity.Patient;
import com.assignment.clinic.constants.UserRole;
import com.assignment.clinic.service.AuthService;
import com.assignment.clinic.repository.DoctorRepository;
import com.assignment.clinic.repository.PatientRepository;
import com.assignment.clinic.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AuthController(AuthService authService, JwtUtil jwtUtil, 
                          DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @PostMapping("test")
    public String test() {
        return "AuthController is working!";
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.registerUser(request.getEmail(), request.getPassword(), UserRole.PATIENT,
                    request.getFullName(), request.getDateOfBirth(), request.getGender(), request.getPhoneNumber());
            
            // Lấy patientId
            Long patientId = null;
            Optional<Patient> patientOpt = patientRepository.findByUserId(user.getId());
            if (patientOpt.isPresent()) {
                patientId = patientOpt.get().getId();
            }
            
            String token = jwtUtil.generateToken(user.getId(), user.getRole().name(), null, patientId);
            return ResponseEntity.ok(new AuthResponse(user.getEmail(), user.getRole().name(), 
                    "Registration successful", token, null, patientId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, null, e.getMessage(), null, null, null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Optional<User> userOptional = authService.authenticateUser(request.getEmail(), request.getPassword());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Lấy doctorId hoặc patientId dựa trên role
            Long doctorId = null;
            Long patientId = null;
            
            if (user.getRole() == UserRole.DOCTOR) {
                Optional<Doctor> doctorOpt = doctorRepository.findByUserId(user.getId());
                if (doctorOpt.isPresent()) {
                    doctorId = doctorOpt.get().getId();
                }
            } else if (user.getRole() == UserRole.PATIENT) {
                Optional<Patient> patientOpt = patientRepository.findByUserId(user.getId());
                if (patientOpt.isPresent()) {
                    patientId = patientOpt.get().getId();
                }
            }
            
            String token = jwtUtil.generateToken(user.getId(), user.getRole().name(), doctorId, patientId);
            return ResponseEntity.ok(new AuthResponse(user.getEmail(), user.getRole().name(), 
                    "Login successful", token, doctorId, patientId));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, "Invalid credentials", null, null, null));
        }
    }
}
