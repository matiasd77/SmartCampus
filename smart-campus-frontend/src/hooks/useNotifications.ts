import { useState, useEffect } from 'react';
import { notificationsAPI } from '../services/api';
import type { NotificationsResponse, Notification, NotificationFilters, NotificationRequest } from '../types/dashboard';
import { useToast } from '../contexts/ToastContext';

export const useNotifications = (initialPage = 0, initialPageSize = 10) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [currentPage, setCurrentPage] = useState(initialPage);
  const [pageSize, setPageSize] = useState(initialPageSize);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [filters, setFilters] = useState<NotificationFilters>({});
  const [unreadCount, setUnreadCount] = useState(0);

  const { showSuccess, showError } = useToast();

  const fetchNotifications = async (page: number, size: number, currentFilters: NotificationFilters) => {
    setIsLoading(true);
    setError(null);

    try {
      console.log('useNotifications: Starting fetch for page:', page, 'size:', size, 'filters:', currentFilters);
      const response: NotificationsResponse = await notificationsAPI.getNotifications(page, size, currentFilters);
      
      console.log('useNotifications: Fetched notifications:', response);
      console.log('useNotifications: Notifications content:', response.content);
      
      // Safe assignment with fallback to empty array if content is undefined/null
      setNotifications(response.content ?? []);
      setTotalPages(response.totalPages ?? 0);
      setTotalElements(response.totalElements ?? 0);
      setCurrentPage(response.number ?? 0);
      setError(null); // Clear any previous errors
    } catch (err: any) {
      console.error('useNotifications: Error fetching notifications:', err);
      
      // Enhanced error handling with specific messages
      let errorMessage = 'Failed to load notifications';
      
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
        errorMessage = 'Access denied. You do not have permission to view notifications.';
      } else if (err.response.status >= 500) {
        errorMessage = 'Server error. Please try again later.';
      } else if (err.response.data?.message) {
        errorMessage = err.response.data.message;
      }
      
      setError(errorMessage);
      setNotifications([]);
      setTotalPages(0);
      setTotalElements(0);
    } finally {
      setIsLoading(false);
    }
  };



  const fetchUnreadCount = async () => {
    try {
      const response = await notificationsAPI.getUnreadCount();
      // Safe assignment with fallback to 0 if count is undefined/null
      setUnreadCount(response.count ?? 0);
    } catch (err) {
      console.error('Error fetching unread count:', err);
    }
  };

  useEffect(() => {
    fetchNotifications(currentPage, pageSize, filters);
  }, [currentPage, pageSize, filters]);

  useEffect(() => {
    fetchUnreadCount();
  }, []);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handlePageSizeChange = (size: number) => {
    setPageSize(size);
    setCurrentPage(0); // Reset to first page when changing page size
  };

  const handleFiltersChange = (newFilters: NotificationFilters) => {
    setFilters(newFilters);
    setCurrentPage(0); // Reset to first page when changing filters
  };

  const clearFilters = () => {
    setFilters({});
    setCurrentPage(0);
  };

  const markAsRead = async (id: number) => {
    try {
      await notificationsAPI.markAsRead(id);
      // Safe array update with fallback to empty array if prev is undefined
      setNotifications(prev => (prev ?? []).map(notification => 
        notification.id === id 
          ? { ...notification, isRead: true, status: 'READ' as const }
          : notification
      ));
      // Refresh unread count
      fetchUnreadCount();
      showSuccess('Notification marked as read');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to mark notification as read';
      showError(errorMessage);
    }
  };

  const markAsArchived = async (id: number) => {
    try {
      await notificationsAPI.markAsArchived(id);
      // Safe array update with fallback to empty array if prev is undefined
      setNotifications(prev => (prev ?? []).map(notification => 
        notification.id === id 
          ? { ...notification, status: 'ARCHIVED' as const }
          : notification
      ));
      showSuccess('Notification archived successfully');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to archive notification';
      showError(errorMessage);
    }
  };

  const markAllAsRead = async () => {
    try {
      await notificationsAPI.markAllAsRead();
      // Safe array update with fallback to empty array if prev is undefined
      setNotifications(prev => (prev ?? []).map(notification => 
        !notification.isRead 
          ? { ...notification, isRead: true, status: 'READ' as const }
          : notification
      ));
      // Refresh unread count
      fetchUnreadCount();
      showSuccess('All notifications marked as read');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to mark all notifications as read';
      showError(errorMessage);
    }
  };

  const deleteNotification = async (id: number) => {
    setIsDeleting(true);
    setError(null);

    try {
      await notificationsAPI.deleteNotification(id);
      // Safe array update with fallback to empty array if prev is undefined
      setNotifications(prev => (prev ?? []).filter(notification => notification.id !== id));
      // Refresh unread count
      fetchUnreadCount();
      showSuccess('Notification deleted successfully');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to delete notification';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsDeleting(false);
    }
  };

  const refresh = () => {
    fetchNotifications(currentPage, pageSize, filters);
    fetchUnreadCount();
  };

  return {
    notifications,
    currentPage,
    pageSize,
    totalPages,
    totalElements,
    isLoading,
    error,
    isCreating,
    isUpdating,
    isDeleting,
    filters,
    unreadCount,
    handlePageChange,
    handlePageSizeChange,
    handleFiltersChange,
    clearFilters,
    markAsRead,
    markAsArchived,
    markAllAsRead,
    deleteNotification,
    refresh,
  };
}; 