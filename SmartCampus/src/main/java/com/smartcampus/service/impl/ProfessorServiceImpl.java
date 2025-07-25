package com.smartcampus.service.impl;

import com.smartcampus.dto.ProfessorDTO;
import com.smartcampus.entity.Professor;
import com.smartcampus.entity.ProfessorStatus;
import com.smartcampus.entity.User;
import com.smartcampus.entity.AcademicRank;
import com.smartcampus.exception.ProfessorNotFoundException;
import com.smartcampus.mapper.ProfessorMapper;
import com.smartcampus.repository.ProfessorRepository;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.service.ProfessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository professorRepository;
    private final ProfessorMapper professorMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProfessorDTO> getAllProfessors() {
        List<Professor> professors = professorRepository.findAll();
        return professorMapper.toDtoList(professors);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfessorDTO> getAllProfessors(Pageable pageable) {
        Page<Professor> professors = professorRepository.findAll(pageable);
        return professors.map(professorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfessorDTO getProfessorById(Long id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> ProfessorNotFoundException.withId(id));
        return professorMapper.toDto(professor);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfessorDTO getProfessorByEmail(String email) {
        Professor professor = professorRepository.findByEmail(email)
                .orElseThrow(() -> ProfessorNotFoundException.withEmail(email));
        return professorMapper.toDto(professor);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfessorDTO getProfessorByUserId(Long userId) {
        Professor professor = professorRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfessorNotFoundException("Professor not found with user id: " + userId));
        return professorMapper.toDto(professor);
    }

    @Override
    public ProfessorDTO createProfessor(ProfessorDTO professorDTO) {
        System.out.println("🔍 ProfessorServiceImpl.createProfessor - Starting with data: " + professorDTO);
        
        // Check if email already exists
        if (professorRepository.existsByEmail(professorDTO.getEmail())) {
            System.err.println("🔍 ProfessorServiceImpl.createProfessor - Email already exists: " + professorDTO.getEmail());
            throw new RuntimeException("Email is already taken by another professor");
        }

        Professor professor = professorMapper.toEntity(professorDTO);
        System.out.println("🔍 ProfessorServiceImpl.createProfessor - Mapped to entity: " + professor);
        
        // Handle userId - fetch the user and set it in the professor entity
        if (professorDTO.getUserId() != null) {
            try {
                System.out.println("🔍 ProfessorServiceImpl.createProfessor - Fetching user with ID: " + professorDTO.getUserId());
                User user = userRepository.findById(professorDTO.getUserId())
                        .orElseThrow(() -> new RuntimeException("User with ID " + professorDTO.getUserId() + " not found"));
                professor.setUser(user);
                System.out.println("🔍 ProfessorServiceImpl.createProfessor - User found and set: " + user);
            } catch (Exception e) {
                System.err.println("🔍 ProfessorServiceImpl.createProfessor - Error fetching user: " + e.getMessage());
                throw new RuntimeException("User with ID " + professorDTO.getUserId() + " not found: " + e.getMessage());
            }
        } else {
            System.out.println("🔍 ProfessorServiceImpl.createProfessor - No userId provided");
        }
        
        // Set default status if not provided
        if (professor.getStatus() == null) {
            professor.setStatus(ProfessorStatus.ACTIVE);
            System.out.println("🔍 ProfessorServiceImpl.createProfessor - Set default status: ACTIVE");
        }
        
        // Set default academic rank if not provided
        if (professor.getAcademicRank() == null) {
            professor.setAcademicRank(AcademicRank.ASSISTANT_PROFESSOR);
            System.out.println("🔍 ProfessorServiceImpl.createProfessor - Set default academic rank: ASSISTANT_PROFESSOR");
        }
        
        // Set audit fields
        professor.setCreatedAt(LocalDateTime.now());
        professor.setUpdatedAt(LocalDateTime.now());
        
        System.out.println("🔍 ProfessorServiceImpl.createProfessor - About to save professor: " + professor);
        Professor savedProfessor = professorRepository.save(professor);
        System.out.println("🔍 ProfessorServiceImpl.createProfessor - Professor saved successfully: " + savedProfessor);
        
        ProfessorDTO result = professorMapper.toDto(savedProfessor);
        System.out.println("🔍 ProfessorServiceImpl.createProfessor - Returning DTO: " + result);
        return result;
    }

    @Override
    public ProfessorDTO updateProfessor(Long id, ProfessorDTO professorDTO) {
        Professor existingProfessor = professorRepository.findById(id)
                .orElseThrow(() -> ProfessorNotFoundException.withId(id));

        // Check if email is being changed and if it's already taken
        if (!existingProfessor.getEmail().equals(professorDTO.getEmail()) &&
            professorRepository.existsByEmail(professorDTO.getEmail())) {
            throw new RuntimeException("Email is already taken by another professor");
        }

        // Update fields
        existingProfessor.setFirstName(professorDTO.getFirstName());
        existingProfessor.setLastName(professorDTO.getLastName());
        existingProfessor.setEmail(professorDTO.getEmail());
        existingProfessor.setDepartment(professorDTO.getDepartment());
        existingProfessor.setPhoneNumber(professorDTO.getPhoneNumber());
        existingProfessor.setOfficeLocation(professorDTO.getOfficeLocation());
        existingProfessor.setBio(professorDTO.getBio());
        existingProfessor.setAcademicRank(professorDTO.getAcademicRank());
        existingProfessor.setStatus(professorDTO.getStatus());
        existingProfessor.setUpdatedAt(LocalDateTime.now());

        Professor updatedProfessor = professorRepository.save(existingProfessor);
        return professorMapper.toDto(updatedProfessor);
    }

    @Override
    public void deleteProfessor(Long id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> ProfessorNotFoundException.withId(id));
        
        // Check if professor has associated courses
        if (professor.getCourses() != null && !professor.getCourses().isEmpty()) {
            throw new RuntimeException("Cannot delete professor with associated courses. Please reassign or delete courses first.");
        }
        
        professorRepository.delete(professor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessorDTO> getProfessorsByDepartment(String department) {
        List<Professor> professors = professorRepository.findByDepartment(department);
        return professorMapper.toDtoList(professors);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessorDTO> getProfessorsByStatus(ProfessorStatus status) {
        List<Professor> professors = professorRepository.findByStatus(status);
        return professorMapper.toDtoList(professors);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessorDTO> getProfessorsByName(String name) {
        List<Professor> professors = professorRepository.findByNameContaining(name);
        return professorMapper.toDtoList(professors);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessorDTO> getProfessorsByDepartmentAndName(String department, String name) {
        List<Professor> professors = professorRepository.findByDepartmentAndNameContaining(department, name);
        return professorMapper.toDtoList(professors);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getProfessorCountByDepartment(String department) {
        return professorRepository.countByDepartment(department);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getProfessorCountByStatus(ProfessorStatus status) {
        return professorRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email, Long excludeId) {
        if (excludeId == null) {
            return !professorRepository.existsByEmail(email);
        }
        return professorRepository.findByEmailAndIdNot(email, excludeId).isEmpty();
    }
} 