package com.smartcampus.service;

import com.smartcampus.dto.ProfessorDTO;
import com.smartcampus.entity.ProfessorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProfessorService {
    List<ProfessorDTO> getAllProfessors();
    Page<ProfessorDTO> getAllProfessors(Pageable pageable);
    ProfessorDTO getProfessorById(Long id);
    ProfessorDTO getProfessorByEmail(String email);
    ProfessorDTO getProfessorByUserId(Long userId);
    ProfessorDTO createProfessor(ProfessorDTO professorDTO);
    ProfessorDTO updateProfessor(Long id, ProfessorDTO professorDTO);
    void deleteProfessor(Long id);
    List<ProfessorDTO> getProfessorsByDepartment(String department);
    List<ProfessorDTO> getProfessorsByStatus(ProfessorStatus status);
    List<ProfessorDTO> getProfessorsByName(String name);
    List<ProfessorDTO> getProfessorsByDepartmentAndName(String department, String name);
    Long getProfessorCountByDepartment(String department);
    Long getProfessorCountByStatus(ProfessorStatus status);
    boolean isEmailAvailable(String email, Long excludeId);
} 