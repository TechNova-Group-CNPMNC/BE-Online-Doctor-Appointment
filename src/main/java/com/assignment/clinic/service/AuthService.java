package com.assignment.clinic.service;

import com.assignment.clinic.entity.Patient;
import com.assignment.clinic.entity.User;
import com.assignment.clinic.constants.UserRole;
import com.assignment.clinic.repository.PatientRepository;
import com.assignment.clinic.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import com.assignment.clinic.constants.Gender;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientRepository patientRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, PatientRepository patientRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.patientRepository = patientRepository;
    }

    public User registerUser(String email, String password, UserRole role, String fullName, LocalDate dateOfBirth, Gender gender, String phoneNumber) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }
        User user = new User(email, passwordEncoder.encode(password), role);
        User savedUser = userRepository.save(user);

        if (role == UserRole.PATIENT) {
            Patient patient = new Patient();
            patient.setUser(savedUser);
            patient.setFullName(fullName != null ? fullName : "New Patient");
            patient.setDateOfBirth(dateOfBirth);
            patient.setGender(gender);
            patient.setPhoneNumber(phoneNumber);
            patientRepository.save(patient);
        }

        return savedUser;
    }

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                return userOptional;
            }
        }
        return Optional.empty();
    }
}
