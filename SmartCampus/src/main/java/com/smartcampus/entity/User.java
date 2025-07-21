package com.smartcampus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required")
    @Column(nullable = false)
    private Role role;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // Contact Information
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Size(max = 200, message = "Address must not exceed 200 characters")
    @Column(name = "address")
    private String address;

    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(name = "city")
    private String city;

    @Size(max = 100, message = "State must not exceed 100 characters")
    @Column(name = "state")
    private String state;

    @Size(max = 20, message = "Zip code must not exceed 20 characters")
    @Column(name = "zip_code")
    private String zipCode;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Column(name = "country")
    private String country;

    // Profile Information
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Size(max = 200, message = "Profile picture URL must not exceed 200 characters")
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    // Academic/Professional Information
    @Size(max = 100, message = "Department must not exceed 100 characters")
    @Column(name = "department")
    private String department;

    @Size(max = 100, message = "Position must not exceed 100 characters")
    @Column(name = "position")
    private String position;

    @Size(max = 100, message = "Student ID must not exceed 100 characters")
    @Column(name = "student_id")
    private String studentId;

    @Size(max = 100, message = "Employee ID must not exceed 100 characters")
    @Column(name = "employee_id")
    private String employeeId;

    @Size(max = 100, message = "Major must not exceed 100 characters")
    @Column(name = "major")
    private String major;

    @Size(max = 100, message = "Minor must not exceed 100 characters")
    @Column(name = "minor")
    private String minor;

    @Column(name = "year")
    private Integer year;

    @Size(max = 20, message = "Semester must not exceed 20 characters")
    @Column(name = "semester")
    private String semester;

    @Size(max = 20, message = "Academic year must not exceed 20 characters")
    @Column(name = "academic_year")
    private String academicYear;

    @Size(max = 100, message = "Advisor must not exceed 100 characters")
    @Column(name = "advisor")
    private String advisor;

    @Size(max = 100, message = "Research area must not exceed 100 characters")
    @Column(name = "research_area")
    private String researchArea;

    @Size(max = 500, message = "Research interests must not exceed 500 characters")
    @Column(name = "research_interests", columnDefinition = "TEXT")
    private String researchInterests;

    // Social Media Links
    @Size(max = 200, message = "Website URL must not exceed 200 characters")
    @Column(name = "website_url")
    private String websiteUrl;

    @Size(max = 200, message = "LinkedIn URL must not exceed 200 characters")
    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Size(max = 200, message = "GitHub URL must not exceed 200 characters")
    @Column(name = "github_url")
    private String githubUrl;

    @Size(max = 200, message = "Twitter URL must not exceed 200 characters")
    @Column(name = "twitter_url")
    private String twitterUrl;

    @Size(max = 200, message = "Facebook URL must not exceed 200 characters")
    @Column(name = "facebook_url")
    private String facebookUrl;

    @Size(max = 200, message = "Instagram URL must not exceed 200 characters")
    @Column(name = "instagram_url")
    private String instagramUrl;

    // Emergency Contact Information
    @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Size(max = 20, message = "Emergency contact phone must not exceed 20 characters")
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @Size(max = 200, message = "Emergency contact relationship must not exceed 200 characters")
    @Column(name = "emergency_contact_relationship")
    private String emergencyContactRelationship;

    @Size(max = 200, message = "Emergency contact address must not exceed 200 characters")
    @Column(name = "emergency_contact_address")
    private String emergencyContactAddress;

    @Size(max = 100, message = "Emergency contact city must not exceed 100 characters")
    @Column(name = "emergency_contact_city")
    private String emergencyContactCity;

    @Size(max = 100, message = "Emergency contact state must not exceed 100 characters")
    @Column(name = "emergency_contact_state")
    private String emergencyContactState;

    @Size(max = 20, message = "Emergency contact zip code must not exceed 20 characters")
    @Column(name = "emergency_contact_zip_code")
    private String emergencyContactZipCode;

    @Size(max = 100, message = "Emergency contact country must not exceed 100 characters")
    @Column(name = "emergency_contact_country")
    private String emergencyContactCountry;

    // Additional Information
    @Size(max = 500, message = "Skills must not exceed 500 characters")
    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @Size(max = 500, message = "Languages must not exceed 500 characters")
    @Column(name = "languages", columnDefinition = "TEXT")
    private String languages;

    @Size(max = 500, message = "Certifications must not exceed 500 characters")
    @Column(name = "certifications", columnDefinition = "TEXT")
    private String certifications;

    @Size(max = 500, message = "Awards must not exceed 500 characters")
    @Column(name = "awards", columnDefinition = "TEXT")
    private String awards;

    @Size(max = 500, message = "Publications must not exceed 500 characters")
    @Column(name = "publications", columnDefinition = "TEXT")
    private String publications;

    @Size(max = 500, message = "Projects must not exceed 500 characters")
    @Column(name = "projects", columnDefinition = "TEXT")
    private String projects;

    @Size(max = 500, message = "Experience must not exceed 500 characters")
    @Column(name = "experience", columnDefinition = "TEXT")
    private String experience;

    @Size(max = 500, message = "Education must not exceed 500 characters")
    @Column(name = "education", columnDefinition = "TEXT")
    private String education;

    @Size(max = 500, message = "Interests must not exceed 500 characters")
    @Column(name = "interests", columnDefinition = "TEXT")
    private String interests;

    @Size(max = 500, message = "Hobbies must not exceed 500 characters")
    @Column(name = "hobbies", columnDefinition = "TEXT")
    private String hobbies;

    @Size(max = 500, message = "Goals must not exceed 500 characters")
    @Column(name = "goals", columnDefinition = "TEXT")
    private String goals;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to check if user is active
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    // Helper method to get full address
    public String getFullAddress() {
        StringBuilder addressBuilder = new StringBuilder();
        if (address != null && !address.trim().isEmpty()) {
            addressBuilder.append(address);
        }
        if (city != null && !city.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(city);
        }
        if (state != null && !state.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(state);
        }
        if (zipCode != null && !zipCode.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(" ");
            addressBuilder.append(zipCode);
        }
        if (country != null && !country.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(country);
        }
        return addressBuilder.toString();
    }

    // Helper method to get full emergency contact address
    public String getFullEmergencyContactAddress() {
        StringBuilder addressBuilder = new StringBuilder();
        if (emergencyContactAddress != null && !emergencyContactAddress.trim().isEmpty()) {
            addressBuilder.append(emergencyContactAddress);
        }
        if (emergencyContactCity != null && !emergencyContactCity.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(emergencyContactCity);
        }
        if (emergencyContactState != null && !emergencyContactState.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(emergencyContactState);
        }
        if (emergencyContactZipCode != null && !emergencyContactZipCode.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(" ");
            addressBuilder.append(emergencyContactZipCode);
        }
        if (emergencyContactCountry != null && !emergencyContactCountry.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(emergencyContactCountry);
        }
        return addressBuilder.toString();
    }

    // Helper method to get display name
    public String getDisplayName() {
        if (name != null && !name.trim().isEmpty()) {
            return name.trim();
        }
        return email; // Fallback to email if name is not available
    }

    // Helper method to get formatted phone number
    public String getFormattedPhoneNumber() {
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            return phoneNumber.trim().replaceAll("\\s+", " ");
        }
        return null;
    }

    // Helper method to get formatted emergency contact phone
    public String getFormattedEmergencyContactPhone() {
        if (emergencyContactPhone != null && !emergencyContactPhone.trim().isEmpty()) {
            return emergencyContactPhone.trim().replaceAll("\\s+", " ");
        }
        return null;
    }

    // Helper method to check if user has complete profile
    public boolean hasCompleteProfile() {
        return name != null && !name.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               phoneNumber != null && !phoneNumber.trim().isEmpty() &&
               address != null && !address.trim().isEmpty();
    }

    // Helper method to get profile completion percentage
    public int getProfileCompletionPercentage() {
        int totalFields = 0;
        int completedFields = 0;

        // Basic information
        totalFields += 2; // name, email
        if (name != null && !name.trim().isEmpty()) completedFields++;
        if (email != null && !email.trim().isEmpty()) completedFields++;

        // Contact information
        totalFields += 5; // phoneNumber, address, city, state, country
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) completedFields++;
        if (address != null && !address.trim().isEmpty()) completedFields++;
        if (city != null && !city.trim().isEmpty()) completedFields++;
        if (state != null && !state.trim().isEmpty()) completedFields++;
        if (country != null && !country.trim().isEmpty()) completedFields++;

        // Academic/Professional information
        totalFields += 3; // department, major, year
        if (department != null && !department.trim().isEmpty()) completedFields++;
        if (major != null && !major.trim().isEmpty()) completedFields++;
        if (year != null) completedFields++;

        // Emergency contact
        totalFields += 2; // emergencyContactName, emergencyContactPhone
        if (emergencyContactName != null && !emergencyContactName.trim().isEmpty()) completedFields++;
        if (emergencyContactPhone != null && !emergencyContactPhone.trim().isEmpty()) completedFields++;

        return totalFields > 0 ? (completedFields * 100) / totalFields : 0;
    }

    // Helper method to get user role display name
    public String getRoleDisplayName() {
        if (role == null) {
            return "Unknown";
        }
        switch (role) {
            case STUDENT:
                return "Student";
            case PROFESSOR:
                return "Professor";
            case ADMIN:
                return "Administrator";
            default:
                return role.name();
        }
    }

    // Helper method to check if user is a student
    public boolean isStudent() {
        return Role.STUDENT.equals(role);
    }

    // Helper method to check if user is a professor
    public boolean isProfessor() {
        return Role.PROFESSOR.equals(role);
    }

    // Helper method to check if user is an admin
    public boolean isAdmin() {
        return Role.ADMIN.equals(role);
    }

    // Helper method to get account age in days
    public Long getAccountAgeInDays() {
        if (createdAt == null) {
            return null;
        }
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toDays();
    }

    // Helper method to get last login age in days
    public Long getLastLoginAgeInDays() {
        if (lastLoginAt == null) {
            return null;
        }
        return java.time.Duration.between(lastLoginAt, LocalDateTime.now()).toDays();
    }

    // Helper method to get formatted account age
    public String getFormattedAccountAge() {
        Long ageInDays = getAccountAgeInDays();
        if (ageInDays == null) {
            return "Unknown";
        }
        
        if (ageInDays < 30) {
            return ageInDays + " day" + (ageInDays == 1 ? "" : "s");
        } else if (ageInDays < 365) {
            long months = ageInDays / 30;
            return months + " month" + (months == 1 ? "" : "s");
        } else {
            long years = ageInDays / 365;
            return years + " year" + (years == 1 ? "" : "s");
        }
    }

    // Helper method to get formatted last login
    public String getFormattedLastLogin() {
        Long ageInDays = getLastLoginAgeInDays();
        if (ageInDays == null) {
            return "Never";
        }
        
        if (ageInDays < 1) {
            return "Today";
        } else if (ageInDays < 7) {
            return ageInDays + " day" + (ageInDays == 1 ? "" : "s") + " ago";
        } else if (ageInDays < 30) {
            long weeks = ageInDays / 7;
            return weeks + " week" + (weeks == 1 ? "" : "s") + " ago";
        } else if (ageInDays < 365) {
            long months = ageInDays / 30;
            return months + " month" + (months == 1 ? "" : "s") + " ago";
        } else {
            long years = ageInDays / 365;
            return years + " year" + (years == 1 ? "" : "s") + " ago";
        }
    }
} 