package com.assignment.clinic.service;

import com.assignment.clinic.dto.RatingRequest;
import com.assignment.clinic.dto.RatingResponse;
import com.assignment.clinic.entity.Appointment;
import com.assignment.clinic.entity.Doctor;
import com.assignment.clinic.entity.Rating;
import com.assignment.clinic.repository.AppointmentRepository;
import com.assignment.clinic.repository.DoctorRepository;
import com.assignment.clinic.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    /**
     * Tạo rating và feedback cho appointment
     * Chỉ appointment COMPLETED mới được rating
     * Mỗi appointment chỉ được rating 1 lần
     */
    @Transactional
    public RatingResponse createRating(Long appointmentId, RatingRequest request) {
        // 1. Kiểm tra appointment tồn tại
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + appointmentId));

        // 2. Kiểm tra appointment đã COMPLETED chưa
        if (appointment.getStatus() != Appointment.Status.COMPLETED) {
            throw new RuntimeException("Can only rate completed appointments. Current status: " + appointment.getStatus());
        }

        // 3. Kiểm tra appointment đã được rating chưa
        if (ratingRepository.findByAppointmentId(appointmentId).isPresent()) {
            throw new RuntimeException("This appointment has already been rated");
        }

        // 4. Tạo rating mới
        Rating rating = new Rating();
        rating.setAppointment(appointment);
        rating.setPatient(appointment.getPatient());
        rating.setDoctor(appointment.getDoctor());
        rating.setStars(request.getStars());
        rating.setFeedbackText(request.getFeedbackText());

        Rating savedRating = ratingRepository.save(rating);

        // 5. Tính toán lại average_rating của bác sĩ
        recalculateDoctorAverageRating(appointment.getDoctor().getId());

        // 6. Trả về response
        return convertToResponse(savedRating);
    }

    /**
     * Tính toán lại average rating của bác sĩ
     */
    private void recalculateDoctorAverageRating(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));

        // Lấy tất cả ratings của bác sĩ
        List<Rating> ratings = ratingRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId);

        if (ratings.isEmpty()) {
            doctor.setAverageRating(BigDecimal.ZERO);
        } else {
            // Tính trung bình cộng
            double sum = ratings.stream()
                    .mapToInt(Rating::getStars)
                    .sum();
            double average = sum / ratings.size();

            // Làm tròn đến 2 chữ số thập phân
            BigDecimal averageRating = BigDecimal.valueOf(average)
                    .setScale(2, RoundingMode.HALF_UP);

            doctor.setAverageRating(averageRating);
        }

        doctorRepository.save(doctor);
    }

    /**
     * Convert Rating entity sang RatingResponse DTO
     */
    private RatingResponse convertToResponse(Rating rating) {
        RatingResponse response = new RatingResponse();
        response.setRatingId(rating.getId());
        response.setAppointmentId(rating.getAppointment().getId());
        response.setPatientId(rating.getPatient().getId());
        response.setPatientName(rating.getPatient().getFullName());
        response.setDoctorId(rating.getDoctor().getId());
        response.setDoctorName(rating.getDoctor().getFullName());
        response.setStars(rating.getStars());
        response.setFeedbackText(rating.getFeedbackText());
        response.setCreatedAt(rating.getCreatedAt());
        return response;
    }
}
