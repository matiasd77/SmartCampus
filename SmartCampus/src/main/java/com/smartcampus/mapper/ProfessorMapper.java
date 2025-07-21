package com.smartcampus.mapper;

import com.smartcampus.dto.ProfessorDTO;
import com.smartcampus.entity.Professor;

import java.util.List;

public interface ProfessorMapper {
    ProfessorDTO toDto(Professor professor);
    Professor toEntity(ProfessorDTO professorDTO);
    List<ProfessorDTO> toDtoList(List<Professor> professors);
} 