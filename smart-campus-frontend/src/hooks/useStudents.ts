import { useState, useEffect } from 'react';
import { studentsAPI } from '../services/api';
import type { StudentsResponse, Student, StudentFilters, StudentRequest } from '../types/dashboard';
import { useToast } from '../contexts/ToastContext';

export const useStudents = (initialPage = 0, initialPageSize = 10) => {
  const [students, setStudents] = useState<Student[]>([]);
  const [currentPage, setCurrentPage] = useState(initialPage);
  const [pageSize, setPageSize] = useState(initialPageSize);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [filters, setFilters] = useState<StudentFilters>({});
  const [majors, setMajors] = useState<string[]>([]);

  const { showSuccess, showError } = useToast();

  const fetchStudents = async (page: number, size: number, currentFilters: StudentFilters) => {
    setIsLoading(true);
    setError(null);

    try {
      const response: StudentsResponse = await studentsAPI.getStudents(page, size, currentFilters);
      
      setStudents(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
      setCurrentPage(response.number);
    } catch (err: any) {
      console.error('Error fetching students:', err);
      setError(err.response?.data?.message || 'Failed to load students');
      setStudents([]);
      setTotalPages(0);
      setTotalElements(0);
    } finally {
      setIsLoading(false);
    }
  };

  const createStudent = async (studentData: StudentRequest) => {
    setIsCreating(true);
    setError(null);

    try {
      const newStudent = await studentsAPI.createStudent(studentData);
      setStudents(prev => [newStudent, ...prev]);
      showSuccess('Student created successfully');
      return newStudent;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to create student';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsCreating(false);
    }
  };

  const updateStudent = async (id: number, studentData: Partial<StudentRequest>) => {
    setIsUpdating(true);
    setError(null);

    try {
      const updatedStudent = await studentsAPI.updateStudent(id, studentData);
      setStudents(prev => prev.map(student => 
        student.id === id ? updatedStudent : student
      ));
      showSuccess('Student updated successfully');
      return updatedStudent;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to update student';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsUpdating(false);
    }
  };

  useEffect(() => {
    fetchStudents(currentPage, pageSize, filters);
  }, [currentPage, pageSize, filters]);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handlePageSizeChange = (size: number) => {
    setPageSize(size);
    setCurrentPage(0); // Reset to first page when changing page size
  };

  const handleFiltersChange = (newFilters: StudentFilters) => {
    setFilters(newFilters);
    setCurrentPage(0); // Reset to first page when changing filters
  };

  const clearFilters = () => {
    setFilters({});
    setCurrentPage(0);
  };

  const deleteStudent = async (id: number) => {
    setIsDeleting(true);
    setError(null);

    try {
      await studentsAPI.deleteStudent(id);
      setStudents(prev => prev.filter(student => student.id !== id));
      showSuccess('Student deleted successfully');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to delete student';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsDeleting(false);
    }
  };

  const refresh = () => {
    fetchStudents(currentPage, pageSize, filters);
  };

  return {
    students,
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
    majors,
    handlePageChange,
    handlePageSizeChange,
    handleFiltersChange,
    clearFilters,
    createStudent,
    updateStudent,
    deleteStudent,
    refresh,
  };
}; 