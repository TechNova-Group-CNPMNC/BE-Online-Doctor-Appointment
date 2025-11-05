package com.assignment.clinic.service;

import com.assignment.clinic.dto.AppointmentRequest;
import com.assignment.clinic.dto.AppointmentResponse;
import com.assignment.clinic.entity.Appointment;
import com.assignment.clinic.entity.Doctor;
import com.assignment.clinic.entity.Patient;
import com.assignment.clinic.entity.TimeSlot;
import com.assignment.clinic.repository.AppointmentRepository;
import com.assignment.clinic.repository.DoctorRepository;
import com.assignment.clinic.repository.PatientRepository;
import com.assignment.clinic.repository.TimeSlotRepository;
import com.assignment.clinic.util.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TimeSlotRepository timeSlotRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                             PatientRepository patientRepository,
                             DoctorRepository doctorRepository,
                             TimeSlotRepository timeSlotRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    /**
     * ✅ Tạo appointment mới - WITH AUTHORIZATION CHECK
     */
    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        // 🔒 STEP 1: Verify patient ownership
        Patient patient = patientRepository.findByIdWithUser(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + request.getPatientId()));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(patient.getUser().getId())) {
            throw new AccessDeniedException("You can only create appointments for yourself");
        }

        // STEP 2: Kiểm tra doctor tồn tại
        // STEP 2: Kiểm tra doctor tồn tại
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + request.getDoctorId()));

        // STEP 3: Kiểm tra time slot tồn tại và available
        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new IllegalArgumentException("Time slot not found with id: " + request.getTimeSlotId()));

        if (timeSlot.getStatus() != TimeSlot.Status.AVAILABLE) {
            throw new IllegalStateException("Time slot is not available");
        }

        // STEP 4: Kiểm tra time slot thuộc về đúng bác sĩ
        if (!timeSlot.getDoctor().getId().equals(request.getDoctorId())) {
            throw new IllegalArgumentException("Time slot does not belong to the specified doctor");
        }

        // STEP 5: Tạo appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setTimeSlot(timeSlot);
        appointment.setSymptoms(request.getSymptoms());
        appointment.setSuspectedDisease(request.getSuspectedDisease());
        appointment.setStatus(Appointment.Status.PENDING);
        appointment.setRescheduleCount(0);
        appointment.setReminderSent(false);

        // STEP 6: Lưu appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // STEP 7: Cập nhật status của time slot thành BOOKED
        timeSlot.setStatus(TimeSlot.Status.BOOKED);
        timeSlotRepository.save(timeSlot);

        return convertToAppointmentResponse(savedAppointment);
    }

    private AppointmentResponse convertToAppointmentResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setPatientId(appointment.getPatient().getId());
        response.setPatientName(appointment.getPatient().getFullName());
        response.setDoctorId(appointment.getDoctor().getId());
        response.setDoctorName(appointment.getDoctor().getFullName());
        response.setTimeSlotId(appointment.getTimeSlot().getId());
        response.setStartTime(appointment.getTimeSlot().getStartTime());
        response.setEndTime(appointment.getTimeSlot().getEndTime());
        response.setSymptoms(appointment.getSymptoms());
        response.setSuspectedDisease(appointment.getSuspectedDisease());
        response.setStatus(appointment.getStatus().name());
        return response;
    }
}
