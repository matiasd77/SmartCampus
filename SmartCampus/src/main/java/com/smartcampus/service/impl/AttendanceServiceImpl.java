package com.smartcampus.service.impl;

import com.smartcampus.dto.AttendanceDTO;
import com.smartcampus.entity.Attendance;
import com.smartcampus.entity.AttendanceStatus;
import com.smartcampus.entity.Course;
import com.smartcampus.entity.Student;
import com.smartcampus.exception.AttendanceNotFoundException;
import com.smartcampus.exception.CourseNotFoundException;
import com.smartcampus.exception.StudentNotFoundException;
import com.smartcampus.mapper.AttendanceMapper;
import com.smartcampus.repository.AttendanceRepository;
import com.smartcampus.repository.CourseRepository;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final AttendanceMapper attendanceMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAllAttendance() {
        List<Attendance> attendances = attendanceRepository.findAll();
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDTO getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> AttendanceNotFoundException.withId(id));
        return attendanceMapper.toDto(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDTO getAttendanceByStudentAndCourseAndDate(Long studentId, Long courseId, LocalDate date) {
        Attendance attendance = attendanceRepository.findByStudentIdAndCourseIdAndDate(studentId, courseId, date)
                .orElseThrow(() -> AttendanceNotFoundException.withStudentAndCourseAndDate(studentId, courseId, date));
        return attendanceMapper.toDto(attendance);
    }

    @Override
    public AttendanceDTO createAttendance(AttendanceDTO attendanceDTO) {
        // Validate student exists
        Student student = studentRepository.findById(attendanceDTO.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException(attendanceDTO.getStudentId()));

        // Validate course exists
        Course course = courseRepository.findById(attendanceDTO.getCourseId())
                .orElseThrow(() -> CourseNotFoundException.withId(attendanceDTO.getCourseId()));

        // Check if attendance already exists for this student, course, and date
        if (attendanceRepository.existsByStudentIdAndCourseIdAndDate(attendanceDTO.getStudentId(), attendanceDTO.getCourseId(), attendanceDTO.getDate())) {
            throw new RuntimeException("Attendance already exists for this student, course, and date");
        }

        Attendance attendance = attendanceMapper.toEntity(attendanceDTO);
        attendance.setStudent(student);
        attendance.setCourse(course);
        
        // Set default values
        if (attendance.getStatus() == null) {
            attendance.setStatus(AttendanceStatus.PRESENT);
        }
        if (attendance.getIsRequired() == null) {
            attendance.setIsRequired(true);
        }
        if (attendance.getIsCountedTowardsGrade() == null) {
            attendance.setIsCountedTowardsGrade(false);
        }
        if (attendance.getIsVerified() == null) {
            attendance.setIsVerified(false);
        }
        if (attendance.getIsExcused() == null) {
            attendance.setIsExcused(false);
        }
        if (attendance.getIsMakeup() == null) {
            attendance.setIsMakeup(false);
        }
        
        // Set audit fields
        attendance.setCreatedAt(LocalDateTime.now());
        attendance.setUpdatedAt(LocalDateTime.now());
        
        Attendance savedAttendance = attendanceRepository.save(attendance);
        return attendanceMapper.toDto(savedAttendance);
    }

    @Override
    public AttendanceDTO updateAttendance(Long id, AttendanceDTO attendanceDTO) {
        Attendance existingAttendance = attendanceRepository.findById(id)
                .orElseThrow(() -> AttendanceNotFoundException.withId(id));

        // Update fields
        if (attendanceDTO.getStatus() != null) {
            existingAttendance.setStatus(attendanceDTO.getStatus());
        }
        if (attendanceDTO.getNotes() != null) {
            existingAttendance.setNotes(attendanceDTO.getNotes());
        }
        if (attendanceDTO.getSessionName() != null) {
            existingAttendance.setSessionName(attendanceDTO.getSessionName());
        }
        if (attendanceDTO.getSessionType() != null) {
            existingAttendance.setSessionType(attendanceDTO.getSessionType());
        }
        if (attendanceDTO.getSessionDuration() != null) {
            existingAttendance.setSessionDuration(attendanceDTO.getSessionDuration());
        }
        if (attendanceDTO.getCheckInTime() != null) {
            existingAttendance.setCheckInTime(attendanceDTO.getCheckInTime());
        }
        if (attendanceDTO.getCheckOutTime() != null) {
            existingAttendance.setCheckOutTime(attendanceDTO.getCheckOutTime());
        }
        if (attendanceDTO.getLateMinutes() != null) {
            existingAttendance.setLateMinutes(attendanceDTO.getLateMinutes());
        }
        if (attendanceDTO.getEarlyDepartureMinutes() != null) {
            existingAttendance.setEarlyDepartureMinutes(attendanceDTO.getEarlyDepartureMinutes());
        }
        if (attendanceDTO.getIsMakeup() != null) {
            existingAttendance.setIsMakeup(attendanceDTO.getIsMakeup());
        }
        if (attendanceDTO.getMakeupDate() != null) {
            existingAttendance.setMakeupDate(attendanceDTO.getMakeupDate());
        }
        if (attendanceDTO.getMakeupReason() != null) {
            existingAttendance.setMakeupReason(attendanceDTO.getMakeupReason());
        }
        if (attendanceDTO.getIsVerified() != null) {
            existingAttendance.setIsVerified(attendanceDTO.getIsVerified());
        }
        if (attendanceDTO.getVerifiedBy() != null) {
            existingAttendance.setVerifiedBy(attendanceDTO.getVerifiedBy());
        }
        if (attendanceDTO.getVerificationDate() != null) {
            existingAttendance.setVerificationDate(attendanceDTO.getVerificationDate());
        }
        if (attendanceDTO.getVerificationNotes() != null) {
            existingAttendance.setVerificationNotes(attendanceDTO.getVerificationNotes());
        }
        if (attendanceDTO.getIsExcused() != null) {
            existingAttendance.setIsExcused(attendanceDTO.getIsExcused());
        }
        if (attendanceDTO.getExcuseReason() != null) {
            existingAttendance.setExcuseReason(attendanceDTO.getExcuseReason());
        }
        if (attendanceDTO.getExcuseApprovedBy() != null) {
            existingAttendance.setExcuseApprovedBy(attendanceDTO.getExcuseApprovedBy());
        }
        if (attendanceDTO.getExcuseApprovalDate() != null) {
            existingAttendance.setExcuseApprovalDate(attendanceDTO.getExcuseApprovalDate());
        }
        if (attendanceDTO.getExcuseNotes() != null) {
            existingAttendance.setExcuseNotes(attendanceDTO.getExcuseNotes());
        }
        if (attendanceDTO.getAttendancePercentage() != null) {
            existingAttendance.setAttendancePercentage(attendanceDTO.getAttendancePercentage());
        }
        if (attendanceDTO.getIsRequired() != null) {
            existingAttendance.setIsRequired(attendanceDTO.getIsRequired());
        }
        if (attendanceDTO.getIsCountedTowardsGrade() != null) {
            existingAttendance.setIsCountedTowardsGrade(attendanceDTO.getIsCountedTowardsGrade());
        }
        if (attendanceDTO.getGradeImpact() != null) {
            existingAttendance.setGradeImpact(attendanceDTO.getGradeImpact());
        }
        
        existingAttendance.setUpdatedAt(LocalDateTime.now());

        Attendance updatedAttendance = attendanceRepository.save(existingAttendance);
        return attendanceMapper.toDto(updatedAttendance);
    }

    @Override
    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> AttendanceNotFoundException.withId(id));
        attendanceRepository.delete(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByStudentId(Long studentId) {
        List<Attendance> attendances = attendanceRepository.findByStudentId(studentId);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByCourseId(Long courseId) {
        List<Attendance> attendances = attendanceRepository.findByCourseId(courseId);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByProfessorId(Long professorId) {
        List<Attendance> attendances = attendanceRepository.findByCourseProfessorId(professorId);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByStudentIdAndCourseId(Long studentId, Long courseId) {
        List<Attendance> attendances = attendanceRepository.findByStudentIdAndCourseId(studentId, courseId);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByStatus(AttendanceStatus status) {
        List<Attendance> attendances = attendanceRepository.findByStatus(status);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByStudentIdAndStatus(Long studentId, AttendanceStatus status) {
        List<Attendance> attendances = attendanceRepository.findByStudentIdAndStatus(studentId, status);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByCourseIdAndStatus(Long courseId, AttendanceStatus status) {
        List<Attendance> attendances = attendanceRepository.findByCourseIdAndStatus(courseId, status);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByProfessorIdAndStatus(Long professorId, AttendanceStatus status) {
        List<Attendance> attendances = attendanceRepository.findByCourseProfessorIdAndStatus(professorId, status);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByStudentIdAndSemester(Long studentId, String semester, Integer academicYear) {
        List<Attendance> attendances = attendanceRepository.findByStudentIdAndSemesterAndAcademicYear(studentId, semester, academicYear);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByProfessorIdAndSemester(Long professorId, String semester, Integer academicYear) {
        List<Attendance> attendances = attendanceRepository.findByProfessorIdAndSemesterAndAcademicYear(professorId, semester, academicYear);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByDate(LocalDate date) {
        List<Attendance> attendances = attendanceRepository.findByDate(date);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByStudentIdAndDate(Long studentId, LocalDate date) {
        List<Attendance> attendances = attendanceRepository.findByStudentIdAndDate(studentId, date);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByCourseIdAndDate(Long courseId, LocalDate date) {
        List<Attendance> attendances = attendanceRepository.findByCourseIdAndDate(courseId, date);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByProfessorIdAndDate(Long professorId, LocalDate date) {
        List<Attendance> attendances = attendanceRepository.findByCourseProfessorIdAndDate(professorId, date);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findByDateBetween(startDate, endDate);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByStudentIdAndDateRange(Long studentId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findByStudentIdAndDateBetween(studentId, startDate, endDate);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByCourseIdAndDateRange(Long courseId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findByCourseIdAndDateBetween(courseId, startDate, endDate);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByProfessorIdAndDateRange(Long professorId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findByProfessorIdAndDateBetween(professorId, startDate, endDate);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByIsMakeup(Boolean isMakeup) {
        List<Attendance> attendances = attendanceRepository.findByIsMakeup(isMakeup);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByStudentIdAndIsMakeup(Long studentId, Boolean isMakeup) {
        List<Attendance> attendances = attendanceRepository.findByStudentIdAndIsMakeup(studentId, isMakeup);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByCourseIdAndIsMakeup(Long courseId, Boolean isMakeup) {
        List<Attendance> attendances = attendanceRepository.findByCourseIdAndIsMakeup(courseId, isMakeup);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByIsVerified(Boolean isVerified) {
        List<Attendance> attendances = attendanceRepository.findByIsVerified(isVerified);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByStudentIdAndIsVerified(Long studentId, Boolean isVerified) {
        List<Attendance> attendances = attendanceRepository.findByStudentIdAndIsVerified(studentId, isVerified);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByCourseIdAndIsVerified(Long courseId, Boolean isVerified) {
        List<Attendance> attendances = attendanceRepository.findByCourseIdAndIsVerified(courseId, isVerified);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByIsExcused(Boolean isExcused) {
        List<Attendance> attendances = attendanceRepository.findByIsExcused(isExcused);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByStudentIdAndIsExcused(Long studentId, Boolean isExcused) {
        List<Attendance> attendances = attendanceRepository.findByStudentIdAndIsExcused(studentId, isExcused);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByCourseIdAndIsExcused(Long courseId, Boolean isExcused) {
        List<Attendance> attendances = attendanceRepository.findByCourseIdAndIsExcused(courseId, isExcused);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getLateAttendances() {
        List<Attendance> attendances = attendanceRepository.findLateAttendances();
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getLateAttendancesByStudentId(Long studentId) {
        List<Attendance> attendances = attendanceRepository.findLateAttendancesByStudentId(studentId);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getLateAttendancesByCourseId(Long courseId) {
        List<Attendance> attendances = attendanceRepository.findLateAttendancesByCourseId(courseId);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getEarlyDepartureAttendances() {
        List<Attendance> attendances = attendanceRepository.findEarlyDepartureAttendances();
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getEarlyDepartureAttendancesByStudentId(Long studentId) {
        List<Attendance> attendances = attendanceRepository.findEarlyDepartureAttendancesByStudentId(studentId);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getEarlyDepartureAttendancesByCourseId(Long courseId) {
        List<Attendance> attendances = attendanceRepository.findEarlyDepartureAttendancesByCourseId(courseId);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getActiveCheckIns() {
        List<Attendance> attendances = attendanceRepository.findActiveCheckIns();
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getActiveCheckInsByStudentId(Long studentId) {
        List<Attendance> attendances = attendanceRepository.findActiveCheckInsByStudentId(studentId);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getActiveCheckInsByCourseId(Long courseId) {
        List<Attendance> attendances = attendanceRepository.findActiveCheckInsByCourseId(courseId);
        return attendanceMapper.toDtoList(attendances);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAttendanceCountByStudentId(Long studentId) {
        return attendanceRepository.countByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAttendanceCountByCourseId(Long courseId) {
        return attendanceRepository.countByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAttendanceCountByProfessorId(Long professorId) {
        return attendanceRepository.countByProfessorId(professorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAttendanceCountByStudentIdAndStatus(Long studentId, AttendanceStatus status) {
        return attendanceRepository.countByStudentIdAndStatus(studentId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAttendanceCountByCourseIdAndStatus(Long courseId, AttendanceStatus status) {
        return attendanceRepository.countByCourseIdAndStatus(courseId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAttendanceCountByProfessorIdAndStatus(Long professorId, AttendanceStatus status) {
        return attendanceRepository.countByProfessorIdAndStatus(professorId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPresentCountByStudentId(Long studentId) {
        return attendanceRepository.countPresentByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPresentCountByCourseId(Long courseId) {
        return attendanceRepository.countPresentByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPresentCountByProfessorId(Long professorId) {
        return attendanceRepository.countPresentByProfessorId(professorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAbsentCountByStudentId(Long studentId) {
        return attendanceRepository.countAbsentByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAbsentCountByCourseId(Long courseId) {
        return attendanceRepository.countAbsentByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAbsentCountByProfessorId(Long professorId) {
        return attendanceRepository.countAbsentByProfessorId(professorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getExcusedCountByStudentId(Long studentId) {
        return attendanceRepository.countExcusedByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getExcusedCountByCourseId(Long courseId) {
        return attendanceRepository.countExcusedByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getExcusedCountByProfessorId(Long professorId) {
        return attendanceRepository.countExcusedByProfessorId(professorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageAttendancePercentageByStudentId(Long studentId) {
        return attendanceRepository.getAverageAttendancePercentageByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageAttendancePercentageByCourseId(Long courseId) {
        return attendanceRepository.getAverageAttendancePercentageByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageAttendancePercentageByProfessorId(Long professorId) {
        return attendanceRepository.getAverageAttendancePercentageByProfessorId(professorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getMaxAttendancePercentageByStudentId(Long studentId) {
        return attendanceRepository.getMaxAttendancePercentageByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getMinAttendancePercentageByStudentId(Long studentId) {
        return attendanceRepository.getMinAttendancePercentageByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getMaxAttendancePercentageByCourseId(Long courseId) {
        return attendanceRepository.getMaxAttendancePercentageByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getMinAttendancePercentageByCourseId(Long courseId) {
        return attendanceRepository.getMinAttendancePercentageByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStudentIdAndCourseIdAndDate(Long studentId, Long courseId, LocalDate date) {
        return attendanceRepository.existsByStudentIdAndCourseIdAndDate(studentId, courseId, date);
    }
} 