import { useState, useEffect, useRef } from 'react';
import { gradesAPI } from '../services/api';
import type { Grade, GradeRequest, GradeFilters, GradesResponse } from '../types/dashboard';
import { useToast } from '../contexts/ToastContext';

export const useGrades = () => {
  const [grades, setGrades] = useState<Grade[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [filters, setFilters] = useState<GradeFilters>({});
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

  const fetchGrades = async (page = 0, size = 10, newFilters?: GradeFilters) => {
    setIsLoading(true);
    setError(null);

    try {
      const response: GradesResponse = await gradesAPI.getGrades(page, size, newFilters || filters);
      
      setGrades(response.content);
      setPagination({
        page: response.number,
        size: response.size,
        totalElements: response.totalElements,
        totalPages: response.totalPages,
        hasNext: !response.last,
        hasPrevious: !response.first,
      });
    } catch (err: any) {
      console.error('Error fetching grades:', err);
      setError(err.response?.data?.message || 'Failed to load grades');
      setGrades([]);
    } finally {
      setIsLoading(false);
    }
  };

  const createGrade = async (gradeData: GradeRequest) => {
    setIsCreating(true);
    setError(null);

    try {
      const newGrade = await gradesAPI.createGrade(gradeData);
      setGrades(prev => [newGrade, ...prev]);
      showSuccess('Grade created successfully');
      return newGrade;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to create grade';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsCreating(false);
    }
  };

  const updateGrade = async (id: number, gradeData: Partial<GradeRequest>) => {
    setIsUpdating(true);
    setError(null);

    try {
      const updatedGrade = await gradesAPI.updateGrade(id, gradeData);
      setGrades(prev => prev.map(grade => 
        grade.id === id ? updatedGrade : grade
      ));
      showSuccess('Grade updated successfully');
      return updatedGrade;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to update grade';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsUpdating(false);
    }
  };

  const deleteGrade = async (id: number) => {
    setIsDeleting(true);
    setError(null);

    try {
      await gradesAPI.deleteGrade(id);
      setGrades(prev => prev.filter(grade => grade.id !== id));
      showSuccess('Grade deleted successfully');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to delete grade';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsDeleting(false);
    }
  };

  const getGradeById = async (id: number) => {
    try {
      return await gradesAPI.getGradeById(id);
    } catch (err: any) {
      console.error('Error fetching grade:', err);
      throw err;
    }
  };

  const getStudentGrades = async (studentId: number) => {
    try {
      const response = await gradesAPI.getStudentGrades(studentId);
      return response;
    } catch (err: any) {
      console.error('Error fetching student grades:', err);
      throw err;
    }
  };

  const getCourseGrades = async (courseId: number) => {
    try {
      const response = await gradesAPI.getCourseGrades(courseId);
      return response;
    } catch (err: any) {
      console.error('Error fetching course grades:', err);
      throw err;
    }
  };

  const getEnrollmentGrades = async (enrollmentId: number) => {
    try {
      const response = await gradesAPI.getEnrollmentGrades(enrollmentId);
      return response;
    } catch (err: any) {
      console.error('Error fetching enrollment grades:', err);
      throw err;
    }
  };

  const updateFilters = (newFilters: GradeFilters) => {
    setFilters(newFilters);
    fetchGrades(0, pagination.size, newFilters);
  };

  const refresh = () => {
    fetchGrades(pagination.page, pagination.size, filters);
  };

  const nextPage = () => {
    if (pagination.hasNext) {
      fetchGrades(pagination.page + 1, pagination.size, filters);
    }
  };

  const previousPage = () => {
    if (pagination.hasPrevious) {
      fetchGrades(pagination.page - 1, pagination.size, filters);
    }
  };

  const goToPage = (page: number) => {
    fetchGrades(page, pagination.size, filters);
  };

  useEffect(() => {
    if (!isInitialized.current) {
      console.log('useGrades: Initial data fetch on mount');
      isInitialized.current = true;
      fetchGrades();
    }
  }, []); // Empty dependency array ensures this runs only once on mount

  return {
    grades, isLoading, error, isCreating, isUpdating, isDeleting,
    filters, pagination, createGrade, updateGrade, deleteGrade,
    getGradeById, getStudentGrades, getCourseGrades, getEnrollmentGrades,
    updateFilters, refresh, nextPage, previousPage, goToPage,
  };
}; 