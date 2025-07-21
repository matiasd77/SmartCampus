package com.smartcampus.mapper;

import com.smartcampus.dto.AnnouncementDTO;
import com.smartcampus.entity.Announcement;

import java.util.List;

public interface AnnouncementMapper {
    AnnouncementDTO toDto(Announcement announcement);
    Announcement toEntity(AnnouncementDTO announcementDTO);
    List<AnnouncementDTO> toDtoList(List<Announcement> announcements);
} 