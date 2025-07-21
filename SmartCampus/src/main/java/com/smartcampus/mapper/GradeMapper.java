package com.smartcampus.mapper;

import com.smartcampus.dto.GradeDTO;
import com.smartcampus.entity.Grade;

import java.util.List;

public interface GradeMapper {
    GradeDTO toDto(Grade grade);
    Grade toEntity(GradeDTO gradeDTO);
    List<GradeDTO> toDtoList(List<Grade> grades);
} 