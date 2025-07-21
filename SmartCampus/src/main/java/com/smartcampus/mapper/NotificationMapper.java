package com.smartcampus.mapper;

import com.smartcampus.dto.NotificationDTO;
import com.smartcampus.entity.Notification;

import java.util.List;

public interface NotificationMapper {
    NotificationDTO toDto(Notification notification);
    Notification toEntity(NotificationDTO notificationDTO);
    List<NotificationDTO> toDtoList(List<Notification> notifications);
} 