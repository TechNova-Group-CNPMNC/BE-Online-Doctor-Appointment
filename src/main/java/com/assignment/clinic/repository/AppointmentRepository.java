package com.assignment.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.assignment.clinic.entity.Appointment;
import com.assignment.clinic.entity.Appointment.Status;
import java.util.List;
import java.util.Optional;

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
    
    // Tìm appointment với patient info (cho authorization check)
    @Query("SELECT a FROM Appointment a JOIN FETCH a.patient p JOIN FETCH p.user WHERE a.id = :appointmentId")
    Optional<Appointment> findByIdWithPatient(@Param("appointmentId") Long appointmentId);
    
    // Tìm appointments của patient theo status (optional filter)
    List<Appointment> findByPatientIdAndStatusOrderByTimeSlotStartTimeDesc(Long patientId, Status status);
    
    // Tìm appointments của doctor theo status (optional filter)
    List<Appointment> findByDoctorIdAndStatusOrderByTimeSlotStartTimeAsc(Long doctorId, Status status);
    
    // Tìm appointments của doctor theo ngày cụ thể
    @Query("SELECT a FROM Appointment a " +
           "JOIN FETCH a.timeSlot ts " +
           "JOIN FETCH a.patient p " +
           "JOIN FETCH a.doctor d " +
           "WHERE d.id = :doctorId " +
           "AND FUNCTION('DATE', ts.startTime) = :date " +
           "ORDER BY ts.startTime ASC")
    List<Appointment> findByDoctorIdAndDate(@Param("doctorId") Long doctorId, 
                                            @Param("date") java.time.LocalDate date);
}