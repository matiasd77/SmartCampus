import { useState, useEffect } from 'react';
import { professorsAPI } from '../services/api';
import type { ProfessorsResponse, Professor, ProfessorFilters, ProfessorRequest } from '../types/dashboard';
import { useToast } from '../contexts/ToastContext';

export const useProfessors = (initialPage = 0, initialPageSize = 10) => {
  const [professors, setProfessors] = useState<Professor[]>([]);
  const [currentPage, setCurrentPage] = useState(initialPage);
  const [pageSize, setPageSize] = useState(initialPageSize);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [filters, setFilters] = useState<ProfessorFilters>({});
  const [departments, setDepartments] = useState<string[]>([]);

  const { showSuccess, showError } = useToast();

  const fetchProfessors = async (page: number, size: number, currentFilters: ProfessorFilters) => {
    setIsLoading(true);
    setError(null);

    console.log('useProfessors: Fetching professors with:', { page, size, currentFilters });

    try {
      const response = await professorsAPI.getProfessors(page, size, currentFilters);
      
      console.log('useProfessors: API response:', response);
      console.log('useProfessors: Response structure:', {
        success: response.success,
        message: response.message,
        hasData: !!response.data,
        dataType: typeof response.data,
        dataKeys: response.data ? Object.keys(response.data) : 'no data'
      });
      
      // Handle the new paginated response format
      if (response.success && response.data) {
        const pageData = response.data;
        console.log('useProfessors: Page data:', pageData);
        console.log('useProfessors: Content array:', pageData.content);
        console.log('useProfessors: Content length:', pageData.content?.length);
        
        setProfessors(pageData.content || []);
        setTotalPages(pageData.totalPages || 0);
        setTotalElements(pageData.totalElements || 0);
        setCurrentPage(pageData.number || 0);
        
        console.log('useProfessors: State updated:', {
          professorsCount: pageData.content?.length || 0,
          totalPages: pageData.totalPages || 0,
          totalElements: pageData.totalElements || 0,
          currentPage: pageData.number || 0
        });
      } else {
        console.error('useProfessors: Invalid response format:', response);
        setError('Failed to load professors');
        setProfessors([]);
        setTotalPages(0);
        setTotalElements(0);
      }
    } catch (err: any) {
      console.error('Error fetching professors:', err);
      console.error('Error details:', {
        message: err.message,
        status: err.response?.status,
        statusText: err.response?.statusText,
        data: err.response?.data
      });
      setError(err.response?.data?.message || 'Failed to load professors');
      setProfessors([]);
      setTotalPages(0);
      setTotalElements(0);
    } finally {
      setIsLoading(false);
    }
  };

  const createProfessor = async (professorData: ProfessorRequest) => {
    setIsCreating(true);
    setError(null);

    try {
      const newProfessor = await professorsAPI.createProfessor(professorData);
      setProfessors(prev => [newProfessor, ...prev]);
      showSuccess('Professor created successfully');
      return newProfessor;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to create professor';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsCreating(false);
    }
  };

  const updateProfessor = async (id: number, professorData: Partial<ProfessorRequest>) => {
    setIsUpdating(true);
    setError(null);

    try {
      const updatedProfessor = await professorsAPI.updateProfessor(id, professorData);
      setProfessors(prev => prev.map(professor => 
        professor.id === id ? updatedProfessor : professor
      ));
      showSuccess('Professor updated successfully');
      return updatedProfessor;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to update professor';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsUpdating(false);
    }
  };

  useEffect(() => {
    fetchProfessors(currentPage, pageSize, filters);
  }, [currentPage, pageSize, filters]);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handlePageSizeChange = (size: number) => {
    setPageSize(size);
    setCurrentPage(0); // Reset to first page when changing page size
  };

  const handleFiltersChange = (newFilters: ProfessorFilters) => {
    setFilters(newFilters);
    setCurrentPage(0); // Reset to first page when changing filters
  };

  const clearFilters = () => {
    setFilters({});
    setCurrentPage(0);
  };

  const deleteProfessor = async (id: number) => {
    setIsDeleting(true);
    setError(null);

    try {
      await professorsAPI.deleteProfessor(id);
      setProfessors(prev => prev.filter(professor => professor.id !== id));
      showSuccess('Professor deleted successfully');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to delete professor';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsDeleting(false);
    }
  };

  const refresh = () => {
    fetchProfessors(currentPage, pageSize, filters);
  };

  return {
    professors,
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
    departments,
    handlePageChange,
    handlePageSizeChange,
    handleFiltersChange,
    clearFilters,
    createProfessor,
    updateProfessor,
    deleteProfessor,
    refresh,
  };
}; 