import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useProfessors } from '../hooks/useProfessors';
import { useUsers } from '../hooks/useUsers';
import { ProfessorCard } from '../components/ProfessorCard';
import { Pagination } from '../components/Pagination';
import { PROFESSOR_STATUSES, PROFESSOR_RANKS } from '../types/dashboard';
import type { Professor, ProfessorFilters, ProfessorRequest } from '../types/dashboard';
import { Navbar } from '../components/Navbar';
import { 
  RefreshCw, 
  GraduationCap, 
  Plus, 
  Search, 
  Filter,
  Calendar,
  User
} from 'lucide-react';
import { ProfessorForm } from '../components/ProfessorForm';

export default function Professors() {
  const { user } = useAuth();
  const {
    professors,
    currentPage,
    pageSize,
    totalPages,
    totalElements,
    isLoading,
    error,
    filters,
    departments,
    handlePageChange,
    handlePageSizeChange,
    handleFiltersChange: updateFilters,
    clearFilters,
    deleteProfessor,
    createProfessor,
    refresh,
  } = useProfessors();

  // Add users hook for admin functionality
  const { users: allUsers, isLoading: usersLoading, error: usersError, refetch: refetchUsers } = useUsers();

  const [searchValue, setSearchValue] = useState('');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingProfessor, setEditingProfessor] = useState<Professor | undefined>();

  // Filter users to show only professors for stats
  const professorUsers = allUsers?.filter(user => user.role === 'PROFESSOR') || [];
  const totalProfessors = professors.length; // Use professors from useProfessors hook
  const activeProfessors = professors.filter(p => p.status === 'ACTIVE').length;
  const fullProfessors = professors.filter(p => p.rank === 'FULL_PROFESSOR').length;

  // Add debugging
  console.log('Professors component render:', {
    professors: professors?.length || 0,
    isLoading,
    error,
    allUsers: allUsers?.length || 0,
    professorUsers: professorUsers.length,
    usersLoading,
    usersError,
    user: user?.role
  });

  // Auto-test endpoints when component mounts
  useEffect(() => {
    console.log('🔍 Professors page mounted - testing endpoints...');
    // testProfessorsEndpoint(); // This line was removed as per the edit hint
    // testAllProfessorsEndpoint(); // This line was removed as per the edit hint
  }, []);

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
    } as ProfessorFilters);
  };

  const handleClearFilters = () => {
    setSearchValue('');
    clearFilters();
  };

  const handleEditProfessor = (professor: Professor) => {
    // TODO: Navigate to edit page or open edit modal
    console.log('Edit professor:', professor);
  };

  const handleDeleteProfessor = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this professor?')) {
      try {
        await deleteProfessor(id);
      } catch (error) {
        console.error('Error deleting professor:', error);
        alert('Failed to delete professor. Please try again.');
      }
    }
  };

  const handleCreateProfessor = () => {
    console.log('🔍 handleCreateProfessor called');
    console.log('🔍 isFormOpen before:', isFormOpen);
    console.log('🔍 editingProfessor:', editingProfessor);
    
    setEditingProfessor(undefined);
    setIsFormOpen(true);
    
    console.log('🔍 isFormOpen after:', true);
    console.log('🔍 ProfessorForm should now be rendered');
  };

  const handleFormSubmit = async (professor: ProfessorRequest) => {
    try {
      if (editingProfessor) {
        // TODO: Implement update professor
        console.log('Update professor:', professor);
      } else {
        // Create new professor
        console.log('Creating professor:', professor);
        await createProfessor(professor);
      }
      setIsFormOpen(false);
      setEditingProfessor(undefined);
      refresh();
      refetchUsers();
    } catch (error) {
      console.error('Error saving professor:', error);
      alert('Failed to save professor. Please try again.');
    }
  };

  const handleFormCancel = () => {
    setIsFormOpen(false);
    setEditingProfessor(undefined);
  };

  const isAdmin = user?.role === 'ADMIN';

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
      <Navbar />
      
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header Section */}
        <div className="bg-white rounded-2xl shadow-xl border border-gray-200 p-8 mb-8 overflow-hidden relative">
          {/* Background decoration */}
          <div className="absolute top-0 right-0 w-64 h-64 bg-gradient-to-br from-blue-100/50 to-indigo-100/50 rounded-full -translate-y-32 translate-x-32"></div>
          <div className="absolute bottom-0 left-0 w-32 h-32 bg-gradient-to-tr from-purple-100/50 to-pink-100/50 rounded-full translate-y-16 -translate-x-16"></div>
          
          <div className="relative z-10">
            <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between">
              <div className="flex-1">
                <div className="flex items-center mb-4">
                  <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg mr-4">
                    <GraduationCap className="w-6 h-6 text-white" />
                  </div>
                  <div>
                    <h1 className="text-3xl lg:text-4xl font-bold text-gray-900 mb-2">
                      Professors
                    </h1>
                    <p className="text-lg text-gray-600">
                      Manage professor information and records
                    </p>
                  </div>
                </div>
                
                {/* Quick stats */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-6">
                  <div className="text-center p-4 bg-gradient-to-br from-blue-50 to-indigo-50 rounded-xl border border-blue-200">
                    <div className="text-2xl font-bold text-blue-700">{totalProfessors}</div>
                    <div className="text-sm text-blue-600 font-medium">Total</div>
                  </div>
                  <div className="text-center p-4 bg-gradient-to-br from-green-50 to-emerald-50 rounded-xl border border-green-200">
                    <div className="text-2xl font-bold text-green-700">{activeProfessors}</div>
                    <div className="text-sm text-green-600 font-medium">Active</div>
                  </div>
                  <div className="text-center p-4 bg-gradient-to-br from-purple-50 to-violet-50 rounded-xl border border-purple-200">
                    <div className="text-2xl font-bold text-purple-700">{fullProfessors}</div>
                    <div className="text-sm text-purple-600 font-medium">Professors</div>
                  </div>
                  <div className="text-center p-4 bg-gradient-to-br from-yellow-50 to-amber-50 rounded-xl border border-yellow-200">
                    <div className="text-2xl font-bold text-yellow-700">1</div>
                    <div className="text-sm text-yellow-600 font-medium">Page</div>
                  </div>
                </div>
              </div>
              
              <div className="mt-6 lg:mt-0 lg:ml-8 flex flex-col sm:flex-row gap-3">
                {isAdmin && (
                  <button
                    onClick={handleCreateProfessor}
                    disabled={isLoading}
                    className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-green-500 to-emerald-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105 disabled:opacity-50 disabled:hover:scale-100"
                  >
                    <User className="w-5 h-5 mr-2" />
                    Create New
                  </button>
                )}
                <button
                  onClick={() => { refresh(); refetchUsers(); }}
                  disabled={isLoading || usersLoading}
                  className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-gray-500 to-slate-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105 disabled:opacity-50 disabled:hover:scale-100"
                >
                  <RefreshCw className={`w-5 h-5 mr-2 ${isLoading || usersLoading ? 'animate-spin' : ''}`} />
                  {isLoading || usersLoading ? 'Refreshing...' : 'Refresh'}
                </button>
                
              </div>
            </div>
          </div>
        </div>

        {/* Search and Filter Section */}
        <div className="bg-white rounded-2xl shadow-xl border border-gray-200 p-6 mb-8">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1 relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Search className="h-5 w-5 text-gray-400" />
              </div>
              <input
                type="text"
                id="professor-search"
                name="professorSearch"
                value={searchValue}
                onChange={(e) => handleSearchChange(e.target.value)}
                placeholder="Search professors by name, email, or department..."
                className="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-xl shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200"
              />
            </div>
            <div className="flex gap-3">
              <button className="inline-flex items-center px-4 py-3 bg-gradient-to-r from-purple-500 to-violet-600 text-white font-semibold rounded-xl shadow-md hover:shadow-lg transition-all duration-200 hover:scale-105">
                <Filter className="w-4 h-4 mr-2" />
                Filter
              </button>
              <button className="inline-flex items-center px-4 py-3 bg-gradient-to-r from-yellow-500 to-amber-600 text-white font-semibold rounded-xl shadow-md hover:shadow-lg transition-all duration-200 hover:scale-105">
                <Calendar className="w-4 h-4 mr-2" />
                Sort
              </button>
            </div>
          </div>
        </div>

        {/* Error Message with Retry Button */}
        {error && (
          <div className="mb-8 bg-red-50 border border-red-200 rounded-2xl p-6">
            <div className="flex items-start justify-between">
              <div className="flex items-start">
                <div className="flex-shrink-0">
                  <svg className="w-6 h-6 text-red-400" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                  </svg>
                </div>
                <div className="ml-3">
                  <h3 className="text-lg font-semibold text-red-800">Error loading professors</h3>
                  <p className="text-red-600 mt-1">{error}</p>
                  <p className="text-sm text-red-500 mt-2">
                    Please check your internet connection and try again.
                  </p>
                </div>
              </div>
              <button
                onClick={refresh}
                disabled={isLoading}
                className="ml-4 inline-flex items-center px-4 py-2 bg-red-600 text-white text-sm font-medium rounded-lg hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                <RefreshCw className={`w-4 h-4 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
                {isLoading ? 'Retrying...' : 'Retry'}
              </button>
            </div>
          </div>
        )}

        {/* Loading State */}
        {(isLoading || usersLoading) && professors.length === 0 && (
          <div className="space-y-6">
            {[...Array(3)].map((_, index) => (
              <div key={index} className="bg-white rounded-2xl shadow-lg border border-gray-200 p-6 animate-pulse overflow-hidden relative">
                <div className="absolute top-0 left-0 w-1 h-full bg-gradient-to-b from-blue-500 to-indigo-600"></div>
                <div className="flex items-start space-x-4">
                  <div className="w-10 h-10 bg-gray-200 rounded-xl flex-shrink-0"></div>
                  <div className="flex-1 space-y-3">
                    <div className="h-6 bg-gray-200 rounded w-3/4"></div>
                    <div className="flex space-x-2">
                      <div className="h-6 bg-gray-200 rounded w-16"></div>
                      <div className="h-6 bg-gray-200 rounded w-20"></div>
                      <div className="h-6 bg-gray-200 rounded w-16"></div>
                    </div>
                    <div className="h-4 bg-gray-200 rounded w-full"></div>
                    <div className="h-4 bg-gray-200 rounded w-2/3"></div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Empty State */}
        {!isLoading && !usersLoading && professors.length === 0 && !error && !usersError && (
          <div className="bg-white rounded-2xl shadow-xl border border-gray-200 p-12 text-center">
            <div className="w-20 h-20 bg-gradient-to-br from-gray-100 to-slate-100 rounded-2xl flex items-center justify-center mx-auto mb-6 shadow-lg">
              <GraduationCap className="w-10 h-10 text-gray-400" />
            </div>
            <h3 className="text-2xl font-bold text-gray-900 mb-3">No professors found</h3>
            <p className="text-gray-600 mb-8 max-w-md mx-auto">
              {Object.keys(filters).length > 0
                ? 'Try adjusting your search or filter criteria to find what you\'re looking for.'
                : 'There are no professors in the system yet. Get started by adding a new professor if you\'re an administrator.'}
            </p>
            {isAdmin && (
              <button
                onClick={handleCreateProfessor}
                className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105"
              >
                <Plus className="w-5 h-5 mr-2" />
                Add First Professor
              </button>
            )}
          </div>
        )}

        {/* Professors Table */}
        {!isLoading && !usersLoading && professors.length > 0 && (
          <>
            <div className="bg-white rounded-2xl shadow-xl border border-gray-200 overflow-hidden">
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gradient-to-r from-gray-50 to-slate-50">
                    <tr>
                      <th className="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                        Professor
                      </th>
                      <th className="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                        ID
                      </th>
                      <th className="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                        Email
                      </th>
                      <th className="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                        Department
                      </th>
                      <th className="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                        Rank
                      </th>
                      <th className="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                        Status
                      </th>
                      {isAdmin && (
                        <th className="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                          Actions
                        </th>
                      )}
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {professors.map((professor) => (
                      <tr key={`professor-${professor.id}`} className="hover:bg-gray-50 transition-colors duration-200">
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="flex items-center">
                            <div className="w-10 h-10 bg-gradient-to-br from-blue-100 to-indigo-100 rounded-xl flex items-center justify-center flex-shrink-0 mr-4">
                              <User className="w-5 h-5 text-blue-600" />
                            </div>
                            <div>
                              <div className="text-sm font-semibold text-gray-900">
                                {`${professor.firstName} ${professor.lastName}`}
                              </div>
                              <div className="text-sm text-gray-500">Professor</div>
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {professor.id}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {professor.email}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {professor.department}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {professor.rank}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold ${
                            professor.status === 'ACTIVE' 
                              ? 'bg-green-100 text-green-800' 
                              : professor.status === 'INACTIVE'
                              ? 'bg-red-100 text-red-800'
                              : 'bg-gray-100 text-gray-800'
                          }`}>
                            {professor.status}
                          </span>
                        </td>
                        {isAdmin && (
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                            <div className="flex space-x-2">
                              <button
                                onClick={() => handleEditProfessor(professor)}
                                className="inline-flex items-center px-3 py-1.5 bg-gradient-to-r from-blue-500 to-indigo-600 text-white text-xs font-medium rounded-lg hover:shadow-lg transition-all duration-200 hover:scale-105"
                              >
                                Edit
                              </button>
                              <button
                                onClick={() => handleDeleteProfessor(professor.id)}
                                disabled={isLoading}
                                className="inline-flex items-center px-3 py-1.5 bg-gradient-to-r from-red-500 to-pink-600 text-white text-xs font-medium rounded-lg hover:shadow-lg transition-all duration-200 hover:scale-105 disabled:opacity-50"
                              >
                                Delete
                              </button>
                            </div>
                          </td>
                        )}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </>
        )}

        {/* Pagination */}
        {!isLoading && professors.length > 0 && totalPages > 1 && (
          <div className="mt-8">
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={handlePageChange}
              pageSize={pageSize}
              onPageSizeChange={handlePageSizeChange}
              totalElements={totalElements}
            />
          </div>
        )}
      </div>

      {isFormOpen && (
        <ProfessorForm
          professor={editingProfessor}
          isOpen={isFormOpen}
          onClose={handleFormCancel}
          onSubmit={handleFormSubmit}
          isLoading={isLoading}
        />
      )}
    </div>
  );
}
