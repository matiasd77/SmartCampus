import { useState, useEffect } from 'react';
import { enrollmentsAPI } from '../services/api';
import type { Enrollment, EnrollmentRequest, EnrollmentFilters, EnrollmentsResponse } from '../types/dashboard';
import { useToast } from '../contexts/ToastContext';

export const useEnrollments = () => {
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [filters, setFilters] = useState<EnrollmentFilters>({});
  const [pagination, setPagination] = useState({
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
    hasNext: false,
    hasPrevious: false,
  });

  const { showSuccess, showError } = useToast();

  const fetchEnrollments = async (page = 0, size = 10, newFilters?: EnrollmentFilters) => {
    setIsLoading(true);
    setError(null);

    try {
      const response: EnrollmentsResponse = await enrollmentsAPI.getEnrollments(page, size, newFilters || filters);
      
      setEnrollments(response.content);
      setPagination({
        page: response.number,
        size: response.size,
        totalElements: response.totalElements,
        totalPages: response.totalPages,
        hasNext: !response.last,
        hasPrevious: !response.first,
      });
    } catch (err: any) {
      console.error('Error fetching enrollments:', err);
      setError(err.response?.data?.message || 'Failed to load enrollments');
      setEnrollments([]);
    } finally {
      setIsLoading(false);
    }
  };

  const createEnrollment = async (enrollmentData: EnrollmentRequest) => {
    setIsCreating(true);
    setError(null);

    try {
      const newEnrollment = await enrollmentsAPI.createEnrollment(enrollmentData);
      setEnrollments(prev => [newEnrollment, ...prev]);
      showSuccess('Enrollment created successfully');
      return newEnrollment;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to create enrollment';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsCreating(false);
    }
  };

  const updateEnrollment = async (id: number, enrollmentData: { status?: string }) => {
    setIsUpdating(true);
    setError(null);

    try {
      const updatedEnrollment = await enrollmentsAPI.updateEnrollment(id, enrollmentData);
      setEnrollments(prev => prev.map(enrollment => 
        enrollment.id === id ? updatedEnrollment : enrollment
      ));
      showSuccess('Enrollment updated successfully');
      return updatedEnrollment;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to update enrollment';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsUpdating(false);
    }
  };

  const deleteEnrollment = async (id: number) => {
    setIsDeleting(true);
    setError(null);

    try {
      await enrollmentsAPI.deleteEnrollment(id);
      setEnrollments(prev => prev.filter(enrollment => enrollment.id !== id));
      showSuccess('Enrollment deleted successfully');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to delete enrollment';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsDeleting(false);
    }
  };

  const getEnrollmentById = async (id: number) => {
    try {
      return await enrollmentsAPI.getEnrollmentById(id);
    } catch (err: any) {
      console.error('Error fetching enrollment:', err);
      throw err;
    }
  };

  const getStudentEnrollments = async (studentId: number) => {
    try {
      const response = await enrollmentsAPI.getStudentEnrollments(studentId);
      return response;
    } catch (err: any) {
      console.error('Error fetching student enrollments:', err);
      throw err;
    }
  };

  const getCourseEnrollments = async (courseId: number) => {
    try {
      const response = await enrollmentsAPI.getCourseEnrollments(courseId);
      return response;
    } catch (err: any) {
      console.error('Error fetching course enrollments:', err);
      throw err;
    }
  };

  const updateFilters = (newFilters: EnrollmentFilters) => {
    setFilters(newFilters);
    fetchEnrollments(0, pagination.size, newFilters);
  };

  const refresh = () => {
    fetchEnrollments(pagination.page, pagination.size, filters);
  };

  const nextPage = () => {
    if (pagination.hasNext) {
      fetchEnrollments(pagination.page + 1, pagination.size, filters);
    }
  };

  const previousPage = () => {
    if (pagination.hasPrevious) {
      fetchEnrollments(pagination.page - 1, pagination.size, filters);
    }
  };

  const goToPage = (page: number) => {
    fetchEnrollments(page, pagination.size, filters);
  };

  useEffect(() => {
    console.log('useEnrollments: Initial data fetch on mount');
    fetchEnrollments();
  }, []); // Empty dependency array ensures this runs only once on mount

  return {
    enrollments, isLoading, error, isCreating, isUpdating, isDeleting,
    filters, pagination, createEnrollment, updateEnrollment, deleteEnrollment,
    getEnrollmentById, getStudentEnrollments, getCourseEnrollments,
    updateFilters, refresh, nextPage, previousPage, goToPage,
  };
}; 