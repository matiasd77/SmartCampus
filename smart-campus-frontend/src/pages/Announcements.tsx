import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useAnnouncements } from '../hooks/useAnnouncements';
import { AnnouncementCard } from '../components/AnnouncementCard';
import { AnnouncementDetailModal } from '../components/AnnouncementDetailModal';
import { Pagination } from '../components/Pagination';
import type { Announcement } from '../types/dashboard';
import { Navbar } from '../components/Navbar';
import { 
  Megaphone, 
  RefreshCw, 
  Plus, 
  Search, 
  Filter,
  Calendar,
  Users,
  TrendingUp
} from 'lucide-react';

export default function Announcements() {
  const { user } = useAuth();
  const {
    announcements,
    currentPage,
    pageSize,
    totalPages,
    totalElements,
    isLoading,
    error,
    isCreating,
    isUpdating,
    isDeleting,
    handlePageChange,
    handlePageSizeChange,
    createAnnouncement,
    updateAnnouncement,
    deleteAnnouncement,
    refresh,
  } = useAnnouncements();

  // Early return if announcements is undefined
  if (!announcements) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
        <Navbar />
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="bg-white rounded-2xl shadow-xl border border-gray-200 p-8 text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
            <p className="text-gray-600">Loading announcements...</p>
          </div>
        </div>
      </div>
    );
  }

  const [selectedAnnouncement, setSelectedAnnouncement] = useState<Announcement | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleViewAnnouncement = (announcement: Announcement) => {
    setSelectedAnnouncement(announcement);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedAnnouncement(null);
  };

  const handleEditAnnouncement = (announcement: Announcement) => {
    // TODO: Navigate to edit page or open edit modal
    console.log('Edit announcement:', announcement);
  };

  const handleDeleteAnnouncement = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this announcement?')) {
      try {
        await deleteAnnouncement(id);
        refresh();
      } catch (error) {
        console.error('Error deleting announcement:', error);
      }
    }
  };

  const isAdmin = user?.role === 'ADMIN';

  // Debug logging for announcements data
  console.log('Announcements component - announcements data:', announcements);
  console.log('Announcements component - announcements length:', announcements?.length);

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
                    <Megaphone className="w-6 h-6 text-white" />
                  </div>
                  <div>
                    <h1 className="text-3xl lg:text-4xl font-bold text-gray-900 mb-2">
                      Announcements
                    </h1>
                    <p className="text-lg text-gray-600">
                      Stay updated with the latest campus news and important information
                    </p>
                  </div>
                </div>
                
                {/* Quick stats */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-6">
                  <div className="text-center p-4 bg-gradient-to-br from-blue-50 to-indigo-50 rounded-xl border border-blue-200">
                    <div className="text-2xl font-bold text-blue-700">{totalElements}</div>
                    <div className="text-sm text-blue-600 font-medium">Total</div>
                  </div>
                  <div className="text-center p-4 bg-gradient-to-br from-green-50 to-emerald-50 rounded-xl border border-green-200">
                    <div className="text-2xl font-bold text-green-700">{(announcements ?? []).filter(a => a.priority === 'HIGH' || a.priority === 'URGENT').length}</div>
                    <div className="text-sm text-green-600 font-medium">Important</div>
                  </div>
                  <div className="text-center p-4 bg-gradient-to-br from-purple-50 to-violet-50 rounded-xl border border-purple-200">
                    <div className="text-2xl font-bold text-purple-700">{(announcements ?? []).filter(a => a.status === 'ACTIVE').length}</div>
                    <div className="text-sm text-purple-600 font-medium">Active</div>
                  </div>
                  <div className="text-center p-4 bg-gradient-to-br from-yellow-50 to-amber-50 rounded-xl border border-yellow-200">
                    <div className="text-2xl font-bold text-yellow-700">{currentPage}</div>
                    <div className="text-sm text-yellow-600 font-medium">Page</div>
                  </div>
                </div>
              </div>
              
              <div className="mt-6 lg:mt-0 lg:ml-8 flex flex-col sm:flex-row gap-3">
                <button
                  onClick={refresh}
                  disabled={isLoading}
                  className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-gray-500 to-slate-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105 disabled:opacity-50 disabled:hover:scale-100"
                >
                  <RefreshCw className={`w-5 h-5 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
                  {isLoading ? 'Refreshing...' : 'Refresh'}
                </button>
                
                {isAdmin && (
                  <button className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-green-500 to-emerald-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105">
                    <Plus className="w-5 h-5 mr-2" />
                    Create New
                  </button>
                )}
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
                placeholder="Search announcements..."
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
                  <h3 className="text-lg font-semibold text-red-800">Error loading announcements</h3>
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
        {isLoading && (announcements ?? []).length === 0 && (
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
        {!isLoading && (announcements ?? []).length === 0 && !error && (
          <div className="bg-white rounded-2xl shadow-xl border border-gray-200 p-12 text-center">
            <div className="w-20 h-20 bg-gradient-to-br from-gray-100 to-slate-100 rounded-2xl flex items-center justify-center mx-auto mb-6 shadow-lg">
              <Megaphone className="w-10 h-10 text-gray-400" />
            </div>
            <h3 className="text-2xl font-bold text-gray-900 mb-3">No announcements yet</h3>
            <p className="text-gray-600 mb-8 max-w-md mx-auto">
              There are no announcements to display at the moment. Check back later for updates or create a new announcement if you're an administrator.
            </p>
            {isAdmin && (
              <button className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105">
                <Plus className="w-5 h-5 mr-2" />
                Create First Announcement
              </button>
            )}
          </div>
        )}

        {/* Announcements List */}
        {!isLoading && (announcements ?? []).length > 0 && (
          <div className="space-y-6 mb-8">
            {(announcements ?? []).map((announcement) => (
              <AnnouncementCard
                key={announcement.id}
                announcement={announcement}
                onView={handleViewAnnouncement}
                onEdit={isAdmin ? handleEditAnnouncement : undefined}
                onDelete={isAdmin ? handleDeleteAnnouncement : undefined}
                showActions={isAdmin}
              />
            ))}
          </div>
        )}

        {/* Pagination */}
        {!isLoading && totalPages > 1 && (
          <div className="bg-white rounded-2xl shadow-xl border border-gray-200 p-6">
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              totalElements={totalElements}
              pageSize={pageSize}
              onPageChange={handlePageChange}
              onPageSizeChange={handlePageSizeChange}
              isLoading={isLoading}
            />
          </div>
        )}
      </div>

      {/* Detail Modal */}
      <AnnouncementDetailModal
        announcement={selectedAnnouncement}
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        onEdit={isAdmin ? handleEditAnnouncement : undefined}
        onDelete={isAdmin ? handleDeleteAnnouncement : undefined}
        showActions={isAdmin}
      />
    </div>
  );
}
