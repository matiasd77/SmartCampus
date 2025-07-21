package com.smartcampus.service;

import com.smartcampus.entity.Student;
import com.smartcampus.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentSecurityService {

    private final StudentRepository studentRepository;

    public boolean isOwnProfile(Long studentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return studentRepository.findByUserEmail(email)
                .map(student -> student.getId().equals(studentId))
                .orElse(false);
    }

    public boolean isOwnProfileByUserId(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return studentRepository.findByUserEmail(email)
                .map(student -> student.getUser().getId().equals(userId))
                .orElse(false);
    }
} 