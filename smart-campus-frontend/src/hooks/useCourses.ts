import { useState, useEffect, useLayoutEffect, useRef, useCallback } from 'react';
import { coursesAPI } from '../services/api';
import type { Course, CourseRequest, CourseFilters } from '../types/dashboard';
import { useToast } from '../contexts/ToastContext';
import { useAuth } from '../contexts/AuthContext';

export const useCourses = () => {
  const [courses, setCourses] = useState<Course[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [filters, setFilters] = useState<CourseFilters>({});
  const [pagination, setPagination] = useState({
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
    hasNext: false,
    hasPrevious: false,
  });

  const { showSuccess, showError } = useToast();
  const { isLoading: authLoading, isAuthenticated, logout } = useAuth();
  const isInitialized = useRef(false);
  const filtersRef = useRef(filters);
  const paginationRef = useRef(pagination);

  // Update refs when state changes
  useEffect(() => {
    filtersRef.current = filters;
  }, [filters]);

  useEffect(() => {
    paginationRef.current = pagination;
  }, [pagination]);

  const fetchCourses = useCallback(async (page = 0, size = 10, newFilters?: CourseFilters) => {
    setIsLoading(true);
    setError(null);

    try {
      console.log('useCourses: Starting fetch for page:', page, 'size:', size, 'filters:', newFilters || filtersRef.current);
      const response = await coursesAPI.getCourses(page, size, newFilters || filtersRef.current);
      
      console.log('useCourses: Fetched courses response:', response);
      
      if (response.success && response.data) {
        const pageData = response.data;
        console.log('useCourses: Page data:', pageData);
        
        setCourses(pageData.content || []);
        setError(null);
        
        setPagination({
          page: pageData.number || 0,
          size: pageData.size || 10,
          totalElements: pageData.totalElements || 0,
          totalPages: pageData.totalPages || 0,
          hasNext: pageData.last === false,
          hasPrevious: pageData.first === false,
        });
      } else {
        console.error('useCourses: Invalid response format:', response);
        setError(response.message || 'Failed to fetch courses');
        setCourses([]);
      }
    } catch (err: any) {
      console.error('useCourses: Error fetching courses:', err);
      
      let errorMessage = 'Failed to fetch courses';
      
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
        // Trigger logout for 401 errors
        logout();
      } else if (err.response.status === 403) {
        errorMessage = 'Access denied. You do not have permission to view courses.';
      } else if (err.response.status >= 500) {
        errorMessage = 'Server error. Please try again later.';
      } else if (err.response.data?.message) {
        errorMessage = err.response.data.message;
      }
      
      setError(errorMessage);
      setCourses([]);
    } finally {
      setIsLoading(false);
    }
  }, [logout]); // Include logout in dependencies

  const createCourse = useCallback(async (courseData: CourseRequest) => {
    setIsCreating(true);
    setError(null);

    try {
      const response = await coursesAPI.createCourse(courseData);
      
      if (response.success && response.data) {
        console.log('useCourses: Course created successfully:', response.data);
        showSuccess('Course created successfully');
        
        // Refresh the courses list using current pagination
        fetchCourses(paginationRef.current.page, paginationRef.current.size);
        
        return response.data;
      } else {
        throw new Error(response.message || 'Failed to create course');
      }
    } catch (err: any) {
      console.error('useCourses: Error creating course:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Failed to create course';
      showError(errorMessage);
      throw err;
    } finally {
      setIsCreating(false);
    }
  }, [fetchCourses, showSuccess, showError]);

  const updateCourse = useCallback(async (id: number, courseData: Partial<CourseRequest>) => {
    setIsUpdating(true);
    setError(null);

    try {
      const response = await coursesAPI.updateCourse(id, courseData);
      
      if (response.success && response.data) {
        console.log('useCourses: Course updated successfully:', response.data);
        showSuccess('Course updated successfully');
        
        setCourses(prev => prev.map(course => 
          course.id === id ? { ...course, ...response.data } : course
        ));
        
        return response.data;
      } else {
        throw new Error(response.message || 'Failed to update course');
      }
    } catch (err: any) {
      console.error('useCourses: Error updating course:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Failed to update course';
      showError(errorMessage);
      throw err;
    } finally {
      setIsUpdating(false);
    }
  }, [showSuccess, showError]);

  const deleteCourse = useCallback(async (id: number) => {
    setIsDeleting(true);
    setError(null);

    try {
      const response = await coursesAPI.deleteCourse(id);
      
      if (response.success) {
        console.log('useCourses: Course deleted successfully');
        showSuccess('Course deleted successfully');
        
        // Remove the course from the local state
        setCourses(prev => prev.filter(course => course.id !== id));
        
        // Handle pagination edge case: if we deleted the last item on the current page
        // and we're not on the first page, go to the previous page
        const currentPage = paginationRef.current.page;
        const currentSize = paginationRef.current.size;
        const currentTotalElements = paginationRef.current.totalElements;
        
        // If we deleted the last item on the current page and there are more pages
        if (courses.length === 1 && currentPage > 0) {
          // Go to previous page
          fetchCourses(currentPage - 1, currentSize);
        } else {
          // Update total elements count
          setPagination(prev => ({
            ...prev,
            totalElements: Math.max(0, currentTotalElements - 1),
            // Recalculate total pages
            totalPages: Math.ceil(Math.max(0, currentTotalElements - 1) / currentSize),
          }));
        }
      } else {
        throw new Error(response.message || 'Failed to delete course');
      }
    } catch (err: any) {
      console.error('useCourses: Error deleting course:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Failed to delete course';
      showError(errorMessage);
      throw err;
    } finally {
      setIsDeleting(false);
    }
  }, [courses.length, fetchCourses, showSuccess, showError]);

  const getCourseById = useCallback(async (id: number) => {
    try {
      const response = await coursesAPI.getCourseById(id);
      
      if (response.success && response.data) {
        return response.data;
      } else {
        throw new Error(response.message || 'Failed to fetch course');
      }
    } catch (err: any) {
      console.error('Error fetching course:', err);
      throw err;
    }
  }, []);

  const getAvailableCourses = useCallback(async () => {
    try {
      const response = await coursesAPI.getAvailableCourses();
      
      if (response.success && response.data) {
        return response.data;
      } else {
        throw new Error(response.message || 'Failed to fetch available courses');
      }
    } catch (err: any) {
      console.error('Error fetching available courses:', err);
      throw err;
    }
  }, []);

  const getCoursesByDepartment = useCallback(async (department: string) => {
    try {
      const response = await coursesAPI.getCoursesByDepartment(department);
      
      if (response.success && response.data) {
        return response.data;
      } else {
        throw new Error(response.message || 'Failed to fetch courses by department');
      }
    } catch (err: any) {
      console.error('Error fetching courses by department:', err);
      throw err;
    }
  }, []);

  const updateFilters = useCallback((newFilters: CourseFilters) => {
    setFilters(newFilters);
    fetchCourses(0, paginationRef.current.size, newFilters);
  }, [fetchCourses]);

  const refresh = useCallback(() => {
    fetchCourses(paginationRef.current.page, paginationRef.current.size, filtersRef.current);
  }, [fetchCourses]);

  const nextPage = useCallback(() => {
    if (paginationRef.current.hasNext) {
      fetchCourses(paginationRef.current.page + 1, paginationRef.current.size, filtersRef.current);
    }
  }, [fetchCourses]);

  const previousPage = useCallback(() => {
    if (paginationRef.current.hasPrevious) {
      fetchCourses(paginationRef.current.page - 1, paginationRef.current.size, filtersRef.current);
    }
  }, [fetchCourses]);

  const goToPage = useCallback((page: number) => {
    fetchCourses(page, paginationRef.current.size, filtersRef.current);
  }, [fetchCourses]);

  // Use useLayoutEffect for initial data loading to avoid visual flicker
  useLayoutEffect(() => {
    if (!authLoading && isAuthenticated && !isInitialized.current) {
      console.log('useCourses: Auth ready, starting initial data fetch');
      isInitialized.current = true;
      fetchCourses();
    }
  }, [authLoading, isAuthenticated, fetchCourses]);

  return {
    courses,
    isLoading,
    error,
    isCreating,
    isUpdating,
    isDeleting,
    filters,
    pagination,
    createCourse,
    updateCourse,
    deleteCourse,
    getCourseById,
    getAvailableCourses,
    getCoursesByDepartment,
    updateFilters,
    refresh,
    nextPage,
    previousPage,
    goToPage,
  };
}; 