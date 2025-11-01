package com.assignment.clinic.service;

import com.assignment.clinic.dto.DoctorDTO;
import com.assignment.clinic.dto.DoctorRegistrationRequest;
import com.assignment.clinic.entity.Doctor;
import com.assignment.clinic.entity.Specialty;
import com.assignment.clinic.entity.User;
import com.assignment.clinic.repository.DoctorRepository;
import com.assignment.clinic.repository.SpecialtyRepository;
import com.assignment.clinic.repository.UserRepository;
import com.assignment.clinic.constants.UserRole;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository, UserRepository userRepository,
                         SpecialtyRepository specialtyRepository, PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.specialtyRepository = specialtyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<DoctorDTO> getAllDoctors() {
        // Sử dụng findAll() để lấy danh sách Doctor Entities
        List<Doctor> doctors = doctorRepository.findAll();

        // Chuyển đổi List<Doctor> sang List<DoctorDTO>
        return doctors.stream()
                .map(DoctorDTO::new) // Sử dụng constructor DoctorDTO(Doctor doctor)
                .collect(Collectors.toList());
    }

    @Transactional // Đảm bảo cả hai thao tác lưu (User và Doctor) là atomic
    public Doctor registerNewDoctor(DoctorRegistrationRequest request) {

        // 1. Kiểm tra Email đã tồn tại
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email " + request.getEmail() + " is already taken.");
        }

        // 2. Tạo và Lưu User
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        // Mã hóa mật khẩu trước khi lưu
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(UserRole.DOCTOR);
        User savedUser = userRepository.save(newUser);
        // 3. Tìm kiếm Specialties
        Set<Specialty> specialties = new HashSet<>();
        if (request.getSpecialtyIds() != null && !request.getSpecialtyIds().isEmpty()) {
            specialties = request.getSpecialtyIds().stream()
                    .map(id -> specialtyRepository.findById(id).orElseThrow(
                            () -> new IllegalArgumentException("Specialty not found with ID: " + id)
                    ))
                    .collect(Collectors.toSet());
        }

        // 4. Tạo và Lưu Doctor
        Doctor newDoctor = new Doctor();
        newDoctor.setUser(savedUser); // Liên kết với User vừa tạo
        newDoctor.setFullName(request.getFullName());
        newDoctor.setDegree(request.getDegree());
        newDoctor.setBio(request.getBio());
        newDoctor.setSpecialties(specialties);

        return doctorRepository.save(newDoctor);
    }
}