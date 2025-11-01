package com.assignment.clinic.service;

import com.assignment.clinic.constants.UserRole;
import com.assignment.clinic.entity.Doctor;
import com.assignment.clinic.entity.Specialty;
import com.assignment.clinic.entity.User;
import com.assignment.clinic.repository.DoctorRepository;
import com.assignment.clinic.repository.SpecialtyRepository;
import com.assignment.clinic.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DoctorService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;

    public DoctorService(UserRepository userRepository, PasswordEncoder passwordEncoder, DoctorRepository doctorRepository, SpecialtyRepository specialtyRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.doctorRepository = doctorRepository;
        this.specialtyRepository = specialtyRepository;
    }

    @Transactional
    public Doctor registerDoctor(String email, String password, String fullName, String degree, String bio, Set<String> specialtyNames) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        User user = new User(email, passwordEncoder.encode(password), UserRole.DOCTOR);
        User savedUser = userRepository.save(user);

        Set<Specialty> specialties = new HashSet<>();
        if (specialtyNames != null && !specialtyNames.isEmpty()) {
            for (String specialtyName : specialtyNames) {
                Specialty specialty = specialtyRepository.findByName(specialtyName)
                        .orElseGet(() -> {
                            Specialty newSpecialty = new Specialty();
                            newSpecialty.setName(specialtyName);
                            return specialtyRepository.save(newSpecialty);
                        });
                specialties.add(specialty);
            }
        }

        Doctor doctor = new Doctor();
        doctor.setUser(savedUser);
        doctor.setFullName(fullName);
        doctor.setDegree(degree);
        doctor.setBio(bio);
        doctor.setSpecialties(specialties);

        return doctorRepository.save(doctor);
    }

    @Transactional(readOnly = true)
    public List<Doctor> searchDoctors(String name, String specialty) {
        if (name != null && !name.trim().isEmpty()) {
            return doctorRepository.findByFullNameContainingIgnoreCase(name);
        } else if (specialty != null && !specialty.trim().isEmpty()) {
            return doctorRepository.findBySpecialties_NameContainingIgnoreCase(specialty);
        } else {
            // If no search parameters are provided, return all doctors or throw an error
            return doctorRepository.findAll();
        }
    }
}
