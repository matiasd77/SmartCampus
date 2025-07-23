import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useEnrollments } from '../hooks/useEnrollments';
import { SearchAndFilters } from '../components/SearchAndFilters';
import { Pagination } from '../components/Pagination';
import { ENROLLMENT_STATUSES } from '../types/dashboard';
import type { Enrollment, EnrollmentFilters } from '../types/dashboard';
import { Navbar } from '../components/Navbar';

export default function Enrollments() {
  const { user } = useAuth();
  const {
    enrollments,
    isLoading,
    error,
    isCreating,
    isUpdating,
    isDeleting,
    filters,
    pagination,
    createEnrollment,
    updateEnrollment,
    deleteEnrollment,
    getEnrollmentById,
    getStudentEnrollments,
    getCourseEnrollments,
    updateFilters,
    refresh,
    nextPage,
    previousPage,
    goToPage,
  } = useEnrollments();

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
    } as EnrollmentFilters);
  };

  const handleClearFilters = () => {
    setSearchValue('');
    updateFilters({});
  };

  const handleEditEnrollment = (enrollment: Enrollment) => {
    // TODO: Navigate to edit page or open edit modal
    console.log('Edit enrollment:', enrollment);
  };

  const handleDeleteEnrollment = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this enrollment?')) {
      try {
        await deleteEnrollment(id);
      } catch (error) {
        console.error('Error deleting enrollment:', error);
      }
    }
  };

  const handleCreateEnrollment = () => {
    // TODO: Navigate to create page or open create modal
    console.log('Create new enrollment');
  };

  const isAdmin = user?.role === 'ADMIN';
  const isProfessor = user?.role === 'PROFESSOR';

  // Prepare filter options
  const filterOptions = [
    {
      key: 'status',
      label: 'Status',
      options: ENROLLMENT_STATUSES.map(status => ({ value: status.value, label: status.label })),
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
                <h1 className="text-2xl font-bold text-gray-900">Enrollments</h1>
                <p className="mt-1 text-sm text-gray-500">
                  Manage student course enrollments and registrations.
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
            placeholder="Search enrollments by student or course..."
            showCreateButton={isAdmin || isProfessor}
            onCreateClick={handleCreateEnrollment}
            createButtonText="Add Enrollment"
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

          {/* Enrollments List */}
          {!isLoading && enrollments.length > 0 && (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                {enrollments.map((enrollment) => (
                  <div key={enrollment.id} className="bg-white shadow rounded-lg p-6">
                    <div className="flex items-center justify-between mb-4">
                      <h3 className="text-lg font-semibold text-gray-900">
                        {enrollment.studentName || `Student ${enrollment.studentId}`}
                      </h3>
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                        enrollment.status === 'ENROLLED' ? 'bg-green-100 text-green-800' :
                        enrollment.status === 'DROPPED' ? 'bg-red-100 text-red-800' :
                        enrollment.status === 'WAITLISTED' ? 'bg-yellow-100 text-yellow-800' :
                        enrollment.status === 'COMPLETED' ? 'bg-blue-100 text-blue-800' :
                        'bg-gray-100 text-gray-800'
                      }`}>
                        {enrollment.status}
                      </span>
                    </div>
                    
                    <div className="space-y-2 mb-4">
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Course:</span> {enrollment.courseName || `Course ${enrollment.courseId}`}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Student ID:</span> {enrollment.studentId}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Course ID:</span> {enrollment.courseId}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Enrollment Date:</span> {new Date(enrollment.enrollmentDate).toLocaleDateString()}
                      </p>
                    </div>

                    {/* Action Buttons */}
                    {(isAdmin || isProfessor) && (
                      <div className="flex space-x-2">
                        <button
                          onClick={() => handleEditEnrollment(enrollment)}
                          className="flex-1 px-3 py-2 bg-blue-600 text-white text-sm rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                          Edit
                        </button>
                        {isAdmin && (
                          <button
                            onClick={() => handleDeleteEnrollment(enrollment.id)}
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
          {!isLoading && enrollments.length === 0 && (
            <div className="text-center py-12">
              <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <h3 className="mt-2 text-sm font-medium text-gray-900">No enrollments found</h3>
              <p className="mt-1 text-sm text-gray-500">
                {Object.keys(filters).length > 0
                  ? 'Try adjusting your search or filter criteria.'
                  : 'Get started by adding a new enrollment.'}
              </p>
              {(isAdmin || isProfessor) && (
                <div className="mt-6">
                  <button
                    onClick={handleCreateEnrollment}
                    className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                  >
                    <svg className="-ml-1 mr-2 h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                    </svg>
                    Add Enrollment
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