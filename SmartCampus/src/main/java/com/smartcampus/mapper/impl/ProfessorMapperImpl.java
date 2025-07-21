package com.smartcampus.mapper.impl;

import com.smartcampus.dto.ProfessorDTO;
import com.smartcampus.entity.Professor;
import com.smartcampus.mapper.ProfessorMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProfessorMapperImpl implements ProfessorMapper {

    @Override
    public ProfessorDTO toDto(Professor professor) {
        if (professor == null) {
            return null;
        }

        return ProfessorDTO.builder()
                .id(professor.getId())
                .firstName(professor.getFirstName())
                .lastName(professor.getLastName())
                .email(professor.getEmail())
                .department(professor.getDepartment())
                .phoneNumber(professor.getPhoneNumber())
                .officeLocation(professor.getOfficeLocation())
                .bio(professor.getBio())
                .academicRank(professor.getAcademicRank())
                .status(professor.getStatus())
                .userId(professor.getUser() != null ? professor.getUser().getId() : null)
                .createdAt(professor.getCreatedAt())
                .updatedAt(professor.getUpdatedAt())
                .build();
    }

    @Override
    public Professor toEntity(ProfessorDTO professorDTO) {
        if (professorDTO == null) {
            return null;
        }

        return Professor.builder()
                .id(professorDTO.getId())
                .firstName(professorDTO.getFirstName())
                .lastName(professorDTO.getLastName())
                .email(professorDTO.getEmail())
                .department(professorDTO.getDepartment())
                .phoneNumber(professorDTO.getPhoneNumber())
                .officeLocation(professorDTO.getOfficeLocation())
                .bio(professorDTO.getBio())
                .academicRank(professorDTO.getAcademicRank())
                .status(professorDTO.getStatus())
                .build();
    }

    @Override
    public List<ProfessorDTO> toDtoList(List<Professor> professors) {
        if (professors == null) {
            return null;
        }

        return professors.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
} 