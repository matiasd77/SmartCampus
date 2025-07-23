import { useState, useEffect } from 'react';
import { announcementsAPI } from '../services/api';
import type { AnnouncementsResponse, Announcement, AnnouncementRequest } from '../types/dashboard';
import { useToast } from '../contexts/ToastContext';

export const useAnnouncements = (initialPage = 0, initialPageSize = 10) => {
  const [announcements, setAnnouncements] = useState<Announcement[]>([]);
  const [currentPage, setCurrentPage] = useState(initialPage);
  const [pageSize, setPageSize] = useState(initialPageSize);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const { showSuccess, showError } = useToast();

  const fetchAnnouncements = async (page: number, size: number) => {
    setIsLoading(true);
    setError(null);

    try {
      console.log('useAnnouncements: Starting fetch for page:', page, 'size:', size);
      const response: AnnouncementsResponse = await announcementsAPI.getAnnouncements(page, size);
      
      console.log('useAnnouncements: Fetched announcements:', response);
      console.log('useAnnouncements: Announcements content:', response.content);
      
      // Safe assignment with fallback to empty array if content is undefined/null
      setAnnouncements(response.content ?? []);
      setTotalPages(response.totalPages ?? 0);
      setTotalElements(response.totalElements ?? 0);
      setCurrentPage(response.number ?? 0);
      setError(null); // Clear any previous errors
    } catch (err: any) {
      console.error('useAnnouncements: Error fetching announcements:', err);
      
      // Enhanced error handling with specific messages
      let errorMessage = 'Failed to load announcements';
      
      if (!err.response) {
        // Network error or timeout
        if (err.code === 'ECONNABORTED') {
          errorMessage = 'Request timeout - server is not responding. Please try again.';
        } else if (err.message.includes('Network Error')) {
          errorMessage = 'Network error - unable to connect to server. Please check if the backend is running.';
        } else {
          errorMessage = 'Connection error - please check your internet connection.';
        }
      } else if (err.response.status === 401) {
        errorMessage = 'Authentication required. Please log in again.';
      } else if (err.response.status === 403) {
        errorMessage = 'Access denied. You do not have permission to view announcements.';
      } else if (err.response.status >= 500) {
        errorMessage = 'Server error. Please try again later.';
      } else if (err.response.data?.message) {
        errorMessage = err.response.data.message;
      }
      
      setError(errorMessage);
      setAnnouncements([]);
      setTotalPages(0);
      setTotalElements(0);
    } finally {
      setIsLoading(false);
    }
  };

  const createAnnouncement = async (announcementData: AnnouncementRequest) => {
    setIsCreating(true);
    setError(null);

    try {
      const newAnnouncement = await announcementsAPI.createAnnouncement(announcementData);
      // Safe array update with fallback to empty array if prev is undefined
      setAnnouncements(prev => [newAnnouncement, ...(prev ?? [])]);
      showSuccess('Announcement created successfully');
      return newAnnouncement;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to create announcement';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsCreating(false);
    }
  };

  const updateAnnouncement = async (id: number, announcementData: Partial<AnnouncementRequest>) => {
    setIsUpdating(true);
    setError(null);

    try {
      const updatedAnnouncement = await announcementsAPI.updateAnnouncement(id, announcementData);
      // Safe array update with fallback to empty array if prev is undefined
      setAnnouncements(prev => (prev ?? []).map(announcement => 
        announcement.id === id ? updatedAnnouncement : announcement
      ));
      showSuccess('Announcement updated successfully');
      return updatedAnnouncement;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to update announcement';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsUpdating(false);
    }
  };

  const deleteAnnouncement = async (id: number) => {
    setIsDeleting(true);
    setError(null);

    try {
      await announcementsAPI.deleteAnnouncement(id);
      // Safe array update with fallback to empty array if prev is undefined
      setAnnouncements(prev => (prev ?? []).filter(announcement => announcement.id !== id));
      showSuccess('Announcement deleted successfully');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to delete announcement';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsDeleting(false);
    }
  };

  const getAnnouncementById = async (id: number) => {
    try {
      return await announcementsAPI.getAnnouncementById(id);
    } catch (err: any) {
      console.error('Error fetching announcement:', err);
      throw err;
    }
  };

  const getActiveAnnouncements = async () => {
    try {
      const response = await announcementsAPI.getActiveAnnouncements();
      return response;
    } catch (err: any) {
      console.error('Error fetching active announcements:', err);
      throw err;
    }
  };

  const getPublicAnnouncements = async () => {
    try {
      const response = await announcementsAPI.getPublicAnnouncements();
      return response;
    } catch (err: any) {
      console.error('Error fetching public announcements:', err);
      throw err;
    }
  };

  const getCourseAnnouncements = async (courseId: number) => {
    try {
      const response = await announcementsAPI.getCourseAnnouncements(courseId);
      return response;
    } catch (err: any) {
      console.error('Error fetching course announcements:', err);
      throw err;
    }
  };

  useEffect(() => {
    fetchAnnouncements(currentPage, pageSize);
  }, [currentPage, pageSize]);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handlePageSizeChange = (size: number) => {
    setPageSize(size);
    setCurrentPage(0); // Reset to first page when changing page size
  };

  const refresh = () => {
    fetchAnnouncements(currentPage, pageSize);
  };

  return {
    announcements,
    currentPage,
    pageSize,
    totalPages,
    totalElements,
    isLoading,
    error,
    isCreating,
    isUpdating,
    isDeleting,
    handlePageChange,
    handlePageSizeChange,
    createAnnouncement,
    updateAnnouncement,
    deleteAnnouncement,
    getAnnouncementById,
    getActiveAnnouncements,
    getPublicAnnouncements,
    getCourseAnnouncements,
    refresh,
  };
}; 