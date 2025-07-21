package com.smartcampus.dto;

import com.smartcampus.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Complete user data transfer object with comprehensive profile information")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {

    @Schema(description = "Unique user identifier", example = "1")
    private Long id;

    @Schema(description = "User's full name", example = "John Smith")
    private String name;

    @Schema(description = "User's email address", example = "john.smith@example.com", format = "email")
    private String email;

    @Schema(description = "User's encrypted password (internal use only)", example = "encrypted_password_hash")
    private String password; // Only for internal use, not exposed in responses

    @Schema(description = "User's role in the system", example = "STUDENT", allowableValues = {"STUDENT", "PROFESSOR", "ADMIN"})
    private Role role;

    @Schema(description = "Whether the user account is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Account creation timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last account update timestamp", example = "2024-01-20T14:45:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Last login timestamp", example = "2024-01-20T14:45:00")
    private LocalDateTime lastLoginAt;

    // Contact Information
    @Schema(description = "User's phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "User's street address", example = "123 Main Street")
    private String address;

    @Schema(description = "User's city", example = "New York")
    private String city;

    @Schema(description = "User's state or province", example = "NY")
    private String state;

    @Schema(description = "User's zip code or postal code", example = "10001")
    private String zipCode;

    @Schema(description = "User's country", example = "USA")
    private String country;

    // Profile Information
    @Schema(description = "User's biographical information", example = "Software engineering student passionate about technology")
    private String bio;

    @Schema(description = "URL to user's profile picture", example = "https://example.com/profile-picture.jpg")
    private String profilePictureUrl;

    // Academic/Professional Information
    @Schema(description = "User's department or faculty", example = "Computer Science")
    private String department;

    @Schema(description = "User's job position or title", example = "Software Engineer")
    private String position;

    @Schema(description = "Student's unique identifier", example = "STU2024001")
    private String studentId;

    @Schema(description = "Employee's unique identifier", example = "EMP2024001")
    private String employeeId;

    @Schema(description = "Student's major field of study", example = "Computer Science")
    private String major;

    @Schema(description = "Student's minor field of study", example = "Mathematics")
    private String minor;

    @Schema(description = "Student's academic year (1-10)", example = "3")
    private Integer year;

    @Schema(description = "Current academic semester", example = "FALL", allowableValues = {"FALL", "SPRING", "SUMMER", "WINTER"})
    private String semester;

    @Schema(description = "Academic year in YYYY-YYYY format", example = "2024-2025")
    private String academicYear;

    @Schema(description = "Student's academic advisor", example = "Dr. Jane Smith")
    private String advisor;

    @Schema(description = "Professor's research area", example = "Artificial Intelligence")
    private String researchArea;

    @Schema(description = "Professor's research interests", example = "Machine Learning, Deep Learning, Computer Vision")
    private String researchInterests;

    // Social Media Links
    @Schema(description = "User's personal website URL", example = "https://johnsmith.dev")
    private String websiteUrl;

    @Schema(description = "User's LinkedIn profile URL", example = "https://linkedin.com/in/johnsmith")
    private String linkedinUrl;

    @Schema(description = "User's GitHub profile URL", example = "https://github.com/johnsmith")
    private String githubUrl;

    @Schema(description = "User's Twitter profile URL", example = "https://twitter.com/johnsmith")
    private String twitterUrl;

    @Schema(description = "User's Facebook profile URL", example = "https://facebook.com/johnsmith")
    private String facebookUrl;

    @Schema(description = "User's Instagram profile URL", example = "https://instagram.com/johnsmith")
    private String instagramUrl;

    // Emergency Contact Information
    @Schema(description = "Emergency contact person's name", example = "Jane Smith")
    private String emergencyContactName;

    @Schema(description = "Emergency contact person's phone number", example = "+1234567890")
    private String emergencyContactPhone;

    @Schema(description = "Relationship to emergency contact person", example = "Mother")
    private String emergencyContactRelationship;

    @Schema(description = "Emergency contact person's address", example = "456 Oak Street")
    private String emergencyContactAddress;

    @Schema(description = "Emergency contact person's city", example = "Los Angeles")
    private String emergencyContactCity;

    @Schema(description = "Emergency contact person's state", example = "CA")
    private String emergencyContactState;

    @Schema(description = "Emergency contact person's zip code", example = "90210")
    private String emergencyContactZipCode;

    @Schema(description = "Emergency contact person's country", example = "USA")
    private String emergencyContactCountry;

    // Additional Information
    @Schema(description = "User's skills and competencies", example = "Java, Spring Boot, React, Docker, AWS")
    private String skills;

    @Schema(description = "Languages the user speaks", example = "English (Native), Albanian (Fluent), Spanish (Intermediate)")
    private String languages;

    @Schema(description = "Professional certifications", example = "AWS Certified Developer, Oracle Certified Professional")
    private String certifications;

    @Schema(description = "Awards and recognitions", example = "Dean's List 2023, Best Student Award 2022")
    private String awards;

    @Schema(description = "Academic publications", example = "Machine Learning Applications in Education (2023)")
    private String publications;

    @Schema(description = "Projects and work experience", example = "SmartCampus Management System, E-commerce Platform")
    private String projects;

    @Schema(description = "Work experience and internships", example = "Software Engineer Intern at TechCorp (2023)")
    private String experience;

    @Schema(description = "Educational background", example = "Bachelor's in Computer Science, University of Technology")
    private String education;

    @Schema(description = "Personal interests and hobbies", example = "Reading, Hiking, Photography, Coding")
    private String interests;

    @Schema(description = "Hobbies and recreational activities", example = "Playing guitar, Swimming, Chess")
    private String hobbies;

    @Schema(description = "Career and personal goals", example = "Become a senior software engineer, Contribute to open source")
    private String goals;

    @Schema(description = "Additional notes and comments", example = "Available for remote work, Open to relocation")
    private String notes;

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

    // Helper method to get user age (if birth date is available)
    public Integer getAge() {
        // This would require a birth date field in the User entity
        // For now, return null
        return null;
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

    // Helper method to mask sensitive information
    public UserDTO maskSensitiveInfo() {
        UserDTO masked = new UserDTO();
        // Copy all fields except sensitive ones
        masked.setId(this.id);
        masked.setName(this.name);
        masked.setEmail(this.email);
        // Don't copy password
        masked.setRole(this.role);
        masked.setIsActive(this.isActive);
        masked.setCreatedAt(this.createdAt);
        masked.setUpdatedAt(this.updatedAt);
        masked.setLastLoginAt(this.lastLoginAt);
        
        // Copy all other fields
        masked.setPhoneNumber(this.phoneNumber);
        masked.setAddress(this.address);
        masked.setCity(this.city);
        masked.setState(this.state);
        masked.setZipCode(this.zipCode);
        masked.setCountry(this.country);
        masked.setBio(this.bio);
        masked.setProfilePictureUrl(this.profilePictureUrl);
        masked.setDepartment(this.department);
        masked.setPosition(this.position);
        masked.setStudentId(this.studentId);
        masked.setEmployeeId(this.employeeId);
        masked.setMajor(this.major);
        masked.setMinor(this.minor);
        masked.setYear(this.year);
        masked.setSemester(this.semester);
        masked.setAcademicYear(this.academicYear);
        masked.setAdvisor(this.advisor);
        masked.setResearchArea(this.researchArea);
        masked.setResearchInterests(this.researchInterests);
        masked.setWebsiteUrl(this.websiteUrl);
        masked.setLinkedinUrl(this.linkedinUrl);
        masked.setGithubUrl(this.githubUrl);
        masked.setTwitterUrl(this.twitterUrl);
        masked.setFacebookUrl(this.facebookUrl);
        masked.setInstagramUrl(this.instagramUrl);
        masked.setEmergencyContactName(this.emergencyContactName);
        masked.setEmergencyContactPhone(this.emergencyContactPhone);
        masked.setEmergencyContactRelationship(this.emergencyContactRelationship);
        masked.setEmergencyContactAddress(this.emergencyContactAddress);
        masked.setEmergencyContactCity(this.emergencyContactCity);
        masked.setEmergencyContactState(this.emergencyContactState);
        masked.setEmergencyContactZipCode(this.emergencyContactZipCode);
        masked.setEmergencyContactCountry(this.emergencyContactCountry);
        masked.setSkills(this.skills);
        masked.setLanguages(this.languages);
        masked.setCertifications(this.certifications);
        masked.setAwards(this.awards);
        masked.setPublications(this.publications);
        masked.setProjects(this.projects);
        masked.setExperience(this.experience);
        masked.setEducation(this.education);
        masked.setInterests(this.interests);
        masked.setHobbies(this.hobbies);
        masked.setGoals(this.goals);
        masked.setNotes(this.notes);
        
        return masked;
    }
} 