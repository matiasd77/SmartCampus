package com.smartcampus.service.impl;

import com.smartcampus.dto.EnrollmentDTO;
import com.smartcampus.entity.Course;
import com.smartcampus.entity.Enrollment;
import com.smartcampus.entity.EnrollmentStatus;
import com.smartcampus.entity.Student;
import com.smartcampus.exception.CourseNotFoundException;
import com.smartcampus.exception.EnrollmentNotFoundException;
import com.smartcampus.exception.StudentNotFoundException;
import com.smartcampus.mapper.EnrollmentMapper;
import com.smartcampus.repository.CourseRepository;
import com.smartcampus.repository.EnrollmentRepository;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentDTO getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> EnrollmentNotFoundException.withId(id));
        return enrollmentMapper.toDto(enrollment);
    }

    @Override
    public EnrollmentDTO createEnrollment(EnrollmentDTO enrollmentDTO) {
        // Validate student exists
        Student student = studentRepository.findById(enrollmentDTO.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException(enrollmentDTO.getStudentId()));

        // Validate course exists
        Course course = courseRepository.findById(enrollmentDTO.getCourseId())
                .orElseThrow(() -> CourseNotFoundException.withId(enrollmentDTO.getCourseId()));

        // Check if student is already enrolled in this course
        if (enrollmentRepository.existsByStudentIdAndCourseId(enrollmentDTO.getStudentId(), enrollmentDTO.getCourseId())) {
            throw new RuntimeException("Student is already enrolled in this course");
        }

        // Check if course has available seats
        if (!course.hasAvailableSeats()) {
            throw new RuntimeException("Course is full. No available seats.");
        }

        // Check if course is active
        if (!course.isActive()) {
            throw new RuntimeException("Course is not active for enrollment");
        }

        Enrollment enrollment = enrollmentMapper.toEntity(enrollmentDTO);
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        
        // Set default values
        if (enrollment.getEnrollmentDate() == null) {
            enrollment.setEnrollmentDate(LocalDateTime.now());
        }
        if (enrollment.getStatus() == null) {
            enrollment.setStatus(EnrollmentStatus.ENROLLED);
        }
        if (enrollment.getIsActive() == null) {
            enrollment.setIsActive(true);
        }
        
        // Set audit fields
        enrollment.setCreatedAt(LocalDateTime.now());
        enrollment.setUpdatedAt(LocalDateTime.now());
        
        // Increment course enrollment count
        course.incrementEnrollment();
        courseRepository.save(course);
        
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDto(savedEnrollment);
    }

    @Override
    public EnrollmentDTO updateEnrollment(Long id, EnrollmentDTO enrollmentDTO) {
        Enrollment existingEnrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> EnrollmentNotFoundException.withId(id));

        // Update fields
        if (enrollmentDTO.getStatus() != null) {
            existingEnrollment.setStatus(enrollmentDTO.getStatus());
        }
        if (enrollmentDTO.getGradeLetter() != null) {
            existingEnrollment.setGradeLetter(enrollmentDTO.getGradeLetter());
        }
        if (enrollmentDTO.getGradePoints() != null) {
            existingEnrollment.setGradePoints(enrollmentDTO.getGradePoints());
        }
        if (enrollmentDTO.getAttendancePercentage() != null) {
            existingEnrollment.setAttendancePercentage(enrollmentDTO.getAttendancePercentage());
        }
        if (enrollmentDTO.getIsActive() != null) {
            existingEnrollment.setIsActive(enrollmentDTO.getIsActive());
        }
        if (enrollmentDTO.getNotes() != null) {
            existingEnrollment.setNotes(enrollmentDTO.getNotes());
        }
        
        existingEnrollment.setUpdatedAt(LocalDateTime.now());

        Enrollment updatedEnrollment = enrollmentRepository.save(existingEnrollment);
        return enrollmentMapper.toDto(updatedEnrollment);
    }

    @Override
    public void deleteEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> EnrollmentNotFoundException.withId(id));
        
        // Decrement course enrollment count if enrollment was active
        if (enrollment.isActiveEnrollment()) {
            Course course = enrollment.getCourse();
            course.decrementEnrollment();
            courseRepository.save(course);
        }
        
        enrollmentRepository.delete(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByStudentId(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByCourseId(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByStatus(EnrollmentStatus status) {
        List<Enrollment> enrollments = enrollmentRepository.findByStatus(status);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getActiveEnrollments() {
        List<Enrollment> enrollments = enrollmentRepository.findByIsActive(true);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByStudentIdAndStatus(Long studentId, EnrollmentStatus status) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndStatus(studentId, status);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByCourseIdAndStatus(Long courseId, EnrollmentStatus status) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdAndStatus(courseId, status);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByProfessorId(Long professorId) {
        List<Enrollment> enrollments = enrollmentRepository.findByProfessorId(professorId);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByProfessorIdAndCourseId(Long professorId, Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByProfessorIdAndCourseId(professorId, courseId);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByStudentIdAndSemester(Long studentId, String semester, Integer academicYear) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndSemesterAndAcademicYear(studentId, semester, academicYear);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Enrollment> enrollments = enrollmentRepository.findByEnrollmentDateBetween(startDate, endDate);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByStudentIdAndDateRange(Long studentId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndEnrollmentDateBetween(studentId, startDate, endDate);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByCourseIdAndDateRange(Long courseId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdAndEnrollmentDateBetween(courseId, startDate, endDate);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    public EnrollmentDTO dropEnrollment(Long enrollmentId, String reason) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> EnrollmentNotFoundException.withId(enrollmentId));
        
        if (!enrollment.canDrop()) {
            throw new RuntimeException("Enrollment cannot be dropped. It may already be dropped or inactive.");
        }
        
        enrollment.dropEnrollment(reason);
        
        // Decrement course enrollment count
        Course course = enrollment.getCourse();
        course.decrementEnrollment();
        courseRepository.save(course);
        
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDto(updatedEnrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isStudentEnrolledInCourse(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getEnrollmentCountByStudentIdAndStatus(Long studentId, EnrollmentStatus status) {
        return enrollmentRepository.countByStudentIdAndStatus(studentId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getEnrollmentCountByCourseIdAndStatus(Long courseId, EnrollmentStatus status) {
        return enrollmentRepository.countByCourseIdAndStatus(courseId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getEnrollmentCountByProfessorIdAndStatus(Long professorId, EnrollmentStatus status) {
        return enrollmentRepository.countByProfessorIdAndStatus(professorId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getActiveEnrollmentCountByStudentId(Long studentId) {
        return enrollmentRepository.countActiveEnrollmentsByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getActiveEnrollmentCountByCourseId(Long courseId) {
        return enrollmentRepository.countActiveEnrollmentsByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getActiveEnrollmentsByStudentAndSemester(Long studentId, String semester, Integer academicYear) {
        List<Enrollment> enrollments = enrollmentRepository.findActiveEnrollmentsByStudentAndSemester(studentId, semester, academicYear);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getGradedEnrollmentsByStudentId(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findGradedEnrollmentsByStudentId(studentId);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getUngradedActiveEnrollmentsByStudentId(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findUngradedActiveEnrollmentsByStudentId(studentId);
        return enrollmentMapper.toDtoList(enrollments);
    }
} 