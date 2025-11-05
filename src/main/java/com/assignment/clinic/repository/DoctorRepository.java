package com.assignment.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.assignment.clinic.entity.Doctor;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Tìm kiếm tất cả Doctors có rating lớn hơn hoặc bằng một giá trị
    List<Doctor> findByAverageRatingGreaterThanEqual(BigDecimal rating);

    List<Doctor> findByFullNameContainingIgnoreCase(String fullName);

    List<Doctor> findBySpecialties_Id(Long specialtyId);

    List<Doctor> findByFullNameContainingIgnoreCaseAndSpecialties_Id(String name, Long specialtyId);
    
    /**
     * Tìm doctor theo ID và load cả User để verify ownership
     */
    @Query("SELECT d FROM Doctor d JOIN FETCH d.user WHERE d.id = :doctorId")
    Optional<Doctor> findByIdWithUser(@Param("doctorId") Long doctorId);
    
    /**
     * Tìm doctor theo userId (từ JWT token)
     */
    @Query("SELECT d FROM Doctor d WHERE d.user.id = :userId")
    Optional<Doctor> findByUserId(@Param("userId") Long userId);
    
    // Tìm bác sĩ có lịch làm việc trong ngày cụ thể
    @Query("SELECT DISTINCT d FROM Doctor d " +
           "JOIN AvailabilityBlock ab ON ab.doctor.id = d.id " +
           "WHERE ab.workDate = :date")
    List<Doctor> findDoctorsWithAvailabilityOnDate(@Param("date") LocalDate date);
    
    // Tìm bác sĩ theo chuyên khoa và có lịch trong ngày cụ thể
    @Query("SELECT DISTINCT d FROM Doctor d " +
           "JOIN d.specialties s " +
           "JOIN AvailabilityBlock ab ON ab.doctor.id = d.id " +
           "WHERE s.id = :specialtyId AND ab.workDate = :date")
    List<Doctor> findDoctorsBySpecialtyAndDate(@Param("specialtyId") Long specialtyId, 
                                                @Param("date") LocalDate date);
    
    // Tìm bác sĩ theo tên, chuyên khoa và có lịch trong ngày cụ thể
    @Query("SELECT DISTINCT d FROM Doctor d " +
           "JOIN d.specialties s " +
           "JOIN AvailabilityBlock ab ON ab.doctor.id = d.id " +
           "WHERE s.id = :specialtyId " +
           "AND LOWER(d.fullName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND ab.workDate = :date")
    List<Doctor> findDoctorsByNameAndSpecialtyAndDate(@Param("name") String name,
                                                       @Param("specialtyId") Long specialtyId,
                                                       @Param("date") LocalDate date);
}
