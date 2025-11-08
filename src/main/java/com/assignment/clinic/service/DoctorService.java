package com.assignment.clinic.service;

import com.assignment.clinic.dto.DoctorDTO;
import com.assignment.clinic.dto.DoctorDetailDTO;
import com.assignment.clinic.dto.DoctorRatingDTO;
import com.assignment.clinic.dto.DoctorRegistrationRequest;
import com.assignment.clinic.dto.DoctorSearchDTO;
import com.assignment.clinic.dto.TimeSlotDTO;
import com.assignment.clinic.entity.AvailabilityBlock;
import com.assignment.clinic.entity.Doctor;
import com.assignment.clinic.entity.Rating;
import com.assignment.clinic.entity.Specialty;
import com.assignment.clinic.entity.TimeSlot;
import com.assignment.clinic.entity.User;
import com.assignment.clinic.repository.AvailabilityBlockRepository;
import com.assignment.clinic.repository.DoctorRepository;
import com.assignment.clinic.repository.RatingRepository;
import com.assignment.clinic.repository.SpecialtyRepository;
import com.assignment.clinic.repository.TimeSlotRepository;
import com.assignment.clinic.repository.UserRepository;
import com.assignment.clinic.constants.UserRole;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;
    private final PasswordEncoder passwordEncoder;
    private final AvailabilityBlockRepository availabilityBlockRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final RatingRepository ratingRepository;

    public DoctorService(DoctorRepository doctorRepository, UserRepository userRepository,
                         SpecialtyRepository specialtyRepository, PasswordEncoder passwordEncoder,
                         AvailabilityBlockRepository availabilityBlockRepository,
                         TimeSlotRepository timeSlotRepository,
                         RatingRepository ratingRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.specialtyRepository = specialtyRepository;
        this.passwordEncoder = passwordEncoder;
        this.availabilityBlockRepository = availabilityBlockRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.ratingRepository = ratingRepository;
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
            throw new IllegalStateException("Email " + request.getEmail() + " đã được sử dụng.");
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
                            () -> new IllegalArgumentException("Không tìm thấy chuyên khoa với ID: " + id)
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

    @Transactional(readOnly = true)
    public List<Doctor> searchDoctors(String name, Long specialtyId) {
        if (name != null && !name.trim().isEmpty() && specialtyId != null) {
            return doctorRepository.findByFullNameContainingIgnoreCaseAndSpecialties_Id(name, specialtyId);
        } else if (name != null && !name.trim().isEmpty()) {
            return doctorRepository.findByFullNameContainingIgnoreCase(name);
        } else if (specialtyId != null) {
            return doctorRepository.findBySpecialties_Id(specialtyId);
        } else {
            return doctorRepository.findAll();
        }
    }

    /**
     * API 2: Search doctors theo specialty, name và date
     */
    @Transactional(readOnly = true)
    public List<DoctorSearchDTO> searchDoctorsWithAvailability(Long specialtyId, String name, LocalDate date) {
        List<Doctor> doctors;
        
        if (specialtyId != null && name != null && !name.trim().isEmpty()) {
            // Có specialty và name
            doctors = doctorRepository.findDoctorsByNameAndSpecialtyAndDate(name, specialtyId, date);
        } else if (specialtyId != null) {
            // Chỉ có specialty
            doctors = doctorRepository.findDoctorsBySpecialtyAndDate(specialtyId, date);
        } else {
            // Chỉ có date (hoặc không có gì cả)
            doctors = doctorRepository.findDoctorsWithAvailabilityOnDate(date);
        }
        
        return doctors.stream()
                .map(this::convertToDoctorSearchDTO)
                .collect(Collectors.toList());
    }

    /**
     * API 3: Get detail information (for patient to make appointment)
     */
    @Transactional(readOnly = true)
    public DoctorDetailDTO getDoctorDetailForAppointment(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với ID: " + doctorId));
        
        // Lấy 7 ngày kế tiếp từ ngày hiện tại
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(7);
        
        // Lấy tất cả availability blocks trong 7 ngày tới
        List<AvailabilityBlock> blocks = availabilityBlockRepository
                .findByDoctorIdAndWorkDateBetween(doctorId, today, endDate);
        
        // Lấy các ngày có lịch (distinct)
        List<LocalDate> availableDates = blocks.stream()
                .map(AvailabilityBlock::getWorkDate)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        // Lấy tất cả time slots trong 7 ngày tới
        List<TimeSlot> allTimeSlots = timeSlotRepository
                .findByDoctorIdAndDateRange(doctorId, today, endDate);
        
        // Nhóm time slots theo ngày
        Map<LocalDate, List<TimeSlotDTO>> timeSlotsByDate = allTimeSlots.stream()
                .collect(Collectors.groupingBy(
                    ts -> ts.getStartTime().toLocalDate(),
                    Collectors.mapping(this::convertToTimeSlotDTO, Collectors.toList())
                ));
        
        // Sort time slots trong mỗi ngày
        timeSlotsByDate.values().forEach(list -> 
            list.sort(Comparator.comparing(TimeSlotDTO::getStartTime))
        );
        
        // Lấy ratings của bác sĩ (tối đa 10 ratings gần nhất)
        List<Rating> ratings = ratingRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId);
        List<DoctorRatingDTO> ratingDTOs = ratings.stream()
                .limit(10) // Giới hạn 10 ratings
                .map(this::convertToDoctorRatingDTO)
                .collect(Collectors.toList());
        
        return convertToDoctorDetailDTO(doctor, availableDates, timeSlotsByDate, ratingDTOs, ratings.size());
    }

    // Helper methods để convert entity sang DTO
    private DoctorSearchDTO convertToDoctorSearchDTO(Doctor doctor) {
        DoctorSearchDTO dto = new DoctorSearchDTO();
        dto.setId(doctor.getId());
        dto.setFullName(doctor.getFullName());
        dto.setDegree(doctor.getDegree());
        dto.setBio(doctor.getBio());
        dto.setAverageRating(doctor.getAverageRating());
        dto.setSpecialties(doctor.getSpecialties().stream()
                .map(Specialty::getName)
                .collect(Collectors.toList()));
        return dto;
    }

    private DoctorDetailDTO convertToDoctorDetailDTO(Doctor doctor, List<LocalDate> availableDates,
                                                      Map<LocalDate, List<TimeSlotDTO>> timeSlotsByDate,
                                                      List<DoctorRatingDTO> ratings,
                                                      Integer totalRatings) {
        DoctorDetailDTO dto = new DoctorDetailDTO();
        dto.setId(doctor.getId());
        dto.setFullName(doctor.getFullName());
        dto.setDegree(doctor.getDegree());
        dto.setBio(doctor.getBio());
        dto.setAverageRating(doctor.getAverageRating());
        dto.setSpecialties(doctor.getSpecialties().stream()
                .map(Specialty::getName)
                .collect(Collectors.toList()));
        dto.setAvailableDates(availableDates);
        dto.setTimeSlotsByDate(timeSlotsByDate);
        dto.setRatings(ratings);
        dto.setTotalRatings(totalRatings);
        return dto;
    }

    private TimeSlotDTO convertToTimeSlotDTO(TimeSlot timeSlot) {
        return new TimeSlotDTO(
            timeSlot.getId(),
            timeSlot.getStartTime(),
            timeSlot.getEndTime(),
            timeSlot.getStatus().name()
        );
    }
    
    private DoctorRatingDTO convertToDoctorRatingDTO(Rating rating) {
        DoctorRatingDTO dto = new DoctorRatingDTO();
        dto.setRatingId(rating.getId());
        dto.setPatientName(rating.getPatient().getFullName());
        dto.setStars(rating.getStars());
        dto.setFeedbackText(rating.getFeedbackText());
        dto.setCreatedAt(rating.getCreatedAt());
        return dto;
    }
}