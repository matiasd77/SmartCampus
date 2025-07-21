package com.smartcampus.repository;

import com.smartcampus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByEmailAndRole(String email, com.smartcampus.entity.Role role);
    
    Optional<User> findByEmailAndIdNot(String email, Long id);
} 