import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useAttendance } from '../hooks/useAttendance';
import { SearchAndFilters } from '../components/SearchAndFilters';
import { Pagination } from '../components/Pagination';
import { ATTENDANCE_STATUSES } from '../types/dashboard';
import type { Attendance, AttendanceFilters } from '../types/dashboard';
import { Navbar } from '../components/Navbar';

export default function Attendance() {
  const { user } = useAuth();
  const {
    attendance,
    isLoading,
    error,
    isCreating,
    isUpdating,
    isDeleting,
    filters,
    pagination,
    createAttendance,
    updateAttendance,
    deleteAttendance,
    getAttendanceById,
    getStudentAttendance,
    getCourseAttendance,
    getAttendanceByDate,
    updateFilters,
    refresh,
    nextPage,
    previousPage,
    goToPage,
  } = useAttendance();

  const [searchValue, setSearchValue] = useState('');

  const handleSearchChange = (value: string) => {
    setSearchValue(value);
    updateFilters({
      ...filters,
      search: value || undefined,
    });
  };

  const handleFiltersChange = (newFilters: Record<string, string | undefined>) => {
    updateFilters({
      ...newFilters,
      search: searchValue || undefined,
    } as AttendanceFilters);
  };

  const handleClearFilters = () => {
    setSearchValue('');
    updateFilters({});
  };

  const handleEditAttendance = (attendanceRecord: Attendance) => {
    // TODO: Navigate to edit page or open edit modal
    console.log('Edit attendance:', attendanceRecord);
  };

  const handleDeleteAttendance = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this attendance record?')) {
      try {
        await deleteAttendance(id);
      } catch (error) {
        console.error('Error deleting attendance:', error);
      }
    }
  };

  const handleCreateAttendance = () => {
    // TODO: Navigate to create page or open create modal
    console.log('Create new attendance record');
  };

  const isAdmin = user?.role === 'ADMIN';
  const isProfessor = user?.role === 'PROFESSOR';

  // Prepare filter options
  const filterOptions = [
    {
      key: 'status',
      label: 'Status',
      options: ATTENDANCE_STATUSES.map(status => ({ value: status.value, label: status.label })),
    },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      
      <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          {/* Header */}
          <div className="mb-8">
            <div className="flex items-center justify-between">
              <div>
                <h1 className="text-2xl font-bold text-gray-900">Attendance</h1>
                <p className="mt-1 text-sm text-gray-500">
                  Manage student attendance records and tracking.
                </p>
              </div>
              <button
                onClick={refresh}
                disabled={isLoading}
                className="px-4 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700 disabled:opacity-50 flex items-center space-x-2"
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
                <span>Refresh</span>
              </button>
            </div>
          </div>

          {/* Error Message */}
          {error && (
            <div className="mb-6 bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded">
              {error}
            </div>
          )}

          {/* Search and Filters */}
          <SearchAndFilters
            searchValue={searchValue}
            onSearchChange={handleSearchChange}
            filters={filters as Record<string, string | undefined>}
            onFiltersChange={handleFiltersChange}
            onClearFilters={handleClearFilters}
            filterOptions={filterOptions}
            placeholder="Search attendance by student or course..."
            showCreateButton={isAdmin || isProfessor}
            onCreateClick={handleCreateAttendance}
            createButtonText="Add Attendance"
          />

          {/* Loading State */}
          {isLoading && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[...Array(6)].map((_, index) => (
                <div key={index} className="bg-white shadow rounded-lg p-6 animate-pulse">
                  <div className="h-4 bg-gray-200 rounded w-3/4 mb-4"></div>
                  <div className="h-3 bg-gray-200 rounded w-1/2 mb-2"></div>
                  <div className="h-3 bg-gray-200 rounded w-2/3"></div>
                </div>
              ))}
            </div>
          )}

          {/* Attendance List */}
          {!isLoading && attendance.length > 0 && (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                {attendance.map((attendanceRecord) => (
                  <div key={attendanceRecord.id} className="bg-white shadow rounded-lg p-6">
                    <div className="flex items-center justify-between mb-4">
                      <h3 className="text-lg font-semibold text-gray-900">
                        {attendanceRecord.studentName || `Student ${attendanceRecord.studentId}`}
                      </h3>
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                        attendanceRecord.status === 'PRESENT' ? 'bg-green-100 text-green-800' :
                        attendanceRecord.status === 'ABSENT' ? 'bg-red-100 text-red-800' :
                        attendanceRecord.status === 'LATE' ? 'bg-yellow-100 text-yellow-800' :
                        attendanceRecord.status === 'EXCUSED' ? 'bg-blue-100 text-blue-800' :
                        'bg-gray-100 text-gray-800'
                      }`}>
                        {attendanceRecord.status}
                      </span>
                    </div>
                    
                    <div className="space-y-2 mb-4">
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Course:</span> {attendanceRecord.courseName || `Course ${attendanceRecord.courseId}`}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Student ID:</span> {attendanceRecord.studentId}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Course ID:</span> {attendanceRecord.courseId}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Date:</span> {new Date(attendanceRecord.date).toLocaleDateString()}
                      </p>
                      {attendanceRecord.notes && (
                        <p className="text-sm text-gray-600">
                          <span className="font-medium">Notes:</span> {attendanceRecord.notes}
                        </p>
                      )}
                    </div>

                    {/* Action Buttons */}
                    {(isAdmin || isProfessor) && (
                      <div className="flex space-x-2">
                        <button
                          onClick={() => handleEditAttendance(attendanceRecord)}
                          className="flex-1 px-3 py-2 bg-blue-600 text-white text-sm rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                          Edit
                        </button>
                        {isAdmin && (
                          <button
                            onClick={() => handleDeleteAttendance(attendanceRecord.id)}
                            disabled={isDeleting}
                            className="flex-1 px-3 py-2 bg-red-600 text-white text-sm rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 disabled:opacity-50"
                          >
                            Delete
                          </button>
                        )}
                      </div>
                    )}
                  </div>
                ))}
              </div>

              {/* Pagination */}
              <Pagination
                currentPage={pagination.page}
                totalPages={pagination.totalPages}
                totalElements={pagination.totalElements}
                pageSize={pagination.size}
                onPageChange={goToPage}
                onPageSizeChange={(size) => {
                  // TODO: Implement page size change
                  console.log('Change page size to:', size);
                }}
              />
            </>
          )}

          {/* Empty State */}
          {!isLoading && attendance.length === 0 && (
            <div className="text-center py-12">
              <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <h3 className="mt-2 text-sm font-medium text-gray-900">No attendance records found</h3>
              <p className="mt-1 text-sm text-gray-500">
                {Object.keys(filters).length > 0
                  ? 'Try adjusting your search or filter criteria.'
                  : 'Get started by adding a new attendance record.'}
              </p>
              {(isAdmin || isProfessor) && (
                <div className="mt-6">
                  <button
                    onClick={handleCreateAttendance}
                    className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                  >
                    <svg className="-ml-1 mr-2 h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                    </svg>
                    Add Attendance
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
} 