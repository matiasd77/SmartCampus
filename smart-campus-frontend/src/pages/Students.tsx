import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useStudents } from '../hooks/useStudents';
import { StudentCard } from '../components/StudentCard';
import { SearchAndFilters } from '../components/SearchAndFilters';
import { Pagination } from '../components/Pagination';
import { STUDENT_STATUSES, STUDENT_YEARS } from '../types/dashboard';
import type { Student, StudentFilters } from '../types/dashboard';
import { Navbar } from '../components/Navbar';

export default function Students() {
  const { user } = useAuth();
  const {
    students,
    currentPage,
    pageSize,
    totalPages,
    totalElements,
    isLoading,
    error,
    filters,
    majors,
    handlePageChange,
    handlePageSizeChange,
    handleFiltersChange: updateFilters,
    clearFilters,
    deleteStudent,
    refresh,
  } = useStudents();

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
    } as StudentFilters);
  };

  const handleClearFilters = () => {
    setSearchValue('');
    clearFilters();
  };

  const handleEditStudent = (student: Student) => {
    // TODO: Navigate to edit page or open edit modal
    console.log('Edit student:', student);
  };

  const handleDeleteStudent = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this student?')) {
      try {
        await deleteStudent(id);
      } catch (error) {
        console.error('Error deleting student:', error);
        alert('Failed to delete student. Please try again.');
      }
    }
  };

  const handleCreateStudent = () => {
    // TODO: Navigate to create page or open create modal
    console.log('Create new student');
  };

  const isAdmin = user?.role === 'ADMIN';

  // Prepare filter options
  const filterOptions = [
    {
      key: 'status',
      label: 'Status',
      options: STUDENT_STATUSES.map(status => ({ value: status.value, label: status.label })),
    },
    {
      key: 'year',
      label: 'Year',
      options: STUDENT_YEARS.map(year => ({ value: year.value.toString(), label: year.label })),
    },
    {
      key: 'major',
      label: 'Major',
      options: majors.map(major => ({ value: major, label: major })),
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
                <h1 className="text-2xl font-bold text-gray-900">Students</h1>
                <p className="mt-1 text-sm text-gray-500">
                  Manage student information and records.
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
            placeholder="Search students by name, email, or student ID..."
            showCreateButton={isAdmin}
            onCreateClick={handleCreateStudent}
            createButtonText="Add Student"
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

          {/* Students List */}
          {!isLoading && students.length > 0 && (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                {students.map((student) => (
                  <StudentCard
                    key={student.id}
                    student={student}
                    onEdit={isAdmin ? handleEditStudent : undefined}
                    onDelete={isAdmin ? handleDeleteStudent : undefined}
                    showActions={isAdmin}
                  />
                ))}
              </div>

              {/* Pagination */}
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                totalElements={totalElements}
                pageSize={pageSize}
                onPageChange={handlePageChange}
                onPageSizeChange={handlePageSizeChange}
              />
            </>
          )}

          {/* Empty State */}
          {!isLoading && students.length === 0 && (
            <div className="text-center py-12">
              <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
              </svg>
              <h3 className="mt-2 text-sm font-medium text-gray-900">No students found</h3>
              <p className="mt-1 text-sm text-gray-500">
                {Object.keys(filters).length > 0
                  ? 'Try adjusting your search or filter criteria.'
                  : 'Get started by adding a new student.'}
              </p>
              {isAdmin && (
                <div className="mt-6">
                  <button
                    onClick={handleCreateStudent}
                    className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                  >
                    <svg className="-ml-1 mr-2 h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                    </svg>
                    Add Student
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
