package com.smartcampus.service;

import com.smartcampus.entity.User;
import com.smartcampus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user details for email: {}", email);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found with email: {}", email);
                        return new UsernameNotFoundException("User not found with email: " + email);
                    });

            log.info("User found: {} (ID: {}) with role: {} and isActive: {}", 
                    user.getName(), user.getId(), user.getRole(), user.getIsActive());

            // Check if user is active
            if (user.getIsActive() == null || !user.getIsActive()) {
                log.warn("User account is inactive for email: {}", email);
                throw new UsernameNotFoundException("User account is inactive: " + email);
            }

            // Create authorities with ROLE_ prefix
            String authority = "ROLE_" + user.getRole().name();
            log.info("Creating UserDetails with authority: {} for user: {}", authority, email);

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(authority))
            );
        } catch (UsernameNotFoundException e) {
            throw e; // Re-throw UsernameNotFoundException
        } catch (Exception e) {
            log.error("Error loading user details for email: {}", email, e);
            throw new UsernameNotFoundException("Error loading user details for email: " + email, e);
        }
    }
} 