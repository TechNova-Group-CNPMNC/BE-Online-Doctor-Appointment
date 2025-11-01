package com.assignment.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.assignment.clinic.entity.Appointment;
import com.assignment.clinic.entity.Appointment.Status;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Tìm kiếm các cuộc hẹn của một Patient
    List<Appointment> findByPatientIdOrderByTimeSlotStartTimeDesc(Long patientId);

    // Tìm kiếm các cuộc hẹn của một Doctor
    List<Appointment> findByDoctorIdOrderByTimeSlotStartTimeAsc(Long doctorId);

    // Tìm kiếm các cuộc hẹn theo trạng thái
    List<Appointment> findByStatus(Status status);

    // Tìm kiếm các cuộc hẹn cần gửi reminder
    List<Appointment> findByReminderSentFalseAndStatus(Status status);
}