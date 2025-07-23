import { useState, useEffect } from 'react';
import { attendanceAPI } from '../services/api';
import type { Attendance, AttendanceRequest, AttendanceFilters, AttendanceResponse } from '../types/dashboard';
import { useToast } from '../contexts/ToastContext';

export const useAttendance = () => {
  const [attendance, setAttendance] = useState<Attendance[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [filters, setFilters] = useState<AttendanceFilters>({});
  const [pagination, setPagination] = useState({
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
    hasNext: false,
    hasPrevious: false,
  });

  const { showSuccess, showError } = useToast();

  const fetchAttendance = async (page = 0, size = 10, newFilters?: AttendanceFilters) => {
    setIsLoading(true);
    setError(null);

    try {
      const response: AttendanceResponse = await attendanceAPI.getAttendance(page, size, newFilters || filters);
      
      setAttendance(response.content);
      setPagination({
        page: response.number,
        size: response.size,
        totalElements: response.totalElements,
        totalPages: response.totalPages,
        hasNext: !response.last,
        hasPrevious: !response.first,
      });
    } catch (err: any) {
      console.error('Error fetching attendance:', err);
      setError(err.response?.data?.message || 'Failed to load attendance');
      setAttendance([]);
    } finally {
      setIsLoading(false);
    }
  };

  const createAttendance = async (attendanceData: AttendanceRequest) => {
    setIsCreating(true);
    setError(null);

    try {
      const newAttendance = await attendanceAPI.createAttendance(attendanceData);
      setAttendance(prev => [newAttendance, ...prev]);
      showSuccess('Attendance record created successfully');
      return newAttendance;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to create attendance record';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsCreating(false);
    }
  };

  const updateAttendance = async (id: number, attendanceData: Partial<AttendanceRequest>) => {
    setIsUpdating(true);
    setError(null);

    try {
      const updatedAttendance = await attendanceAPI.updateAttendance(id, attendanceData);
      setAttendance(prev => prev.map(record => 
        record.id === id ? updatedAttendance : record
      ));
      showSuccess('Attendance record updated successfully');
      return updatedAttendance;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to update attendance record';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsUpdating(false);
    }
  };

  const deleteAttendance = async (id: number) => {
    setIsDeleting(true);
    setError(null);

    try {
      await attendanceAPI.deleteAttendance(id);
      setAttendance(prev => prev.filter(record => record.id !== id));
      showSuccess('Attendance record deleted successfully');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to delete attendance record';
      setError(errorMessage);
      showError(errorMessage);
      throw err;
    } finally {
      setIsDeleting(false);
    }
  };

  const getAttendanceById = async (id: number) => {
    try {
      return await attendanceAPI.getAttendanceById(id);
    } catch (err: any) {
      console.error('Error fetching attendance:', err);
      throw err;
    }
  };

  const getStudentAttendance = async (studentId: number) => {
    try {
      const response = await attendanceAPI.getStudentAttendance(studentId);
      return response;
    } catch (err: any) {
      console.error('Error fetching student attendance:', err);
      throw err;
    }
  };

  const getCourseAttendance = async (courseId: number) => {
    try {
      const response = await attendanceAPI.getCourseAttendance(courseId);
      return response;
    } catch (err: any) {
      console.error('Error fetching course attendance:', err);
      throw err;
    }
  };

  const getAttendanceByDate = async (date: string) => {
    try {
      const response = await attendanceAPI.getAttendanceByDate(date);
      return response;
    } catch (err: any) {
      console.error('Error fetching attendance by date:', err);
      throw err;
    }
  };

  const updateFilters = (newFilters: AttendanceFilters) => {
    setFilters(newFilters);
    fetchAttendance(0, pagination.size, newFilters);
  };

  const refresh = () => {
    fetchAttendance(pagination.page, pagination.size, filters);
  };

  const nextPage = () => {
    if (pagination.hasNext) {
      fetchAttendance(pagination.page + 1, pagination.size, filters);
    }
  };

  const previousPage = () => {
    if (pagination.hasPrevious) {
      fetchAttendance(pagination.page - 1, pagination.size, filters);
    }
  };

  const goToPage = (page: number) => {
    fetchAttendance(page, pagination.size, filters);
  };

  useEffect(() => {
    fetchAttendance();
  }, []);

  return {
    attendance, isLoading, error, isCreating, isUpdating, isDeleting,
    filters, pagination, createAttendance, updateAttendance, deleteAttendance,
    getAttendanceById, getStudentAttendance, getCourseAttendance, getAttendanceByDate,
    updateFilters, refresh, nextPage, previousPage, goToPage,
  };
}; 