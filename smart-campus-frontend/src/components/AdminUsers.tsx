import React, { useState, useEffect } from 'react';
import { adminAPI } from '../services/api';
import { useToast } from '../contexts/ToastContext';

interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  isActive: boolean;
  createdAt: string;
}

const AdminUsers: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deletingUserId, setDeletingUserId] = useState<number | null>(null);
  const { showSuccess, showError } = useToast();

  // Fetch users on component mount
  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await adminAPI.getAllUsers();
      
      if (response.success && response.data) {
        setUsers(response.data);
      } else {
        setError('Failed to fetch users');
        showError('Error', 'Failed to fetch users');
      }
    } catch (err: any) {
      console.error('Error fetching users:', err);
      const errorMessage = err.response?.data?.message || 'Failed to fetch users';
      setError(errorMessage);
      showError('Error', errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteUser = async (userId: number, userName: string) => {
    if (!confirm(`Are you sure you want to delete user "${userName}"? This action cannot be undone.`)) {
      return;
    }

    try {
      setDeletingUserId(userId);
      const response = await adminAPI.deleteUser(userId);
      
      if (response.success) {
        setUsers(prevUsers => prevUsers.filter(user => user.id !== userId));
        showSuccess('Success', `User "${userName}" has been deleted successfully`);
      } else {
        showError('Error', response.message || 'Failed to delete user');
      }
    } catch (err: any) {
      console.error('Error deleting user:', err);
      const errorMessage = err.response?.data?.message || 'Failed to delete user';
      showError('Error', errorMessage);
    } finally {
      setDeletingUserId(null);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const getRoleBadgeColor = (role: string) => {
    switch (role.toUpperCase()) {
      case 'ADMIN':
        return 'bg-red-100 text-red-800 border-red-200';
      case 'PROFESSOR':
        return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'STUDENT':
        return 'bg-green-100 text-green-800 border-green-200';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="bg-white rounded-lg shadow p-6">
            <div className="animate-pulse">
              <div className="h-8 bg-gray-200 rounded w-1/4 mb-6"></div>
              <div className="space-y-3">
                {[...Array(5)].map((_, i) => (
                  <div key={i} className="h-12 bg-gray-200 rounded"></div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="bg-white rounded-lg shadow p-6">
            <div className="text-center">
              <div className="text-red-600 text-lg font-semibold mb-2">Error Loading Users</div>
              <div className="text-gray-600 mb-4">{error}</div>
              <button
                onClick={fetchUsers}
                className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg transition-colors"
              >
                Try Again
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">User Management</h1>
          <p className="mt-2 text-gray-600">Manage all users in the system</p>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <div className="text-2xl font-bold text-gray-900">{users.length}</div>
            <div className="text-gray-600">Total Users</div>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <div className="text-2xl font-bold text-red-600">
              {users.filter(u => u.role === 'ADMIN').length}
            </div>
            <div className="text-gray-600">Admins</div>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <div className="text-2xl font-bold text-blue-600">
              {users.filter(u => u.role === 'PROFESSOR').length}
            </div>
            <div className="text-gray-600">Professors</div>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <div className="text-2xl font-bold text-green-600">
              {users.filter(u => u.role === 'STUDENT').length}
            </div>
            <div className="text-gray-600">Students</div>
          </div>
        </div>

        {/* Users Table */}
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="px-6 py-4 border-b border-gray-200">
            <h2 className="text-lg font-semibold text-gray-900">All Users</h2>
          </div>
          
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    ID
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Name
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Email
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Role
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Created
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {users.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="px-6 py-4 text-center text-gray-500">
                      No users found
                    </td>
                  </tr>
                ) : (
                  users.map((user) => (
                    <tr key={user.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        {user.id}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">
                          {user.firstName} {user.lastName}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {user.email}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full border ${getRoleBadgeColor(user.role)}`}>
                          {user.role}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                          user.isActive 
                            ? 'bg-green-100 text-green-800 border border-green-200' 
                            : 'bg-red-100 text-red-800 border border-red-200'
                        }`}>
                          {user.isActive ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {formatDate(user.createdAt)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <button
                          onClick={() => handleDeleteUser(user.id, `${user.firstName} ${user.lastName}`)}
                          disabled={deletingUserId === user.id}
                          className={`text-red-600 hover:text-red-900 font-medium ${
                            deletingUserId === user.id ? 'opacity-50 cursor-not-allowed' : ''
                          }`}
                        >
                          {deletingUserId === user.id ? 'Deleting...' : 'Delete'}
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminUsers; 