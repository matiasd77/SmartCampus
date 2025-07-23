import { useState, useEffect, useRef } from 'react';
import { coursesAPI } from '../services/api';
import type { Course, CourseRequest, CourseFilters } from '../types/dashboard';
import { useToast } from '../contexts/ToastContext';

export const useCourses = () => {
  const [courses, setCourses] = useState<Course[]>([]);
  const [isLoading, setIsLoading] = useState(true);
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
  const isInitialized = useRef(false);

  const fetchCourses = async (page = 0, size = 10, newFilters?: CourseFilters) => {
    setIsLoading(true);
    setError(null);

    try {
      console.log('useCourses: Starting fetch for page:', page, 'size:', size, 'filters:', newFilters || filters);
      const response = await coursesAPI.getCourses(page, size, newFilters || filters);
      
      console.log('useCourses: Fetched courses response:', response);
      
      if (response.success && response.data) {
        // Safe assignment with fallback to empty array
        setCourses(response.data ?? []);
        setError(null); // Clear any previous errors
        
        // Update pagination if available
        if (response.data && response.data.length > 0) {
          setPagination(prev => ({
            ...prev,
            page,
            size,
            totalElements: response.data.length, // This might need adjustment based on actual response
            hasNext: response.data.length === size,
            hasPrevious: page > 0,
          }));
        }
      } else {
        setError(response.message || 'Failed to fetch courses');
        setCourses([]); // Set empty array on error
      }
    } catch (err: any) {
      console.error('useCourses: Error fetching courses:', err);
      
      // Enhanced error handling with specific messages
      let errorMessage = 'Failed to fetch courses';
      
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
        errorMessage = 'Access denied. You do not have permission to view courses.';
      } else if (err.response.status >= 500) {
        errorMessage = 'Server error. Please try again later.';
      } else if (err.response.data?.message) {
        errorMessage = err.response.data.message;
      } else if (err.message) {
        errorMessage = err.message;
      }
      
      setError(errorMessage);
      setCourses([]); // Set empty array on error
    } finally {
      setIsLoading(false);
    }
  };

  const createCourse = async (courseData: CourseRequest) => {
    setIsCreating(true);
    setError(null);

    try {
      const response = await coursesAPI.createCourse(courseData);
      
      if (response.success && response.data) {
        // Safe state update with fallback to empty array
        setCourses(prev => [response.data, ...(prev ?? [])]);
        showSuccess('Course created successfully', 'The course has been added to the system.');
        return response.data;
      } else {
        throw new Error(response.message || 'Failed to create course');
      }
    } catch (err: any) {
      console.error('Error creating course:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Failed to create course';
      setError(errorMessage);
      showError('Failed to create course', errorMessage);
      throw err;
    } finally {
      setIsCreating(false);
    }
  };

  const updateCourse = async (id: number, courseData: Partial<CourseRequest>) => {
    setIsUpdating(true);
    setError(null);

    try {
      const response = await coursesAPI.updateCourse(id, courseData);
      
      if (response.success && response.data) {
        // Safe state update with fallback to empty array
        setCourses(prev => (prev ?? []).map(course => 
          course.id === id ? response.data : course
        ));
        showSuccess('Course updated successfully', 'The course has been updated.');
        return response.data;
      } else {
        throw new Error(response.message || 'Failed to update course');
      }
    } catch (err: any) {
      console.error('Error updating course:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Failed to update course';
      setError(errorMessage);
      showError('Failed to update course', errorMessage);
      throw err;
    } finally {
      setIsUpdating(false);
    }
  };

  const deleteCourse = async (id: number) => {
    setIsDeleting(true);
    setError(null);

    try {
      const response = await coursesAPI.deleteCourse(id);
      
      if (response.success) {
        // Safe state update with fallback to empty array
        setCourses(prev => (prev ?? []).filter(course => course.id !== id));
        showSuccess('Course deleted successfully', 'The course has been removed from the system.');
      } else {
        throw new Error(response.message || 'Failed to delete course');
      }
    } catch (err: any) {
      console.error('Error deleting course:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Failed to delete course';
      setError(errorMessage);
      showError('Failed to delete course', errorMessage);
      throw err;
    } finally {
      setIsDeleting(false);
    }
  };

  const getCourseById = async (id: number) => {
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
  };

  const getAvailableCourses = async () => {
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
  };

  const getCoursesByDepartment = async (department: string) => {
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
  };

  const updateFilters = (newFilters: CourseFilters) => {
    setFilters(newFilters);
    fetchCourses(0, pagination.size, newFilters);
  };

  const refresh = () => {
    fetchCourses(pagination.page, pagination.size, filters);
  };

  const nextPage = () => {
    if (pagination.hasNext) {
      fetchCourses(pagination.page + 1, pagination.size, filters);
    }
  };

  const previousPage = () => {
    if (pagination.hasPrevious) {
      fetchCourses(pagination.page - 1, pagination.size, filters);
    }
  };

  const goToPage = (page: number) => {
    fetchCourses(page, pagination.size, filters);
  };

  useEffect(() => {
    if (!isInitialized.current) {
      console.log('useCourses: Initial data fetch on mount');
      isInitialized.current = true;
      fetchCourses();
    }
  }, []); // Empty dependency array ensures this runs only once on mount

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