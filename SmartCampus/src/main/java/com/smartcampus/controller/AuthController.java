package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.JwtResponse;
import com.smartcampus.dto.LoginRequest;
import com.smartcampus.dto.RegisterRequest;
import com.smartcampus.entity.User;
import com.smartcampus.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
        summary = "User Login",
        description = "Authenticate user with email and password, returns JWT token",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Login credentials",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequest.class),
                examples = @ExampleObject(
                    name = "Student Login",
                    value = "{\"email\": \"student@example.com\", \"password\": \"Password123!\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JwtResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\"success\": true, \"message\": \"Login successful\", \"data\": {\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"user\": {\"id\": 1, \"name\": \"John Doe\", \"email\": \"student@example.com\", \"role\": \"STUDENT\"}}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = "{\"success\": false, \"message\": \"Invalid email or password\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = "{\"success\": false, \"message\": \"Validation failed\", \"data\": {\"email\": \"Email should be valid\", \"password\": \"Password is required\"}}"
                )
            )
        )
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login attempt for email: {}", loginRequest.getEmail());
            
            JwtResponse response = authService.login(loginRequest);
            
            log.info("Login successful for email: {}", loginRequest.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
            
        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Invalid email or password", "Please check your credentials and try again."));
                    
        } catch (UsernameNotFoundException e) {
            log.warn("User not found for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Invalid email or password", "Please check your credentials and try again."));
                    
        } catch (Exception e) {
            log.error("Unexpected error during login for email: {}", loginRequest.getEmail(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Login failed", "An unexpected error occurred. Please try again later."));
        }
    }

    @PostMapping("/register")
    @Operation(
        summary = "Student Registration",
        description = "Register a new student user. Only STUDENT role is allowed for public registration. " +
                     "Professors and Administrators must be created through admin interfaces.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Student registration details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RegisterRequest.class),
                examples = @ExampleObject(
                    name = "Student Registration",
                    value = "{\"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"password\": \"Password123!\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Registration successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\"success\": true, \"message\": \"Registration successful\", \"data\": {\"id\": 1, \"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"role\": \"STUDENT\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error or email already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = "{\"success\": false, \"message\": \"Validation failed\", \"data\": {\"email\": \"Email should be valid\", \"password\": \"Password must be between 8 and 100 characters\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Email already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Conflict Error",
                    value = "{\"success\": false, \"message\": \"Email already exists\"}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = authService.register(registerRequest);
        return ResponseEntity.status(201).body(ApiResponse.success("Registration successful", user));
    }

    @GetMapping("/debug")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
        summary = "Debug Authentication",
        description = "Debug endpoint to check authentication status and authorities"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Debug information retrieved successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> debugAuth() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("authenticated", authentication != null && authentication.isAuthenticated());
            debugInfo.put("principal", authentication != null ? authentication.getPrincipal() : null);
            debugInfo.put("authorities", authentication != null ? 
                authentication.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toList()) : null);
            debugInfo.put("credentials", authentication != null ? authentication.getCredentials() : null);
            debugInfo.put("details", authentication != null ? authentication.getDetails() : null);
            
            log.info("Debug auth called - authenticated: {}, authorities: {}", 
                debugInfo.get("authenticated"), debugInfo.get("authorities"));
            
            return ResponseEntity.ok(ApiResponse.success("Debug information retrieved", debugInfo));
        } catch (Exception e) {
            log.error("Error in debug endpoint", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Debug failed", e.getMessage()));
        }
    }
} 