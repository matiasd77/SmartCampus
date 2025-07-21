package com.smartcampus.service;

import com.smartcampus.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfessorSecurityService {

    private final ProfessorRepository professorRepository;

    public boolean isOwnProfile(Long professorId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return professorRepository.findByUserEmail(email)
                .map(professor -> professor.getId().equals(professorId))
                .orElse(false);
    }

    public boolean isOwnProfileByUserId(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return professorRepository.findByUserEmail(email)
                .map(professor -> professor.getUser().getId().equals(userId))
                .orElse(false);
    }
} 