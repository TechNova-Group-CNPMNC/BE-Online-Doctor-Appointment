package com.assignment.clinic.service;

import com.assignment.clinic.dto.AppointmentRequest;
import com.assignment.clinic.dto.AppointmentResponse;
import com.assignment.clinic.dto.UpdateAppointmentRequest;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * ✅ API 2: Cancel Appointment (Delete) - Chỉ được hủy trước 48h
     */
    @Transactional
    public String cancelAppointment(Long appointmentId) {
        // STEP 1: Tìm appointment với patient info để check authorization
        Appointment appointment = appointmentRepository.findByIdWithPatient(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));

        // STEP 2: Authorization check - Chỉ patient tạo appointment mới được hủy
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(appointment.getPatient().getUser().getId())) {
            throw new AccessDeniedException("You can only cancel your own appointments");
        }

        // STEP 3: Kiểm tra status - Không thể hủy appointment đã CANCELED hoặc COMPLETED
        if (appointment.getStatus() == Appointment.Status.CANCELED) {
            throw new IllegalStateException("Appointment is already canceled");
        }
        if (appointment.getStatus() == Appointment.Status.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed appointment");
        }

        // STEP 4: Kiểm tra thời gian - Phải hủy trước 48h
        LocalDateTime appointmentTime = appointment.getTimeSlot().getStartTime();
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilAppointment = ChronoUnit.HOURS.between(now, appointmentTime);

        if (hoursUntilAppointment < 48) {
            throw new IllegalStateException("Cannot cancel appointment. Must cancel at least 48 hours in advance. " +
                    "Only " + hoursUntilAppointment + " hours remaining.");
        }

        // STEP 5: Cập nhật status appointment thành CANCELED
        appointment.setStatus(Appointment.Status.CANCELED);
        appointmentRepository.save(appointment);

        // STEP 6: Giải phóng time slot (chuyển status từ BOOKED → AVAILABLE)
        TimeSlot timeSlot = appointment.getTimeSlot();
        timeSlot.setStatus(TimeSlot.Status.AVAILABLE);
        timeSlotRepository.save(timeSlot);

        return "Appointment canceled successfully. Time slot is now available for other patients.";
    }

    /**
     * ✅ API 3: Update Appointment - Update symptoms/time với validation
     */
    @Transactional
    public AppointmentResponse updateAppointment(Long appointmentId, UpdateAppointmentRequest request) {
        // STEP 1: Tìm appointment với patient info để check authorization
        Appointment appointment = appointmentRepository.findByIdWithPatient(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));

        // STEP 2: Authorization check - Chỉ patient tạo appointment mới được update
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(appointment.getPatient().getUser().getId())) {
            throw new AccessDeniedException("You can only update your own appointments");
        }

        // STEP 3: Kiểm tra status - Không thể update appointment đã CANCELED hoặc COMPLETED
        if (appointment.getStatus() == Appointment.Status.CANCELED) {
            throw new IllegalStateException("Cannot update canceled appointment");
        }
        if (appointment.getStatus() == Appointment.Status.COMPLETED) {
            throw new IllegalStateException("Cannot update completed appointment");
        }

        // STEP 4: Update symptoms và suspected disease (không giới hạn thời gian/số lần)
        if (request.getSymptoms() != null && !request.getSymptoms().isBlank()) {
            appointment.setSymptoms(request.getSymptoms());
        }
        if (request.getSuspectedDisease() != null && !request.getSuspectedDisease().isBlank()) {
            appointment.setSuspectedDisease(request.getSuspectedDisease());
        }

        // STEP 5: Update time slot (nếu có yêu cầu đổi lịch)
        if (request.getNewTimeSlotId() != null) {
            // 5.1: Kiểm tra thời gian - Phải đổi lịch trước 48h
            LocalDateTime appointmentTime = appointment.getTimeSlot().getStartTime();
            LocalDateTime now = LocalDateTime.now();
            long hoursUntilAppointment = ChronoUnit.HOURS.between(now, appointmentTime);

            if (hoursUntilAppointment < 48) {
                throw new IllegalStateException("Cannot reschedule appointment. Must reschedule at least 48 hours in advance. " +
                        "Only " + hoursUntilAppointment + " hours remaining.");
            }

            // 5.2: Kiểm tra số lần reschedule - Tối đa 2 lần
            if (appointment.getRescheduleCount() >= 2) {
                throw new IllegalStateException("Cannot reschedule appointment. Maximum 2 reschedules allowed. " +
                        "Current reschedule count: " + appointment.getRescheduleCount());
            }

            // 5.3: Kiểm tra new time slot tồn tại và available
            TimeSlot newTimeSlot = timeSlotRepository.findById(request.getNewTimeSlotId())
                    .orElseThrow(() -> new IllegalArgumentException("New time slot not found with id: " + request.getNewTimeSlotId()));

            if (newTimeSlot.getStatus() != TimeSlot.Status.AVAILABLE) {
                throw new IllegalStateException("New time slot is not available");
            }

            // 5.4: Kiểm tra new time slot thuộc về cùng bác sĩ
            if (!newTimeSlot.getDoctor().getId().equals(appointment.getDoctor().getId())) {
                throw new IllegalArgumentException("New time slot does not belong to the same doctor");
            }

            // 5.5: Giải phóng old time slot (chuyển BOOKED → AVAILABLE)
            TimeSlot oldTimeSlot = appointment.getTimeSlot();
            oldTimeSlot.setStatus(TimeSlot.Status.AVAILABLE);
            timeSlotRepository.save(oldTimeSlot);

            // 5.6: Book new time slot (chuyển AVAILABLE → BOOKED)
            newTimeSlot.setStatus(TimeSlot.Status.BOOKED);
            timeSlotRepository.save(newTimeSlot);

            // 5.7: Cập nhật appointment với new time slot và tăng reschedule count
            appointment.setTimeSlot(newTimeSlot);
            appointment.setRescheduleCount(appointment.getRescheduleCount() + 1);
        }

        // STEP 6: Lưu appointment
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return convertToAppointmentResponse(updatedAppointment);
    }

    /**
     * ✅ API 4: Get Appointments List - Với optional filter theo status
     */
    public List<AppointmentResponse> getAppointments(Long patientId, String statusFilter) {
        // STEP 1: Authorization check - Patient chỉ được xem appointments của mình
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Patient patient = patientRepository.findByIdWithUser(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + patientId));

        if (!currentUserId.equals(patient.getUser().getId())) {
            throw new AccessDeniedException("You can only view your own appointments");
        }

        // STEP 2: Get appointments với optional filter
        List<Appointment> appointments;
        
        if (statusFilter != null && !statusFilter.isBlank()) {
            // Filter theo status
            try {
                Appointment.Status status = Appointment.Status.valueOf(statusFilter.toUpperCase());
                appointments = appointmentRepository.findByPatientIdAndStatusOrderByTimeSlotStartTimeDesc(patientId, status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status filter. Valid values: PENDING, COMPLETED, CANCELED");
            }
        } else {
            // Không filter, lấy tất cả
            appointments = appointmentRepository.findByPatientIdOrderByTimeSlotStartTimeDesc(patientId);
        }

        // STEP 3: Convert to response DTOs
        return appointments.stream()
                .map(this::convertToAppointmentResponse)
                .collect(Collectors.toList());
    }
}
