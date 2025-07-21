package com.smartcampus.mapper;

import com.smartcampus.dto.AttendanceDTO;
import com.smartcampus.entity.Attendance;

import java.util.List;

public interface AttendanceMapper {
    AttendanceDTO toDto(Attendance attendance);
    Attendance toEntity(AttendanceDTO attendanceDTO);
    List<AttendanceDTO> toDtoList(List<Attendance> attendances);
} 