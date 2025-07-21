package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.PasswordChangeRequest;
import com.smartcampus.dto.UserProfileDTO;
import com.smartcampus.dto.UserProfileUpdateRequest;
import com.smartcampus.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "User Profile", description = "User profile and password management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Current User Profile",
        description = "Retrieve the profile information of the currently authenticated user"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Profile retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserProfileDTO.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\"success\": true, \"message\": \"User profile retrieved successfully\", \"data\": {\"id\": 1, \"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"role\": \"STUDENT\", \"isActive\": true}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions"
        )
    })
    public ResponseEntity<ApiResponse<UserProfileDTO>> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserProfileDTO profile = userProfileService.getCurrentUserProfile(email);
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", profile));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get User Profile by ID",
        description = "Retrieve profile information of a specific user by their ID (Admin/Professor only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Profile retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserProfileDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions"
        )
    })
    public ResponseEntity<ApiResponse<UserProfileDTO>> getUserProfileById(@PathVariable Long userId) {
        UserProfileDTO profile = userProfileService.getUserProfileById(userId);
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", profile));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Update Current User Profile",
        description = "Update the profile information of the currently authenticated user",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Profile update details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserProfileUpdateRequest.class),
                examples = @ExampleObject(
                    name = "Profile Update",
                    value = "{\"name\": \"John Smith\", \"email\": \"john.smith@example.com\", \"phoneNumber\": \"+1234567890\", \"address\": \"123 Main St, City, State\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserProfileDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error or email already taken",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = "{\"success\": false, \"message\": \"Profile update failed\", \"data\": \"Email is already taken by another user\"}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateUserProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserProfileDTO updatedProfile = userProfileService.updateUserProfile(email, request);
        return ResponseEntity.ok(ApiResponse.success("User profile updated successfully", updatedProfile));
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Change User Password",
        description = "Change the password of the currently authenticated user with current password verification",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Password change details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PasswordChangeRequest.class),
                examples = @ExampleObject(
                    name = "Password Change",
                    value = "{\"currentPassword\": \"OldPassword123!\", \"newPassword\": \"NewPassword456!\", \"confirmPassword\": \"NewPassword456!\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Password changed successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Password change failed",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Password Error",
                    value = "{\"success\": false, \"message\": \"Password change failed\", \"data\": \"Current password is incorrect\"}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        userProfileService.changePassword(email, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @GetMapping("/email-available")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Check Email Availability",
        description = "Check if an email address is available for use (excluding current user if excludeUserId is provided)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Email availability checked successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Available",
                    value = "{\"success\": true, \"message\": \"Email availability checked successfully\", \"data\": true}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(
            @RequestParam String email,
            @RequestParam(required = false) Long excludeUserId) {
        boolean isAvailable = userProfileService.isEmailAvailable(email, excludeUserId);
        return ResponseEntity.ok(ApiResponse.success("Email availability checked successfully", isAvailable));
    }
} 