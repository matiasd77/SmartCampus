package com.smartcampus.exception;

public class AnnouncementNotFoundException extends RuntimeException {
    
    public AnnouncementNotFoundException(String message) {
        super(message);
    }
    
    public AnnouncementNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static AnnouncementNotFoundException withId(Long id) {
        return new AnnouncementNotFoundException("Announcement not found with id: " + id);
    }
} 