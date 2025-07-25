import { useState, useEffect, useCallback, useRef } from 'react';
import { notificationsAPI } from '../services/api';
import type { NotificationsResponse, Notification, NotificationFilters, NotificationRequest } from '../types/dashboard';
import { useToast } from '../contexts/ToastContext';
import { useAuth } from '../contexts/AuthContext';

export const useNotifications = (initialPage = 0, initialPageSize = 10) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [currentPage, setCurrentPage] = useState(initialPage);
  const [pageSize, setPageSize] = useState(initialPageSize);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [filters, setFilters] = useState<NotificationFilters>({});
  const [unreadCount, setUnreadCount] = useState(0);

  const { showSuccess, showError } = useToast();
  const { isLoading: authLoading, isAuthenticated } = useAuth();
  const isInitialized = useRef(false);
  const filtersRef = useRef(filters);
  const currentPageRef = useRef(currentPage);
  const pageSizeRef = useRef(pageSize);

  // Update refs when state changes
  useEffect(() => {
    filtersRef.current = filters;
  }, [filters]);

  useEffect(() => {
    currentPageRef.current = currentPage;
  }, [currentPage]);

  useEffect(() => {
    pageSizeRef.current = pageSize;
  }, [pageSize]);

  const fetchNotifications = useCallback(async (page: number, size: number, currentFilters: NotificationFilters) => {
    setIsLoading(true);
    setError(null);

    try {
      console.log('useNotifications: Starting fetch for page:', page, 'size:', size, 'filters:', currentFilters);
      const response: NotificationsResponse = await notificationsAPI.getNotifications(page, size, currentFilters);
      
      console.log('useNotifications: Fetched notifications:', response);
      console.log('useNotifications: Notifications content:', response.content);
      
      setNotifications(response.content ?? []);
      setTotalPages(response.totalPages ?? 0);
      setTotalElements(response.totalElements ?? 0);
      setCurrentPage(response.number ?? 0);
      setError(null);
    } catch (err: any) {
      console.error('useNotifications: Error fetching notifications:', err);
      
      let errorMessage = 'Failed to load notifications';
      
      if (!err.response) {
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
  }, []); // Empty dependency array - function is stable

  const fetchUnreadCount = useCallback(async () => {
    try {
      const response = await notificationsAPI.getUnreadCount();
      setUnreadCount(response.count ?? 0);
    } catch (err) {
      console.error('Error fetching unread count:', err);
    }
  }, []);

  // Main effect for initial data loading
  useEffect(() => {
    console.log('useNotifications: useEffect triggered', {
      authLoading,
      isAuthenticated,
      isInitialized: isInitialized.current,
      currentPage: currentPageRef.current,
      pageSize: pageSizeRef.current,
      filters: filtersRef.current
    });
    
    if (!authLoading && isAuthenticated && !isInitialized.current) {
      console.log('useNotifications: Auth ready, starting initial data fetch');
      isInitialized.current = true;
      fetchNotifications(currentPageRef.current, pageSizeRef.current, filtersRef.current);
      fetchUnreadCount();
    } else if (!authLoading && !isAuthenticated) {
      console.log('useNotifications: User not authenticated, clearing data');
      setNotifications([]);
      setError(null);
      isInitialized.current = false;
    }
  }, [authLoading, isAuthenticated]); // Removed fetchNotifications and fetchUnreadCount from dependencies

  // Additional effect to ensure data is loaded on mount
  useEffect(() => {
    console.log('useNotifications: Mount effect triggered');
    if (!authLoading && isAuthenticated && notifications.length === 0 && !isLoading) {
      console.log('useNotifications: Mount effect - loading data');
      fetchNotifications(currentPageRef.current, pageSizeRef.current, filtersRef.current);
      fetchUnreadCount();
    }
  }, []); // Only run on mount

  const handlePageChange = useCallback((page: number) => {
    setCurrentPage(page);
  }, []);

  const handlePageSizeChange = useCallback((size: number) => {
    setPageSize(size);
    setCurrentPage(0); // Reset to first page when changing page size
  }, []);

  const handleFiltersChange = useCallback((newFilters: NotificationFilters) => {
    setFilters(newFilters);
    setCurrentPage(0); // Reset to first page when changing filters
  }, []);

  const clearFilters = useCallback(() => {
    setFilters({});
    setCurrentPage(0);
  }, []);

  const markAsRead = useCallback(async (id: number) => {
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
  }, [fetchUnreadCount, showSuccess, showError]);

  const markAsArchived = useCallback(async (id: number) => {
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
  }, [showSuccess, showError]);

  const markAllAsRead = useCallback(async () => {
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
  }, [fetchUnreadCount, showSuccess, showError]);

  const deleteNotification = useCallback(async (id: number) => {
    try {
      await notificationsAPI.deleteNotification(id);
      // Safe array update with fallback to empty array if prev is undefined
      setNotifications(prev => (prev ?? []).filter(notification => notification.id !== id));
      showSuccess('Notification deleted successfully');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to delete notification';
      showError(errorMessage);
    }
  }, [showSuccess, showError]);

  const createNotification = useCallback(async (notificationData: NotificationRequest) => {
    setIsCreating(true);
    setError(null);

    try {
      const response = await notificationsAPI.createNotification(notificationData);
      
      if (response.success && response.data) {
        console.log('useNotifications: Notification created successfully:', response.data);
        showSuccess('Notification created successfully');
        
        // Refresh the notifications list
        fetchNotifications(currentPageRef.current, pageSizeRef.current, filtersRef.current);
        
        return response.data;
      } else {
        throw new Error(response.message || 'Failed to create notification');
      }
    } catch (err: any) {
      console.error('useNotifications: Error creating notification:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Failed to create notification';
      showError(errorMessage);
      throw err;
    } finally {
      setIsCreating(false);
    }
  }, [fetchNotifications, showSuccess, showError]);

  const refresh = useCallback(() => {
    console.log('useNotifications: Manual refresh triggered');
    if (isAuthenticated) {
      fetchNotifications(currentPageRef.current, pageSizeRef.current, filtersRef.current);
      fetchUnreadCount();
    }
  }, [isAuthenticated]);

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
    createNotification,
    refresh,
  };
}; 