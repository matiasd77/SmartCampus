package com.smartcampus.mapper.impl;

import com.smartcampus.dto.AttendanceDTO;
import com.smartcampus.entity.Attendance;
import com.smartcampus.mapper.AttendanceMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AttendanceMapperImpl implements AttendanceMapper {

    @Override
    public AttendanceDTO toDto(Attendance attendance) {
        if (attendance == null) {
            return null;
        }

        return AttendanceDTO.builder()
                .id(attendance.getId())
                .studentId(attendance.getStudent() != null ? attendance.getStudent().getId() : null)
                .studentName(attendance.getStudent() != null ? 
                        attendance.getStudent().getFirstName() + " " + attendance.getStudent().getLastName() : null)
                .studentIdNumber(attendance.getStudent() != null ? attendance.getStudent().getStudentId() : null)
                .studentEmail(attendance.getStudent() != null && attendance.getStudent().getUser() != null ? 
                        attendance.getStudent().getUser().getEmail() : null)
                .courseId(attendance.getCourse() != null ? attendance.getCourse().getId() : null)
                .courseName(attendance.getCourse() != null ? attendance.getCourse().getName() : null)
                .courseCode(attendance.getCourse() != null ? attendance.getCourse().getCode() : null)
                .professorName(attendance.getCourse() != null && attendance.getCourse().getProfessor() != null ? 
                        attendance.getCourse().getProfessor().getFirstName() + " " + attendance.getCourse().getProfessor().getLastName() : null)
                .date(attendance.getDate())
                .status(attendance.getStatus())
                .notes(attendance.getNotes())
                .sessionName(attendance.getSessionName())
                .sessionType(attendance.getSessionType())
                .sessionDuration(attendance.getSessionDuration())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .lateMinutes(attendance.getLateMinutes())
                .earlyDepartureMinutes(attendance.getEarlyDepartureMinutes())
                .isMakeup(attendance.getIsMakeup())
                .makeupDate(attendance.getMakeupDate())
                .makeupReason(attendance.getMakeupReason())
                .isVerified(attendance.getIsVerified())
                .verifiedBy(attendance.getVerifiedBy())
                .verificationDate(attendance.getVerificationDate())
                .verificationNotes(attendance.getVerificationNotes())
                .isExcused(attendance.getIsExcused())
                .excuseReason(attendance.getExcuseReason())
                .excuseApprovedBy(attendance.getExcuseApprovedBy())
                .excuseApprovalDate(attendance.getExcuseApprovalDate())
                .excuseNotes(attendance.getExcuseNotes())
                .attendancePercentage(attendance.getAttendancePercentage())
                .isRequired(attendance.getIsRequired())
                .isCountedTowardsGrade(attendance.getIsCountedTowardsGrade())
                .gradeImpact(attendance.getGradeImpact())
                .createdAt(attendance.getCreatedAt())
                .updatedAt(attendance.getUpdatedAt())
                .build();
    }

    @Override
    public Attendance toEntity(AttendanceDTO attendanceDTO) {
        if (attendanceDTO == null) {
            return null;
        }

        return Attendance.builder()
                .id(attendanceDTO.getId())
                .date(attendanceDTO.getDate())
                .status(attendanceDTO.getStatus())
                .notes(attendanceDTO.getNotes())
                .sessionName(attendanceDTO.getSessionName())
                .sessionType(attendanceDTO.getSessionType())
                .sessionDuration(attendanceDTO.getSessionDuration())
                .checkInTime(attendanceDTO.getCheckInTime())
                .checkOutTime(attendanceDTO.getCheckOutTime())
                .lateMinutes(attendanceDTO.getLateMinutes())
                .earlyDepartureMinutes(attendanceDTO.getEarlyDepartureMinutes())
                .isMakeup(attendanceDTO.getIsMakeup())
                .makeupDate(attendanceDTO.getMakeupDate())
                .makeupReason(attendanceDTO.getMakeupReason())
                .isVerified(attendanceDTO.getIsVerified())
                .verifiedBy(attendanceDTO.getVerifiedBy())
                .verificationDate(attendanceDTO.getVerificationDate())
                .verificationNotes(attendanceDTO.getVerificationNotes())
                .isExcused(attendanceDTO.getIsExcused())
                .excuseReason(attendanceDTO.getExcuseReason())
                .excuseApprovedBy(attendanceDTO.getExcuseApprovedBy())
                .excuseApprovalDate(attendanceDTO.getExcuseApprovalDate())
                .excuseNotes(attendanceDTO.getExcuseNotes())
                .attendancePercentage(attendanceDTO.getAttendancePercentage())
                .isRequired(attendanceDTO.getIsRequired())
                .isCountedTowardsGrade(attendanceDTO.getIsCountedTowardsGrade())
                .gradeImpact(attendanceDTO.getGradeImpact())
                .build();
    }

    @Override
    public List<AttendanceDTO> toDtoList(List<Attendance> attendances) {
        if (attendances == null) {
            return null;
        }

        return attendances.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
} 