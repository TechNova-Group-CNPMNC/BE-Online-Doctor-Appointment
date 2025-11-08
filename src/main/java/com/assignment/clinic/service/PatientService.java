package com.assignment.clinic.service;

import com.assignment.clinic.dto.PatientProfileResponse;
import com.assignment.clinic.dto.PatientUpdateRequest;
import com.assignment.clinic.entity.Patient;
import com.assignment.clinic.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Lấy thông tin cá nhân của bệnh nhân
     */
    public PatientProfileResponse getPatientProfile(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bệnh nhân với ID: " + patientId));
        
        return PatientProfileResponse.builder()
            .patientId(patient.getId())
            .email(patient.getUser().getEmail())
            .fullName(patient.getFullName())
            .dateOfBirth(patient.getDateOfBirth())
            .gender(patient.getGender())
            .phoneNumber(patient.getPhoneNumber())
            .address(patient.getAddress())
            .medicalHistory(patient.getMedicalHistory())
            .build();
    }

    /**
     * Cập nhật thông tin cá nhân của bệnh nhân
     * CHỈ UPDATE CÁC FIELDS KHÔNG NULL - Partial update
     */
    @Transactional
    public PatientProfileResponse updatePatientProfile(Long patientId, PatientUpdateRequest request) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bệnh nhân với ID: " + patientId));
        
        // Chỉ update các fields không null (partial update)
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            patient.setFullName(request.getFullName());
        }
        
        if (request.getDateOfBirth() != null) {
            patient.setDateOfBirth(request.getDateOfBirth());
        }
        
        if (request.getGender() != null) {
            patient.setGender(request.getGender());
        }
        
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            patient.setPhoneNumber(request.getPhoneNumber());
        }
        
        if (request.getAddress() != null) {
            patient.setAddress(request.getAddress());
        }
        
        if (request.getMedicalHistory() != null) {
            patient.setMedicalHistory(request.getMedicalHistory());
        }
        
        Patient updatedPatient = patientRepository.save(patient);
        
        return PatientProfileResponse.builder()
            .patientId(updatedPatient.getId())
            .email(updatedPatient.getUser().getEmail())
            .fullName(updatedPatient.getFullName())
            .dateOfBirth(updatedPatient.getDateOfBirth())
            .gender(updatedPatient.getGender())
            .phoneNumber(updatedPatient.getPhoneNumber())
            .address(updatedPatient.getAddress())
            .medicalHistory(updatedPatient.getMedicalHistory())
            .build();
    }
}
